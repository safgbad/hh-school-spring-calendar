package me.safgbad.calendar.controllers;

import me.safgbad.calendar.dto.DistributionDto;
import me.safgbad.calendar.dto.TaskDto;
import me.safgbad.calendar.services.CalendarService;
import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

  private final CalendarService calendarService;

  @Autowired
  public CalendarController(CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  @PostMapping
  public ResponseEntity<Object> addTask(@RequestBody TaskDto taskDto) {
    try {
      calendarService.addTask(taskDto);
    } catch (PropertyValueException | DateTimeParseException | IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(e.toString());
    } catch (RuntimeException e) {
      return ResponseEntity.internalServerError()
          .body(e.toString());
    }

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
    TaskDto taskDto = calendarService.getTask(id);
    if (taskDto == null) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(taskDto);
  }

  @GetMapping
  public ResponseEntity<Object> getTasks(@RequestBody DistributionDto distributionDto) {
    try {
      return ResponseEntity.ok()
          .body(calendarService.getTasks(distributionDto));
    } catch (NoSuchElementException e) {
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.toString());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> updateTask(@RequestBody TaskDto taskDto,
                                           @PathVariable Long id) {
    try {
      calendarService.updateTask(taskDto, id);
    } catch (NoSuchElementException e) {
      return ResponseEntity.noContent().build();
    } catch (DateTimeParseException e) {
      return ResponseEntity.badRequest()
          .body(e.toString());
    } catch (RuntimeException e) {
      return ResponseEntity.internalServerError()
          .body(e.toString());
    }

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
    try {
      calendarService.deleteTask(id);
    } catch (NoSuchElementException e) {
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.toString());
    }

    return ResponseEntity.ok().build();
  }
}
