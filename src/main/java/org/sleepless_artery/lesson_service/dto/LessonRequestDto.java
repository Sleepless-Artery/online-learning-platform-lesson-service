package org.sleepless_artery.lesson_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class LessonRequestDto {

    @NotBlank(message = "Lesson title cannot be blank")
    private String title;

    @NotNull(message = "Course ID must be defined")
    private final Long courseId;

    @NotNull(message = "Sequence number must be defined")
    private Long sequenceNumber;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 65535, message = "Lesson too long")
    private String content;
}
