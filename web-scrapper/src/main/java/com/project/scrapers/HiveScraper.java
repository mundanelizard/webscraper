package com.project.scrapers;

import com.project.datastore.BookListing;
import org.openqa.selenium.By;

/**
 * Scrapes hive for book comparison
 */
public class HiveScraper extends Scraper {

    /**
     * Sets up id and baseUrl for HIVe
     * @throws InterruptedException when there's an error setting up scraper
     */
    public HiveScraper() throws InterruptedException {
        super("HIVE", "https://www.hive.co.uk/");
    }


    @Override
    public BookListing getBook(String isbn, String title) {
        try {
            scraper.get(PROVIDER_BASE_URL);
            scraper.findElement(By.cssSelector("#keyword"))
                    .sendKeys(isbn + "\n");

            var link = scraper.getCurrentUrl();
            var price = scraper.findElement(
                    By.cssSelector("#body > div > div.productInfo > div > div.priceAreaWrap > div > div.price > p.sitePrice"))
                    .getText();

            var listing = new BookListing();
            listing.setUrl(link);
            listing.setPrice(price);

            return listing;
        } catch(Exception ex) {
            return null;
        }
    }
}
