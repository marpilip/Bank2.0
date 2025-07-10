package spring.core.console_handler.operations;

import org.springframework.stereotype.Component;
import spring.core.User;
import spring.core.console_handler.BankOperationType;
import spring.core.console_handler.Command;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.Scanner;

@Component
public class CreateUserCommand implements Command {
    @Override
    public void execute(Scanner scanner, UserService userService, AccountService accountService){
        System.out.println("Введите логин пользователя: ");
        String login = scanner.nextLine().trim();
        User user = userService.createUser(login);

        System.out.println("Пользователь создан: ID: " + user.getId() + ", login: " + user.getLogin());
    }

    @Override
    public BankOperationType getOperationType() {
        return BankOperationType.USER_CREATE;
    }

    @Override
    public String getDescription() {
        return "Создать нового пользователя";
    }
}
