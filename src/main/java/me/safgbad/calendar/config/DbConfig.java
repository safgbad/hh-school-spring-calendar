package me.safgbad.calendar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class DbConfig {

  private final Environment environment;

  @Autowired
  public DbConfig(Environment environment) {
    this.environment = environment;
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setPackagesToScan("me.safgbad.calendar.model");
    sessionFactory.setHibernateProperties(hibernateProperties());
    return sessionFactory;
  }

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(
        environment.getRequiredProperty("spring.datasource.driver-class-name"));
    dataSource.setUrl(
        environment.getRequiredProperty("spring.datasource.url"));
    dataSource.setUsername(
        environment.getRequiredProperty("spring.datasource.username"));
    dataSource.setPassword(
        environment.getRequiredProperty("spring.datasource.password"));
    return dataSource;
  }

  private Properties hibernateProperties() {
    Properties properties = new Properties();
    properties.put("hibernate.dialect",
        environment.getRequiredProperty("spring.jpa.properties.hibernate.dialect"));
    properties.put("hibernate.show_sql",
        environment.getRequiredProperty("spring.jpa.show-sql"));
    properties.put("hibernate.format_sql",
        environment.getRequiredProperty("spring.jpa.properties.hibernate.format_sql"));
    properties.put("hibernate.hbm2ddl.auto",
        environment.getRequiredProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
    return properties;
  }

  @Bean
  public HibernateTransactionManager transactionManager() {
    HibernateTransactionManager txManager = new HibernateTransactionManager();
    txManager.setSessionFactory(sessionFactory().getObject());
    return txManager;
  }
}
