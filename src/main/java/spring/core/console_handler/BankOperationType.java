package spring.core.console_handler;

import spring.core.Account;
import spring.core.User;
import spring.core.exceptions.AccountNotFoundException;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.List;
import java.util.Scanner;

public enum BankOperationType {
    USER_CREATE,
    SHOW_ALL_USERS,
    ACCOUNT_CREATE,
    ACCOUNT_CLOSE,
    ACCOUNT_DEPOSIT,
    ACCOUNT_TRANSFER,
    ACCOUNT_WITHDRAW
}
