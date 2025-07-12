package spring.core.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import org.springframework.stereotype.Service;
import spring.core.Account;
import spring.core.User;
import spring.core.exceptions.UserAlreadyExistsException;

import java.util.*;

@Service
public class UserService {
    private final SessionFactory sessionFactory;

    public UserService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User createUser(String login) {

        Transaction transaction = null;
        User user;
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Строка пуста. Введите логин пользователя.");
        }
        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();

            user = new User(login);
            Query<User> query = session.createQuery("FROM User WHERE login = :login", User.class);
            query.setParameter("login", login);

            if (!query.getResultList().isEmpty()) {
                throw new UserAlreadyExistsException(login);
            }

            session.persist(user);

            Account account = new Account();
            user.addAccount(account);
            session.persist(account);

            transaction.commit();

            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Ошибка при создании пользователя ", e);
        }
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
