package com.suusoft.locoindia.objects.book;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by SuuSoft.com on 12/05/2016.
 */

public class TripObj1 implements Parcelable {

    private static final String CANCELED = "cancelled";
    private static final String FINISHED = "finished";
    private static final String ON_GOING = "processing";

    public static final String ACTION_CREATE = "create";
    public static final String ACTION_DENY = "deny";
    public static final String ACTION_CANCEL = "cancel";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_FINISH = "finish";
    public static final String ACTION_DETAIL = "detail";

    public static final String PROCESSING = "processing";
    public static final String HISTORY = "history";

    private String id, driverId, passengerId, pickup, destination, eta, paymentMethod, paymentStatus, transportType, driverName,
            driverEmail, driverPhone, status;
    private long time;
    private LatLng latLngPickup, latLngDestination, latLngDriver;
    private float estimateFare, actualFare, rateOfDriver;
    private double distance, routeDistance;
    private int duration, routeDuration, rateQuantity, passengerQuantity;
    private boolean driverIsDelivery;
    public int estimateTime;
    public String title, note;

    public TripObj1() {
    }

    public TripObj1(String note, String startPoint, String endPoint) {
        this.note = note;
        this.pickup = startPoint;
        this.destination = endPoint;
        this.estimateTime = 6;
        this.latLngPickup = new LatLng(20.978194, 105.712023);
        this.latLngDestination = new LatLng(20.988310, 105.715962);
        this.driverId = "0";
    }

    protected TripObj1(Parcel in) {
        id = in.readString();
        driverId = in.readString();
        passengerId = in.readString();
        pickup = in.readString();
        destination = in.readString();
        eta = in.readString();
        paymentMethod = in.readString();
        paymentStatus = in.readString();
        transportType = in.readString();
        driverName = in.readString();
        driverEmail = in.readString();
        driverPhone = in.readString();
        status = in.readString();
        time = in.readLong();
        latLngPickup = in.readParcelable(LatLng.class.getClassLoader());
        latLngDestination = in.readParcelable(LatLng.class.getClassLoader());
        latLngDriver = in.readParcelable(LatLng.class.getClassLoader());
        estimateFare = in.readFloat();
        actualFare = in.readFloat();
        rateOfDriver = in.readFloat();
        distance = in.readDouble();
        routeDistance = in.readDouble();
        duration = in.readInt();
        routeDuration = in.readInt();
        rateQuantity = in.readInt();
        passengerQuantity = in.readInt();
        driverIsDelivery = in.readByte() != 0;
        estimateTime = in.readInt();
        title = in.readString();
        note = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(driverId);
        dest.writeString(passengerId);
        dest.writeString(pickup);
        dest.writeString(destination);
        dest.writeString(eta);
        dest.writeString(paymentMethod);
        dest.writeString(paymentStatus);
        dest.writeString(transportType);
        dest.writeString(driverName);
        dest.writeString(driverEmail);
        dest.writeString(driverPhone);
        dest.writeString(status);
        dest.writeLong(time);
        dest.writeParcelable(latLngPickup, flags);
        dest.writeParcelable(latLngDestination, flags);
        dest.writeParcelable(latLngDriver, flags);
        dest.writeFloat(estimateFare);
        dest.writeFloat(actualFare);
        dest.writeFloat(rateOfDriver);
        dest.writeDouble(distance);
        dest.writeDouble(routeDistance);
        dest.writeInt(duration);
        dest.writeInt(routeDuration);
        dest.writeInt(rateQuantity);
        dest.writeInt(passengerQuantity);
        dest.writeByte((byte) (driverIsDelivery ? 1 : 0));
        dest.writeInt(estimateTime);
        dest.writeString(title);
        dest.writeString(note);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripObj1> CREATOR = new Creator<TripObj1>() {
        @Override
        public TripObj1 createFromParcel(Parcel in) {
            return new TripObj1(in);
        }

        @Override
        public TripObj1[] newArray(int size) {
            return new TripObj1[size];
        }
    };

    public String getId() {
        return (id == null) ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public int getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(int estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return distance as metre
     */
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LatLng getLatLngPickup() {
        return latLngPickup;
    }

    public void setLatLngPickup(LatLng latLngPickup) {
        this.latLngPickup = latLngPickup;
    }

    public LatLng getLatLngDestination() {
        return latLngDestination;
    }

    public void setLatLngDestination(LatLng latLngDestination) {
        this.latLngDestination = latLngDestination;
    }

    public LatLng getLatLngDriver() {
        return latLngDriver;
    }

    public void setLatLngDriver(LatLng latLngDriver) {
        this.latLngDriver = latLngDriver;
    }

    public float getEstimateFare() {
        return estimateFare;
    }

    public void setEstimateFare(float estimateFare) {
        this.estimateFare = estimateFare;
    }

    public float getActualFare() {
        return actualFare;
    }

    public void setActualFare(float actualFare) {
        this.actualFare = actualFare;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public float getRateOfDriver() {
        return rateOfDriver / 2;
    }

    public void setRateOfDriver(float rateOfDriver) {
        this.rateOfDriver = rateOfDriver;
    }

    /**
     * @return duration as second
     */
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * @return how many people have rated this driver
     */
    public int getRateQuantity() {
        return rateQuantity;
    }

    public void setRateQuantity(int rateQuantity) {
        this.rateQuantity = rateQuantity;
    }

    public int getPassengerQuantity() {
        return passengerQuantity;
    }

    public void setPassengerQuantity(int passengerQuantity) {
        this.passengerQuantity = passengerQuantity;
    }

    public boolean isFinished() {
        return getStatus().equalsIgnoreCase(FINISHED);
    }

    public boolean isCanceled() {
        return getStatus().equalsIgnoreCase(CANCELED);
    }

    public boolean isOnGoing() {
        return getStatus().equalsIgnoreCase(ON_GOING);
    }

    public boolean isPaid() {
        return getPaymentStatus().equalsIgnoreCase("1");
    }

    public boolean driverIsDelivery() {
        return driverIsDelivery;
    }

    public void setDriverIsDelivery(boolean driverIsDelivery) {
        this.driverIsDelivery = driverIsDelivery;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public double getRouteDistance() {
        return routeDistance;
    }

    public void setRouteDistance(double routeDistance) {
        this.routeDistance = routeDistance;
    }

    public int getRouteDuration() {
        return routeDuration;
    }

    public void setRouteDuration(int routeDuration) {
        this.routeDuration = routeDuration;
    }

    @Override
    public String toString() {
        return super.toString();
    }





}
