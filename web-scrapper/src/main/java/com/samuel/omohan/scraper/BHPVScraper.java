package com.samuel.omohan.scraper;

import com.samuel.omohan.datastore.Camera;
import com.samuel.omohan.datastore.Listing;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class BHPVScraper extends OldScraper<Listing> {
    public final String BASE_URL = "https://www.bhphotovideo.com/";

    public BHPVScraper(List<Camera> cameras) {
        super(cameras);
    }

    public Listing scrape() {
        Camera camera = getNextCamera();

        if (DEBUG) {
            System.out.println(camera.getTitle());
        }

        // Search for camera by title
        String search = URLEncoder.encode(camera.getTitle(), StandardCharsets.UTF_8).replaceAll("[+]+", "%20");
        scraper.get(BASE_URL + "c/search?Ntt=" + search);

        if (scraper.getPageSource().contains("No results were found matching")) {
            System.out.println("No item found");
            return null;
        }

        try {
            WebElement linkElement = scraper.findElement(By.cssSelector("[data-selenium=\"miniProductPageProductNameLink\"]"));
            WebElement priceElement = scraper.findElement(By.cssSelector("[data-selenium=\"uppedDecimalPrice\"]"));
            WebElement featuresElement = scraper.findElement(By.cssSelector("[data-selenium=\"miniProductPageSellingPointsList\"]"));

            String priceString = priceElement.getText().replaceAll("[,$]+", "").replaceAll("\\s", ".");
            double price = Double.parseDouble(priceString);
            String url = linkElement.getAttribute("href");
            String features = featuresElement.getText();

            if (DEBUG) {
                System.out.println("\t" + price);
                System.out.println("\t" + url);
                System.out.println("\t" + features.length());
            }

            Listing listing = new Listing();
            listing.setDate(new Date());
            listing.setPrice(price);
            listing.setUrl(url);
            listing.setProviderId(Listing.BHPHOTO_PROVIDER);
            listing.setFeatures(features);
            listing.setCameraId(camera.getId());

            return listing;
        } catch (Exception ex) {
            System.out.println("Item is unavailable");
            return null;
        }
    }
}
