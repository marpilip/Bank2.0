package spring.core.console_handler.operations;

import org.springframework.stereotype.Component;
import spring.core.console_handler.BankOperationType;
import spring.core.console_handler.Command;
import spring.core.exceptions.AccountNotFoundException;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.Scanner;

@Component
public class AccountTransferCommand implements Command {
    @Override
    public void execute(Scanner scanner, UserService userService, AccountService accountService) {
        System.out.println("Введите ID счета отправителя:");
        Long fromAccountId = null;
        Long toAccountId = null;

        try {
            fromAccountId = Long.parseLong(scanner.nextLine().trim());
            accountService.searchAccountById(fromAccountId);
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Введите ID счета получателя:");
        try {
            toAccountId = Long.parseLong(scanner.nextLine().trim());
            accountService.searchAccountById(toAccountId);
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Введите сумму:");
        double amount = scanner.nextDouble();

        accountService.transferMoney(fromAccountId, toAccountId, amount);

        System.out.println("Перевод выполнен с " + fromAccountId + " на " +
                toAccountId + ", сумма перевода: " + amount);
    }

    @Override
    public BankOperationType getOperationType() {
        return BankOperationType.ACCOUNT_TRANSFER;
    }

    @Override
    public String getDescription() {
        return "Перевести деньги";
    }
}
