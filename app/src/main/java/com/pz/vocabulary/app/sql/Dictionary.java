package com.pz.vocabulary.app.sql;

import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by piotr on 05/06/14.
 */
public interface Dictionary extends QuizHistory{

    public boolean hasItems(Class clz);
    public long numberOfItems(Class clz);

    public long insertWord(Word word);
    public long findWord(long langID, String spelling);
    public Word findWord(long id);
    public boolean deleteWord(long wordID);
    public List<Word> getAllWords();
    public List<Word> findWords(long languageId);
    public Map<Language, List<Word>> getWordsByLanguage();
    public List<Word> getWordsInsertedSince(Date date);

    public void addMemoryToTranslation(long memoryId, long translationId);
    public long insertTranslation(long wordFrom, long wordTo);
    public long insertTranslation(long wordFrom, long wordTo, Long memoryId);
    public void deleteTranslation(long wordFrom, long wordTo);
    public long insertWordsAndTranslation(Word word1, Word word2, Memory memory);
    public Translation findTranslation(long wordFrom, long wordTo);
    public List<Translation> findMeanings(long wordId);

    public Memory findMemory(long memoryId);
    public long insertMemory(Memory memory);

    public Language findLanguage(long id);
    public List<Language> getLanguages();

    public void close();

    List<Memory> getAllMemories();

    public void destroyEverything();

    public Date getInsertionDate(long wordID);


    public void addLanguages(String language, String language1);
}
