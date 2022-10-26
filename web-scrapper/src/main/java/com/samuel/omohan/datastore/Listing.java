package com.samuel.omohan.datastore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="listings")
public class Listing implements Serializable {
    static public final int AMAZON_PROVIDER = 1;
    static public final int BHPHOTO_PROVIDER = 2;
    static public final int EBAY_PROVIDER = 3;
    static public final int WILKIN_PROVIDER = 4;
    static public final int JESSOPS_PROVIDER = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="listing_id", unique = true)
    private int id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name="price", nullable = false)
    private double price;

    @Column(name="date", nullable = false)
    private Date date;

    @Column(name="provider_id", nullable = false)
    private int providerId;

    @Column(name="camera_id", nullable = false)
    private int camera_id;

    @Column(name="features", nullable = false)
    private String features;

    public void setCameraId(int cameraId) {
        this.camera_id = cameraId;
    }

    public int getCameraId() {
        return this.camera_id;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public int getProviderId() {
        return providerId;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
