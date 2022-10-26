package com.samuel.omohan.scraper;

import com.samuel.omohan.datastore.Camera;
import com.samuel.omohan.datastore.Listing;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class AmazonScraper extends OldScraper<Listing> {
    public final String BASE_URL = "https://www.amazon.co.uk/";

    public AmazonScraper(List<Camera> cameras) {
        super(cameras);
    }

    public Listing scrape() {
        Camera camera = getNextCamera();

        if (DEBUG) {
            System.out.println(camera.getTitle());
        }

        // Search for camera by title
        String search = URLEncoder.encode(camera.getTitle(), StandardCharsets.UTF_8);

        scraper.get(BASE_URL + "s?k=" + search);

        WebElement linkElement = scraper.findElement(By.cssSelector(".a-link-normal.a-text-normal"));
        WebElement priceElement = scraper.findElement(By.cssSelector(".a-price .a-offscreen"));

        double price = Double.parseDouble(priceElement.getAttribute("innerHTML").replaceAll("[,£]+", ""));
        String url = linkElement.getAttribute("href");
        String features = "Visit vendor for details";

        if (DEBUG) {
            System.out.println("\t" + price);
            System.out.println("\t" + url);
            System.out.println("\t" + features.length());
        }

        Listing listing = new Listing();
        listing.setDate(new Date());
        listing.setPrice(price);
        listing.setUrl(url);
        listing.setProviderId(Listing.AMAZON_PROVIDER);
        listing.setFeatures(features);
        listing.setCameraId(camera.getId());

        return listing;
    }
}
