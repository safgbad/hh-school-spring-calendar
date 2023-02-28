package me.safgbad.calendar.util;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Component
public class TransactionUtils {
  private final SessionFactory sessionFactory;

  @Autowired
  public TransactionUtils(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public <T> T inTransaction(Supplier<T> supplier) {
    Optional<Transaction> transaction = beginTransaction();
    try {
      T result = supplier.get();
      transaction.ifPresent(Transaction::commit);
      return result;
    } catch (RuntimeException e) {
      transaction
          .filter(Transaction::isActive)
          .ifPresent(Transaction::rollback);
      throw e;
    }
  }

  public void inTransaction(Runnable runnable) {
    inTransaction(() -> {
      runnable.run();
      return null;
    });
  }

  private Optional<Transaction> beginTransaction() {
    Transaction transaction = sessionFactory.getCurrentSession().getTransaction();
    if (transaction.isActive()) {
      return Optional.empty();
    }
    transaction.begin();
    return Optional.of(transaction);
  }
}
