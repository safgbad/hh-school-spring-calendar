package me.safgbad.calendar.util;

import me.safgbad.calendar.dto.TaskDto;
import me.safgbad.calendar.model.Task;

import java.util.Optional;

public class ConvertUtils {

  public static Task getTaskFromDto(TaskDto taskDto) {
    if (taskDto == null) {
      return null;
    }
    Task task = new Task(taskDto.getSummary(),
        taskDto.getRepeatability(),
        taskDto.getTaskTime(),
        taskDto.getIsActive());
    Optional.ofNullable(task.getDescription()).ifPresent(task::setDescription);

    return task;
  }

  public static TaskDto getDtoFromTask(Task task) {
    return new TaskDto(
        task.getId(),
        task.getSummary(),
        task.getDescription(),
        task.getRepeatability(),
        task.getTaskTime(),
        task.getIsActive()
    );
  }
}
