package com.project.scrapers;

import com.project.Debug;
import com.project.datastore.Book;

import java.util.*;

public class ScraperManager implements Debug {
    private final List<Scraper> scrapers;
    private final List<Book> books;

    public ScraperManager(List<Book> books) {
        scrapers = new ArrayList<>();
        this.books = books;
    }

    public void addScraper(Scraper scraper) {
        if (scrapers.stream().anyMatch(s -> Objects.equals(s.PROVIDER_ID, scraper.PROVIDER_ID)))
            return;

        scraper.setTasks(books);
        this.scrapers.add(scraper);
    }

    public void start() throws InterruptedException {
        info("Starting scraper");
        while (!Thread.interrupted()) {
            for (var scraper : scrapers) {
                // if it is still scraping skip over the item.
                if (scraper.getLastScrapeTime() == -1) {
                    continue;
                }

                // if it has been scraped in the last 50000 seconds then skip the scraper
                if (System.currentTimeMillis() - scraper.getLastScrapeTime() < 50000) {
                    continue;
                }

                // if the number of cores on the computer has been exhausted skip the scraper
                var processCount = Runtime.getRuntime().availableProcessors();
                var activeThreads = Thread.activeCount();

                if (processCount == 1 && activeThreads > 2) {
                    continue;
                }

                if (processCount > 1 && activeThreads > processCount) {
                    continue;
                }

                new Thread(scraper).start();
            }

            // waits for 10000 seconds before checking the application status.
            debug("Main thread is going to sleep.");
            Thread.sleep(10000);
        }
        info("Stopping Scraper");
    }

}
