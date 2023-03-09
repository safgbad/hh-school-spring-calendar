package me.safgbad.calendar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class DistributionDto {

  private static final String DATE_FORMATTER_PATTERN = "dd.MM.yyyy";

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMATTER_PATTERN);

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMATTER_PATTERN)
  private LocalDate dateFrom;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMATTER_PATTERN)
  private LocalDate dateTo;

  private Boolean isActive;
}
