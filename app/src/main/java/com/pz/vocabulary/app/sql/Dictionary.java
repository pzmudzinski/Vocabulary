package com.pz.vocabulary.app.sql;

import com.pz.vocabulary.app.models.db.Language;
import com.pz.vocabulary.app.models.db.Memory;
import com.pz.vocabulary.app.models.db.QuizResponse;
import com.pz.vocabulary.app.models.db.Translation;
import com.pz.vocabulary.app.models.db.Word;

import java.util.List;
import java.util.Map;

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
    public long insertMemory(Memory memory);
    public List<Word> getAllWords();

    public Language findLanguage(long id);
    public List<Language> getLanguages();

    public List<Word> findWords(long languageId);
    public Map<Language, List<Word>> getWordsByLanguage();

    public long insertResponse(long wordFrom, String response, QuizQuestionResult result);
    public List<QuizResponse> findResponsesWithResult(QuizQuestionResult result);

    public enum QuizQuestionResult
    {
        ResponseCorrect,
        ResponseWrong,
        ResponseSkipped
    }

    public void close();

}
