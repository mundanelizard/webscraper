package com.project.scrapers;

import com.project.datastore.BookListing;
import com.project.datastore.Database;
import com.project.Debug;
import com.project.datastore.Book;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;

public abstract class Scraper implements Runnable, Debug {
    public final String PROVIDER_ID;

    Scraper(String id) {
        PROVIDER_ID = id.toUpperCase();
    }

    private List<Book> tasks;

    private int index;
    private long lastScrapeTime = 0;

    public void setTasks(List<Book> books) {
        this.tasks = books;
    }


    public int getProgress() {
        if (tasks == null) return 0;
        return (index + 1) / tasks.size();
    }
    public long getLastScrapeTime() {
        return lastScrapeTime;
    }

    public void run() {
        debug(PROVIDER_ID + " is scraping in the background");
        lastScrapeTime = -1;
        index = 0;

        if(tasks == null) {
            throw new IllegalStateException(getClass().getName() + " - " + PROVIDER_ID + " is missing a task list");
        }

        for (var book : tasks) {
            if (Thread.interrupted()) break;

            scrape(book);

            if (Thread.interrupted()) break;

            try {
                debug(PROVIDER_ID + " is handing over execution for 2 seconds.");
                wait(2000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                break;
            }

            index += 1;
        }

        lastScrapeTime = System.currentTimeMillis();
        debug(PROVIDER_ID + " is done scraping in the background.");
    }

    void scrape(Book book) {
        debug(this.PROVIDER_ID + " is scraping " + book.getTitle());

        var params = new Database.Parameter[]{
                new Database.Parameter("book_id", book.getId()),
                new Database.Parameter("provider", PROVIDER_ID),
        };

        var items = Database.getItemsWhere(
                BookListing.class,
                "t.book_id = :book_id AND t.provider = :provider",
                params);

        if (items.size() != 0) {
            // scrape only after 1 day
            var seconds = items.get(0).getUpdatedAt().toEpochSecond(LocalTime.now(), ZoneOffset.UTC);
            var current = System.currentTimeMillis() / 1000;
            // TODO: Make a proper calculation.
            if (seconds < current) {
                debug(this.PROVIDER_ID + " has recently scraped" + book.getTitle());
                return;
            }
        }

        var listing = getBook(book.getTitle());

        // book not found.
        if (listing == null) {
            debug(this.PROVIDER_ID + " couldn't find " + book.getTitle());
            return;
        }

        if (items.size() != 0) {
            listing.setId(items.get(0).getId());
        }

        Database.createOrUpdate(listing);
    }

    abstract public BookListing getBook(String title);
}
