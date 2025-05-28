package org.sleepless_artery.lesson_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;


@Getter
@AllArgsConstructor
public class LessonContentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private Long courseId;
    private String description;
    private String content;
}
