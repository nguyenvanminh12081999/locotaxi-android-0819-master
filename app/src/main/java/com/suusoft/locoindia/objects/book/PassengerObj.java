package com.suusoft.locoindia.objects.book;

import android.os.Parcel;
import android.os.Parcelable;

public class PassengerObj implements Parcelable {

    private String passengerId;
    private String passengerAvatar;
    private String passengerName;
    private String passengerRate;
    private String passengerRateCount;
    private String passengerPhone;

    public PassengerObj() {
    }

    //    private String passengerVisible;
//    private String passengerConfirm;
//    private String passengerRated;
//    private String passengerFinished;


    protected PassengerObj(Parcel in) {
        passengerId = in.readString();
        passengerAvatar = in.readString();
        passengerName = in.readString();
        passengerRate = in.readString();
        passengerRateCount = in.readString();
        passengerPhone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(passengerId);
        dest.writeString(passengerAvatar);
        dest.writeString(passengerName);
        dest.writeString(passengerRate);
        dest.writeString(passengerRateCount);
        dest.writeString(passengerPhone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PassengerObj> CREATOR = new Creator<PassengerObj>() {
        @Override
        public PassengerObj createFromParcel(Parcel in) {
            return new PassengerObj(in);
        }

        @Override
        public PassengerObj[] newArray(int size) {
            return new PassengerObj[size];
        }
    };

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerAvatar() {
        return passengerAvatar;
    }

    public void setPassengerAvatar(String passengerAvatar) {
        this.passengerAvatar = passengerAvatar;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerRate() {
        return passengerRate;
    }

    public void setPassengerRate(String passengerRate) {
        this.passengerRate = passengerRate;
    }

    public String getPassengerRateCount() {
        return passengerRateCount;
    }

    public void setPassengerRateCount(String passengerRateCount) {
        this.passengerRateCount = passengerRateCount;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }
}
