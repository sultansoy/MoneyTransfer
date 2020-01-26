package com.sultansoy.app.transactions;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TransactionsRouteConstants {

    public final String TRANSACTIONS = "/accounts";
    public final String TRANSACTIONS_ID_PARAM = "uuid";
    public final String TRANSACTIONS_WITH_UUID = TRANSACTIONS + "/:" + TRANSACTIONS_ID_PARAM;

    public final String TRANSACTION_FROM_QUERY = "from";
    public final String TRANSACTION_TO_QUERY = "to";

    

}