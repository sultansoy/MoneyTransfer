package com.sultansoy.app.transactions;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    String uuid;
    String fromId;
    String toId;
    long amount;
    Instant date;
}
