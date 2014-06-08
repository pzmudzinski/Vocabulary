package com.pz.vocabulary.app.models;

/**
 * Created by piotr on 04/06/14.
 */
public class Memory extends BaseEntity {
    private String description;

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
}
