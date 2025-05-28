package org.sleepless_artery.lesson_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.sleepless_artery.lesson_service.dto.LessonInfoDto;
import org.sleepless_artery.lesson_service.dto.LessonRequestDto;
import org.sleepless_artery.lesson_service.dto.LessonContentDto;
import org.sleepless_artery.lesson_service.model.Lesson;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LessonMapper {

    Lesson toLesson(LessonRequestDto lessonRequestDto);

    LessonContentDto toLessonContentDto(Lesson lesson);

    LessonInfoDto toLessonInfoDto(Lesson lesson);
}
