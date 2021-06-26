package com.deivmercer.kanbanboard.factory;

import com.deivmercer.kanbanboard.model.Column;

public class ColumnFactory {

    public static Column getColumn(String title) {

        Column column = new Column();
        column.setTitle(title);
        return column;
    }
}
