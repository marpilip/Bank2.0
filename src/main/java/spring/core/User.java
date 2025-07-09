package spring.core;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final Long id;
    private final String login;
    private final List<Account> accountList;

    public User(Long id, String login) {
        this.id = id;
        this.login = login;
        this.accountList = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accountList.add(account);
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccountList() {
        return accountList;
    }
}
