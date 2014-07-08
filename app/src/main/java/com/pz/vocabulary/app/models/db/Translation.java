package com.pz.vocabulary.app.models.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pz.vocabulary.app.sql.DBColumns;
import com.pz.vocabulary.app.sql.DatabaseTables;

import java.util.Date;

/**
 * Created by piotr on 04/06/14.
 */
@DatabaseTable(tableName = DatabaseTables.TABLE_TRANSLATIONS)
public class Translation extends BaseEntity {

    @DatabaseField(foreign = true, foreignColumnName = DBColumns.ID, columnName = DBColumns.WORD_TO)
    public Word wordTo;

    @DatabaseField(foreign = true, foreignColumnName = DBColumns.ID)
    public Memory memory;

    @DatabaseField(columnName = DBColumns.WORD_FROM, foreign = true, foreignColumnName = DBColumns.ID)
    public Word wordFrom;

    @DatabaseField(version = true, columnName = DBColumns.TIMESTAMP, dataType = DataType.DATE_LONG)
    private Date timestamp;

    public Translation()
    {
        super();
    }
    
    public Translation(long id, Word wordTo, Memory memory)
    {
        super(id);
        this.wordTo = wordTo;
        this.memory = memory;
    }

    public Translation(Word wordFrom, Word wordTo, Memory memory)
    {
        super();
        this.wordFrom = wordFrom;
        this.wordTo = wordTo;
        this.memory = memory;
    }

    public Translation(long wordFrom, long wordTo) {
           this(wordFrom,wordTo, null);
    }

    public Translation(long wordFrom, long wordTo, Long memoryId) {
        Word wordToObject = new Word();
        wordToObject.setId(wordTo);
        Word wordFromObject = new Word();
        wordFromObject.setId(wordFrom);

        this.wordTo = wordToObject;
        this.wordFrom = wordFromObject;
        if (memoryId != null)
        {
            Memory memory = new Memory();
            memory.setId(memoryId);
            this.memory = memory;
        }
    }

    public Memory getMemory()
    {
        return memory;
    }

    public long getWordTo()
    {
        return wordTo.getId();
    }

    public Word getMeaning()
    {
        return wordFrom;
    }

    public Word getTranslation()
    {
        return wordTo;
    }

    public Date getTimestamp() { return timestamp; }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void swap()
    {
        Word wordFrom = this.wordFrom;
        this.wordFrom = this.wordTo;
        this.wordTo = wordFrom;
    }

}
