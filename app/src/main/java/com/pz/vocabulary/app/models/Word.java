package com.pz.vocabulary.app.models;

import java.text.Normalizer;

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

    public String getNormalizedSpelling()
    {
        String asciiName = Normalizer.normalize(spelling, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return asciiName;
    }

    public String getSpelling()
    {
        return spelling;
    }

    public long getLanguage()
    {
        return language;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != o.getClass())
            return false;

        Word other = (Word) o;

        return other.getLanguage() == this.getLanguage() && other.getSpelling().equals(this.getSpelling());
    }

    @Override
    public String toString() {
        return "[lang:"+language+"/id:"+id+"]"+" " + spelling + " ("+getNormalizedSpelling()+")";
    }
}
