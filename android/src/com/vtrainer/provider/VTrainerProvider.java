package com.vtrainer.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.vtrainer.logging.Logger;
import com.vtrainer.utils.Constants;

public class VTrainerProvider extends ContentProvider {
    private static final String TAG = "VTrainerProvider";

    // provide a mechanism to identify all uri patterns
    private static final UriMatcher uriMatcher;

    private static final int WORDS_URI_INDICATOR = 1;
    private static final int PROPOSAL_WORDS_URI_INDICATOR = 2;
    private static final int TRAINING_WORD_URI_INDICATOR = 3;
//    private static final int TRAINING_COUNT_URI_INDICATOR = 4;
    private static final int ADD_CAT_TO_TRAINING_URI_INDICATOR = 5;
    private static final int VOCABULARY_URI_INDICATOR = 6;
    private static final int TARGET_LANGUAGE_CHANGED_URI_INDICATOR = 7;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, VocabularyMetaData.WORDS_PATH, WORDS_URI_INDICATOR);
        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, VocabularyMetaData.PROPOSAL_WORDS_PATH + "/#", PROPOSAL_WORDS_URI_INDICATOR);

        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, TrainingMetaData.TRAINING_WORD_PATH, TRAINING_WORD_URI_INDICATOR);
//        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, TrainingMetaData.TABLE_NAME + "/count", TRAINING_COUNT_URI_INDICATOR);

        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, VocabularyMetaData.ADD_CATEGORY_TO_TRAINING_PATH, ADD_CAT_TO_TRAINING_URI_INDICATOR);

        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, VocabularyMetaData.VOCABULARY_PATH + "/#", VOCABULARY_URI_INDICATOR);

        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, Constants.TARGET_LANGUAGE_CHANGED_PATH, TARGET_LANGUAGE_CHANGED_URI_INDICATOR);
    }

    private VTrainerDatabase vtrainerDatabase;

    @Override
    public boolean onCreate() {
        vtrainerDatabase = new VTrainerDatabase(getContext());

        return true;
    }

/*
    public Cursor getCountWordAvalaibleToTraining(String type) {
        return dbHelper.getReadableDatabase().rawQuery(
            "SELECT COUNT(*) FROM " + TrainingMetaData.TABLE_NAME + " WHERE " + TrainingMetaData.TYPE + " = " + type, null); //TODO add where by time
        //TODO move SQL to SQLBuilder
    }
*/
    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        Cursor cursor = null;
        Logger.debug(TAG, uri.toString());
        switch (uriMatcher.match(uri)) {
        case WORDS_URI_INDICATOR:
            cursor = vtrainerDatabase.getWords(projection, selection, selectionArgs, sortOrder, null);
            break;
        case PROPOSAL_WORDS_URI_INDICATOR:
            cursor = vtrainerDatabase.getWords(projection, selection, selectionArgs, sortOrder, uri.getPathSegments().get(1));
            break;
        case TRAINING_WORD_URI_INDICATOR:
            cursor = vtrainerDatabase.getTrainingWord(uri.getPathSegments().get(1), projection, selection, selectionArgs, sortOrder);
            break;
        case VOCABULARY_URI_INDICATOR:
            cursor = vtrainerDatabase.getWords(uri.getPathSegments().get(1), projection);
            break;
//        case TRAINING_COUNT_URI_INDICATOR:
//            return getCountWordAvalaibleToTraining(uri.getPathSegments().get(1));
        default:
            String msg = "Unknown URI" + uri;
            Logger.error(TAG, msg, getContext());
            return null;
        }

        // tell the cursor what uri to watch so it knows when its source data changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int delete(final Uri arg0, final String arg1, final String[] arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(final Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        switch (uriMatcher.match(uri)) {
        case WORDS_URI_INDICATOR:
            return vtrainerDatabase.addNewWord(uri, values);
        case ADD_CAT_TO_TRAINING_URI_INDICATOR:
            return vtrainerDatabase.addCategoryToTrain(uri, values);
        case TRAINING_WORD_URI_INDICATOR:
            if (vtrainerDatabase.addWordToTrain(values.getAsLong(TrainingMetaData.WORD_ID)) == 1) { // TODO update
                return uri;
            } else {
                return null;
            }
        default:
            Logger.error(TAG, "Unknown URI " + uri, getContext());
            return null;
        }
    }

    @Override
    public int bulkInsert(final Uri uri, final ContentValues[] values) {
        switch (uriMatcher.match(uri)) {
        case TRAINING_WORD_URI_INDICATOR:
            return vtrainerDatabase.addWordsToTrain(uri, values);
        default:
            Logger.error(TAG, "Unknown URI " + uri, getContext());
            return 0;
        }
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
        case TRAINING_WORD_URI_INDICATOR:
            count = vtrainerDatabase.updateTrainingData(values, selection, selectionArgs);
            break;
        case TARGET_LANGUAGE_CHANGED_URI_INDICATOR:
            vtrainerDatabase.updateStaticContent(selectionArgs[0]);
            return 0;
        default:
            Logger.error(TAG, "Unknown URI " + uri, getContext());
            return 0;
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
   }
}