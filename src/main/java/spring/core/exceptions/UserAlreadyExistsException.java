package spring.core.exceptions;

public class UserAlreadyExistsException extends BankException {
    public UserAlreadyExistsException(String login) {
        super("Пользователь с логином '" + login + "' уже существует");
    }
}
