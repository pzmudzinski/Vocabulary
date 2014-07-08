package com.pz.vocabulary.app.models.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pz.vocabulary.app.sql.DatabaseTables;

/**
 * Created by piotr on 04/06/14.
 */
@DatabaseTable(tableName = DatabaseTables.TABLE_MEMORIES)
public class Memory extends BaseEntity {
    @DatabaseField(unique = true)
    private String description;

    public Memory()
    {
        super();
    }

    public Memory(long id, String desc)
    {
        super(id);
        this.description = desc;
    }

    public Memory(String desc)
    {
        super();
        this.description = desc;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
