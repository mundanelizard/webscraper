package com.samuel.omohan.scrapers;

import com.samuel.omohan.datastore.Book;

import java.util.*;

public class ScraperManager {
    private final List<Scraper> scrapers;
    private final List<Book> books;

    public ScraperManager(List<Book> books) {
        scrapers = new ArrayList<>();
        this.books = books;
    }

    public void addScraper(Scraper scraper) {
        if (scrapers.stream().anyMatch(s -> Objects.equals(s.getId(), scraper.getId())))
            return;

        scraper.setTasks(books);
        this.scrapers.add(scraper);
    }

    public void start() throws InterruptedException {
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
                if (Runtime.getRuntime().availableProcessors() - 1 == Thread.activeCount()) {
                    continue;
                }

                new Thread(scraper).start();
            }

            // waits for 10000 seconds before checking the application status.
            Thread.sleep(10000);
        }
    }

}
