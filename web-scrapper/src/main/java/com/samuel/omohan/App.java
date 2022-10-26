package com.samuel.omohan;

import com.samuel.omohan.datastore.Camera;
import com.samuel.omohan.datastore.Listing;
import com.samuel.omohan.scraper.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry class for web scrapping application.
 *
 */
public class App 
{
    public static void main( String[] args ) {
        // Create a web scraper manages the scrapers
        CameraScraper cs = new CameraScraper(new ArrayList<>());
        List<Camera> cameras = cs.getCameras();

        // instantiate this with spring
        if (cameras.size() <= 10) {
            cs.getAllCamera();
        }

        OldScraper<Listing> scraper = new WilkinsonScraper(cameras);
        scraper.start();
    }
}
