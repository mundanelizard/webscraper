package com.samuel.omohan;


import com.samuel.omohan.datastore.Book;
import com.samuel.omohan.datastore.Database;
import com.samuel.omohan.scrapers.BookScraper;
import com.samuel.omohan.scrapers.ScraperManager;

/**
 * Entry class for web scrapping application.
 *
 */
public class App 
{
    public static void main (String[] args) throws InterruptedException {
        var books = Database.getItems(Book.class);

        if (books.size() < 100) {
            // start scraping to populate the book list.
            new BookScraper().start();
            books = Database.getItems(Book.class);
        }

        ScraperManager sm = new ScraperManager(books);

        sm.start();

        /*
         * TODO:
         * - build a 5 web scrapers to populate listings
         * - build a scraper manager to manage the scrapers
         * - build an updater to update the scraped data if it's less than a day old.
         */
    }
}


/*
// OLD

 // Create a web scraper manages the scrapers
 CameraScraper cs = new CameraScraper(new ArrayList<>());
 List<Camera> cameras = cs.getCameras();

 // instantiate this with spring
 if (cameras.size() <= 10) {
 cs.getAllCamera();
 }

 OldScraper<Listing> scraper = new WilkinsonScraper(cameras);
 scraper.start();
 */