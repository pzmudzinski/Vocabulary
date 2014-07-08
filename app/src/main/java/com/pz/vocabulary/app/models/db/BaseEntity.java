package com.pz.vocabulary.app.models.db;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by piotr on 04/06/14.
 */
public class BaseEntity {

    @DatabaseField(generatedId = true, unique = true)
    protected long id;

    public BaseEntity(long id)
    {
        super();
        this.id = id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public BaseEntity()
    {
        super();
    }
}
