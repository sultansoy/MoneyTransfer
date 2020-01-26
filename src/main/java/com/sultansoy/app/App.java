package com.sultansoy.app;

import com.sultansoy.app.accounts.AccountsRepository;
import com.sultansoy.app.transactions.TransactionsRepository;
import com.sultansoy.app.transactions.TransactionsService;
import io.javalin.Javalin;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import static io.javalin.apibuilder.ApiBuilder.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class App {

    static AccountsRepository accountsRepository;
    static TransactionsService transactionsService;
    static TransactionsRepository transactionsRepository;

    public static void main(String[] args) {

        accountsRepository = new AccountsRepository();
        transactionsRepository = new TransactionsRepository();
        transactionsService = new TransactionsService(transactionsRepository, accountsRepository);

        Javalin app = Javalin.create()
                .start(3003);


    }
}
