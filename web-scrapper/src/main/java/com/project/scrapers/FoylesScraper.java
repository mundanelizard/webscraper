package com.project.scrapers;

import com.project.datastore.BookListing;
import org.openqa.selenium.By;

public class FoylesScraper extends Scraper {
    public FoylesScraper()  throws InterruptedException {
        super("FOYLES", "https://www.foyles.co.uk/");
    }

    @Override
    public BookListing getBook(String isbn, String title) {
        try {
            scraper.get(PROVIDER_BASE_URL);
            scraper.findElement(By.cssSelector("#ctl00_txtTerm"))
                    .sendKeys(title + "\n");

            var link = scraper.getCurrentUrl();

            synchronized (scraper)
            {
                scraper.wait(1000);
            }

            var price = scraper
                    .findElement(By.cssSelector(".Price1"))
                    .getText();

            var listing = new BookListing();
            listing.setUrl(link);
            listing.setPrice(price);

            return listing;
        } catch (Exception ex) {
            return null;
        }
    }
}
