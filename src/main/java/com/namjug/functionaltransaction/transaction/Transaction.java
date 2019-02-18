package com.namjug.functionaltransaction.transaction;

import lombok.SneakyThrows;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.TransactionManager;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Transaction<T> implements TransactionPublisher {
    private final Function<Void, T> action;

    private Transaction(Function<Void, T> action) {
        this.action = action;
    }

    public static <T> Transaction<T> create(Callable<T> callable) {
        return new Transaction<>(aVoid -> silentCallable(callable));
    }

    public static Transaction<Void> create(Runnable runnable) {
        return new Transaction<>(aVoid -> {
            runnable.run();
            return null;
        });
    }

    public <U> Transaction<U> map(Function<? super T, ? extends U> mapper) {
        return new Transaction<>(action.andThen(mapper));
    }

    public <U> Transaction<U> flatMap(Function<? super T, ? extends Transaction<U>> mapper) {
        return new Transaction<>(aVoid -> {
            Transaction<U> apply = action.andThen(mapper).apply(null);
            return apply.get();
        });
    }

    private T get() {
        return action.apply(null);
    }

    @Override
    public T execute(PlatformTransactionManager platformTransactionManager, Propagation propagation) {
        TransactionTemplate txTemplate = new TransactionTemplate(platformTransactionManager);

        txTemplate.setPropagationBehavior(propagation.value());
        return txTemplate.execute(status -> action.apply(null));
    }

    @SneakyThrows
    private static <T> T silentCallable(Callable<T> callable) {
        return callable.call();
    }
}
