package me.safgbad.calendar.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class DistributionDto {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  private String dateFrom;
  private String dateTo;
  private Boolean isActive;
}
