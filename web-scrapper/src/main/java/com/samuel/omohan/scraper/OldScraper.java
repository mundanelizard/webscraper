package com.samuel.omohan.scraper;

import com.samuel.omohan.Debug;
import com.samuel.omohan.datastore.Camera;
import com.samuel.omohan.datastore.Database;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;

public abstract class OldScraper<T> extends Thread implements Debug {
    private boolean run = true;
    volatile protected static WebDriver scraper;
    protected List<Camera> cameras;

    public OldScraper(List<Camera> cameras) {
        this.cameras = new ArrayList<>(cameras);
    }

    protected Camera getNextCamera() {
        // Basically a queue, I could've used a queue data structure,
        // but this works out of the box because JPA gives you a list.
        Camera camera = cameras.get(0);
        cameras.remove(0);
        return camera;
    }

    static public void closeScraper() {
        scraper.quit();
    }

    static public void initialiseScraper() {
        if (scraper != null) {
            return;
        }

        ChromeOptions options = new ChromeOptions();
        options.setHeadless(!DEBUG);
        scraper = new ChromeDriver(options);

        try {
            sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static {
        initialiseScraper();
    }

    abstract T scrape();

    protected void updateTable(T listing) {
        if (listing == null)
            return;

        if (DEBUG) {
            System.out.println("\tSaving Listing to database");
        }
        Database.createOrUpdate(listing);
    }

    private boolean shouldScrape() {
        return cameras.size() > 0;
    }

    public void run() {
        final int SLEEP_DURATION = 1000 * 60 * 3;

        try {
            while (shouldScrape() && run) {
                T cameraListing = scrape();
                updateTable(cameraListing);
                Thread.sleep(SLEEP_DURATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            run = false;
        }

    }
}
