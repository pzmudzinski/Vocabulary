package com.pz.vocabulary.app.models;

import java.util.List;

/**
 * Created by piotr on 05/06/14.
 */
public interface Dictionary {
    public long insertWord(Word word);
    public long findWord(long langID, String spelling);
    public Word findWord(long id);
    public void addMemoryToTranslation(long memoryId, long translationId);
    public long insertTranslation(long wordFrom, long wordTo);
    public long insertTranslation(long wordFrom, long wordTo, Long memoryId);
    public long insertWordsAndTranslation(Word word1, Word word2, Memory memory);
    public List<Translation> findMeanings(long wordId);
    public Memory findMemory(long memoryId);
    public List<Word> getAllWords();

    public Language getLanguage(long id);
}
