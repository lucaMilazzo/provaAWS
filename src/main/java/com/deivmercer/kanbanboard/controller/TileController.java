package com.deivmercer.kanbanboard.controller;

import com.deivmercer.kanbanboard.exception.UnsupportedFormatException;
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

    @PostMapping(path = "/addTextTile")
    public ResponseEntity<String> addTextTile(@RequestParam String title, @RequestParam String author,
                                              @RequestParam String content, @RequestParam char content_type,
                                              @RequestParam String column_title) {

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
    public ResponseEntity<String> addImageTile(@RequestParam String title, @RequestParam String author,
                                               @RequestParam MultipartFile content, @RequestParam char content_type,
                                               @RequestParam String column_title) {

        Optional<User> user = userRepository.findByUsername(author);
        if (user.isEmpty())
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        Optional<Column> column = columnRepository.findByTitle(column_title);
        if (column.isEmpty())
            return new ResponseEntity<>("Column not found.", HttpStatus.NOT_FOUND);
        else if (column.get().getStatus() != 'O')
            return new ResponseEntity<>("Cannot add tiles to an archived column.", HttpStatus.BAD_REQUEST);
        try {
            String filePath = saveImage(content);
            Tile tileEntity = TileFactory.getTile(title, user.get(), filePath, content_type, 'I',
                    column.get());
            tileRepository.save(tileEntity);
            return new ResponseEntity<>("Tile created.", HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Cannot save image.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UnsupportedFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Unsupported file format.", HttpStatus.BAD_REQUEST);
        }
    }

    public String saveImage(MultipartFile content) throws IOException, UnsupportedFormatException {

        Path filePath = Paths.get("user_generated_content");
        if (Files.notExists(filePath))
            Files.createDirectory(filePath);
        String extension = content.getOriginalFilename().split("\\.")[1].toLowerCase();
        if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("jpeg")
                && !extension.equals("bmp") && !extension.equals("gif") && !extension.equals("tif"))
            throw new UnsupportedFormatException(extension + " is not supported.");
        if (!extension.equals("png") && !extension.equals("gif"))
            extension = "png";
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
            return file.getPath();
        } else
            throw new IOException("Cannot create file.");
    }

    @GetMapping(path = "/getTile/{tile_id}")
    public ResponseEntity<Tile> getTile(@PathVariable int tile_id) {

        Optional<Tile> tile = tileRepository.findById(tile_id);
        if (tile.isPresent())
            return new ResponseEntity<>(tile.get(), HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PatchMapping(path = "/moveTile")
    public ResponseEntity<String> moveTile(@RequestParam int tile_id, @RequestParam String column_title) {

        Optional<Tile> tile = tileRepository.findById(tile_id);
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

    @PatchMapping(path = "/editTextTile")
    public ResponseEntity<String> editTextTile(@RequestParam int tile_id,
                                               @RequestParam(required = false) String new_title,
                                               @RequestParam(required = false) String content) {

        Optional<Tile> tile = tileRepository.findById(tile_id);
        if (tile.isPresent()) {
            Tile tileEntity = tile.get();
            if (tileEntity.getColumn().getStatus() == 'A')
                return new ResponseEntity<>("Cannot move a tile from an archived column.", HttpStatus.BAD_REQUEST);
            if (new_title != null)
                tileEntity.setTitle(new_title);
            String previousImage = null;
            if (content != null) {
                if (tileEntity.getTile_type() == 'I')
                    previousImage = tileEntity.getContent();
                tileEntity.setContent(content);
                tileEntity.setTile_type('T');
            }
            tileRepository.save(tileEntity);
            if (previousImage != null) {
                try {
                    Files.delete(Paths.get(previousImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new ResponseEntity<>("Tile modified.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Tile not found.", HttpStatus.NOT_FOUND);
    }

    @PatchMapping(path = "/editImageTile")
    public ResponseEntity<String> editImageTile(@RequestParam int tile_id,
                                                @RequestParam(required = false) String new_title,
                                                @RequestParam(required = false) MultipartFile content) {

        Optional<Tile> tile = tileRepository.findById(tile_id);
        if (tile.isPresent()) {
            Tile tileEntity = tile.get();
            if (tileEntity.getColumn().getStatus() == 'A')
                return new ResponseEntity<>("Cannot move a tile from an archived column.", HttpStatus.BAD_REQUEST);
            if (new_title != null)
                tileEntity.setTitle(new_title);
            String previousImage = null;
            if (content != null) {
                try {
                    String filePath = saveImage(content);
                    if (tileEntity.getTile_type() == 'I')
                        previousImage = tileEntity.getContent();
                    tileEntity.setContent(filePath);
                    tileEntity.setTile_type('I');
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>("Cannot save image.", HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (UnsupportedFormatException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>("Unsupported file format.", HttpStatus.BAD_REQUEST);
                }
            }
            tileRepository.save(tileEntity);
            if (previousImage != null) {
                try {
                    Files.delete(Paths.get(previousImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new ResponseEntity<>("Tile modified.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Tile not found.", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path = "/deleteTile")
    public ResponseEntity<String> deleteTile(@RequestParam int tile_id) {

        Optional<Tile> tile = tileRepository.findById(tile_id);
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
