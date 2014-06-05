package com.pz.vocabulary.app.models;

/**
 * Created by piotr on 04/06/14.
 */
public class BaseEntity {
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
