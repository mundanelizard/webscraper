package com.samuel.omohan.scraper;

import com.samuel.omohan.datastore.Camera;
import com.samuel.omohan.datastore.Listing;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class EbayScraper extends OldScraper<Listing> {
    public final String BASE_URL = "https://ebay.co.uk/";

    public EbayScraper(List<Camera> cameras) {
        super(cameras);
    }

    public Listing scrape() {
        Camera camera = getNextCamera();

        if(DEBUG) {
            System.out.println(camera.getTitle());
        }

        // Search cameras by title;
        String search = URLEncoder.encode(camera.getTitle(), StandardCharsets.UTF_8);
        scraper.get(BASE_URL + "sch/i.html?_nkw=" + search);

        WebElement linkElement = scraper.findElement(By.cssSelector(".srp-results .s-item__link"));
        WebElement priceElement = scraper.findElement(By.cssSelector(".srp-results .s-item__price"));

        double price = Double.parseDouble(priceElement.getText().replaceAll("[,£]+", ""));
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
        listing.setProviderId(Listing.EBAY_PROVIDER);
        listing.setFeatures(features);
        listing.setCameraId(camera.getId());

        return listing;
    }

}
