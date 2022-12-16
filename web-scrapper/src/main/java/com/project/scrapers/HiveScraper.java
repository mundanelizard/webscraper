package com.project.scrapers;

import com.project.datastore.BookListing;
import org.openqa.selenium.By;

public class HiveScraper extends Scraper {

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
