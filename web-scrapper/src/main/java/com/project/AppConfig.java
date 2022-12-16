package com.project;

import com.project.scrapers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class AppConfig {

    @Bean
    public ScraperManager scraperManager() throws InterruptedException {
        var scrapers = new ArrayList<Scraper>();
        scrapers.add(new AmazonScraper());
        scrapers.add(new WaterstonesScraper());
        scrapers.add(new BlackwellScraper());
        scrapers.add(new FoylesScraper());
        scrapers.add(new HiveScraper());

        return new ScraperManager(scrapers);
    }
}
