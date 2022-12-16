package com.project.scrapers;

import com.project.Debug;
import com.project.datastore.Book;

import java.util.*;

public class ScraperManager implements Debug {
    private final List<Scraper> scrapers;
    // 12 Hours wait time
    private final long WAIT_TIME = 12 * 60 * 60 * 1000;
    protected List<Book> books;

    /**
     * Creates a manager for web scraper.
     * @param scrapers list of scrapers
     */
    public ScraperManager(List<Scraper> scrapers) {
        this.scrapers = scrapers;
    }

    /**
     * Sets the books to scrape
     * @param books list of books to scrape
     */
    public void setBooks(List<Book> books) {
        this.books = books;
    }

    /**
     * Starts all the web scrapers in the application on different threads at once.
     * @throws InterruptedException if the scrape fails to start
     */
    public void start() throws InterruptedException {
        info("Starting web scraper manager");
        while (!Thread.interrupted()) {
            for (var scraper : scrapers) {
                // if it is still scraping skip over the item.
                if (scraper.getIsRunning()) {
                    continue;
                }

                scraper.setTasks(books);
                new Thread(scraper).start();
            }

            // waits for 10000 seconds before checking the application status.
            debug("Main thread is going to sleep.");
            Thread.sleep(WAIT_TIME);
        }
        info("Stopping Scraper");
    }
}
