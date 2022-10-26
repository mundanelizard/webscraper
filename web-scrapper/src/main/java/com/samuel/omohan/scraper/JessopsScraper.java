package com.samuel.omohan.scraper;

import com.samuel.omohan.datastore.Camera;
import com.samuel.omohan.datastore.Listing;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


/**
 *
 */
public class JessopsScraper extends OldScraper<Listing> {
    public final String BASE_URL = "https://www.jessops.com/";

    public JessopsScraper(List<Camera> cameras) {
        super(cameras);
    }

    public Listing scrape() {
        Camera camera = getNextCamera();

        if (DEBUG) {
            System.out.println(camera.getTitle());
        }

        // search for camera by title
        String search = URLEncoder.encode(camera.getTitle().split("[-+/^]")[0], StandardCharsets.UTF_8);

        int index = 0;

        try {
            // Navigating to search page.
            scraper.get(BASE_URL + "search?q=" + search);

            // Searching for all search result title [names]
            List<WebElement> names = scraper.findElements(By.cssSelector("h4 a"));

            // Checks if the search has results. If it doesn't, it skips the item.
            if(names.size() == 0)
                return null;

            // Checking for a name that is an exact match with what's we got from the DB
            // if we don't get any, we go with 0 which is the default for 'index'.
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).getText().equals(camera.getTitle())) {
                    index = i;
                    break;
                }
            }
        } catch (Exception ex) {
            if(DEBUG) {
                System.out.println("\tSkipping...");
            }
            return null;
        }


        // Getting the details from the listing index that best matches our search
        WebElement priceElement = scraper.findElements(By.cssSelector("p.price.larger")).get(index);
        WebElement urlElement = scraper.findElements(By.cssSelector(".f-grid.prod-row h4 a")).get(index);
        WebElement featuresElements = scraper.findElements(By.cssSelector(".details-pricing .f-list.j-list")).get(index);

        double price = Double.parseDouble(priceElement.getText().replaceAll("[,£]+", ""));
        String url = urlElement.getAttribute("href");
        String features = featuresElements.getAttribute("innerHTML");

        if (DEBUG) {
            System.out.println("\t" + price);
            System.out.println("\t" + url);
            System.out.println("\tfeatures length of " + features.length());
        }

        Listing listing = new Listing();
        listing.setDate(new Date());
        listing.setPrice(price);
        listing.setUrl(url);
        listing.setProviderId(Listing.JESSOPS_PROVIDER);
        listing.setFeatures(features);
        listing.setCameraId(camera.getId());

        return listing;
    }
}
