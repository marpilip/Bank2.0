package spring.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import spring.core.console_handler.Command;
import spring.core.console_handler.OperationsConsoleListener;
import spring.core.services.AccountService;
import spring.core.services.UserService;

import java.util.List;

@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
public class ApplicationConfig {
    @Bean
    public OperationsConsoleListener operationsConsoleListener(List<Command> commands, UserService userService, AccountService accountService) {
        return new OperationsConsoleListener(commands, userService, accountService);
    }
}
