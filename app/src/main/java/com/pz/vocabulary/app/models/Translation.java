package com.pz.vocabulary.app.models;

/**
 * Created by piotr on 04/06/14.
 */
public class Translation extends BaseEntity {

    private long wordTo;
    private Memory memory;
    
    public Translation(long id, long wordTo, Memory memory)
    {
        super(id);
        this.wordTo = wordTo;
        this.memory = memory;
    }

    public Memory getMemory()
    {
        return memory;
    }

    public long getWordTo()
    {
        return wordTo;
    }
}
