package com.sultansoy.app.accounts;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    String uuid;
    long balance;
}
