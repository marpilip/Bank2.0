package spring.core.console_handler;

import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.Scanner;

public interface Command {
    void execute(Scanner scanner, UserService userService, AccountService accountService);
    String getName();
    String getDescription();
}
