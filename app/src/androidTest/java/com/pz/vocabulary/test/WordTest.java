package com.pz.vocabulary.test;

import com.pz.vocabulary.app.models.Word;

import junit.framework.TestCase;

/**
 * Created by piotr on 05/06/14.
 */
public class WordTest extends TestCase{

    public void testEquality()
    {
        Word word1 = new Word(0, "b");
        Word word2 = new Word(0, "b");
        Word word3 = new Word(1, "b");
        Word word4 = new Word(0, "c");

        assertTrue(word1.equals(word2));

        assertFalse(word1.equals(word3));

        assertFalse(word3.equals(word4));
    }
}
