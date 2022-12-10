package com.project.scrapers;

import com.project.datastore.BookListing;
import org.openqa.selenium.By;


public class AmazonScraper extends Scraper {
    public AmazonScraper() throws InterruptedException {
        super("AMAZON","https://www.amazon.co.uk/s/ref=sr_adv_b?search-alias=stripbooks&field-isbn=");
    }

    @Override
    public BookListing getBook(String isbn) {
        try {
            scraper.get(PROVIDER_BASE_URL + isbn);
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
            System.out.println(ex);
            return null;
        }
    }
}
