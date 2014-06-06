package com.pz.vocabulary.app.models;

/**
 * Created by piotr on 04/06/14.
 */
public class Language extends BaseEntity {

    public static final long POLISH = 1;
    public static final long ENGLISH = 2;

    private String name;

    public Word newWord(String spelling)
    {
        Word word = new Word(this.id, spelling);
        return word;
    }

    public Language(long id, String name)
    {
        super(id);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
