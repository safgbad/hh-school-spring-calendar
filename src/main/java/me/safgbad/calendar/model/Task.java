package me.safgbad.calendar.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.safgbad.calendar.model.enums.Repeatability;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String summary;

  private String description;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Repeatability repeatability;

  @Column(name = "creation_time", nullable = false)
  private LocalDateTime creationTime;

  @Column(name = "task_time", nullable = false)
  private LocalDateTime taskTime;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  public boolean checkAvailabilityOnThatDate(LocalDate date) {
    LocalDate taskDate = taskTime.toLocalDate();
    boolean isTaskBeforeDate = taskDate.equals(date) || taskDate.isBefore(date);
    if (!isTaskBeforeDate) {
      return false;
    }
    switch (repeatability) {
      case ONE_TIME -> {
        return taskDate.equals(date);
      }
      case DAILY -> {
        return true;
      }
      case WEEKLY -> {
        return taskDate.getDayOfWeek() == date.getDayOfWeek();
      }
      case MONTHLY -> {
        return taskDate.getDayOfMonth() == date.getDayOfMonth();
      }
      case YEARLY -> {
        return taskDate.getDayOfMonth() == date.getDayOfMonth()
            && taskDate.getMonth() == date.getMonth();
      }
    }

    return false;
  }
}
