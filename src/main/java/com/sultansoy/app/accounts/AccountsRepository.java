package com.sultansoy.app.accounts;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountsRepository {

    Map<String, Account> accounts;

    public Account create(Account account) {
        if (nonNull(account.getUuid())) {
            throw new AccountException("New account uuid must be null! Uuid will be generated automatically");
        }

        if (account.getBalance() < 0) {
            throw new AccountException("Invalid balance");
        }

        account.setUuid(UUID.randomUUID().toString());
        accounts.put(account.getUuid(), account);
        return copy(account);
    }

    public Account update(Account account) {
        Account stored = accounts.get(account.getUuid());
        if (isNull(stored)) {
            throw new AccountException("Can't find account");
        }
        if (account.getBalance() < 0) {
            throw new AccountException("Invalid balance");
        }
        stored.setBalance(account.getBalance());
        return copy(stored);
    }

    public List<Account> getAll() {
        return accounts.values()
                .stream()
                .map(this::copy)
                .collect(Collectors.toList());
    }

    public Account get(String uuid) {
        Account account = accounts.get(uuid);
        if (isNull(account)) {
            return null;
        }
        return copy(account);
    }

    public void delete(String uuid) {
        accounts.remove(uuid);
    }

    private Account copy(Account account) {
        Account copy = new Account();
        copy.setUuid(account.getUuid());
        copy.setBalance(account.getBalance());
        return copy;
    }
}
