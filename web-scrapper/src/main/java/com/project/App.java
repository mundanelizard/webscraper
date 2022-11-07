package com.project;


import com.project.datastore.Database;
import com.project.scrapers.AmazonScraper;
import com.project.scrapers.BookScraper;
import com.project.scrapers.ScraperManager;
import com.project.datastore.Book;

/**
 * Entry class for web scrapping application.
 *
 */
public class App 
{
    public static void main (String[] args) throws InterruptedException {
        var books = Database.getItems(Book.class);

        if (books.size() < 1000) {
            // start scraping to populate the book list.
            new BookScraper().start();
            books = Database.getItems(Book.class);
        }

        ScraperManager sm = new ScraperManager(books);
        sm.addScraper(new AmazonScraper());

        sm.start();

        /*
         * TODO:
         * - Send an email to Carl copying David stating my details doesn't show up.
         * - Send David a copy of my project proposal to mark and review.
         *
         * - Remove the micro management for the scraper.
         * - Separate the book edition from the book price comparison.
         */

        /*
         * TODO:
         * - build a 4 more web scrapers to populate listings
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