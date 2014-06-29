package com.pz.vocabulary.test;

import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Word;

import junit.framework.TestCase;

/**
 * Created by piotr on 05/06/14.
 */
public class WordTest extends TestCase{

    public void testEquality()
    {
        Language language = new Language(0, "eng");
        Language language1 = new Language(1, "por");
        Word word1 = new Word(language, "b");
        Word word2 = new Word(language, "b");
        Word word3 = new Word(language1, "b");
        Word word4 = new Word(language, "c");

        assertTrue(word1.equals(word2));

        assertFalse(word1.equals(word3));

        assertFalse(word3.equals(word4));
    }
}
