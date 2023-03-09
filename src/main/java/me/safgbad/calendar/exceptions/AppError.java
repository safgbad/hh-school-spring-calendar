package me.safgbad.calendar.exceptions;

public class AppError {

  private final int statusCode;
  private final String message;

  public AppError(int statusCode, String message) {
    this.statusCode = statusCode;
    this.message = message;
  }
}
