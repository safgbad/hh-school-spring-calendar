package me.safgbad.calendar.services.impl;

import me.safgbad.calendar.dao.TaskDao;
import me.safgbad.calendar.dto.DistributionDto;
import me.safgbad.calendar.dto.TaskDto;
import me.safgbad.calendar.model.Task;
import me.safgbad.calendar.services.CalendarService;
import me.safgbad.calendar.util.ConvertUtils;
import me.safgbad.calendar.util.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class CalendarServiceImpl implements CalendarService {

  private final TaskDao taskDao;
  private final TransactionUtils transactionUtils;

  @Value("${calendar.number.of.days.ahead}")
  private int numberOfDaysAhead;

  @Autowired
  public CalendarServiceImpl(TaskDao taskDao,
                             TransactionUtils transactionUtils) {
    this.taskDao = taskDao;
    this.transactionUtils = transactionUtils;
  }

  @Override
  public void addTask(TaskDto taskDto) {
    Task task = ConvertUtils.getTaskFromDto(taskDto);
    transactionUtils.inTransaction(() -> {
      task.setCreationTime(LocalDateTime.now());
      task.setIsActive(Objects.requireNonNullElse(task.getIsActive(), true));
      taskDao.save(task);
    });
  }

  @Override
  public TaskDto getTask(Long id) {
    return ConvertUtils.getDtoFromTask(
        transactionUtils.inTransaction(() -> taskDao.get(id)));
  }

  @Override
  public Map<String, List<TaskDto>> getTasks(DistributionDto distributionDto) {
    LocalDate dateFrom = ConvertUtils.getDateFromDto(distributionDto.getDateFrom());
    LocalDate dateTo = ConvertUtils.getDateFromDto(distributionDto.getDateTo());
    if (dateTo != null && dateFrom != null && dateTo.isBefore(dateFrom)) {
      throw new IllegalArgumentException();
    }
    List<Task> allTasks = transactionUtils.inTransaction(()
        -> taskDao.getAll(distributionDto.getIsActive()));
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
  public void updateTask(TaskDto taskDto, Long id) {
    Task task = ConvertUtils.getTaskFromDto(taskDto);
    transactionUtils.inTransaction(() -> {
      Task foundTask = taskDao.get(id);
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
      taskDao.save(foundTask);
    });
  }

  @Override
  public void deleteTask(Long id) {
    transactionUtils.inTransaction(() -> {
      Task foundTask = taskDao.get(id);
      if (foundTask == null) {
        throw new NoSuchElementException();
      }
      taskDao.remove(foundTask);
    });
  }
}
