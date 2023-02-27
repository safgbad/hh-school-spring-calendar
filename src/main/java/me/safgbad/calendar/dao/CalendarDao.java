package me.safgbad.calendar.dao;

import me.safgbad.calendar.model.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalendarDao {

  private final SessionFactory sessionFactory;

  @Autowired
  public CalendarDao(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public void save(Task task) {
    if (task == null) {
      return;
    }
    getSession().saveOrUpdate(task);
  }

  public Task get(Long id) {
    return getSession().get(Task.class, id);
  }

  public List<Task> getAll(Boolean isActive) {
    if (isActive == null) {
      return getSession().createQuery("select t from Task t", Task.class).getResultList();
    }

    return getSession().createQuery("select t " +
            "from Task t " +
            "where t.isActive = :isActive", Task.class)
        .setParameter("isActive", isActive)
        .getResultList();
  }

  public void remove(Task task) {
    getSession().remove(task);
  }

  private Session getSession() {
    return sessionFactory.getCurrentSession();
  }
}
