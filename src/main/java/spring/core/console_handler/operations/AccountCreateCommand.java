package spring.core.console_handler.operations;

import org.springframework.stereotype.Component;
import spring.core.Account;
import spring.core.console_handler.BankOperationType;
import spring.core.console_handler.Command;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.Scanner;

@Component
public class AccountCreateCommand implements Command {
    @Override
    public void execute(Scanner scanner, UserService userService, AccountService accountService) {
        System.out.println("Введите ID пользователя: ");

        Long userId = Long.parseLong(scanner.nextLine().trim());
        Account account = accountService.createAccount(userId);

        System.out.println("Счет создан: ID=" + account.getId() +
                ", баланс=" + account.getMoneyAmount());
    }

    @Override
    public BankOperationType getOperationType() {
        return BankOperationType.ACCOUNT_CREATE;
    }

    @Override
    public String getDescription() {
        return "Создать счет";
    }
}
