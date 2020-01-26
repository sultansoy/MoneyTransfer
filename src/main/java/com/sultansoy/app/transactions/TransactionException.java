package com.sultansoy.app.transactions;

public class TransactionException extends RuntimeException {

    public static final String WRONG_TRANSACTION = "Wrong transaction!";
    public static final String NOT_FOUND_TO = "Can't find \"to\" account!";
    public static final String NOT_FOUND_FROM = "Can't find \"from\" account!";
    public static final String NO_SUCH_MONEY = "No such money";
    public static final String INVALID_AMOUNT = "Invalid amount";
    public static final String NOT_NULL_UUID = "New transaction uuid must be null! Uuid will be generated automatically";


    public TransactionException(String message) {
        super(message);
    }
}
