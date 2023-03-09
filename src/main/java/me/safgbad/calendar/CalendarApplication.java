package me.safgbad.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class CalendarApplication {

  public static void main(String[] args) {
    SpringApplication.run(CalendarApplication.class, args);
  }

}
