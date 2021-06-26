package com.deivmercer.kanbanboard.controller;

import com.deivmercer.kanbanboard.factory.TileFactory;
import com.deivmercer.kanbanboard.model.Column;
import com.deivmercer.kanbanboard.model.Tile;
import com.deivmercer.kanbanboard.model.User;
import com.deivmercer.kanbanboard.repository.ColumnRepository;
import com.deivmercer.kanbanboard.repository.TileRepository;
import com.deivmercer.kanbanboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
public class TileController {

    @Autowired
    private TileRepository tileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ColumnRepository columnRepository;

    @PostMapping(path = "/addTile")
    public ResponseEntity<String> addTile(@RequestParam String title, @RequestParam Integer author,
                                          @RequestParam String content, @RequestParam char content_type) {

        Optional<User> user = userRepository.findById(author);
        if(user.isPresent()) {
            Tile tile = TileFactory.getTile(title, user.get(), content, content_type);
            tileRepository.save(tile);
            return new ResponseEntity<>("Tile created.", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/getTile/{title}")
    public ResponseEntity<Tile> getTile(@PathVariable String title) {

        Optional<Tile> tile = tileRepository.findByTitle(title);
        if (tile.isPresent())
            return new ResponseEntity<>(tile.get(), HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PatchMapping(path = "/moveTile")
    public ResponseEntity<String> moveTile(@RequestParam String tile_title, @RequestParam String column_title) {

        Optional<Tile> tile = tileRepository.findByTitle(tile_title);
        if (tile.isPresent()) {
            Optional<Column> column = columnRepository.findByTitle(column_title);
            if (column.isPresent()) {
                Tile tileEntity = tile.get();
                tileEntity.setColumn(column.get());
                tileRepository.save(tileEntity);
                return new ResponseEntity<>("Tile moved.", HttpStatus.OK);
            }
            return new ResponseEntity<>("Not found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Not found.", HttpStatus.NOT_FOUND);
    }

    @PatchMapping(path = "/editTile")
    public ResponseEntity<String> editTile(@RequestParam String old_title,
                                           @RequestParam(required = false) String new_title,
                                           @RequestParam(required = false) Integer author,
                                           @RequestParam(required = false) String content,
                                           @RequestParam(required = false) Character content_type) {

        Optional<Tile> tile = tileRepository.findByTitle(old_title);
        if (tile.isPresent()) {
            Tile tileEntity = tile.get();
            if (new_title != null)
                tileEntity.setTitle(new_title);
            if (author != null) {
                Optional<User> user = userRepository.findById(author);
                if(user.isPresent())
                    tileEntity.setAuthor(user.get());
                else
                    return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }
            if (content != null)
                tileEntity.setContent(content);
            if (content_type != null) {
                try {
                    tileEntity.setContent_type(content_type);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>("Wrong content_type.", HttpStatus.BAD_REQUEST);
                }
            }
            tileRepository.save(tileEntity);
            return new ResponseEntity<>("Tile modified.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Tile not found.", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path = "/deleteTile")
    public ResponseEntity<String> deleteTile(@RequestParam String title) {

        Optional<Tile> tile = tileRepository.findByTitle(title);
        if (tile.isPresent()) {
            tileRepository.delete(tile.get());
            return new ResponseEntity<>("Tile deleted.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Tile not found.", HttpStatus.NOT_FOUND);
    }
}
