package com.sultansoy.app.configs;

import com.sultansoy.app.accounts.Account;
import com.sultansoy.app.accounts.AccountsController;
import com.sultansoy.app.accounts.AccountsRepository;
import com.sultansoy.app.transactions.Transaction;
import com.sultansoy.app.transactions.TransactionsController;
import com.sultansoy.app.transactions.TransactionsRepository;
import com.sultansoy.app.transactions.TransactionsService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Container {

    AccountsRepository accountsRepository;
    AccountsController accountsController;
    TransactionsRepository transactionsRepository;
    TransactionsService transactionsService;
    TransactionsController transactionsController;

    Container() {
        this.accountsRepository = new AccountsRepository(new HashMap<>());
        this.accountsController = new AccountsController(this.accountsRepository);

        this.transactionsRepository = new TransactionsRepository(new HashMap<>());
        this.transactionsService = new TransactionsService(this.transactionsRepository, this.accountsRepository);
        this.transactionsController = new TransactionsController(this.transactionsService);
    }

    Container(Map<String, Account> accounts, Map<String, Transaction> transactions) {
        this.accountsRepository = new AccountsRepository(accounts);
        this.accountsController = new AccountsController(this.accountsRepository);

        this.transactionsRepository = new TransactionsRepository(transactions);
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
