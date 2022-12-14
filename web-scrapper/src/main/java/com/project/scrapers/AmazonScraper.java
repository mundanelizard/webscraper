package com.project.scrapers;

import com.project.datastore.BookListing;
import org.openqa.selenium.By;


/**
 * Handles Amazon Scraping
 */
public class AmazonScraper extends Scraper {
    /**
     * Creates a new Amazon scraper
     * @throws InterruptedException
     */
    public AmazonScraper() throws InterruptedException {
        super("AMAZON", "https://www.amazon.co.uk/s/ref=sr_adv_b?search-alias=stripbooks&field-isbn=");
    }

    @Override
    public BookListing getBook(String isbn, String title) {
        try {
            scraper.get(PROVIDER_BASE_URL + isbn);

            // TODO: Update the way you get the price
            var element = scraper.findElement(
                    By.cssSelector(String.format("[data-asin=\"%s\"] .a-price .a-offscreen", isbn)));
            var price = element.getText();

            element = scraper.findElement(By.cssSelector(String.format("[data-asin=\"%s\"] h2 a", isbn)));
            var link = element.getAttribute("href");

            var listing = new BookListing();
            listing.setUrl(link);
            listing.setPrice(price);

            return listing;
        } catch(Exception ex) {
            return null;
        }
    }
}
