package com.sultansoy.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sultansoy.app.accounts.Account;
import com.sultansoy.app.accounts.AccountException;
import com.sultansoy.app.configs.Routing;
import com.sultansoy.app.transactions.Transaction;
import io.javalin.Javalin;
import kong.unirest.GenericType;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sultansoy.app.transactions.TransactionException.*;
import static org.junit.Assert.*;

public class FunctionalTests {

    private static final String url = "http://localhost:5005";
    static Javalin app;
    static Map<String, Transaction> transactions = new HashMap<>();
    static Map<String, Account> accounts = new HashMap<>();

    @BeforeClass
    public static void initTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Unirest.config().setObjectMapper(new JacksonObjectMapper(mapper));
        app = Routing.initTestApp(accounts, transactions).start(5005);
    }

    @After
    public void teardown() {
        accounts.clear();
        transactions.clear();
    }


    @Test
    @SneakyThrows
    public void POST_add_account() {
        var account = new Account();
        account.setBalance(1000);
        var res = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        assertEquals(200, res.getStatus());
        assertNotNull(res.getBody());
        assertEquals(account.getBalance(), res.getBody().getBalance());
        assertNotNull(res.getBody().getUuid());
    }

    @Test
    @SneakyThrows
    public void POST_add_account_with_uuid() {
        var account = new Account();
        account.setUuid("abc");
        account.setBalance(1000);
        var res = Unirest.post(url + "/accounts").body(account).asString();
        assertEquals(400, res.getStatus());
        assertEquals(AccountException.NOT_NULL_UUID, res.getBody());
    }

    @Test
    @SneakyThrows
    public void POST_add_account_with_invalid_balance() {
        var account = new Account();
        account.setBalance(-1000);
        var res = Unirest.post(url + "/accounts").body(account).asString();
        assertEquals(400, res.getStatus());
        assertEquals(AccountException.INVALID_BALANCE, res.getBody());
    }

    @Test
    @SneakyThrows
    public void GET_get_account() {
        var account = new Account();
        account.setBalance(2000);
        var created = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        String uuid = created.getBody().getUuid();
        var res = Unirest.get(url + "/accounts/{uuid}").routeParam("uuid", uuid).asObject(Account.class);
        assertEquals(200, res.getStatus());
        assertNotNull(res.getBody());
        assertEquals(uuid, res.getBody().getUuid());
        assertEquals(account.getBalance(), res.getBody().getBalance());
    }

    @Test
    @SneakyThrows
    public void GET_get_accounts() {
        var account = new Account();
        account.setBalance(2000);
        Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        account.setBalance(3000);
        Unirest.post(url + "/accounts").body(account).asObject(Account.class);

        var res = Unirest.get(url + "/accounts").asObject(new GenericType<List<Account>>() {
        });
        assertEquals(2, res.getBody().size());
    }

    @Test
    @SneakyThrows
    public void DELETE_account() {
        var account = new Account();
        account.setBalance(2000);
        var created = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        String uuid = created.getBody().getUuid();
        var resList = Unirest.get(url + "/accounts").asObject(new GenericType<List<Account>>() {
        });
        assertEquals(1, resList.getBody().size());

        var res = Unirest.delete(url + "/accounts/{uuid}").routeParam("uuid", uuid).asEmpty();
        assertEquals(200, res.getStatus());

        resList = Unirest.get(url + "/accounts").asObject(new GenericType<List<Account>>() {
        });
        assertEquals(0, resList.getBody().size());
    }

    @Test
    public void POST_execute_transaction() {
        String uuidTo, uuidFrom;
        var account = new Account();
        account.setBalance(2000);
        var createdAcc = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        uuidTo = createdAcc.getBody().getUuid();

        account.setBalance(3000);
        createdAcc = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        uuidFrom = createdAcc.getBody().getUuid();

        Transaction transaction = new Transaction();
        transaction.setAmount(1000);
        transaction.setToId(uuidTo);
        transaction.setFromId(uuidFrom);

        var res = Unirest.post(url + "/transactions").body(transaction).asObject(Transaction.class);
        assertEquals(200, res.getStatus());
        assertNotNull(res.getBody().getUuid());
        assertNotNull(res.getBody().getDate());

        var toAcc = Unirest.get(url + "/accounts/{uuid}").routeParam("uuid", uuidTo).asObject(Account.class);
        var fromAcc = Unirest.get(url + "/accounts/{uuid}").routeParam("uuid", uuidFrom).asObject(Account.class);
        assertEquals(3000, toAcc.getBody().getBalance());
        assertEquals(2000, fromAcc.getBody().getBalance());
    }

    @Test
    public void POST_execute_transaction_with_uuid() {

        Transaction transaction = new Transaction();
        transaction.setAmount(1000);
        transaction.setUuid("abc");
        transaction.setToId(UUID.randomUUID().toString());
        transaction.setFromId(UUID.randomUUID().toString());

        var res = Unirest.post(url + "/transactions").body(transaction).asString();
        assertEquals(400, res.getStatus());
        assertEquals(WRONG_TRANSACTION, res.getBody());
    }

    @Test
    public void POST_execute_transaction_with_negative_amount() {

        Transaction transaction = new Transaction();
        transaction.setAmount(-1000);
        transaction.setToId(UUID.randomUUID().toString());
        transaction.setFromId(UUID.randomUUID().toString());

        var res = Unirest.post(url + "/transactions").body(transaction).asString();
        assertEquals(400, res.getStatus());
        assertEquals(WRONG_TRANSACTION, res.getBody());
    }

    @Test
    public void POST_execute_transaction_with_null_to() {

        Transaction transaction = new Transaction();
        transaction.setAmount(1000);
        transaction.setFromId(UUID.randomUUID().toString());

        var res = Unirest.post(url + "/transactions").body(transaction).asString();
        assertEquals(400, res.getStatus());
        assertEquals(WRONG_TRANSACTION, res.getBody());
    }

    @Test
    public void POST_execute_transaction_with_null_from() {

        Transaction transaction = new Transaction();
        transaction.setAmount(1000);
        transaction.setToId(UUID.randomUUID().toString());

        var res = Unirest.post(url + "/transactions").body(transaction).asString();
        assertEquals(400, res.getStatus());
        assertEquals(WRONG_TRANSACTION, res.getBody());
    }

    @Test
    public void POST_execute_transaction_with_not_found_from() {

        Transaction transaction = new Transaction();
        transaction.setAmount(1000);
        transaction.setFromId(UUID.randomUUID().toString());
        transaction.setToId(UUID.randomUUID().toString());

        var res = Unirest.post(url + "/transactions").body(transaction).asString();
        assertEquals(400, res.getStatus());
        assertEquals(NOT_FOUND_FROM, res.getBody());
    }

    @Test
    public void POST_execute_transaction_with_no_such_money() {
        String uuidTo, uuidFrom;
        var account = new Account();
        account.setBalance(2000);
        var createdAcc = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        uuidTo = createdAcc.getBody().getUuid();

        account.setBalance(3000);
        createdAcc = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        uuidFrom = createdAcc.getBody().getUuid();

        Transaction transaction = new Transaction();
        transaction.setAmount(10000);
        transaction.setFromId(uuidFrom);
        transaction.setToId(uuidTo);

        var res = Unirest.post(url + "/transactions").body(transaction).asString();
        assertEquals(400, res.getStatus());
        assertEquals(NO_SUCH_MONEY, res.getBody());
    }

    @Test
    public void POST_execute_transaction_with_not_found_to() {
        String uuidFrom;
        var account = new Account();
        account.setBalance(2000);
        var createdAcc = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        uuidFrom = createdAcc.getBody().getUuid();

        Transaction transaction = new Transaction();
        transaction.setAmount(1000);
        transaction.setFromId(uuidFrom);
        transaction.setToId(UUID.randomUUID().toString());

        var res = Unirest.post(url + "/transactions").body(transaction).asString();
        assertEquals(400, res.getStatus());
        assertEquals(NOT_FOUND_TO, res.getBody());
    }

    @Test
    public void GET_transactions() {
        String uuidFirst, uuidSecond, uuidThird;
        var account = new Account();
        account.setBalance(2000);
        var createdAcc = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        uuidFirst = createdAcc.getBody().getUuid();

        account.setBalance(3000);
        createdAcc = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        uuidSecond = createdAcc.getBody().getUuid();

        account.setBalance(4000);
        createdAcc = Unirest.post(url + "/accounts").body(account).asObject(Account.class);
        uuidThird = createdAcc.getBody().getUuid();

        Transaction transaction = new Transaction();
        transaction.setAmount(1000);
        transaction.setToId(uuidFirst);
        transaction.setFromId(uuidSecond);

        Unirest.post(url + "/transactions").body(transaction).asObject(Transaction.class);

        transaction = new Transaction();
        transaction.setAmount(1000);
        transaction.setToId(uuidFirst);
        transaction.setFromId(uuidThird);

        Unirest.post(url + "/transactions").body(transaction).asObject(Transaction.class);

        transaction = new Transaction();
        transaction.setAmount(1000);
        transaction.setToId(uuidSecond);
        transaction.setFromId(uuidThird);

        Unirest.post(url + "/transactions").body(transaction).asObject(Transaction.class);

        var res = Unirest.get(url + "/transactions").asObject(new GenericType<List<Transaction>>() {
        });
        assertEquals(3, res.getBody().size());

        res = Unirest.get(url + "/transactions").queryString("to", uuidFirst).asObject(new GenericType<List<Transaction>>() {
        });
        assertEquals(2, res.getBody().size());

        res = Unirest.get(url + "/transactions").queryString("from", uuidSecond).asObject(new GenericType<List<Transaction>>() {
        });
        assertEquals(1, res.getBody().size());

        res = Unirest.get(url + "/transactions").queryString("from", uuidThird).asObject(new GenericType<List<Transaction>>() {
        });
        assertEquals(2, res.getBody().size());

        res = Unirest.get(url + "/transactions").queryString("to", uuidSecond).queryString("from", uuidThird).asObject(new GenericType<List<Transaction>>() {
        });
        assertEquals(1, res.getBody().size());
    }
}
