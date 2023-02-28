package me.safgbad.calendar.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/calendar")
@Tag(
    name = "Calendar API",
    description = "CRUD-operations + getting tasks distribution between days of a specified period"
)
public class CalendarController {

  private final CalendarService calendarService;

  @Autowired
  public CalendarController(CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  @PostMapping
  @Operation(summary = "Adding task")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "<i>repeatability</i> accepts values {\"<b>one_time</b>\", \"<b>daily</b>\"," +
          " \"<b>weekly</b>\", \"<b>monthly</b>\", \"<b>yearly</b>\"} (case insensitive)<br>" +
          "<i>taskDate</i> format: \"dd.MM.yyyy HH:mm\" (i.e. \"<b>28.02.2023 17:04</b>\")<br>" +
          "<i>id</i> can be omitted, it will be generated automatically",
      content = @Content(schema = @Schema(implementation = TaskDto.class))
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Task is successfully added to database"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad input; details in response",
          content = {
              @Content(mediaType = "text/plain")
          }
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error; details in response",
          content = {
              @Content(mediaType = "text/plain")
          }
      )
  })
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
  @Operation(summary = "Getting task by id")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Task with specified id is successfully found",
          content = {
              @Content(schema = @Schema(implementation = TaskDto.class))
          }
      ),
      @ApiResponse(
          responseCode = "204",
          description = "Task with specified id hasn't been found"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error; details in response",
          content = {
              @Content(mediaType = "text/plain")
          }
      )
  })
  public ResponseEntity<Object> getTask(@PathVariable Long id) {
    TaskDto taskDto;
    try {
      taskDto = calendarService.getTask(id);
    } catch (RuntimeException e) {
      return ResponseEntity.internalServerError()
          .body(e.toString());
    }
    if (taskDto == null) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(taskDto);
  }

  @GetMapping
  @Operation(summary = "Getting tasks distribution between days of a specified period")
  @Parameters(value = {
      @Parameter(
          name = "distributionDto",
          description = "<i>dateFrom</i> == <b>null</b> " +
              "==> period left restriction equals the most early task in db<br>" +
              "<i>dateTo</i> == <b>null</b> " +
              "==> period right restriction equals (current date + 30 days)<br>" +
              "<i>isActive</i> == <b>null</b> " +
              "==> tasks are taken into account no matter if they are active or not",
          schema = @Schema(implementation = DistributionDto.class)
      )
  })
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Tasks are successfully found and " +
              "distributed between days in specified period"
      ),
      @ApiResponse(
          responseCode = "204",
          description = "Task haven't been found in specified period"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error; details in response",
          content = {
              @Content(mediaType = "text/plain")
          }
      )
  })
  public ResponseEntity<Object> getTasks(@RequestBody DistributionDto distributionDto) {
    Map<String, List<TaskDto>> tasks;
    try {
      tasks = calendarService.getTasks(distributionDto);
    } catch (NoSuchElementException e) {
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      return ResponseEntity.internalServerError().body(e.toString());
    }

    return ResponseEntity.ok(tasks);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Updating task with specified id")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "<i>repeatability</i> accepts values {\"<b>one_time</b>\", \"<b>daily</b>\"," +
          " \"<b>weekly</b>\", \"<b>monthly</b>\", \"<b>yearly</b>\"} (case insensitive)<br>" +
          "<i>taskDate</i> format: \"dd.MM.yyyy HH:mm\" (i.e. \"<b>28.02.2023 17:04</b>\")<br><br>" +
          "<i>id</i> can be omitted, it will automatically be ignored<br>" +
          "fields won't be updated if corresponding values are not specified or null",
      content = @Content(schema = @Schema(implementation = TaskDto.class))
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Task with specified id is successfully found and updated"
      ),
      @ApiResponse(
          responseCode = "204",
          description = "Task with specified id hasn't been found"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad input; details in response",
          content = {
              @Content(mediaType = "text/plain")
          }
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error; details in response",
          content = {
              @Content(mediaType = "text/plain")
          }
      )
  })
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
  @Operation(summary = "Deleting task by id")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Task with specified id is successfully deleted"
      ),
      @ApiResponse(
          responseCode = "204",
          description = "Task with specified id hasn't been found"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error; details in response",
          content = {
              @Content(mediaType = "text/plain")
          }
      )
  })
  public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
    try {
      calendarService.deleteTask(id);
    } catch (NoSuchElementException e) {
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      return ResponseEntity.internalServerError().body(e.toString());
    }

    return ResponseEntity.ok().build();
  }
}
