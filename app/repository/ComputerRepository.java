package repository;

import io.ebean.DB;
import io.ebean.Transaction;
import models.Book;


import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class ComputerRepository {

    private final DatabaseExecutionContext executionContext;

    @Inject
    public ComputerRepository(DatabaseExecutionContext executionContext) {
        this.executionContext = executionContext;
    }




    public CompletionStage<Optional<Long>> update(Long id, String coverImageUrl, String isbn) {
        return supplyAsync(() -> {
            Transaction txn = DB.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Book book1 = DB.find(Book.class).setId(id).findOne();
                book1.update(coverImageUrl, isbn);
                txn.commit();
                value = Optional.of(id);
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }


}