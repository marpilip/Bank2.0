package spring.core.console_handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.HashMap;

import java.util.Map;
import java.util.Scanner;

@Component
public class OperationsConsoleListener implements Runnable {
    private final UserService userService;
    private final AccountService accountService;
    private final Scanner scanner;
    private final Map<String, Command> commands;

    @Autowired
    public OperationsConsoleListener(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
        this.commands = new HashMap<>();

        for (BankCommand cmd : BankCommand.values()) {
            commands.put(cmd.getName(), cmd);
        }
    }

    @Override
    public void run() {
        printStartMessage();

        while (true) {
            try {
                printInfoMessage();
                String command = scanner.nextLine();

                if ("HELP".equalsIgnoreCase(command)) {
                    printHelp();
                    continue;
                }

                if ("EXIT".equalsIgnoreCase(command)) {
                    break;
                }

                Command cmd = commands.get(command);
                if (cmd == null) {
                    System.out.println("Неизвестная команда. Введите 'HELP' для списка команд.");
                    continue;
                }

                cmd.execute(scanner, userService, accountService);
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void printStartMessage() {
        System.out.println("Банковское приложение запущено. Введите команду:");
    }

    private void printInfoMessage(){
        System.out.println("Введите команду (или 'HELP' для списка команд): \n" +
                "Введите 'EXIT', чтобы завершить работу приложения");
    }

    private void printHelp() {
        commands.values().forEach(cmd ->
                System.out.printf("%s - %s\n", cmd.getName(), cmd.getDescription()));
    }
}
