package controllers;

import io.ebean.DB;
import io.ebean.Transaction;
import models.Book;
import org.apache.pekko.japi.Pair;
import play.mvc.Controller;
import play.mvc.Result;
import utils.GoogleBookClient;

import java.util.List;

public class SecretAdmin extends Controller {

    public Result test() throws Exception {
        //Pair pair = GoogleBookClient.getCoverImageUrlAndIsbn("The Little Book That Beats the Market");
        //return ok(pair.first() + "        " + pair.second());
        return null;
    }

    public Result populateBooks() {
        try {
            Transaction txn = DB.beginTransaction();
            List<Book> books = Book.find.all();
            int count = 0;
            for (Book book : books) {
                //Pair<String, String> pair = GoogleBookClient.getCoverImageUrlAndIsbn(book.title);
                /*if(pair != null) {
                    book.coverImageUrl = pair.first();
                    book.isbn = pair.second();
                    book.update();
                    txn.commit();
                    txn = DB.beginTransaction();
                    System.out.println(count++);
                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ok(e.getMessage());
        }

        return ok("Done");
    }

    /* The wrapper code to refer when coding custom jobs
    public static Result xyzJob() {
        Transaction txn = Ebean.beginTransaction();
        try {
            // You code here..

            txn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return ok(e.getMessage());
        }

        return ok("Done");
    } */
}
