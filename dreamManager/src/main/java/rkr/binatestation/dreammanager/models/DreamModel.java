package rkr.binatestation.dreammanager.models;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import static rkr.binatestation.dreammanager.database.DreamManagerContract.DreamListTable.COLUMN_ACHIEVE_DATE;
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
    private int id;
    private String name;
    private long achieveDate;
    private double targetAmount;
    private double perMonthAmount;
    private Uri imageUri;

    public DreamModel(String name, long achieveDate, double targetAmount, double perMonthAmount, Uri imageUri) {
        this.name = name;
        this.achieveDate = achieveDate;
        this.targetAmount = targetAmount;
        this.perMonthAmount = perMonthAmount;
        this.imageUri = imageUri;
    }

    private DreamModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        achieveDate = in.readLong();
        targetAmount = in.readDouble();
        perMonthAmount = in.readDouble();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
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
        contentValues.put(COLUMN_ACHIEVE_DATE, dreamModel.getAchieveDate());
        contentValues.put(COLUMN_TARGET_AMOUNT, dreamModel.getTargetAmount());
        contentValues.put(COLUMN_PER_MONTH_AMOUNT, dreamModel.getPerMonthAmount());
        contentValues.put(COLUMN_IMAGE_URI, dreamModel.getImageUri().toString());
        Log.d(TAG, "getContentValues() returned: " + contentValues);
        return contentValues;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeLong(achieveDate);
        out.writeDouble(targetAmount);
        out.writeDouble(perMonthAmount);
        out.writeParcelable(imageUri, flags);
    }
}