package com.sultansoy.app.transactions;

import io.javalin.http.Context;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static com.sultansoy.app.transactions.TransactionsRouteConstants.*;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionsController {

    TransactionsService transactionsService;


    public void executeTransaction(Context context) {
        Transaction transaction = context.bodyAsClass(Transaction.class);
        Transaction executed = transactionsService.executeTransaction(transaction);
        context.json(executed);
    }

    public void get(Context context) {
        String uuid = context.pathParam(TRANSACTIONS_ID_PARAM);
        Transaction transaction = transactionsService.get(uuid);
        context.json(transaction);
    }

    public void getList(Context context) {
        String from = context.queryParam(TRANSACTION_FROM_QUERY);
        String to = context.queryParam(TRANSACTION_TO_QUERY);

        List<Transaction> transactions = transactionsService.getList(from, to);
        context.json(transactions);
    }


}
