package com.sultansoy.app.transactions;

import com.sultansoy.app.accounts.AccountsRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Clock;
import java.util.List;

import static com.sultansoy.app.utils.BooleanUtils.not;
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
        if (not(validate(transaction))) {
            throw new TransactionException("Wrong transaction!");
        }

        var fromUuid = transaction.getFromId();
        var from = accountsRepository.get(fromUuid);
        if (isNull(from)) {
            throw new TransactionException("Can't find \"from\" account!");
        }
        if (from.getBalance() < transaction.getAmount()) {
            throw new TransactionException("No such money");
        }

        var toUuid = transaction.getFromId();
        var to = accountsRepository.get(toUuid);
        if (isNull(to)) {
            throw new TransactionException("Can't find \"to\" account!");
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

    private boolean validate(Transaction transaction) {
        return isNull(transaction.getUuid())
                && isNull(transaction.getDate())
                && nonNull(transaction.getFromId())
                && nonNull(transaction.getToId())
                //depends on business rules
                //&& not(Objects.equals(transaction.getFromId(), transaction.getToId()))
                && transaction.getAmount() > 0;
    }

}
