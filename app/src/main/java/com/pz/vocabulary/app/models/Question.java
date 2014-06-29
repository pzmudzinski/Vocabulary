package com.pz.vocabulary.app.models;

import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Word;

/**
* Created by piotr on 07/06/14.
*/
public class Question
{
    private Word word;
    private Memory memory;

    public Question(Word word, Memory memory)
    {
        this.word = word;
        this.memory = memory;
    }

    public Question(Word word)
    {
        this.word = word;
    }

    public Word getWord()
    {
        return word;
    }

    public Memory getMemory()
    {
        return memory;
    }
}
