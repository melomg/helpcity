package com.projects.melih.helpcity.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.Exclude;
import com.projects.melih.helpcity.FirebaseDatabaseHelper;
import com.projects.melih.helpcity.R;
import com.projects.melih.helpcity.common.DateUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by melih on 4.12.2017.
 */

public class Vote implements Parcelable {

    public boolean isAdded;

    private BitmapDescriptor markerPin;

    private String address;

    private String voteId;

    private double latitude;

    private double longitude;

    private int voteType;

    private Marker marker;

    private String date;

    private String uid;

    public BitmapDescriptor getMarkerPin() {
        return markerPin;
    }

    public Vote(int voteType, double latitude, double longitude) {
        this.uid = FirebaseDatabaseHelper.getInstance().getUser().getUid();

        NumberFormat formatter = new DecimalFormat("#000.0000000");
        this.voteId = formatter.format(latitude).concat(formatter.format(longitude)).replaceAll("\\.", "").concat(uid);
        this.voteType = voteType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = DateUtil.getCurrentDate();
    }

    @DrawableRes
    public static int getMarkerPinDrawable(@VoteType.ID int rating) {
        switch (rating) {
            case VoteType.EXTREME_HAPPY: {
                return R.drawable.emoji5;
            }
            case VoteType.HAPPY: {
                return R.drawable.emoji4;
            }
            case VoteType.NORMAL: {
                return R.drawable.emoji3;
            }
            case VoteType.SAD: {
                return R.drawable.emoji2;
            }
            case VoteType.EXTREME_SAD: {
                return R.drawable.emoji1;
            }
            case VoteType.NOT_VOTED:
            default: {
                return R.drawable.vote_focussed;
            }
        }
    }

    public void setMarkerPin(@NonNull BitmapDescriptor bitmapDescriptor) {
        markerPin = bitmapDescriptor;
    }

    public String getDate() {
        return date;
    }

    public int getVoteType() {
        return voteType;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Marker getMarker() {
        return marker;
    }

    public String getUid() {
        return uid;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getVoteId() {
        return voteId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.voteId);
        dest.writeString(this.date);
        dest.writeString(this.address);
        dest.writeString(this.uid);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.voteType);
    }

    protected Vote(Parcel in) {
        this.voteId = in.readString();
        this.date = in.readString();
        this.address = in.readString();
        this.uid = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.voteType = in.readInt();
    }

    public static final Creator<Vote> CREATOR = new Creator<Vote>() {
        @Override
        public Vote createFromParcel(Parcel source) {
            return new Vote(source);
        }

        @Override
        public Vote[] newArray(int size) {
            return new Vote[size];
        }
    };

    public @interface VoteType {
        int NOT_VOTED = -1;
        int EXTREME_SAD = 0;
        int SAD = EXTREME_SAD + 1;
        int NORMAL = SAD + 1;
        int HAPPY = NORMAL + 1;
        int EXTREME_HAPPY = HAPPY + 1;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({
                NOT_VOTED,
                EXTREME_SAD,
                SAD,
                NORMAL,
                HAPPY,
                EXTREME_HAPPY
        })
        @interface ID {
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("voteId", voteId);
        result.put("uid", uid);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("voteType", voteType);
        result.put("date", date);
        return result;
    }
}