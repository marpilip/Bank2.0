package spring.core.console_handler.operations;

import org.springframework.stereotype.Component;
import spring.core.console_handler.BankOperationType;
import spring.core.console_handler.Command;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.Scanner;

@Component
public class AccountCloseCommand implements Command {
    @Override
    public void execute(Scanner scanner, UserService userService, AccountService accountService) {
        System.out.println("Введите ID счета: ");

        Long accountId = Long.parseLong(scanner.nextLine().trim());
        accountService.closeAccount(accountId);

        System.out.println("Счет закрыт: ID: " + accountId);
    }

    @Override
    public BankOperationType getOperationType() {
        return BankOperationType.ACCOUNT_CLOSE;
    }

    @Override
    public String getDescription() {
        return "Закрыть счет";
    }
}
