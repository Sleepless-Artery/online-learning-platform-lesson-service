package org.sleepless_artery.lesson_service.service;

import org.sleepless_artery.lesson_service.dto.LessonContentDto;
import org.sleepless_artery.lesson_service.dto.LessonInfoDto;
import org.sleepless_artery.lesson_service.dto.LessonRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LessonService {

    LessonContentDto getLessonById(Long lessonId);

    boolean existsById(Long id);

    Page<LessonInfoDto> getLessonsByCourseId(Long courseId, Pageable pageable);

    LessonContentDto createLesson(LessonRequestDto lessonRequestDto);

    LessonContentDto updateLesson(Long lessonId, LessonRequestDto lessonRequestDto);

    void deleteLesson(Long lessonId);

    void deleteLessonsByCourseId(Long courseId);
}
