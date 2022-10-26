package com.samuel.omohan.scraper;


import com.samuel.omohan.Debug;
import com.samuel.omohan.datastore.Camera;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Manager - All Camera threads
 */
public class Manager extends Thread implements Debug {
    private final ArrayList<Scraper> scrapers = new ArrayList<>();
    private final Queue<Integer> queue = new PriorityQueue<>();

    Manager() {}

    Manager(ArrayList<Scraper> scrapers) {
        for (var i = 0; i < scrapers.size(); i++) {
            var scraper = scrapers.get(i);
            addScraper(scraper);
            queue.add(i);
        }
    }

    Manager addScraper(Scraper scraper) {
        if(!scrapers.contains(scraper)) {
            this.scrapers.add(scraper);
        }

        return this;
    }


    public void run() {
        final int SLEEP_DURATION = 1000 * 60 * 3;

        while(scrapers) {

        }
    }

    private void updateCamera()  {

    }
}

interface Scraper {
    void resume();
    void pause();
}