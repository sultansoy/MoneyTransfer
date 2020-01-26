package com.sultansoy.app.accounts;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AccountsRouteConstants {

    public final String ACCOUNTS = "/accounts";
    public final String ACCOUNTS_ID_PARAM = "uuid";
    public final String ACCOUNTS_WITH_UUID = ACCOUNTS + "/:" + ACCOUNTS_ID_PARAM;


}