package org.sleepless_artery.lesson_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.lesson_service.service.LessonService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final LessonService lessonService;

    @KafkaListener(topics = "course.courses.deleted", groupId = "lesson-service")
    public void listen(Long id) {
        lessonService.deleteLessonsByCourseId(id);
    }
}
