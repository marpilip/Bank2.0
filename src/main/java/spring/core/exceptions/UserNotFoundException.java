package spring.core.exceptions;

public class UserNotFoundException extends BankException {
    public UserNotFoundException(Long userId) {
        super("Пользователь с ID: " + userId + " не найден");
    }
}
