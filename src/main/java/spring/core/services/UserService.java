package spring.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import spring.core.User;
import spring.core.exceptions.BankException;
import spring.core.exceptions.UserAlreadyExistsException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final AccountService accountService;

    @Autowired
    public UserService(@Lazy AccountService accountService) {
        this.accountService = accountService;
    }

    public User createUser(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Строка пуста. Введите логин пользователя.");
        }

        if (users.values().stream().anyMatch(u -> u.getLogin().equals(login))) {
            throw new UserAlreadyExistsException(login);
        }

        Long userId = idGenerator.getAndIncrement();
        User user = new User(userId, login);
        users.put(userId, user);

        accountService.createAccount(userId);

        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(Long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(()-> new BankException("Пользователь с таким логином не найден"));
    }
}
