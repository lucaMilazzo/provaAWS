package com.deivmercer.kanbanboard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

@Entity
public class Column {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @javax.persistence.Column(nullable = false, unique=true)
    private String title;

    @javax.persistence.Column(nullable = false)
    private char status = 'O'; // Ongoing / Archived

    @JsonIgnoreProperties("column")
    @OneToMany(mappedBy = "column")
    private List<Tile> tiles;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public char getStatus() {
        return status;
    }

    public void changeStatus() {

        if (this.status == 'O')
            this.status = 'A';
        else
            this.status = 'O';
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}
