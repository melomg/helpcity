package com.projects.melih.helpcity.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.projects.melih.helpcity.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by melih on 4.12.2017.
 */

public class Vote implements Parcelable {
    public boolean isAdded;

    private BitmapDescriptor markerPin;

    private String address;

    private double latitude;

    private double longitude;

    private int voteType;

    private Marker marker;

    public BitmapDescriptor getMarkerPin() {
        return markerPin;
    }

    @DrawableRes
    public int getMarkerPinDrawable() {
        @VoteType.ID int id = voteType;
        switch (id) {
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
            default: {
                return R.drawable.emoji3;
            }
        }
    }

    public void setMarkerPin(@NonNull BitmapDescriptor bitmapDescriptor) {
        markerPin = bitmapDescriptor;
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

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.voteType);
    }

    protected Vote(Parcel in) {
        this.address = in.readString();
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
        int EXTREME_SAD = 0;
        int SAD = EXTREME_SAD + 1;
        int NORMAL = SAD + 1;
        int HAPPY = NORMAL + 1;
        int EXTREME_HAPPY = HAPPY + 1;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({
                EXTREME_SAD,
                SAD,
                NORMAL,
                HAPPY,
                EXTREME_HAPPY
        })
        @interface ID {
        }
    }
}