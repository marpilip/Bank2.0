package spring.core.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.core.Account;
import spring.core.User;
import spring.core.exceptions.*;

@Service
public class AccountService {
    private final SessionFactory sessionFactory;

    @Value("${account.default-amount}")
    private Double defaultAmount;

    @Value("${account.transfer-commission}")
    private Double transferCommission;

    @Autowired
    public AccountService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Account createAccount(Long userId) {
        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            User user = session.find(User.class, userId);
            Account account = new Account(defaultAmount);
            user.addAccount(account);
            session.persist(account);
            transaction.commit();

            return account;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException(e);
        }
    }

    public void deposit(Long accountId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Account account = session.find(Account.class, accountId);

            if (account == null) {
                throw new AccountNotFoundException(accountId);
            }

            account.setMoneyAmount(account.getMoneyAmount() + amount);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Ошибка при пополнении счета", e);
        }

    }

    public void withdraw(Long accountId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            Account account = session.find(Account.class, accountId);
            if (account.getMoneyAmount() < amount) {
                throw new InsufficientFundsException(accountId, amount);
            }

            account.setMoneyAmount(account.getMoneyAmount() - amount);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Ошибка при снятии со счета", e);
        }
    }

    public void transferMoney(Long fromAccountId, Long toAccountId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }

        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            Account toAccount = session.find(Account.class, toAccountId);
            Account fromAccount = session.find(Account.class, fromAccountId);

            boolean isExternalTransfer = !fromAccount.getUser().getId().equals(toAccount.getUser().getId());
            Double commission = isExternalTransfer ?
                    amount * transferCommission / 100 : 0;

            double totalAmount = amount + commission;

            if (totalAmount > fromAccount.getMoneyAmount()) {
                throw new InsufficientFundsException(fromAccountId, fromAccount.getMoneyAmount());
            }

            fromAccount.setMoneyAmount(fromAccount.getMoneyAmount() - totalAmount);
            toAccount.setMoneyAmount(toAccount.getMoneyAmount() + totalAmount);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Ошибка при переводе со счета на счет", e);
        }

    }

    public void closeAccount(Long accountId) {
        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

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

            transaction.commit();
            session.remove(accountId);
            user.getAccountList().remove(account);
        } catch (Exception e) {
            throw new BankException("Ошибка при закрытии счета");
        }
    }

    public void searchAccountById(Long accountId) {
        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            Account account = session.find(Account.class, accountId);

            if (account == null) {
                throw new AccountNotFoundException(accountId);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка при поиске счета", e);
        }
    }
}