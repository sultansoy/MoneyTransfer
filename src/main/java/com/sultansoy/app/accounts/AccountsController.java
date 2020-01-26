package com.sultansoy.app.accounts;

import io.javalin.http.Context;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountsController {

    AccountsRepository accountsRepository;

    public void create(Context context) {
        Account account = context.bodyAsClass(Account.class);
        Account created = accountsRepository.create(account);
        context.json(created);
    }

    public void get(Context context) {
        String uuid = context.pathParam(AccountsRouteConstants.ACCOUNTS_ID_PARAM);
        Account account = accountsRepository.get(uuid);
        context.json(account);
    }

    public void delete(Context context) {
        String uuid = context.pathParam(AccountsRouteConstants.ACCOUNTS_ID_PARAM);
        accountsRepository.delete(uuid);
        context.status(200);
    }

    public void getAll(Context context) {
        context.json(accountsRepository.getAll());
    }

}
