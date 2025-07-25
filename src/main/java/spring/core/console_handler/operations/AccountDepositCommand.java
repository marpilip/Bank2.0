package spring.core.console_handler.operations;

import org.springframework.stereotype.Component;
import spring.core.console_handler.BankOperationType;
import spring.core.console_handler.Command;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.Scanner;

@Component
public class AccountDepositCommand implements Command {
    @Override
    public void execute(Scanner scanner, UserService userService, AccountService accountService) {
        System.out.println("Введите ID счета: ");
        Long accountId = Long.parseLong(scanner.nextLine().trim());

        System.out.println("Введите сумму: ");
        double amount = scanner.nextDouble();
        accountService.deposit(accountId, amount);

        System.out.println("Счет пополнен: ID: " + accountId + ", сумма = " + amount);
    }

    @Override
    public BankOperationType getOperationType() {
        return BankOperationType.ACCOUNT_DEPOSIT;
    }

    @Override
    public String getDescription() {
        return "Пополнить аккаунт";
    }
}
