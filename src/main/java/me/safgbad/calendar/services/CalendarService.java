package me.safgbad.calendar.services;

import me.safgbad.calendar.dto.DistributionDto;
import me.safgbad.calendar.dto.TaskDto;

import java.util.List;
import java.util.Map;

public interface CalendarService {
  void addTask(TaskDto taskDto);

  TaskDto getTask(Long id);

  Map<String, List<TaskDto>> getTasks(DistributionDto distributionDto);

  void updateTask(TaskDto taskDto, Long id);

  void deleteTask(Long id);
}
