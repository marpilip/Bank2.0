package spring.core.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.springframework.stereotype.Service;
import spring.core.User;
import spring.core.exceptions.UserAlreadyExistsException;

import java.util.*;

@Service
public class UserService {
    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;
    private final AccountService accountService;

    public UserService(SessionFactory sessionFactory, TransactionHelper transactionHelper,
                       AccountService accountService) {
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
        this.accountService = accountService;
    }

    public User createUser(String login) {
        return transactionHelper.doInTransaction(session -> {
            if (login == null || login.trim().isEmpty()) {
                throw new IllegalArgumentException("Строка пуста. Введите логин пользователя.");
            }

            boolean isLoginExists = session.createQuery(
                            "SELECT count(u) > 0 FROM User u WHERE u.login = :login", Boolean.class)
                    .setParameter("login", login)
                    .getSingleResult();

            if (isLoginExists) {
                throw new UserAlreadyExistsException(login);
            }

            User user = new User(login);
            session.persist(user);

            accountService.createAccount(user.getId(), session);

            return user;
        });
    }

    public List<User> getAllUsers() {
        Session session;
        Transaction transaction = null;
        List<User> users;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            users = sessionFactory.getCurrentSession()
                    .createQuery("FROM User u LEFT JOIN FETCH u.accounts", User.class)
                    .getResultList();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Ошибка при получении списка пользователей ", e);
        }

        return users;

    }

    public User getUserById(Long id) {
        Session session = sessionFactory.getCurrentSession();

        try {
            session.beginTransaction();
            User user = session.find(User.class, id);
            session.getTransaction().commit();
            return user;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Произошла проблема при поиске пользователя", e);
        }
    }
}
