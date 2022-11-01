package com.samuel.omohan.scrapers;

import com.samuel.omohan.datastore.Book;
import com.samuel.omohan.datastore.BookListing;
import com.samuel.omohan.datastore.Database;

public class AmazonScraper extends Scraper {
    AmazonScraper() {
        super("AMAZON");
    }

    @Override
    public BookListing getBook(String title) {
        return null;
    }
}
