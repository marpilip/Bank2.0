package spring.core.console_handler.operations;

import org.springframework.stereotype.Component;
import spring.core.Account;
import spring.core.User;
import spring.core.console_handler.BankOperationType;
import spring.core.console_handler.Command;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.List;
import java.util.Scanner;

@Component
public class ShowAllUsersCommand implements Command {
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

    @Override
    public BankOperationType getOperationType() {
        return BankOperationType.SHOW_ALL_USERS;
    }

    @Override
    public String getDescription() {
        return "Показать всех пользователей";
    }
}
