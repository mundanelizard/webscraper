package com.project;


import com.project.datastore.Database;
import com.project.scrapers.*;
import com.project.datastore.Book;

import java.sql.Array;
import java.util.ArrayList;

/**
 * Entry class for web scrapping application.
 *
 */
public class App 
{
    public static void main (String[] args) throws InterruptedException {
        var scrapers = new ArrayList<Scraper>();
//        scrapers.add(new AmazonScraper());
        scrapers.add(new WaterstonesScraper());

        var books = Database.getItems(Book.class);

        if (books.size() < 100) {
            // start scraping to populate the book list.
            new BookScraper().start();
            books = Database.getItems(Book.class);
        }

        new ScraperManager(books, scrapers)
                .start();
        /*
         * TODO:
         * - build 4 more web scrapers to populate listings
         * - build an updater to update the scraped data if it's less than a day old.
         */
    }
}
