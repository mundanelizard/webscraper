package com.project;

import com.project.datastore.Database;
import com.project.scrapers.*;
import com.project.datastore.Book;

import java.util.ArrayList;

/**
 * Entry class for web scrapping application.
 */
public class App {

    public static void main (String[] args) throws InterruptedException {
        /* TODO: Initialize the scrapers with dependency injection */
        var scrapers = new ArrayList<Scraper>();
        scrapers.add(new AmazonScraper());
        scrapers.add(new WaterstonesScraper());
        scrapers.add(new BlackwellScraper());
        scrapers.add(new FoylesScraper());
        scrapers.add(new HiveScraper());

        var books = Database.getItems(Book.class);

        if (books.size() < 100) {
            // start scraping to populate the book list.
            new BookScraper().start();
            books = Database.getItems(Book.class);
        }

        new ScraperManager(books, scrapers)
                .start();
    }
}
