package com.deivmercer.kanbanboard.factory;

import com.deivmercer.kanbanboard.model.Column;
import com.deivmercer.kanbanboard.model.Tile;
import com.deivmercer.kanbanboard.model.User;

public class TileFactory {

    public static Tile getTile(String title, User author, String content, char content_type, Column column) {

        Tile tile = new Tile();
        tile.setTitle(title);
        tile.setAuthor(author);
        tile.setContent(content);
        tile.setContent_type(content_type);
        tile.setColumn(column);
        return tile;
    }
}
