package me.safgbad.calendar.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskDto {
  private String summary;
  private String description;
  private String repeatability;
  private String taskTime;
  private Boolean isActive;
}
