package com.project.scrapers;

import com.project.datastore.BookListing;
import org.openqa.selenium.By;

/**
 * Handles scraping book of blackwells.co.uk
 */
public class BlackwellScraper extends Scraper {

    /**
     * Sets id and baseurl for scraper
     * @throws InterruptedException
     */
    public BlackwellScraper() throws InterruptedException {
        super("BLACKWELLS", "https://blackwells.co.uk/bookshop/home");
    }

    @Override
    public BookListing getBook(String isbn, String title) {
        try {
            scraper.get(PROVIDER_BASE_URL);
            scraper.findElement(By.cssSelector("#keyword"))
                    .sendKeys(isbn + "\n");

            var price = scraper.findElement(
                    By.cssSelector("#main-content > div.product_page > div.container.container--50.u-relative > div:nth-child(2) > div > div.product__price > div > ul > li"))
                    .getText();
            var link = scraper.getCurrentUrl();

            var listing = new BookListing();
            listing.setUrl(link);
            listing.setPrice(price);


            return listing;
        } catch (Exception ex) {
            return null;
        }
    }
}
