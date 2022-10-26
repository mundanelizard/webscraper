package com.samuel.omohan.datastore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cameras")
public class Camera implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "camera_id", unique = true)
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "images", nullable = false)
    private String images;

    @Column(name = "specs", nullable = false)
    private String specs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }
}
