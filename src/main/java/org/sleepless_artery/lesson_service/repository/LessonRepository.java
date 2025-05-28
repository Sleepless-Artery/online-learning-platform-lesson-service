package org.sleepless_artery.lesson_service.repository;

import org.sleepless_artery.lesson_service.model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Page<Lesson> findAllByCourseIdOrderBySequenceNumber(Long courseId, Pageable pageable);

    @Query("SELECT l.id FROM Lesson l WHERE l.courseId = :courseId")
    List<Long> findAllLessonIdsByCourseId(@Param("courseId") Long courseId);

    boolean existsByCourseIdAndTitle(Long courseId, String title);

    boolean existsByCourseIdAndSequenceNumber(Long courseId, Long sequenceNumber);

    void deleteByCourseId(Long courseId);
}
