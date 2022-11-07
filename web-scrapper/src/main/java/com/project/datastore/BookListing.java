package com.project.datastore;

// name logo url


import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name="book_listings")
public class BookListing {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true)
    private long id;

    @Column(name="book_id", nullable = false)
    private long bookId;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="key", nullable = false)
    private String key;

    @Column(name="logo", nullable = false)
    private String logo;

    @Column(name="url", nullable = false)
    private String url;

    @Column(name="createdAt", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate createdAt;

    @Column(name="updatedAt", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
