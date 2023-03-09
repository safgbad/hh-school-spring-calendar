package me.safgbad.calendar.services.impl;

import me.safgbad.calendar.dao.TaskDao;
import me.safgbad.calendar.dto.DistributionDto;
import me.safgbad.calendar.dto.TaskDto;
import me.safgbad.calendar.model.Task;
import me.safgbad.calendar.services.CalendarService;
import me.safgbad.calendar.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CalendarServiceImpl implements CalendarService {

  private final TaskDao taskDao;

  @Value("${calendar.number.of.days.ahead}")
  private int numberOfDaysAhead;

  @Autowired
  public CalendarServiceImpl(TaskDao taskDao) {
    this.taskDao = taskDao;
  }

  @Override
  @Transactional
  public void addTask(TaskDto taskDto) {
    Task task = ConvertUtils.getTaskFromDto(taskDto);
    taskDao.save(task);
  }

  @Override
  @Transactional
  public TaskDto getTask(Long id) {
    Task foundTask = Optional.ofNullable(taskDao.get(id))
        .orElseThrow(NoSuchElementException::new);

    return ConvertUtils.getDtoFromTask(foundTask);
  }

  @Override
  @Transactional
  public Map<String, List<TaskDto>> getTasks(DistributionDto distributionDto) {
    LocalDate dateFrom = distributionDto.getDateFrom();
    LocalDate dateTo = distributionDto.getDateTo();
    if (dateTo != null && dateFrom != null && dateTo.isBefore(dateFrom)) {
      throw new IllegalArgumentException("dateTo should be after dateFrom");
    }
    List<Task> allTasks = taskDao.getAll(distributionDto.getIsActive());
    if (dateFrom == null) {
      dateFrom = allTasks.stream()
          .map(task -> task.getTaskTime().toLocalDate())
          .min(Comparator.naturalOrder()).orElseThrow(NoSuchElementException::new);
    }
    if (dateTo == null) {
      dateTo = LocalDate.now().plusDays(numberOfDaysAhead);
    }
    Map<String, List<TaskDto>> result = new LinkedHashMap<>();
    for (LocalDate date = dateFrom;
         date.isBefore(dateTo.plusDays(1));
         date = date.plusDays(1)) {
      LocalDate finalDate = date;
      List<TaskDto> tasks = allTasks.stream()
          .filter(task -> task.checkAvailabilityOnThatDate(finalDate))
          .sorted(Comparator.comparing(Task::getTaskTime))
          .map(ConvertUtils::getDtoFromTask)
          .toList();
      if (tasks.size() > 0) {
        result.put(date.format(DistributionDto.DATE_FORMATTER), tasks);
      }
    }
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }

    return result;
  }

  @Override
  @Transactional
  public void updateTask(TaskDto taskDto, Long id) {
    Task task = ConvertUtils.getTaskFromDto(taskDto);
    Task foundTask = taskDao.get(id);
    if (foundTask == null) {
      throw new NoSuchElementException();
    }
    Optional.ofNullable(task.getSummary()).ifPresent(foundTask::setSummary);
    Optional.ofNullable(task.getDescription()).ifPresent(foundTask::setDescription);
    Optional.ofNullable(task.getRepeatability()).ifPresent(foundTask::setRepeatability);
    Optional.ofNullable(task.getTaskTime()).ifPresent(foundTask::setTaskTime);
    Optional.ofNullable(task.getIsActive()).ifPresent(foundTask::setIsActive);
    taskDao.save(foundTask);
  }

  @Override
  @Transactional
  public void deleteTask(Long id) {
    Task foundTask = taskDao.get(id);
    if (foundTask == null) {
      throw new NoSuchElementException();
    }
    taskDao.remove(foundTask);
  }
}
