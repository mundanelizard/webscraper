package com.samuel.omohan.scrapers;

import com.samuel.omohan.datastore.Book;

import java.util.*;

public abstract class Scraper implements Runnable {
    private final String id = UUID.randomUUID().toString();

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

    // scrapes only when the book hasn't been scraped or is more than 1 day old.
    abstract void scrape(Book book);

    // create run and start - make them mirrors
}
