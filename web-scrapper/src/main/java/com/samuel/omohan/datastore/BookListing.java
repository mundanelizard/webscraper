package com.samuel.omohan.datastore;

// name logo url


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="book_listings")
public class BookListing {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true)
    private int id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="key", nullable = false)
    private String key;

    @Column(name="logo", nullable = false)
    private String logo;

    @Column(name="url", nullable = false)
    private String url;

    @Column(name="createdAt", nullable = false)
    private Date createdAt;

    @Column(name="updatedAt", nullable = false)
    private Date updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
