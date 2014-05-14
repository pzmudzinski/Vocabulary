package com.pz.vocabulary.app.models;

import java.io.Serializable;

/**
 * Created by piotr on 27.04.2014.
 */
public interface GenericDAO <T, PK extends Serializable>{
    /** Persist the newInstance object into database */
    PK create(T newInstance);

    /** Retrieve an object that was previously persisted to the database using
     *   the indicated id as primary key
     */
    T read(PK id);

    /** Save changes made to a persistent object.  */
    void update(T transientObject);

    /** Remove an object from persistent storage in the database */
    void delete(T persistentObject);
}
