package spring.core.exceptions;

public class InvalidAmountException extends BankException {
    public InvalidAmountException(Double amount) {
        super("Некорректная сумма: " + amount + ". Сумма должна быть положительной");
    }
}
