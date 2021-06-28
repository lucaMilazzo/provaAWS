package com.deivmercer.kanbanboard.controller;

import com.deivmercer.kanbanboard.factory.ColumnFactory;
import com.deivmercer.kanbanboard.model.Column;
import com.deivmercer.kanbanboard.model.Tile;
import com.deivmercer.kanbanboard.repository.ColumnRepository;
import com.deivmercer.kanbanboard.repository.TileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ColumnController {

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private TileRepository tileRepository;

    @PostMapping(path = "/addColumn")
    public ResponseEntity<String> addColumn(@RequestParam String title) {

        Optional<Column> column = columnRepository.findByTitle(title);
        if (column.isPresent())
            return new ResponseEntity<>("A column with this title already exists.", HttpStatus.BAD_REQUEST);
        Column columnEntity = ColumnFactory.getColumn(title);
        columnRepository.save(columnEntity);
        return new ResponseEntity<>("Column created.", HttpStatus.CREATED);
    }

    @GetMapping(path = "/getAllColumns")
    public Iterable<Column> getAllColumns() {

        return columnRepository.findAll();
    }

    @GetMapping(path = "/getColumn/{title}")
    public ResponseEntity<Column> getColumn(@PathVariable String title) {

        Optional<Column> column = columnRepository.findByTitle(title);
        if (column.isPresent())
            return new ResponseEntity<>(column.get(), HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/getColumnTiles/{title}")
    public ResponseEntity<List<Tile>> getColumnTiles(@PathVariable String title) {

        Optional<Column> column = columnRepository.findByTitle(title);
        if (column.isPresent())
            return new ResponseEntity<>(column.get().getTiles(), HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PatchMapping(path = "/changeColumnStatus")
    public ResponseEntity<String> changeColumnStatus(@RequestParam String title) {

        Optional<Column> column = columnRepository.findByTitle(title);
        if (column.isPresent()) {
            Column columnEntity = column.get();
            columnEntity.changeStatus();
            columnRepository.save(columnEntity);
            return new ResponseEntity<>("Column status modified.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Column not found", HttpStatus.NOT_FOUND);
    }

    @PatchMapping(path = "/editColumn")
    public ResponseEntity<String>  editColumn(@RequestParam String old_title, @RequestParam String new_title) {

        Optional<Column> column = columnRepository.findByTitle(old_title);
        if (column.isPresent()) {
            Column columnEntity = column.get();
            columnEntity.setTitle(new_title);
            columnRepository.save(columnEntity);
            return new ResponseEntity<>("Column modified.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Not found.", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path = "/deleteColumn")
    public ResponseEntity<String> deleteColumn(@RequestParam String title) {

        Optional<Column> column = columnRepository.findByTitle(title);
        if (column.isPresent()) {
            Column columnEntity = column.get();
            if (columnEntity.getStatus() == 'O') {
                for (Tile tile : columnEntity.getTiles())
                    tileRepository.delete(tile);
                columnRepository.delete(column.get());
                return new ResponseEntity<>("Column deleted.", HttpStatus.OK);
            }
            return new ResponseEntity<>("Can't delete an archived column.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Not found.", HttpStatus.NOT_FOUND);
    }
}
