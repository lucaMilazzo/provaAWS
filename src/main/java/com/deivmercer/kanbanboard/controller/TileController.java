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
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
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

    @Autowired
    private HttpServletRequest request;

    @PostMapping(path = "/addTextTile")
    public ResponseEntity<String> addTextTile(@RequestParam String title, @RequestParam String author,
                                          @RequestParam String content, @RequestParam char content_type,
                                          @RequestParam String column_title) {

        Optional<Tile> tile = tileRepository.findByTitle(title);
        if (tile.isPresent())
            return new ResponseEntity<>("A tile with this title already exists.", HttpStatus.BAD_REQUEST);
        Optional<User> user = userRepository.findByUsername(author);
        if (user.isEmpty())
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        Optional<Column> column = columnRepository.findByTitle(column_title);
        if (column.isEmpty())
            return new ResponseEntity<>("Column not found.", HttpStatus.NOT_FOUND);
        else if (column.get().getStatus() != 'O')
            return new ResponseEntity<>("Cannot add tiles to an archived column.", HttpStatus.BAD_REQUEST);
        Tile tileEntity = TileFactory.getTile(title, user.get(), content, content_type, 'T', column.get());
        tileRepository.save(tileEntity);
        return new ResponseEntity<>("Tile created.", HttpStatus.CREATED);
    }

    @PostMapping(path = "/addImageTile")
    public ResponseEntity<String> addImageTile(
                                           @RequestParam String title, @RequestParam String author,
                                           @RequestParam MultipartFile content, @RequestParam char content_type,
                                           @RequestParam String column_title) {

        Optional<Tile> tile = tileRepository.findByTitle(title);
        if (tile.isPresent())
            return new ResponseEntity<>("A tile with this title already exists.", HttpStatus.BAD_REQUEST);
        Optional<User> user = userRepository.findByUsername(author);
        if (user.isEmpty())
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        Optional<Column> column = columnRepository.findByTitle(column_title);
        if (column.isEmpty())
            return new ResponseEntity<>("Column not found.", HttpStatus.NOT_FOUND);
        else if (column.get().getStatus() != 'O')
            return new ResponseEntity<>("Cannot add tiles to an archived column.", HttpStatus.BAD_REQUEST);
        Path filePath = Paths.get("user_generated_content");
        try {
            if (Files.notExists(filePath))
                Files.createDirectory(filePath);
            String extension = content.getOriginalFilename().split("\\.")[1];
            File file = new File(filePath + "/" + new Date().getTime() +  "." + extension);
            if (file.createNewFile()) {
                BufferedImage bufferedImage = ImageIO.read(content.getInputStream());
                int width = bufferedImage.getWidth(), height = bufferedImage.getHeight();
                if (width > 900) {
                    height = (height * 900) / width;
                    width = 900;
                }
                if (height > 900) {
                    width = (width * 900) / height;
                    height = 900;
                }
                Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                bufferedImage.getGraphics().drawImage(image, 0, 0, null);
                ImageIO.write(bufferedImage, extension, file);
                Tile tileEntity = TileFactory.getTile(title, user.get(), file.getPath(), content_type, 'I',
                                                      column.get());
                tileRepository.save(tileEntity);
                return new ResponseEntity<>("Tile created.", HttpStatus.CREATED);
            } else
                throw new IOException("Cannot create file.");
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Cannot save image.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            Tile tileEntity = tile.get();
            if (tileEntity.getColumn().getStatus() == 'A')
                return new ResponseEntity<>("Cannot move a tile from an archived column.", HttpStatus.BAD_REQUEST);
            Optional<Column> column = columnRepository.findByTitle(column_title);
            if (column.isPresent()) {
                tileEntity.setColumn(column.get());
                tileRepository.save(tileEntity);
                return new ResponseEntity<>("Tile moved.", HttpStatus.OK);
            }
            return new ResponseEntity<>("Column not found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Tile not found.", HttpStatus.NOT_FOUND);
    }

    @PatchMapping(path = "/editTile")
    public ResponseEntity<String> editTile(@RequestParam String old_title,
                                           @RequestParam(required = false) String new_title,
                                           @RequestParam(required = false) String content,
                                           @RequestParam(required = false) Character content_type) {

        Optional<Tile> tile = tileRepository.findByTitle(old_title);
        if (tile.isPresent()) {
            Tile tileEntity = tile.get();
            if (tileEntity.getColumn().getStatus() == 'A')
                return new ResponseEntity<>("Cannot move a tile from an archived column.", HttpStatus.BAD_REQUEST);
            if (new_title != null) {
                Optional<Tile> tile2 = tileRepository.findByTitle(new_title);
                if (tile2.isPresent())
                    return new ResponseEntity<>("A tile with this title already exists.", HttpStatus.BAD_REQUEST);
                tileEntity.setTitle(new_title);
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
            Tile tileEntity = tile.get();
            if (tileEntity.getColumn().getStatus() == 'O') {
                if (tileEntity.getTile_type() == 'I') {
                    try {
                        Files.delete(Paths.get(tileEntity.getContent()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                tileRepository.delete(tile.get());
                return new ResponseEntity<>("Tile deleted.", HttpStatus.OK);
            } else
                return new ResponseEntity<>("Cannot delete tile from an archived column.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Tile not found.", HttpStatus.NOT_FOUND);
    }
}
