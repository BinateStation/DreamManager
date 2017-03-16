package rkr.binatestation.dreammanager.models;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static rkr.binatestation.dreammanager.database.DreamManagerContract.DreamListTable.COLUMN_ACHIEVE_DATE;
import static rkr.binatestation.dreammanager.database.DreamManagerContract.DreamListTable.COLUMN_AMOUNT_SPENT_TILL_DAY;
import static rkr.binatestation.dreammanager.database.DreamManagerContract.DreamListTable.COLUMN_CREATED_DATE;
import static rkr.binatestation.dreammanager.database.DreamManagerContract.DreamListTable.COLUMN_IMAGE_URI;
import static rkr.binatestation.dreammanager.database.DreamManagerContract.DreamListTable.COLUMN_NAME;
import static rkr.binatestation.dreammanager.database.DreamManagerContract.DreamListTable.COLUMN_PER_MONTH_AMOUNT;
import static rkr.binatestation.dreammanager.database.DreamManagerContract.DreamListTable.COLUMN_TARGET_AMOUNT;
import static rkr.binatestation.dreammanager.database.DreamManagerContract.DreamListTable.CONTENT_URI;

/**
 * Created by RKR on 14-03-2017.
 * DreamModel.
 */

public class DreamModel implements Parcelable {
    public static final Parcelable.Creator<DreamModel> CREATOR
            = new Parcelable.Creator<DreamModel>() {
        public DreamModel createFromParcel(Parcel in) {
            return new DreamModel(in);
        }

        public DreamModel[] newArray(int size) {
            return new DreamModel[size];
        }
    };
    private static final String TAG = "DreamModel";
    private long id;
    private String name = "";
    private long createdDate = new Date().getTime();
    private long achieveDate;
    private double targetAmount;
    private double perMonthAmount;
    private double amountSpentTillDay;
    private String imagePath;

    public DreamModel() {
    }

    private DreamModel(String name, long createdDate, long achieveDate, double targetAmount, double perMonthAmount, double amountSpentTillDay, String imagePath) {
        this.name = name;
        this.createdDate = createdDate;
        this.achieveDate = achieveDate;
        this.targetAmount = targetAmount;
        this.perMonthAmount = perMonthAmount;
        this.amountSpentTillDay = amountSpentTillDay;
        this.imagePath = imagePath;
    }

    private DreamModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        createdDate = in.readLong();
        achieveDate = in.readLong();
        targetAmount = in.readDouble();
        perMonthAmount = in.readDouble();
        amountSpentTillDay = in.readDouble();
        imagePath = in.readString();
    }

    public static int delete(ContentResolver contentResolver, long id) {
        Log.d(TAG, "delete() called with: contentResolver = [" + contentResolver + "], id = [" + id + "]");
        return contentResolver.delete(
                CONTENT_URI,
                _ID + " = ? ",
                new String[]{"" + id}
        );
    }

    public static long insert(ContentResolver contentResolver, DreamModel dreamModel) {
        Log.d(TAG, "insert() called with: contentResolver = [" + contentResolver + "], dreamModel = [" + dreamModel + "]");
        if (dreamModel != null && contentResolver != null) {
            Uri uri = contentResolver.insert(
                    CONTENT_URI,
                    getContentValues(dreamModel)
            );
            long insertId = ContentUris.parseId(uri);
            Log.d(TAG, "insert() returned: " + insertId);
            return insertId;
        } else {
            Log.d(TAG, "insert() returned: " + -1);
            return -1;
        }
    }

    private static ContentValues getContentValues(DreamModel dreamModel) {
        Log.d(TAG, "getContentValues() called with: dreamModel = [" + dreamModel + "]");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, dreamModel.getName());
        contentValues.put(COLUMN_CREATED_DATE, dreamModel.getCreatedDate());
        contentValues.put(COLUMN_ACHIEVE_DATE, dreamModel.getAchieveDate());
        contentValues.put(COLUMN_TARGET_AMOUNT, dreamModel.getTargetAmount());
        contentValues.put(COLUMN_PER_MONTH_AMOUNT, dreamModel.getPerMonthAmount());
        contentValues.put(COLUMN_AMOUNT_SPENT_TILL_DAY, dreamModel.getAmountSpentTillDay());
        contentValues.put(COLUMN_IMAGE_URI, dreamModel.getImagePath() != null ? dreamModel.getImagePath() : "");
        Log.d(TAG, "getContentValues() returned: " + contentValues);
        return contentValues;
    }

    public static CursorLoader getAll(Context context) {
        return new CursorLoader(
                context,
                CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    public static List<DreamModel> getAll(Cursor cursor) {
        List<DreamModel> dreamModels = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    dreamModels.add(cursorToDreamModel(cursor));
                } while (cursor.moveToNext());
            }
        }
        return dreamModels;
    }

    private static DreamModel cursorToDreamModel(Cursor cursor) {
        DreamModel dreamModel = new DreamModel(
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_DATE)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_ACHIEVE_DATE)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_TARGET_AMOUNT)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_PER_MONTH_AMOUNT)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT_SPENT_TILL_DAY)),
                cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI))
        );
        dreamModel.setId(cursor.getLong(0));
        return dreamModel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public long getAchieveDate() {
        return achieveDate;
    }

    public void setAchieveDate(long achieveDate) {
        this.achieveDate = achieveDate;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getPerMonthAmount() {
        return perMonthAmount;
    }

    public void setPerMonthAmount(double perMonthAmount) {
        this.perMonthAmount = perMonthAmount;
    }

    public double getAmountSpentTillDay() {
        return amountSpentTillDay;
    }

    public void setAmountSpentTillDay(double amountSpentTillDay) {
        this.amountSpentTillDay = amountSpentTillDay;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeLong(createdDate);
        out.writeLong(achieveDate);
        out.writeDouble(targetAmount);
        out.writeDouble(perMonthAmount);
        out.writeDouble(amountSpentTillDay);
        out.writeString(imagePath);
    }
}
