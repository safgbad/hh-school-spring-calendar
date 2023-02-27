package me.safgbad.calendar.services;

import me.safgbad.calendar.dto.TaskDto;
import me.safgbad.calendar.model.Task;

public interface CalendarService {
  void addTask(TaskDto taskDto);

  Task getTask(Long id);

  void updateTask(TaskDto taskDto, Long id);

  void deleteTask(Long id);
}
