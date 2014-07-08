package com.pz.vocabulary.app.sql.ormlite;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * Created by piotr on 28/06/14.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt");
    }
}
