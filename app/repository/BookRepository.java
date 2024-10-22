package repository;

import io.ebean.DB;
import io.ebean.Transaction;
import models.Book;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class BookRepository {
    private final DatabaseExecutionContext executionContext;

    @Inject
    public BookRepository(DatabaseExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public CompletionStage<Optional<Long>> update(Long id, Book book) {
        return supplyAsync(() -> {
            Transaction txn = DB.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                book.update();
                txn.commit();
                value = Optional.of(id);
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }
}
