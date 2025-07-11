package spring.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.core.console_handler.OperationsConsoleListener;

public class BankApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ApplicationConfig.class);

        OperationsConsoleListener listener = context.getBean(OperationsConsoleListener.class);
        new Thread(listener).start();
    }

}
