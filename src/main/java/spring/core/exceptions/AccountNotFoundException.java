package spring.core.exceptions;

public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(Long accountId) {
        super("Счет с ID: " + accountId + " не найден");
    }
}
