package com.project.scrapers;

import com.project.datastore.BookListing;

public class AmazonScraper extends Scraper {
    public AmazonScraper() {
        super("AMAZON");
    }

    @Override
    public BookListing getBook(String title) {
        // TODO: search on amazon
        // TODO: select the first book
        // TODO: get book details
        // TODO: return book
        return null;
    }
}
