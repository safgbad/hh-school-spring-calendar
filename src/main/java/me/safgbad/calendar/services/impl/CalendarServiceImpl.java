package me.safgbad.calendar.services.impl;

import me.safgbad.calendar.dao.CalendarDao;
import me.safgbad.calendar.dto.TaskDto;
import me.safgbad.calendar.model.Task;
import me.safgbad.calendar.services.CalendarService;
import me.safgbad.calendar.util.ConvertUtils;
import me.safgbad.calendar.util.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class CalendarServiceImpl implements CalendarService {

  private final CalendarDao calendarDao;
  private final TransactionUtils transactionUtils;

  @Autowired
  public CalendarServiceImpl(CalendarDao calendarDao,
                             TransactionUtils transactionUtils) {
    this.calendarDao = calendarDao;
    this.transactionUtils = transactionUtils;
  }

  @Override
  public void addTask(TaskDto taskDto) {
    Task task = ConvertUtils.getTaskFromDto(taskDto);
    transactionUtils.inTransaction(() -> {
      task.setCreationTime(LocalDateTime.now());
      task.setIsActive(Objects.requireNonNullElse(task.getIsActive(), true));
      calendarDao.save(task);
    });
  }

  @Override
  public Task getTask(Long id) {
    return transactionUtils.inTransaction(() -> calendarDao.get(id));
  }

  @Override
  public void updateTask(TaskDto taskDto, Long id) {
    Task task = ConvertUtils.getTaskFromDto(taskDto);
    transactionUtils.inTransaction(() -> {
      Task foundTask = calendarDao.get(id);
      if (foundTask == null) {
        throw new NoSuchElementException();
      }
      foundTask.setSummary(Objects.requireNonNullElse(
          task.getSummary(),
          foundTask.getSummary()));
      foundTask.setDescription(Objects.requireNonNullElse(
          task.getDescription(),
          foundTask.getDescription()));
      foundTask.setRepeatability(Objects.requireNonNullElse(
          task.getRepeatability(),
          foundTask.getRepeatability()));
      foundTask.setTaskTime(Objects.requireNonNullElse(
          task.getTaskTime(),
          foundTask.getTaskTime()));
      foundTask.setIsActive(Objects.requireNonNullElse(
          task.getIsActive(),
          foundTask.getIsActive()));
      calendarDao.save(foundTask);
    });
  }

  @Override
  public void deleteTask(Long id) {
    transactionUtils.inTransaction(() -> {
      Task foundTask = calendarDao.get(id);
      if (foundTask == null) {
        throw new NoSuchElementException();
      }
      calendarDao.remove(foundTask);
    });
  }
}
