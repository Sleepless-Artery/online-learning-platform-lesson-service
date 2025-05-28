package org.sleepless_artery.lesson_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sleepless_artery.lesson_service.dto.LessonContentDto;
import org.sleepless_artery.lesson_service.dto.LessonInfoDto;
import org.sleepless_artery.lesson_service.dto.LessonRequestDto;
import org.sleepless_artery.lesson_service.exception.InvalidCourseIdException;
import org.sleepless_artery.lesson_service.exception.LessonAlreadyExistsException;
import org.sleepless_artery.lesson_service.exception.LessonNotFoundException;
import org.sleepless_artery.lesson_service.grpc.client.CourseVerificationServiceGrpcClient;
import org.sleepless_artery.lesson_service.kafka.producer.KafkaProducer;
import org.sleepless_artery.lesson_service.mapper.LessonMapper;
import org.sleepless_artery.lesson_service.model.Lesson;
import org.sleepless_artery.lesson_service.repository.LessonRepository;
import org.sleepless_artery.lesson_service.service.LessonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    private final CacheManager cacheManager;

    private final CourseVerificationServiceGrpcClient courseVerificationServiceGrpcClient;


    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.topic.prefix}")
    private String prefix;

    @Value("${spring.kafka.topic.domain}")
    private String domain;



    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "lessons", key = "#lessonId")
    public LessonContentDto getLessonById(Long lessonId) {
        log.info("Getting lesson with id: {}", lessonId);

        return lessonMapper.toLessonContentDto(lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.warn("Lesson with id '{}' does not exist", lessonId);
                    return new LessonNotFoundException();
                })
        );
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "courseLessons", key = "#courseId")
    public Page<LessonInfoDto> getLessonsByCourseId(Long courseId, Pageable pageable) {
        log.info("Getting lessons from course with ID: {}", courseId);
        return lessonRepository.findAllByCourseIdOrderBySequenceNumber(courseId, pageable)
                .map(lessonMapper::toLessonInfoDto);
    }


    @Override
    @Transactional
    @CacheEvict(value = "courseLessons", key = "#lessonRequestDto.courseId")
    public LessonContentDto createLesson(LessonRequestDto lessonRequestDto) {
        String title = lessonRequestDto.getTitle();

        log.info("Creating lesson '{}'", title);

        Long courseId = lessonRequestDto.getCourseId();
        Long sequenceNumber = lessonRequestDto.getSequenceNumber();

        if (!courseVerificationServiceGrpcClient.verifyCourseExistence(courseId)) {
            log.warn("Course with ID '{}' does not exist", courseId);
            throw new InvalidCourseIdException("Course with ID '" + courseId + "' does not exist");
        }

        if (lessonRepository.existsByCourseIdAndTitle(courseId, title)) {
            log.warn("Lesson '{}' already exists in course with ID '{}'", title, courseId);
            throw new LessonAlreadyExistsException("Lesson '" + title + "' already exists");
        }

        if (lessonRepository.existsByCourseIdAndSequenceNumber(courseId, sequenceNumber)) {
            log.warn("Lesson with sequence number '{}' already exists in course with ID {}", sequenceNumber, courseId);
            throw new LessonAlreadyExistsException(
                    "Lesson with sequence number '" + sequenceNumber + "' already exists"
            );
        }

        LessonContentDto lessonContent = lessonMapper.toLessonContentDto(
                lessonRepository.save(lessonMapper.toLesson(lessonRequestDto))
        );
        cacheManager.getCache("lessons").put(lessonContent.getId(), lessonContent);

        kafkaProducer.send(
                String.format("%s.%s.%s", prefix, domain, "updated"),
                courseId
        );

        return lessonContent;
    }


    @Override
    @Transactional
    @CachePut(value = "lessons", key = "#lessonId")
    @CacheEvict(value = "courseLessons", key = "#lessonRequestDto.courseId")
    public LessonContentDto updateLesson(Long lessonId, LessonRequestDto lessonRequestDto) {
        log.info("Updating lesson with ID '{}'", lessonId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                   log.warn("Cannot found lesson with ID: {}", lessonId);
                   return new LessonNotFoundException();
                });

        Long courseId = lesson.getCourseId();

        if (!courseId.equals(lessonRequestDto.getCourseId())) {
            log.warn("Course IDs don't match");
            throw new InvalidCourseIdException("Course ID does not match the source one");
        }

        String newTitle = lessonRequestDto.getTitle();
        Long order = lessonRequestDto.getSequenceNumber();

        if (!lesson.getTitle().equals(newTitle)) {
            if (lessonRepository.existsByCourseIdAndTitle(courseId, newTitle)) {
                log.warn("Lesson '{}' already exists in course with ID {}",
                        newTitle, lesson.getCourseId()
                );
                throw new LessonAlreadyExistsException("Lesson '" + newTitle + "' already exists");
            }
            lesson.setTitle(lessonRequestDto.getTitle());
        }

        if (!Objects.equals(lesson.getSequenceNumber(), order)) {
            if (lessonRepository.existsByCourseIdAndSequenceNumber(courseId, order)) {
                log.warn("Lesson with sequence number '{}' already exists in course with ID {}", order, courseId);
                throw new LessonAlreadyExistsException("Lesson with sequence number '" + order + "' already exists");
            }
            lesson.setSequenceNumber(lessonRequestDto.getSequenceNumber());
        }

        if (!lesson.getDescription().equals(lessonRequestDto.getDescription())) {
            lesson.setDescription(lessonRequestDto.getDescription());
        }

        if (!lesson.getContent().equals(lessonRequestDto.getContent())) {
            lesson.setContent(lessonRequestDto.getContent());
        }

        kafkaProducer.send(
                String.format("%s.%s.%s", prefix, domain, "updated"),
                courseId
        );

        return lessonMapper.toLessonContentDto(lessonRepository.save(lesson));
    }


    @Override
    @Transactional
    @CacheEvict(value = "lessons", key = "#lessonId")
    public void deleteLesson(Long lessonId) {
        log.info("Deleting lesson with ID '{}'", lessonId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.warn("Lesson with ID '{}' does not exist", lessonId);
                    return new LessonNotFoundException();
                });

        lessonRepository.delete(lesson);
        cacheManager.getCache("courseLessons").evict(lesson.getCourseId());

        kafkaProducer.send(
                String.format("%s.%s.%s", prefix, domain, "updated"),
                lesson.getCourseId()
        );
    }


    @Override
    @Transactional
    @CacheEvict(value = "courseLessons", key = "#courseId")
    public void deleteLessonsByCourseId(Long courseId) {
        log.info("Deleting lessons from course with id '{}'", courseId);
        lessonRepository.findAllLessonIdsByCourseId(courseId)
                .forEach(id -> cacheManager.getCache("lessons").evict(id));
        lessonRepository.deleteByCourseId(courseId);
    }
}