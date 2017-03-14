/*
 * Copyright (c) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rkr.binatestation.dreammanager.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import rkr.binatestation.dreammanager.BuildConfig;


/**
 * DreamManagerContract represents the contract for storing data in the SQLite database.
 */
public final class DreamManagerContract {
    // The content paths.
    static final String PATH_DREAM_LIST = "dream_list";
    // The name for the entire content provider.
    private static final String CONTENT_AUTHORITY = "rkr.bharathi.kineticinsight";
    private static final String CONTENT_AUTHORITY_DEBUG = CONTENT_AUTHORITY;
    private static final String CREATE_TABLE = " CREATE TABLE ";
    /**
     * INT,
     * INTEGER,
     * TINYINT,
     * SMALLINT,
     * MEDIUMINT,
     * BIGINT,
     * UNSIGNED BIG INT,
     * INT2,
     * INT8
     */
    private static final String INTEGER = " INTEGER ";
    /**
     * CHARACTER(20),
     * VARCHAR(255),
     * VARYING CHARACTER(255),
     * NCHAR(55),
     * NATIVE CHARACTER(70),
     * NVARCHAR(100),
     * TEXT,
     * CLOB
     */
    private static final String TEXT = " TEXT ";
    /**
     * REAL,
     * DOUBLE,
     * DOUBLE PRECISION,
     * FLOAT
     */
    private static final String REAL = " REAL ";
    /**
     * BLOB
     * no data type specified
     */
    private static final String NONE = " NONE ";
    /**
     * NUMERIC,
     * DECIMAL(10,5),
     * BOOLEAN,
     * DATE
     * DATETIME
     */
    private static final String NUMERIC = " NUMERIC ";
    private static final String PRIMARY_KEY = " PRIMARY KEY ";
    private static final String AUTOINCREMENT = " AUTOINCREMENT ";
    private static final String UNIQUE = " UNIQUE ";
    private static final String NOT_NULL = " NOT NULL ";
    private static final String ON_CONFLICT_REPLACE = " ON CONFLICT REPLACE ";
    private static final String COMMA = " , ";
    private static final String OPEN_PARENTHESIS = " ( ";
    private static final String CLOSE_PARENTHESIS = " ) ";
    private static final String SEMI_COLON = " ; ";
    // Base of all URIs that  will be used to contact the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + getContentAuthority());

    static String getContentAuthority() {
        if (BuildConfig.DEBUG) {
            return CONTENT_AUTHORITY_DEBUG;
        } else {
            return CONTENT_AUTHORITY;
        }
    }

    public static final class DreamListTable implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DREAM_LIST).build();
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ACHIEVE_DATE = "achieve_date";
        public static final String COLUMN_TARGET_AMOUNT = "target_amount";
        public static final String COLUMN_PER_MONTH_AMOUNT = "per_month_amount";
        public static final String COLUMN_IMAGE_URI = "image_uri";
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + getContentAuthority() + "." + PATH_DREAM_LIST;
        // Name of the workout temp table.
        static final String TABLE_NAME = PATH_DREAM_LIST;
        static final String SQL_CREATE_TABLE = CREATE_TABLE + TABLE_NAME + OPEN_PARENTHESIS +
                _ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA +
                COLUMN_NAME + TEXT + COMMA +
                COLUMN_ACHIEVE_DATE + INTEGER + COMMA +
                COLUMN_TARGET_AMOUNT + REAL + COMMA +
                COLUMN_PER_MONTH_AMOUNT + REAL + COMMA +
                COLUMN_IMAGE_URI + TEXT +
                CLOSE_PARENTHESIS + SEMI_COLON;

        // Returns the Uri referencing a workout temp with the specified id.
        static Uri buildUriWithAppendedId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
