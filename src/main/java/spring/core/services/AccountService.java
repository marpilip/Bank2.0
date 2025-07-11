package spring.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.core.Account;
import spring.core.User;
import spring.core.exceptions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AccountService {
    private final Map<Long, Account> accounts = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final UserService userService;

    @Value("${account.default-amount}")
    private Double defaultAmount;

    @Value("${account.transfer-commission}")
    private Double transferCommission;

    @Autowired
    public AccountService(UserService userService) {
        this.userService = userService;
    }

    public Account createAccount(Long userId) {
        User user = userService.getUserById(userId);

        Account account = new Account(
                generateAccountId(),
                userId,
                defaultAmount
        );

        accounts.put(account.getId(), account);
        user.addAccount(account);

        return account;
    }

    private Long generateAccountId() {
        return idGenerator.getAndIncrement();
    }

    public void deposit(Long accountId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        Account account = accounts.get(accountId);
        account.setMoneyAmount(account.getMoneyAmount() + amount);
    }

    public void withdraw(Long accountId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        Account account = accounts.get(accountId);

        if (account.getMoneyAmount() < amount) {
            throw new InsufficientFundsException(accountId, amount);
        }

        account.setMoneyAmount(account.getMoneyAmount() - amount);
    }

    public void transferMoney(Long fromAccountId, Long toAccountId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        Account fromAccount = accounts.get(fromAccountId);
        Account toAccount = accounts.get(toAccountId);

        boolean isExternalTransfer = !fromAccount.getUserId().equals(toAccount.getUserId());
        Double commission = isExternalTransfer ?
                amount * transferCommission / 100 : 0;

        double totalAmount = amount + commission;

        if (totalAmount > fromAccount.getMoneyAmount()) {
            throw new InsufficientFundsException(fromAccountId, fromAccount.getMoneyAmount());
        }

        fromAccount.setMoneyAmount(fromAccount.getMoneyAmount() - totalAmount);
        toAccount.setMoneyAmount(toAccount.getMoneyAmount() + totalAmount);
    }

    public void closeAccount(Long accountId) {
        Account account = Optional.ofNullable(accounts.get(accountId))
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        User user = userService.getUserById(account.getUserId());

        if (user.getAccountList().size() <= 1) {
            throw new LastAccountException(account.getUserId());
        }

        Account targetAccount = user.getAccountList().stream()
                .filter(a -> !a.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        synchronized (this) {
            try {
                transferMoney(accountId, targetAccount.getId(), account.getMoneyAmount());

                accounts.remove(accountId);
                user.getAccountList().remove(account);

            } catch (Exception e) {
                throw new BankException("Ошибка при закрытии счета");
            }
        }
    }

    public void searchAccountById(Long accountId) {
        Optional.ofNullable(accounts.get(accountId))
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
