package me.safgbad.calendar.util;

import me.safgbad.calendar.dto.DistributionDto;
import me.safgbad.calendar.dto.TaskDto;
import me.safgbad.calendar.model.Task;
import me.safgbad.calendar.model.enums.Repeatability;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConvertUtils {

  public static Task getTaskFromDto(TaskDto taskDto) {
    if (taskDto == null) {
      return null;
    }

    Task task = new Task();
    task.setSummary(taskDto.getSummary());
    task.setDescription(taskDto.getDescription());
    task.setIsActive(taskDto.getIsActive());

    String dtoRepeatability;
    if ((dtoRepeatability = taskDto.getRepeatability()) != null) {
      Repeatability repeatability = Repeatability.valueOf(dtoRepeatability.toUpperCase());
      task.setRepeatability(repeatability);
    }

    String dtoTaskTime;
    if ((dtoTaskTime = taskDto.getTaskTime()) != null) {
      LocalDateTime taskTime = LocalDateTime.parse(dtoTaskTime, TaskDto.DATE_TIME_FORMATTER);
      task.setTaskTime(taskTime);
    }

    return task;
  }

  public static TaskDto getDtoFromTask(Task task) {
    return new TaskDto(
        task.getSummary(),
        task.getDescription(),
        task.getRepeatability().name(),
        task.getTaskTime().format(TaskDto.DATE_TIME_FORMATTER),
        task.getIsActive()
    );
  }

  public static LocalDate getDateFromDto(String dtoDate) {
    if (dtoDate == null) {
      return null;
    }

    return LocalDate.parse(dtoDate, DistributionDto.DATE_FORMATTER);
  }
}
