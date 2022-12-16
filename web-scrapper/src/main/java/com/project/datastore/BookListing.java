package com.project.datastore;

// name logo url


import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

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

    @Column(name="provider", nullable = false)
    private String provider;

    @Column(name="url", nullable = false)
    private String url;

    @Column(name="price", nullable = false)
    private String price;

    @Column(name="created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Date createdAt;

    @Column(name="updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Date updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String key) {
        this.provider = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return id + " " + price + " " + bookId + " " + provider + " " + url + " " + price + " " + createdAt + " " + updatedAt;
    }
}
