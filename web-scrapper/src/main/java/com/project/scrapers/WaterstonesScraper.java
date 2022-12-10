package com.project.scrapers;

import com.project.datastore.BookListing;
import org.openqa.selenium.By;

public class WaterstonesScraper extends Scraper{

    public WaterstonesScraper() throws InterruptedException {
        super("WATERSTONES", "https://www.waterstones.com/");
    }

    @Override
    public BookListing getBook(String isbn) {
        try {
            scraper.get(PROVIDER_BASE_URL);
            scraper.findElement(
                    By.cssSelector("#masthead > div.main-nav-holder div.header-search form > div > input"))
                    .sendKeys(isbn + "\n");

            var link = scraper.getCurrentUrl();
            var price = scraper.findElement(
                    By.cssSelector("body > div.main-container > div.main-page.row > div:nth-child(2) > section.book-detail.span12.alpha.omega > div.span7.mobile-span12.alpha.tablet-alpha > div.book-actions > div > div > div > div.price > div > b"))
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
