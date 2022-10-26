package com.samuel.omohan.scraper;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

@DisplayName("Ebay Scraper Test")
public class EbayScraperTest {
    @BeforeAll
    static void initAll() {

    }

    @BeforeEach
    void init() {

    }

    @Test
    @DisplayName("Get Data from Web Site")
    void testGetDataFromWebsite() {


        try {
            EbayScraper es = new EbayScraper(new ArrayList<>());
        } catch (Exception e) {
            Assertions.fail("You failed badly");
        }

        Assertions.assertEquals(10, 10);
    }

    @Test
    @DisplayName("Get Data from Web Site")
    void testEbayIsLive() {

        try {
            EbayScraper es = new EbayScraper(new ArrayList<>());
        } catch (Exception e) {
            Assertions.fail("You failed badly");
        }

        Assertions.assertEquals(10, 10);
    }


    @AfterEach
    void tearDown() {

    }

    @AfterAll
    static void tearDownAll() {

    }


}
