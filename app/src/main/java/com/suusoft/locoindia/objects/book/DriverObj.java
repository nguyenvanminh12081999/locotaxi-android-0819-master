package com.suusoft.locoindia.objects.book;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.suusoft.locoindia.R;


import java.util.ArrayList;

/**
 * Created by SuuSoft.com on 12/01/2016.
 */

public class DriverObj implements Parcelable {
    public static final int DRIVER_ACTIVED = 1; // value =1: actived, value =0 unactive;
    public static final int DRIVER_AVAILABLE = 1; // value =1: actived, value =0 unactive;
    public static final int DRIVER_UNAVAILABLE = 0; // value =1: actived, value =0 unactive;
    private LatLng latLng;
    private String driver_license, online_started, avatar;
    private float fare;
    private int user_id, driver_experience, online_duration, is_active, is_online;
    private boolean isDelivery;
    private int is_delivery;
    private String fare_type;
    private ServiceObj serviceObj;


    public DriverObj() {
    }

    private String id, name, phone, email, lat, lng;
    private double rate, rate_count;
    private String type;
    private double distance, duration, estimate_fare;


    protected DriverObj(Parcel in) {
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        driver_license = in.readString();
        online_started = in.readString();
        avatar = in.readString();
        fare = in.readFloat();
        user_id = in.readInt();
        driver_experience = in.readInt();
        online_duration = in.readInt();
        is_active = in.readInt();
        is_online = in.readInt();
        isDelivery = in.readByte() != 0;
        is_delivery = in.readInt();
        fare_type = in.readString();
        serviceObj = in.readParcelable(ServiceObj.class.getClassLoader());
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        lat = in.readString();
        lng = in.readString();
        rate = in.readDouble();
        rate_count = in.readDouble();
        type = in.readString();
        distance = in.readDouble();
        duration = in.readDouble();
        estimate_fare = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(latLng, flags);
        dest.writeString(driver_license);
        dest.writeString(online_started);
        dest.writeString(avatar);
        dest.writeFloat(fare);
        dest.writeInt(user_id);
        dest.writeInt(driver_experience);
        dest.writeInt(online_duration);
        dest.writeInt(is_active);
        dest.writeInt(is_online);
        dest.writeByte((byte) (isDelivery ? 1 : 0));
        dest.writeInt(is_delivery);
        dest.writeString(fare_type);
        dest.writeParcelable(serviceObj, flags);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeDouble(rate);
        dest.writeDouble(rate_count);
        dest.writeString(type);
        dest.writeDouble(distance);
        dest.writeDouble(duration);
        dest.writeDouble(estimate_fare);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DriverObj> CREATOR = new Creator<DriverObj>() {
        @Override
        public DriverObj createFromParcel(Parcel in) {
            return new DriverObj(in);
        }

        @Override
        public DriverObj[] newArray(int size) {
            return new DriverObj[size];
        }
    };

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getRate_count() {
        return rate_count;
    }

    public void setRate_count(double rate_count) {
        this.rate_count = rate_count;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getEstimate_fare() {
        return estimate_fare;
    }

    public void setEstimate_fare(int estimate_fare) {
        this.estimate_fare = estimate_fare;
    }

    public DriverObj(LatLng latLng,
                     String driver_license,
                     String online_started,
                     float fare, int user_id,
                     int driver_experience,
                     int online_duration,
                     int is_active,
                     int is_online,
                     String type,
                     String fare_type,
                     ServiceObj serviceObj) {
        this.latLng = latLng;
        this.driver_license = driver_license;
        this.online_started = online_started;
        this.fare = fare;
        this.user_id = user_id;
        this.driver_experience = driver_experience;
        this.online_duration = online_duration;
        this.is_active = is_active;
        this.is_online = is_online;
        this.isDelivery = isDelivery;
        this.is_delivery = is_delivery;
        this.type = type;
        this.fare_type = fare_type;
        this.serviceObj = serviceObj;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getFare() {
        return fare;
    }

    public void setFare(float fare) {
        this.fare = fare;
    }

    public boolean isAvailable() {
        return is_online == DRIVER_AVAILABLE;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public boolean isActive() {
        return getIs_active() == DRIVER_ACTIVED;
    }

    public void setAvailable(int available) {
        is_online = available;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
    }

    public ServiceObj getServiceObj() {
        return serviceObj;
    }

    public void setServiceObj(ServiceObj serviceObj) {
        this.serviceObj = serviceObj;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getDriver_license() {
        return driver_license;
    }

    public void setDriver_license(String driver_license) {
        this.driver_license = driver_license;
    }

    public int getIs_delivery() {
        return is_delivery;
    }

    public void setIs_delivery(int is_delivery) {
        this.is_delivery = is_delivery;
    }

    public String getFare_type() {
        return fare_type;
    }

    public void setFare_type(String fare_type) {
        this.fare_type = fare_type;
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getDriver_experience() {
        return driver_experience;
    }

    public void setDriver_experience(int driver_experience) {
        this.driver_experience = driver_experience;
    }

    public int getOnline_duration() {
        return online_duration;
    }

    public void setOnline_duration(int online_duration) {
        this.online_duration = online_duration;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public static ArrayList<DriverObj> getListDriver(Context context, Place place){
        ArrayList<DriverObj> driverObjs = new ArrayList<>();
        LatLng latLng;
        double i = 0.0012345;
        if (place!=null)
            latLng = place.getLatLng();
        else
            latLng = new LatLng(11.568921, 104.9192803);
        latLng = new LatLng(latLng.latitude + 0, latLng.longitude +0);
        ServiceObj serviceObj = new ServiceObj(ServiceObj.CLASSIC,context. getString(R.string.classic), R.drawable.ic_classic);
        serviceObj.setMinimumFare(5);
        serviceObj.setFlagDownFee(2);
        serviceObj.setPerson(4);
        serviceObj.setPriceKilomet(2);
        serviceObj.setPriceMinute(0);
        DriverObj driverObj = new DriverObj(latLng, "","", 3f, 2,
                2,
                2,
                1,
                1,
                "",
                "",
                serviceObj );
        driverObjs.add(driverObj);

        //////////////

        latLng = new LatLng(latLng.latitude + i, latLng.longitude +0);
        serviceObj = new ServiceObj(ServiceObj.RICKSHAW, context. getString(R.string.richshaw), R.drawable.ic_rickshaw);
        serviceObj.setMinimumFare(4);
        serviceObj.setFlagDownFee(2);
        serviceObj.setPerson(3);
        serviceObj.setPriceKilomet(2);
        serviceObj.setPriceMinute(0);
        driverObj = new DriverObj(latLng, "","", 3f, 2,
                2,
                2,
                1,
                1,
                "",
                "",
                serviceObj );
        driverObjs.add(driverObj);

        //////////////

        latLng = new LatLng(latLng.latitude + 0, latLng.longitude +i);
        serviceObj = new ServiceObj(ServiceObj.SUV, context. getString(R.string.suv), R.drawable.ic_suv);
        serviceObj.setMinimumFare(3);
        serviceObj.setFlagDownFee(5);
        serviceObj.setPerson(3);
        serviceObj.setPriceKilomet(2);
        serviceObj.setPriceMinute(0);
        driverObj = new DriverObj(latLng, "","", 3f, 2,
                2,
                2,
                1,
                1,
                "",
                "",
                serviceObj );
        driverObjs.add(driverObj);

        //////////////

        latLng = new LatLng(latLng.latitude + i, latLng.longitude +0);
        serviceObj = new ServiceObj(ServiceObj.TUK_TUK, context. getString(R.string.khmer_tuk_tuk), R.drawable.tuktuk);
        serviceObj.setMinimumFare(5);
        serviceObj.setFlagDownFee(4);
        serviceObj.setPerson(4);
        serviceObj.setPriceKilomet(2);
        serviceObj.setPriceMinute(0);
        driverObj = new DriverObj(latLng, "","", 3f, 2,
                2,
                2,
                1,
                1,
                "",
                "",
                serviceObj );
        driverObjs.add(driverObj);


        //////////////

        latLng = new LatLng(latLng.latitude + 0, latLng.longitude +i);
        serviceObj = new ServiceObj(ServiceObj.TUK_TUK, context. getString(R.string.khmer_tuk_tuk), R.drawable.tuktuk);
        serviceObj.setMinimumFare(5);
        serviceObj.setFlagDownFee(4);
        serviceObj.setPerson(4);
        serviceObj.setPriceKilomet(2);
        serviceObj.setPriceMinute(0);
        driverObj = new DriverObj(latLng, "","", 3f, 2,
                2,
                2,
                1,
                1,
                "",
                "",
                serviceObj );
        driverObjs.add(driverObj);

        //////////////

        latLng = new LatLng(latLng.latitude + i, latLng.longitude +0);
        serviceObj = new ServiceObj(ServiceObj.TUK_TUK, context. getString(R.string.khmer_tuk_tuk), R.drawable.tuktuk);
        serviceObj.setMinimumFare(5);
        serviceObj.setFlagDownFee(4);
        serviceObj.setPerson(4);
        serviceObj.setPriceKilomet(2);
        serviceObj.setPriceMinute(0);
        driverObj = new DriverObj(latLng, "","", 3f, 2,
                2,
                2,
                1,
                1,
                "",
                "",
                serviceObj );
        driverObjs.add(driverObj);

        return driverObjs;
    }

}
