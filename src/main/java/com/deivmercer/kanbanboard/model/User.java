package com.deivmercer.kanbanboard.model;

import javax.persistence.Column;
import javax.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "author")
    private List<Tile> tiles;

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getPassword() {

        return this.password;
    }

    public void setPassword(String password) {

        this.password = password;
    }
}
