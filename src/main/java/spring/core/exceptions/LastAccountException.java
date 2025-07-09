package spring.core.exceptions;

public class LastAccountException extends BankException {
    public LastAccountException(Long userId) {
        super("Невозможно закрыть последний счет пользователя ID: " + userId);
    }
}
