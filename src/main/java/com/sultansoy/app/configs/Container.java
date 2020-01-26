package com.sultansoy.app.configs;

import com.sultansoy.app.accounts.AccountsController;
import com.sultansoy.app.accounts.AccountsRepository;
import com.sultansoy.app.transactions.TransactionsController;
import com.sultansoy.app.transactions.TransactionsRepository;
import com.sultansoy.app.transactions.TransactionsService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Container {

    AccountsRepository accountsRepository;
    AccountsController accountsController;
    TransactionsRepository transactionsRepository;
    TransactionsService transactionsService;
    TransactionsController transactionsController;

    Container() {
        this.accountsRepository = new AccountsRepository();
        this.accountsController = new AccountsController(this.accountsRepository);

        this.transactionsRepository = new TransactionsRepository();
        this.transactionsService = new TransactionsService(this.transactionsRepository, this.accountsRepository);
        this.transactionsController = new TransactionsController(this.transactionsService);
    }

    public TransactionsController transactionsController() {
        return this.transactionsController;
    }

    public AccountsController accountsController() {
        return this.accountsController;
    }

}
