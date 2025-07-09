package spring.core.console_handler;

import spring.core.Account;
import spring.core.User;
import spring.core.exceptions.AccountNotFoundException;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.List;
import java.util.Scanner;

public enum BankCommand implements Command {
    USER_CREATE("USER_CREATE", "Создать нового пользователя"){
        @Override
        public void execute(Scanner scanner, UserService userService, AccountService accountService){
            System.out.println("Введите логин пользователя: ");
            String login = scanner.nextLine().trim();
            User user = userService.createUser(login);

            System.out.println("Пользователь создан: ID: " + user.getId() + ", login: " + user.getLogin());
        }
    },

    SHOW_ALL_USERS("SHOW_ALL_USERS", "Показать всех пользователей") {
        @Override
        public void execute(Scanner scanner, UserService userService, AccountService accountService) {
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                System.out.println("Нет пользователей");
                return;
            }

            for (User user : users) {
                System.out.println("Пользователь ID: " + user.getId() + ", login: " + user.getLogin());
                for (Account account : user.getAccountList()) {
                    System.out.println("  Счет ID: " + account.getId() +
                            ", баланс: " + account.getMoneyAmount());
                }
            }
        }
    },

    ACCOUNT_CREATE("ACCOUNT_CREATE", "Создать счет"){
        @Override
        public void execute(Scanner scanner, UserService userService, AccountService accountService) {
            System.out.println("Введите ID пользователя: ");

            Long userId = Long.parseLong(scanner.nextLine().trim());
            Account account = accountService.createAccount(userId);

            System.out.println("Счет создан: ID=" + account.getId() +
                    ", баланс=" + account.getMoneyAmount());
        }
    },

    ACCOUNT_CLOSE("ACCOUNT_CLOSE", "Закрыть счет"){
        @Override
        public void execute(Scanner scanner, UserService userService, AccountService accountService) {
            System.out.println("Введите ID счета: ");

            Long accountId = Long.parseLong(scanner.nextLine().trim());
            accountService.closeAccount(accountId);

            System.out.println("Счет закрыт: ID: " + accountId);
        }
    },

    ACCOUNT_DEPOSIT("ACCOUNT_DEPOSIT", "Пополнить аккаунт"){
        @Override
        public void execute(Scanner scanner, UserService userService, AccountService accountService) {
            System.out.println("Введите ID счета: ");
            Long accountId = Long.parseLong(scanner.nextLine().trim());

            System.out.println("Введите сумму: ");
            double amount = scanner.nextDouble();
            accountService.deposit(accountId, amount);

            System.out.println("Счет пополнен: ID: " + accountId + ", сумма = " + amount);
        }
    },

    ACCOUNT_TRANSFER("ACCOUNT_TRANSFER", "Перевести деньги"){
        @Override
        public void execute(Scanner scanner, UserService userService, AccountService accountService) {
            System.out.println("Введите ID счета отправителя:");
            Long fromAccountId = null;
            Long toAccountId = null;

            try {
                fromAccountId = Long.parseLong(scanner.nextLine().trim());
                accountService.searchAccountById(fromAccountId);
            } catch (AccountNotFoundException e){
                System.out.println(e.getMessage());
            }

            System.out.println("Введите ID счета получателя:");
            try {
                toAccountId = Long.parseLong(scanner.nextLine().trim());
                accountService.searchAccountById(toAccountId);
            } catch (AccountNotFoundException e){
                System.out.println(e.getMessage());
            }

            System.out.println("Введите сумму:");
            double amount = scanner.nextDouble();

            accountService.transferMoney(fromAccountId, toAccountId, amount);

            System.out.println("Перевод выполнен с " + fromAccountId + " на " +
                    toAccountId + ", сумма перевода: " + amount);
        }
    },

    ACCOUNT_WITHDRAW("ACCOUNT_WITHDRAW", "Снять деньги"){
        @Override
        public void execute(Scanner scanner, UserService userService, AccountService accountService) {
            System.out.println("Введите ID счета: ");
            Long accountId = Long.parseLong(scanner.nextLine().trim());

            System.out.println("Введите сумму: ");
            double amount = scanner.nextDouble();

            accountService.withdraw(accountId, amount);
            System.out.println("Средства сняты: ID: " + accountId + ", сумма снятия: " + amount);
        }
    };

    private final String name;
    private final String description;

    BankCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
