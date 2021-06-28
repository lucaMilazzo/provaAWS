package com.deivmercer.kanbanboard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

@Entity
@Table(name = "tile")
public class Tile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @javax.persistence.Column(nullable = false, unique=true)
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @javax.persistence.Column(nullable = false)
    private String content;

    @javax.persistence.Column(nullable = false)
    private char content_type;  // Organizational / Informational

    @JsonIgnoreProperties("tiles")
    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    private Column column;

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public User getAuthor() {

        return author;
    }

    public void setAuthor(User author) {

        this.author = author;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public char getContent_type() {

        return content_type;
    }

    public void setContent_type(char content_type) throws IllegalArgumentException {

        if (content_type != 'O' && content_type != 'I')
            throw new IllegalArgumentException("Content type " + content_type + " is invalid.");
        this.content_type = content_type;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }
}
