package me.safgbad.calendar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.safgbad.calendar.model.enums.Repeatability;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

  private Long id;

  private String summary;

  private String description;

  private Repeatability repeatability;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
  private LocalDateTime taskTime;

  private Boolean isActive;
}
