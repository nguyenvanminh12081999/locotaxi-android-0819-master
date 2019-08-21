package com.suusoft.locoindia.objects.book;

import android.os.Parcel;
import android.os.Parcelable;


import com.suusoft.locoindia.R;

import java.util.ArrayList;

import static com.suusoft.locoindia.utils.ResourceUtils.getString;

/**
 * Created by SuuSoft.com on 11/29/2016.
 */

public class ServiceObj implements Parcelable {

    // This constants MUST be match with server
    public static final String ALL = "all";
    public static final String SUV = "suv";
    public static final String CLASSIC = "classic";
    public static final String RICKSHAW = "rickshaw";
    public static final String TUK_TUK = "tuk_tuk";

    //khong dung den nua
    public static final String DELIVERY = "delivery";

    private String type, name;
    private int icon;
    private double minimumFare;
    private double flagDownFee;
    private double priceMinute;
    private double priceKilomet;
    private int person;

    public ServiceObj() {
    }

    public ServiceObj(String type, String name, int icon) {
        this.type = type;
        this.name = name;
        this.icon = icon;
    }

    public ServiceObj(String type, String name) {
        this.type = type;
        this.name = name;
    }

    protected ServiceObj(Parcel in) {
        type = in.readString();
        name = in.readString();
        icon = in.readInt();
        minimumFare = in.readDouble();
        flagDownFee = in.readDouble();
        priceMinute = in.readDouble();
        priceKilomet = in.readDouble();
        person = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(name);
        dest.writeInt(icon);
        dest.writeDouble(minimumFare);
        dest.writeDouble(flagDownFee);
        dest.writeDouble(priceMinute);
        dest.writeDouble(priceKilomet);
        dest.writeInt(person);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ServiceObj> CREATOR = new Creator<ServiceObj>() {
        @Override
        public ServiceObj createFromParcel(Parcel in) {
            return new ServiceObj(in);
        }

        @Override
        public ServiceObj[] newArray(int size) {
            return new ServiceObj[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return super.toString();
    }



    public double getMinimumFare() {
        return minimumFare;
    }

    public void setMinimumFare(double minimumFare) {
        this.minimumFare = minimumFare;
    }

    public double getFlagDownFee() {
        return flagDownFee;
    }

    public void setFlagDownFee(double flagDownFee) {
        this.flagDownFee = flagDownFee;
    }

    public double getPriceMinute() {
        return priceMinute;
    }

    public void setPriceMinute(double priceMinute) {
        this.priceMinute = priceMinute;
    }

    public double getPriceKilomet() {
        return priceKilomet;
    }

    public void setPriceKilomet(double priceKilomet) {
        this.priceKilomet = priceKilomet;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public static ArrayList<ServiceObj> getListService(){
        ArrayList<ServiceObj> serviceObjs = new ArrayList<>();

        ServiceObj serviceObj = new ServiceObj(ServiceObj.CLASSIC, getString(R.string.classic), R.drawable.ic_motor);
        serviceObj.setMinimumFare(3000 );
        serviceObj.setFlagDownFee(2000);
        serviceObj.setPerson(2);
        serviceObj.setPriceKilomet(2000);
        serviceObj.setPriceMinute(200);
        serviceObjs.add(serviceObj);

        serviceObj = new ServiceObj(ServiceObj.RICKSHAW, getString(R.string.richshaw), R.drawable.ic_4cho);
        serviceObj.setMinimumFare(4000);
        serviceObj.setFlagDownFee(2000);
        serviceObj.setPerson(4);
        serviceObj.setPriceKilomet(2000);
        serviceObj.setPriceMinute(250);
        serviceObjs.add(serviceObj);

//        serviceObj = new ServiceObj(ServiceObj.SUV, getString(R.string.suv), R.drawable.ic_suv);
//        serviceObj.setMinimumFare(3000);
//        serviceObj.setFlagDownFee(5000);
//        serviceObj.setPerson(3);
//        serviceObj.setPriceKilomet(2000);
//        serviceObj.setPriceMinute(0);
//        serviceObjs.add(serviceObj);

        serviceObj = new ServiceObj(ServiceObj.TUK_TUK, getString(R.string.khmer_tuk_tuk), R.drawable.tuktuk);
        serviceObj.setMinimumFare(3000);
        serviceObj.setFlagDownFee(2000);
        serviceObj.setPerson(7);
        serviceObj.setPriceKilomet(1200);
        serviceObj.setPriceMinute(200);
        serviceObjs.add(serviceObj);

        return serviceObjs;

    }
}
