package spring.core.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TransactionHelper {
    private final SessionFactory sessionFactory;
    private final ThreadLocal<Transaction> currentTransaction = new ThreadLocal<>();

    @Autowired
    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T doInTransaction(Function<Session, T> function) {
        Session session = null;
        Transaction transaction = null;
        boolean isNewTransaction = false;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = currentTransaction.get();

            if (transaction == null) {
                transaction = session.beginTransaction();
                currentTransaction.set(transaction);
                isNewTransaction = true;
            }

            T result = function.apply(session);

            if (isNewTransaction) {
                transaction.commit();
            }

            return result;
        } catch (final Exception e) {
            if (isNewTransaction && transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Не удалось завершить транзакцию", e);
        } finally {
            if (isNewTransaction) {
                currentTransaction.remove();
                session.close();
            }
        }
    }
}
