package com.pz.vocabulary.app.models;

/**
 * Created by piotr on 04/06/14.
 */
public class Word extends BaseEntity{
    private String spelling;
    private long language;

    public Word(long id,long language, String spelling)
    {
        super(id);
        this.language = language;
        this.spelling = spelling;
    }

    public Word(long language, String spelling)
    {
        super();
        this.language = language;
        this.spelling = spelling;
    }

    public String getSpelling()
    {
        return spelling;
    }

    public long getLanguage()
    {
        return language;
    }
}
