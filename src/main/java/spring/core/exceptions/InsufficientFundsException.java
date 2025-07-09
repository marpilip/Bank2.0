package spring.core.exceptions;

public class InsufficientFundsException extends BankException {
    public InsufficientFundsException(Long accountId, Double available) {
        super("Недостаточно средств на счете ID: " + accountId + ". Доступно: " + available);
    }
}
