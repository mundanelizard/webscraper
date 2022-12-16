package com.project;

import com.project.datastore.Database;
import com.project.scrapers.*;
import com.project.datastore.Book;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Entry class for web scrapping application.
 */
public class App {

    public static void main (String[] args) throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        var books = Database.getItems(Book.class);

        if (books.size() < 100) {
            // start scraping to populate the book list.
            new BookScraper().start();
            books = Database.getItems(Book.class);
        }

        var scraperManager = (ScraperManager) context.getBean("scraperManager");
        scraperManager.setBooks(books);
        scraperManager.start();
    }
}
