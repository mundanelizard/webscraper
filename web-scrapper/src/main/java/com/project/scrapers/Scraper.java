package com.project.scrapers;

import com.project.datastore.BookListing;
import com.project.datastore.Database;
import com.project.Debug;
import com.project.datastore.Book;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.*;

public abstract class Scraper implements Runnable, Debug {
    public final String PROVIDER_ID;
    public final String PROVIDER_BASE_URL;

    protected WebDriver scraper;

    private final ChromeOptions options;

    private final Thread hook = new Thread(this::close);

    /**
     * Creates a new generic web scraper
     * @param id web scraper identification used for logging and saving in the database
     * @param baseUrl the url where the web scraper will scrape
     * @throws InterruptedException when it fails to create create a webscraper
     */
    Scraper(String id, String baseUrl) throws InterruptedException {
        PROVIDER_ID = id.toUpperCase();
        options = new ChromeOptions();
        options.setHeadless(!SHOW_BROWSER);
        this.PROVIDER_BASE_URL = baseUrl;

        Thread.sleep(3000);
    }

    /**
     * Instantiates the scraper
     */
    private void startScraper() {
        if (scraper != null) {
            return;
        }

        scraper = new ChromeDriver(options);
        scraper.get(PROVIDER_BASE_URL);
    }

    private List<Book> tasks;

    private Boolean isRunning = false;

    /**
     * Is true is thread is active and false when it isn't
     * @return a boolean
     */
    public Boolean getIsRunning() {
        return isRunning;
    }

    /**
     * Set the books for the scraper to scrape.
     * @param books list of books to scrape
     */
    public void setTasks(List<Book> books) {
        this.tasks = books;
    }

    /**
     * Starts the automated scraping process
     */
    synchronized public void run() {
        if (isRunning) return;

        startScraper();

        Runtime.getRuntime().addShutdownHook(hook);

        debug(PROVIDER_ID + " is scraping in the background");
        isRunning = true;

        if(tasks == null) {
            isRunning = false;
            throw new IllegalStateException(getClass().getName() + " - " + PROVIDER_ID + " is missing a task list");
        }

        for (var book : tasks) {
            if (Thread.interrupted())
                break;

            scrape(book);

            if (Thread.interrupted())
                break;

            try {
                debug(PROVIDER_ID + " is handing over execution for 10 seconds.");
                wait(10000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                break;
            }
        }

        isRunning = false;
        Runtime.getRuntime().removeShutdownHook(hook);
        debug(PROVIDER_ID + " is done scraping in the background.");
    }

    /**
     * Handles the scraping and saving of a single scraper.
     * @param book the book to scrape
     */
    private void scrape(Book book) {
        debug(this.PROVIDER_ID + " is scraping " + book.getIsbn());

        var params = new Database.Parameter[]{
                new Database.Parameter("provider", PROVIDER_ID),
                new Database.Parameter("book_id", book.getId()),
        };

        var items = Database.getItemsWhere(
                BookListing.class,
                "(t.provider = :provider) AND (t.bookId = :book_id)",
                params);

        if (items.size() != 0) {
            // performs scrape if last scrape is longer than a day
            var updateTime = items.get(0).getUpdatedAt().getTime();

            var currentTime = System.currentTimeMillis();

            var timeDifference = currentTime - updateTime;

            // TODO: Make a proper calculation.
            long MIN_TIME_DIFF = 24 * 60 * 1000;
            if (timeDifference < MIN_TIME_DIFF) {
                debug(this.PROVIDER_ID + " recently scraped " + book.getIsbn());
                return;
            }
        }

        var listing = getBook(book.getIsbn(), book.getTitle());

        // book not found.
        if (listing == null) {
            debug(this.PROVIDER_ID + " couldn't find " + book.getIsbn());
            return;
        }

        listing.setBookId(book.getId());
        listing.setProvider(PROVIDER_ID);

        if (items.size() == 0) {
            // sets the created at time for a new listing.
            listing.setCreatedAt(new Date());
        } else {
            // set the listing id, so it'll be an update for an old listing.
            listing.setId(items.get(0).getId());
            var createdAt = items.get(0).getCreatedAt();
            listing.setCreatedAt(createdAt != null ? createdAt : new Date());
        }

        listing.setUpdatedAt(new Date());
        Database.createOrUpdate(listing);
        debug(this.PROVIDER_ID + " successfully scraped " + book.getIsbn());
    }

    /**
     * Closes the scraper gracefully.
     */
    public void close() {
        System.out.println("Shutting down " + PROVIDER_ID + " scraper.");
        if (scraper != null)
            scraper.close();
        System.out.println(PROVIDER_ID + " shutdown was successful");
    }

    abstract public BookListing getBook(String isbn, String title);
}
