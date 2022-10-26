package com.samuel.omohan.scraper;

import com.samuel.omohan.Debug;
import com.samuel.omohan.datastore.Camera;
import com.samuel.omohan.datastore.Database;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CameraScraper extends OldScraper<Camera> implements Debug {
    // Jessops will serve as the source of all camera details and description.
    public final String BASE_URL = "https://www.jessops.com/";
    public int itemIndex = 0;
    public int pageIndex = 0;

    public CameraScraper(List<Camera> cameras) {
        super(cameras);
    }

    /**
     * Gets camera details from a page.
     * @return camera details.
     */
    public Camera scrape() {
        navigateTo(pageIndex, itemIndex);
        if (DEBUG) {
            System.out.println("\tScraping page...");
        }
        WebElement titleElement = scraper.findElement(By.cssSelector("h1 > span"));
        List<WebElement> imageElements = scraper.findElements(By.cssSelector(".product-images .f-slider-container li picture img"));
        WebElement specsElement = scraper.findElement(By.cssSelector(".f-table"));

        StringBuilder images = new StringBuilder();

        for(WebElement image : imageElements) {
            images.append(";");
            images.append(image.getAttribute("src"));
        }

        Camera camera = new Camera();
        camera.setImages(images.toString());
        camera.setSpecs(specsElement.getAttribute("innerHTML"));
        camera.setTitle(titleElement.getText());

        return camera;
    }

    /**
     * Adds new cameras listing in the database.
     * @param camera cameraDetails to update/add to the database.
     */
    public void updateTable(Camera camera) {
        if (DEBUG) {
            System.out.println("\tSaving Camera to database...");
        }
        Database.createOrUpdate(camera);
    }

    /**
     * Checks if there is more times to scrape.
     * @return false if you are out of items to scrape.
     */
    boolean shouldScrape() {
        return false;
    }

    /**
     * Gets all the cameras details on Jessops site
     */
    public void getAllCamera() {
        // number of pages on the site found on the site.

        try {
            int count = 13;

            for (pageIndex = 1; pageIndex <= count; pageIndex++) {
                if (DEBUG) {
                    System.out.println("------Page " + pageIndex + "-------");
                }
                getAllCamerasInPage();
            }
        } catch (Exception ex) {
            System.out.println("\n\n\n");
            ex.printStackTrace();
            OldScraper.closeScraper();
            Database.closeEntityManager();
            System.exit(0);
        }

    }

    /**
     * Gets all the cameras details in the page and details.
     */
    private void getAllCamerasInPage() {
        int count = getPageItemsCount();

        for (itemIndex = 1; itemIndex <= count; itemIndex++) {
            if (DEBUG) {
                System.out.println(ANSI_GREEN+ "\nScraping item " + itemIndex + " on page " + pageIndex + ANSI_RESET);
            }
            Camera cd = scrape();
            updateTable(cd);
        }
    }

    /**
     * Gets the total number of items in a page.
     * @return total number of items to scrap in a page.
     */
    private int getPageItemsCount() {
        if (DEBUG) {
            System.out.println("Getting page items count.");
        }
        navigateTo(pageIndex);
        return scraper.findElements(By.cssSelector(".f-grid.prod-row h4 a")).size();
    }

    /**
     * Navigates to the requested page.
     *
     * @param pageIndex index of page to visit in the cameras listing.
     * @param itemIndex index of item to scrape in the items list.
     */
    private void navigateTo(int pageIndex, int itemIndex) {
        navigateTo(pageIndex);
        WebElement link = scraper.findElements(By.cssSelector(".f-grid.prod-row h4 a")).get(itemIndex - 1);
        String url = link.getAttribute("href");
        if (DEBUG) {
            System.out.println("\tOpening: " + url);
        }
        scraper.navigate().to(url);
    }

    /**
     * Navigates to listing page.
     * @param pageIndex listing page number to visit.
     */
    private void navigateTo(int pageIndex) {
        String url = BASE_URL + "cameras?fh_start_index=" + (pageIndex * 21 - 21) + "&fh_view_size=21";
        scraper.get(url);
    }

    /**
     * Gets all the cameras in the database.
     * @return all the cameras in the database
     */
    public List<Camera> getCameras() {
        return Database.getCameras();
    }
}
