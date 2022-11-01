package com.samuel.omohan.scrapers;

import com.samuel.omohan.datastore.Book;
import com.samuel.omohan.datastore.BookListing;
import com.samuel.omohan.datastore.Database;

import java.util.*;

public abstract class Scraper implements Runnable {
    private final String id = UUID.randomUUID().toString();
    private final String PROVIDER_ID;

    Scraper(String id) {
        PROVIDER_ID = id;
    }

    private List<Book> tasks;

    private int index;
    private long lastScrapeTime = System.currentTimeMillis();

    public void setTasks(List<Book> books) {
        this.tasks = books;
    }

    public String getId() {
        return id;
    }
    public int getProgress() {
        if (tasks == null) return 0;
        return (index + 1) / tasks.size();
    }
    public long getLastScrapeTime() {
        return lastScrapeTime;
    }

    public void run() {
        lastScrapeTime = -1;
        index = 0;

        if(tasks == null) {
            throw new IllegalStateException(getClass().getName() + " - " + getId() + " is missing a task list");
        }

        for (var book : tasks) {
            if (Thread.interrupted()) break;

            scrape(book);

            if (Thread.interrupted()) break;

            try {
                wait(2000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                break;
            }

            index += 1;
        }

        lastScrapeTime = System.currentTimeMillis();
    }

    void scrape(Book book) {
        var params = new Database.Parameter[]{
                new Database.Parameter("book_id", book.getId()),
                new Database.Parameter("provider", PROVIDER_ID),
        };

        var items = Database.getItemsWhere(
                BookListing.class,
                "t.book_id = %s AND t.provider = %s",
                params);

//        updateAt - createdAt

        // scrapes only when the book hasn't been scraped or is more than 1 day old.
        // perform proper date calculations
        if (items.size() != 0 && items.get(0).getUpdatedAt() - System.currentTimeMillis() < 5000) {
            return;
        }

        var listing = getBook(book.getTitle());

        if (items.size() != 0) {
            listing.setId(items.get(0).getId());
        }

        Database.createOrUpdate(listing);
    }

    abstract public BookListing getBook(String title);

    // create run and start - make them mirrors
}
