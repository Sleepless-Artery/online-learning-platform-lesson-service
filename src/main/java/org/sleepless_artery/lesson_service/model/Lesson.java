package org.sleepless_artery.lesson_service.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "lessons")
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "sequence_number")
    private  Long sequenceNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "content")
    private String content;
}
