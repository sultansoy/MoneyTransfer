package com.sultansoy.app.configs;

import com.sultansoy.app.accounts.AccountException;
import com.sultansoy.app.accounts.AccountsController;
import com.sultansoy.app.accounts.AccountsRouteConstants;
import com.sultansoy.app.transactions.TransactionException;
import com.sultansoy.app.transactions.TransactionsController;
import com.sultansoy.app.transactions.TransactionsRouteConstants;
import io.javalin.Javalin;


public class Routing {

    private static final Container container = new Container();

    public static Javalin initApp() {

        Javalin app = Javalin.create();

        AccountsController accountsController = container.accountsController();
        TransactionsController transactionsController = container.transactionsController();

        app.get(AccountsRouteConstants.ACCOUNTS, accountsController::getAll);
        app.post(AccountsRouteConstants.ACCOUNTS, accountsController::create);
        app.get(AccountsRouteConstants.ACCOUNTS_WITH_UUID, accountsController::get);
        app.delete(AccountsRouteConstants.ACCOUNTS_WITH_UUID, accountsController::delete);

        app.get(TransactionsRouteConstants.TRANSACTIONS, transactionsController::getList);
        app.post(TransactionsRouteConstants.TRANSACTIONS, transactionsController::executeTransaction);
        app.post(TransactionsRouteConstants.TRANSACTIONS_WITH_UUID, transactionsController::get);

        app.exception(AccountException.class, (e, ctx) -> ctx.result(e.getMessage()));
        app.exception(TransactionException.class, (e, ctx) -> ctx.result(e.getMessage()));
        app.exception(Exception.class, (e, ctx) -> ctx.status(500));

        return app;
    }

}
