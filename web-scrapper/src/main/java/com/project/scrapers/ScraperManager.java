package com.project.scrapers;

import com.project.Debug;
import com.project.datastore.Book;

import java.util.*;

public class ScraperManager implements Debug {
    private final List<Scraper> scrapers;
    // 12 Hours wait time
    private final long WAIT_TIME = 12 * 60 * 60 * 1000;
    protected final List<Book> books;

    public ScraperManager(List<Book> books, List<Scraper> scrapers) {
        this.books = books;
        this.scrapers = scrapers;
    }


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
