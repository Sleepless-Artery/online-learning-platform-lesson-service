package org.sleepless_artery.lesson_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Getter @Setter
@AllArgsConstructor
public class LessonInfoDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private Long courseId;
    private String description;
}
