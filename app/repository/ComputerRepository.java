package repository;

import io.ebean.DB;
import io.ebean.Transaction;
import models.Book;

import java.util.Optional;

/**
 * A repository that executes database operations in a different
 * execution context.
 */
public class ComputerRepository {

    public static void update(Long id, String coverImageUrl, String isbn, String description, String previewUrl, String authorDescription) {
        Transaction txn = DB.beginTransaction();
        Optional<Long> value = Optional.empty();
        try {
            Book book1 = DB.find(Book.class).setId(id).findOne();
            //book1.update(coverImageUrl, isbn, description, previewUrl, authorDescription);
            txn.commit();
            value = Optional.of(id);
        } finally {
            txn.end();
        }
    }
}