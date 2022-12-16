package com.project.scrapers;

import com.project.Debug;
import com.project.datastore.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scrapes all the books of amazon which we'll crosscheck later on other platforms
 */
public class BookScraper implements Debug {
    public static final String BASE_URL = "https://www.amazon.co.uk";

    private final WebDriver scraper;
    private int page = 0;
    private int total = 0;
    private int index = 0;
    private String currentPage = String.format("%s/s?i=stripbooks&fs=true&page=%d", BASE_URL, page);


    /**
     * Set options and create webscraper
     * @throws InterruptedException when it fails to start webscraper.
     */
    public BookScraper() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(!DEBUG);
        scraper = new ChromeDriver(options);
        scraper.get(BASE_URL);

        Thread.sleep(3000);

    }

    /**
     * Begins scraping books of amazon.
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        while(hasNext()) {
            var next = getNext();

            if (next == null) {
                continue;
            }

            try {
                scrape(next);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                info(e.getMessage());
            }
        }
    }

    /**
     * Checks if there's more books on the list
     * @return true if there is and no if there isn't.
     */
    private boolean hasNext() {
        if (index < total) {
            return true;
        }

        // increase counter
        page += 1;

        if (page >= 75) {
            return false;
        }

        currentPage = String.format("%s/s?i=stripbooks&fs=true&page=%d", BASE_URL, page);
        total = getCurrentPageItems().size();
        index = 0;

        return total != 0;
    }

    /**
     * Get the current element on the page
     * @return a list of WebElements
     */
    private List<WebElement> getCurrentPageItems() {
        // get the next page
        scraper.get(String.format(currentPage, BASE_URL, page));
        return scraper.findElements(By.cssSelector(".s-result-item .s-card-container"));
    }

    /**
     * Gets the next book to scrape
     * @return
     */
    private String getNext() {
        index += 1;

        try {
            var element = getCurrentPageItems().get(index - 1);
            WebElement link = element.findElement(By.cssSelector("h2 .a-link-normal"));

            return link.getAttribute("href");
        } catch (Exception e) {
            debug(e.getMessage());
            return null;
        }
    }

    /**
     * Handles the book scraping process.
     * @param url of the book to scrape
     * @throws Exception
     */
    private void scrape(String url) throws Exception {
        debug(url);
        scraper.get(url);

        // create book
        var book = getBook();

        // create book-genre relationships
        createBookGenreRelationship(book.getId(), getGenres());

        // create author-book relationships
        createAuthorBookRelationship(book.getId(), getAuthors());

        debug("Successfully scraped " + book.getTitle());
    }

    /**
     * Creates a relationship between books and genre
     * @param bookId the id of the book
     * @param genreIds the id of the genre
     */
    private void createBookGenreRelationship(Long bookId, List<Long> genreIds) {
        for (var genreId : genreIds) {
            var bg = new BooksGenres();
            bg.setBookId(bookId);
            bg.setGenreId(genreId);
            Database.createOrUpdate(bg);
        }
    }

    /**
     * Creates a relationship between author and book
     * @param bookId the id of the book
     * @param authorsIds the ids of the authors
     */
    private void createAuthorBookRelationship(Long bookId, List<Long> authorsIds) {
        for (var authorId : authorsIds) {
            var ab = new BooksAuthors();
            ab.setBookId(bookId);
            ab.setAuthorId(authorId);
            Database.createOrUpdate(ab);
        }
    }

    /**
     * Gets all the details a book
     * @return an instance of a Book
     * @throws Exception when the web element search fails.
     */
    private Book getBook() throws Exception {
        var book = new Book();

        String title = scraper.findElement(By.cssSelector("#productTitle")).getText().toLowerCase();

        var params = new Database.Parameter[]{ new Database.Parameter("title", title)};

        var result = Database.getItemsWhere(
                Book.class,
                "t.title = :title",
                params
        );

        if (result.size() > 0) {
            throw new Exception("Skipping Book: " + title + " - already exists.");
        }


        var image = getImage();
        var description = getDescription(title);
        var isbn = getIsbn();

        if (isbn == null) {
            throw new Exception("Skipping Book: " + title + " - doesn't have an ISBN-10");
        }

        book.setDescription(description);
        book.setTitle(title);
        book.setImage(image);
        book.setIsbn(isbn);

        Database.createOrUpdate(book);

        return book;
    }

    /**
     * Gets all the authors for a book
     * @return a list of authors
     */
    private List<Long> getAuthors() {
        List<String> authors = scraper
                .findElements(By.cssSelector("#bylineInfo span.author a.a-link-normal.contributorNameID"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());


        if (authors.size() == 0) {
            authors = scraper.findElements(By.cssSelector("#bylineInfo span.author a.a-link-normal"))
                    .stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
        }

        // add an unknown author field.

        if (authors.size() == 0) {
            authors = new ArrayList<>();
            authors.add("Unknown");
        }

        var authorIds = new ArrayList<Long>();

        for (var authorName : authors) {
            authorName = authorName.toLowerCase();

            var params = new Database.Parameter[]{ new Database.Parameter("name", authorName)};

            var result = Database.getItemsWhere(
                    Author.class,
                    "t.name = :name",
                    params
            );

            if (result.size() >= 1) {
                authorIds.add(result.get(0).getId());
                continue;
            }

            var author = new Author();
            author.setName(authorName);

            Database.createOrUpdate(author);

            authorIds.add(author.getId());
        }

        return authorIds;
    }

    /**
     * Gets all the genre for a book
     * @return a list of genre ids
     */
    private List<Long> getGenres() {
        var genres = scraper
                .findElement(By.cssSelector("#detailBulletsWrapper_feature_div > ul:nth-child(4) > li > span > ul"))
                .findElements(By.cssSelector("li a"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        var genreIds = new ArrayList<Long>();

        for (var genreTitle : genres) {
            genreTitle = genreTitle.toLowerCase();

            var result = Database.getItemsWhere(
                    Genre.class,
                    "t.title = :title",
                    new Database.Parameter[]{ new Database.Parameter("title", genreTitle)}
            );

            if (result.size() >= 1) {
                genreIds.add(result.get(0).getId());
                continue;
            }

            var genre = new Genre();
            genre.setTitle(genreTitle);

            Database.createOrUpdate(genre);

            genreIds.add(genre.getId());
        }

        return genreIds;
    }

    /**
     * Gets the image of the book
     * @return a string to the image url
     */
    private String getImage() {
        List<String> images = scraper.findElements(By.cssSelector("#ebooksImgBlkFront"))
                .stream()
                .map(e -> e.getAttribute("src"))
                .collect(Collectors.toList());

        if (images.size() == 0) {
            images = scraper
                    .findElements(By.cssSelector("#imgBlkFront"))
                    .stream()
                    .map(e -> e.getAttribute("src"))
                    .collect(Collectors.toList());
        }

        var image = "";

        if (images.size() > 0) {
            image = images.get(0);
        }

        return image;
    }

    /**
     * Gets the description of a book
     * @param title the title of the book to get the description
     * @return the books description
     * @throws Exception when there isn't a web element.
     */
    private String getDescription(String title) throws Exception {
        List<String> descriptions = scraper
                .findElements(
                        By.cssSelector("#editorialReviews_feature_div > div.a-section.a-spacing-small.a-padding-base")
                )
                .stream()
                .map(e -> e.getAttribute("innerHTML"))
                .collect(Collectors.toList());

        if (descriptions.size() == 0) {
            throw new Exception("Skipping Book: " + title + " - book doesn't have a description.");
        }

        return descriptions.get(0);
    }

    /**
     * Gets the isbn of the book
     * @return the book isbn
     */
    private String getIsbn() {
        try {
        return scraper
                .findElement(
                        By.cssSelector("#detailBullets_feature_div > ul > li:nth-child(4) > span > span:nth-child(2)"))
                .getText();
        } catch (Exception ex) {
            return null;
        }
    }
}

