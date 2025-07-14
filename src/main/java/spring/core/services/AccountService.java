package spring.core.services;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.core.Account;
import spring.core.User;
import spring.core.exceptions.*;

@Service
public class AccountService {
    private final TransactionHelper transactionHelper;

    @Value("${account.default-amount}")
    private Double defaultAmount;

    @Value("${account.transfer-commission}")
    private Double transferCommission;

    @Autowired
    public AccountService(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    public Account createAccount(Long userId) {
        return transactionHelper.doInTransaction(session -> {
            User user = session.find(User.class, userId);

            if (user == null) {
                throw new UserNotFoundException(userId);
            }

            Account account = new Account(defaultAmount);
            user.addAccount(account);
            session.persist(account);
            return account;
        });
    }

    public void createAccount(Long userId, Session session) {
        User user = session.find(User.class, userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        Account account = new Account(defaultAmount);
        user.addAccount(account);
        session.persist(account);
    }

    public void deposit(Long accountId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        transactionHelper.doInTransaction(session -> {
            Account account = session.find(Account.class, accountId);
            if (account == null) {
                throw new AccountNotFoundException(accountId);
            }

            account.setMoneyAmount(account.getMoneyAmount() + amount);
            return account;
        });
    }

    public void withdraw(Long accountId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        transactionHelper.doInTransaction(session -> {
            Account account = session.find(Account.class, accountId);
            if (account == null) {
                throw new AccountNotFoundException(accountId);
            }

            account.setMoneyAmount(account.getMoneyAmount() - amount);
            return account;
        });
    }

    public void transferMoney(Long fromAccountId, Long toAccountId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        transactionHelper.doInTransaction(session -> {
            Account fromAccount = session.find(Account.class, fromAccountId);
            Account toAccount = session.find(Account.class, toAccountId);
            if (fromAccount == null) {
                throw new AccountNotFoundException(fromAccountId);
            }

            if (toAccount == null) {
                throw new AccountNotFoundException(toAccountId);
            }

            boolean isExternalTransfer = !fromAccount.getUser().getId().equals(toAccount.getUser().getId());
            Double commission = isExternalTransfer ?
                    amount * transferCommission / 100 : 0;

            double totalAmount = amount + commission;

            if (totalAmount > fromAccount.getMoneyAmount()) {
                throw new InsufficientFundsException(fromAccountId, fromAccount.getMoneyAmount());
            }

            fromAccount.setMoneyAmount(fromAccount.getMoneyAmount() - totalAmount);
            toAccount.setMoneyAmount(toAccount.getMoneyAmount() + totalAmount);

            return toAccount;
        });
    }

    public void closeAccount(Long accountId) {
        transactionHelper.doInTransaction(session -> {
            Account account = session.find(Account.class, accountId);
            if (account == null) {
                throw new AccountNotFoundException(accountId);
            }

            User user = session.find(User.class, account.getUser().getId());

            if (user.getAccountList().size() <= 1) {
                throw new LastAccountException(account.getUser().getId());
            }

            Account targetAccount = user.getAccountList().stream()
                    .filter(a -> !a.getId().equals(accountId))
                    .findFirst()
                    .orElseThrow(() -> new AccountNotFoundException(accountId));


            transferMoney(accountId, targetAccount.getId(), account.getMoneyAmount());

            session.remove(accountId);
            user.getAccountList().remove(account);

            return user;
        });
    }

    public void searchAccountById(Long accountId) {
        transactionHelper.doInTransaction(session -> {
            Account account = session.find(Account.class, accountId);
            if (account == null) {
                throw new AccountNotFoundException(accountId);
            }

            return account;
        });
    }
}