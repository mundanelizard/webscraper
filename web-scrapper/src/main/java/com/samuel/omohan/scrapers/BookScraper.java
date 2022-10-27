package com.samuel.omohan.scrapers;

import com.samuel.omohan.Debug;
import com.samuel.omohan.datastore.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookScraper implements Debug {
    public static final String BASE_URL = "https://www.amazon.co.uk";

    private final WebDriver scraper;
    private int page = 0;
    private int total = 0;
    private int index = 0;
    private String currentPage = String.format("%s/s?i=stripbooks&fs=true&page=%d", BASE_URL, page);

    // store the current list url

    public BookScraper() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(!DEBUG);
        scraper = new ChromeDriver(options);
        scraper.get(BASE_URL);

        Thread.sleep(3000);

    }

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
                System.out.println(e.getMessage());
//                e.printStackTrace();
            }
        }
    }

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

    private List<WebElement> getCurrentPageItems() {
        // get the next page
        scraper.get(String.format(currentPage, BASE_URL, page));
        return scraper.findElements(By.cssSelector(".s-result-item .s-card-container"));
    }

    private String getNext() {
        index += 1;

        try {
            var element = getCurrentPageItems().get(index - 1);
            WebElement link = element.findElement(By.cssSelector("h2 .a-link-normal"));

            return link.getAttribute("href");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void scrape(String url) throws Exception {
        System.out.println(url);
        scraper.get(url);

        // create book
        var book = getBook(getLanguages());

        // create book-genre relationships
        createBookGenreRelationship(book.getId(), getGenres());

        // create author-book relationships
        createAuthorBookRelationship(book.getId(), getAuthors());

        System.out.println("Successfully scraped " + book.getTitle());
    }

    private void createBookGenreRelationship(Long bookId, List<Long> genreIds) {
        for (var genreId : genreIds) {
            var bg = new BooksGenres();
            bg.setBookId(bookId);
            bg.setGenreId(genreId);
            Database.createOrUpdate(bg);
        }
    }

    private void createAuthorBookRelationship(Long bookId, List<Long> authorsIds) {
        for (var authorId : authorsIds) {
            var ab = new BooksAuthors();
            ab.setBookId(bookId);
            ab.setAuthorId(authorId);
            Database.createOrUpdate(ab);
        }
    }

    private Book getBook(Long languageId) throws Exception {
        var book = new Book();

        String title = scraper.findElement(By.cssSelector("#productTitle")).getText().toLowerCase();

        var params = new Database.Parameter[]{ new Database.Parameter("title", title)};

        var result = Database.getItemsWhere(
                Book.class,
                "t.title = :title",
                params
        );

        if (result.size() > 0) {
            throw new Exception("Book - " + title + " already exists.");
        }

        List<String> descriptions = scraper
                .findElements(
                        By.cssSelector("#editorialReviews_feature_div > div.a-section.a-spacing-small.a-padding-base")
                )
                .stream()
                .map(e -> e.getAttribute("innerHTML"))
                .collect(Collectors.toList());

        if (descriptions.size() == 0) {
            throw new Exception("Skipping " + title);
        }

        var description = descriptions.get(0);

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

        book.setLanguageId(languageId);
        book.setDescription(description);
        book.setTitle(title);
        book.setImage(image);

        Database.createOrUpdate(book);

        return book;
    }

    private Long getLanguages() {
        List<String> languages = scraper
                .findElements(By.cssSelector("#detailBullets_feature_div > ul > li:nth-child(2) > span > span:nth-child(2)"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        if (languages.size() == 0) {
            languages = new ArrayList<>();
            languages.add("Unknown");
        }

        var languagesIds = new ArrayList<Long>();


        for (var languageName : languages) {
            languageName = languageName.toLowerCase();

            var params = new Database.Parameter[]{ new Database.Parameter("name", languageName)};

            var result = Database.getItemsWhere(
                    Language.class,
                    "t.name = :name",
                    params
            );

            if (result.size() >= 1) {
                languagesIds.add(result.get(0).getId());
                break;
            }

            var language = new Language();
            language.setName(languageName);

            Database.createOrUpdate(language);

            languagesIds.add(language.getId());
            break;
        }

        return languagesIds.get(0);
    }

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
}

