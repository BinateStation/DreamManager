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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import rkr.binatestation.dreammanager.BuildConfig;

/**
 * DreamManagerContentProvider is a ContentProvider that provides videos for the rest of applications.
 */
public class DreamManagerContentProvider extends ContentProvider {
    // These codes are returned from sUriMatcher#match when the respective Uri matches.
    private static final int DREAM_LIST = 1;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DreamManagerDB mOpenHelper;
    private ContentResolver mContentResolver;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BuildConfig.APPLICATION_ID;

        // For each type of URI to add, create a corresponding code.
        matcher.addURI(authority, DreamManagerContract.PATH_DREAM_LIST, DREAM_LIST);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mContentResolver = context != null ? context.getContentResolver() : null;
        mOpenHelper = new DreamManagerDB(context);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case DREAM_LIST:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DreamManagerContract.DreamListTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        retCursor.setNotificationUri(mContentResolver, uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            // The application is querying the db for its own contents.
            case DREAM_LIST:
                return DreamManagerContract.DreamListTable.CONTENT_TYPE;
            // We aren't sure what is being asked of us.
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final Uri returnUri;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DREAM_LIST: {
                long _id = mOpenHelper.getWritableDatabase().insertWithOnConflict(
                        DreamManagerContract.DreamListTable.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                if (_id > 0) {
                    returnUri = DreamManagerContract.DreamListTable.buildUriWithAppendedId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        mContentResolver.notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case DREAM_LIST: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        DreamManagerContract.DreamListTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsDeleted != 0) {
            mContentResolver.notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case DREAM_LIST: {
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        DreamManagerContract.DreamListTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsUpdated != 0) {
            mContentResolver.notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (sUriMatcher.match(uri)) {
            case DREAM_LIST: {
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                int returnCount = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(
                                DreamManagerContract.DreamListTable.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                mContentResolver.notifyChange(uri, null);
                return returnCount;
            }
            default: {
                return super.bulkInsert(uri, values);
            }
        }
    }
}
