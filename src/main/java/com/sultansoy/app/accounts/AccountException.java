package com.sultansoy.app.accounts;


public class AccountException extends RuntimeException {

    public static final String NOT_NULL_UUID = "New account uuid must be null! Uuid will be generated automatically";
    public static final String INVALID_BALANCE = "Invalid balance";
    public static final String NOT_FOUND = "Can't find account";

    public AccountException(String message) {
        super(message);
    }
}
