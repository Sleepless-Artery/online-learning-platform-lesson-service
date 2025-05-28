package org.sleepless_artery.lesson_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sleepless_artery.lesson_service.dto.LessonContentDto;
import org.sleepless_artery.lesson_service.dto.LessonInfoDto;
import org.sleepless_artery.lesson_service.dto.LessonRequestDto;
import org.sleepless_artery.lesson_service.service.LessonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("lessons")
@RequiredArgsConstructor
@Validated
public class LessonController {

    private final LessonService lessonService;


    @GetMapping("/{id}")
    public ResponseEntity<LessonContentDto> getLesson(@PathVariable final Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }


    @GetMapping("/course/{id}")
    public ResponseEntity<Page<LessonInfoDto>> getLessonsOfCourse(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(id, pageable));
    }


    @PostMapping
    public ResponseEntity<LessonContentDto> createLesson(@Valid @RequestBody LessonRequestDto lessonRequestDto) {
        return new ResponseEntity<>(lessonService.createLesson(lessonRequestDto), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<LessonContentDto> updateLesson(
            @PathVariable Long id, @Valid @RequestBody LessonRequestDto lessonDto
    ) {
        return ResponseEntity.ok(lessonService.updateLesson(id, lessonDto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/course/{id}")
    public ResponseEntity<Void> deleteLessonsFromCourse(@PathVariable Long id) {
        lessonService.deleteLessonsByCourseId(id);
        return ResponseEntity.noContent().build();
    }
}
