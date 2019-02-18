package com.namjug.functionaltransaction.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;

public interface TransactionPublisher<T> {
    T execute(PlatformTransactionManager platformTransactionManager, Propagation propagation);
}
