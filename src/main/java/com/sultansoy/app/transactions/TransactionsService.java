package com.sultansoy.app.transactions;

import com.sultansoy.app.accounts.AccountsRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Clock;
import java.util.List;

import static com.sultansoy.app.transactions.TransactionException.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionsService {

    TransactionsRepository transactionsRepository;

    AccountsRepository accountsRepository;


    public Transaction get(String uuid) {
        return transactionsRepository.get(uuid);
    }

    public List<Transaction> getList(String from, String to) {
        if (isNull(from) && isNull(to)) {
            return transactionsRepository.getAll();
        }
        if (isNull(from)) {
            return transactionsRepository.getTo(to);
        }
        if (isNull(to)) {
            return transactionsRepository.getFrom(from);
        }
        return transactionsRepository.getFromTo(from, to);
    }

    public Transaction executeTransaction(Transaction transaction) {
        if (validate(transaction)) {
            var fromUuid = transaction.getFromId();
            var from = accountsRepository.get(fromUuid);
            if (isNull(from)) {
                throw new TransactionException(NOT_FOUND_FROM);
            }
            if (from.getBalance() < transaction.getAmount()) {
                throw new TransactionException(NO_SUCH_MONEY);
            }

            var toUuid = transaction.getToId();
            var to = accountsRepository.get(toUuid);
            if (isNull(to)) {
                throw new TransactionException(NOT_FOUND_TO);
            }

            var fromBalance = from.getBalance() - transaction.getAmount();
            var toBalance = to.getBalance() + transaction.getAmount();
            from.setBalance(fromBalance);
            to.setBalance(toBalance);
            accountsRepository.update(from);
            accountsRepository.update(to);

            transaction.setDate(Clock.systemDefaultZone().instant());
            return transactionsRepository.create(transaction);
        }
        throw new TransactionException(TransactionException.WRONG_TRANSACTION);
    }

    private boolean validate(Transaction transaction) {
        return isNull(transaction.getUuid())
                && isNull(transaction.getDate())
                && nonNull(transaction.getFromId())
                && nonNull(transaction.getToId())
                //depends on business requirements
                //&& !Objects.equals(transaction.getFromId(), transaction.getToId())
                && transaction.getAmount() > 0;
    }

}
