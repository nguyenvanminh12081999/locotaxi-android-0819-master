package com.suusoft.locoindia.objects.book;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class TripObj implements Parcelable {

    public static final String NEW = "new";
    public static final String CANCELED = "cancelled";
    public static final String FINISHED = "finished";
    public static final String ON_GOING = "processing";

    private String id;
    private double price;
    private String name;
    private String driver;
    private String service;
    private String company;
    private String status;
    private String timestamp;
    private String addressFrom, addressTo;
    private LatLng latLngFrom, latLngDestination, latLngDriver;

    //@SerializedName("passenger_finished")
    private String passengerFinished;
    //@SerializedName("passenger_id")
    private String passengerId;
    //@SerializedName("passenger_confirm")
    private String passengerConfirm;
    //@SerializedName("passenger_avatar")
    private String passengerAvatar;
    //@SerializedName("passenger_name")
    private String passengerName;
    private String passengerRate;
    private String passengerRateCount;
    //@SerializedName("passenger_phone")
    private String passengerPhone;

    private String driverId;
    private String driverType;
    private String driverName;
    private String driverEmail;
    private String driverPhone;
    //@SerializedName("driver_avatar")
    private String driverAvatar;
    private String driverFinished;
    private String created_date;
    private String modified_date;

    private double estimateDistance;
    private double estimateFare ;

    private float  rateOfDriver;
    private double  distance;
    private long  time;
    private int  is_active;
    private double  estimate_duration;
    private double  time_to_passenger;
    private ServiceObj serviceObj;
    private DriverObj driverObj;
    private Note note;





    public TripObj() {
    }

    public TripObj(String id, double price, String name, String driver, String service, String company, String status, String timestamp) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.driver = driver;
        this.service = service;
        this.company = company;
        this.status = status;
        this.timestamp = timestamp;
    }

    public TripObj(String id, String name, String status, String timestamp) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.timestamp = timestamp;
    }

    public TripObj(String id, String name, String driver, String service, String company, String status, String timestamp) {
        this.id = id;
        this.name = name;
        this.driver = driver;
        this.service = service;
        this.company = company;
        this.status = status;
        this.timestamp = timestamp;
    }


    protected TripObj(Parcel in) {
        id = in.readString();
        price = in.readDouble();
        name = in.readString();
        driver = in.readString();
        service = in.readString();
        company = in.readString();
        status = in.readString();
        timestamp = in.readString();
        addressFrom = in.readString();
        addressTo = in.readString();
        latLngFrom = in.readParcelable(LatLng.class.getClassLoader());
        latLngDestination = in.readParcelable(LatLng.class.getClassLoader());
        latLngDriver = in.readParcelable(LatLng.class.getClassLoader());
        passengerFinished = in.readString();
        passengerId = in.readString();
        passengerConfirm = in.readString();
        passengerAvatar = in.readString();
        passengerName = in.readString();
        passengerRate = in.readString();
        passengerRateCount = in.readString();
        passengerPhone = in.readString();
        driverId = in.readString();
        driverType = in.readString();
        driverName = in.readString();
        driverEmail = in.readString();
        driverPhone = in.readString();
        driverAvatar = in.readString();
        driverFinished = in.readString();
        created_date = in.readString();
        modified_date = in.readString();
        estimateDistance = in.readDouble();
        estimateFare = in.readDouble();
        rateOfDriver = in.readFloat();
        distance = in.readDouble();
        time = in.readLong();
        is_active = in.readInt();
        estimate_duration = in.readDouble();
        time_to_passenger = in.readDouble();
        serviceObj = in.readParcelable(ServiceObj.class.getClassLoader());
        driverObj = in.readParcelable(DriverObj.class.getClassLoader());
        note = in.readParcelable(Note.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(price);
        dest.writeString(name);
        dest.writeString(driver);
        dest.writeString(service);
        dest.writeString(company);
        dest.writeString(status);
        dest.writeString(timestamp);
        dest.writeString(addressFrom);
        dest.writeString(addressTo);
        dest.writeParcelable(latLngFrom, flags);
        dest.writeParcelable(latLngDestination, flags);
        dest.writeParcelable(latLngDriver, flags);
        dest.writeString(passengerFinished);
        dest.writeString(passengerId);
        dest.writeString(passengerConfirm);
        dest.writeString(passengerAvatar);
        dest.writeString(passengerName);
        dest.writeString(passengerRate);
        dest.writeString(passengerRateCount);
        dest.writeString(passengerPhone);
        dest.writeString(driverId);
        dest.writeString(driverType);
        dest.writeString(driverName);
        dest.writeString(driverEmail);
        dest.writeString(driverPhone);
        dest.writeString(driverAvatar);
        dest.writeString(driverFinished);
        dest.writeString(created_date);
        dest.writeString(modified_date);
        dest.writeDouble(estimateDistance);
        dest.writeDouble(estimateFare);
        dest.writeFloat(rateOfDriver);
        dest.writeDouble(distance);
        dest.writeLong(time);
        dest.writeInt(is_active);
        dest.writeDouble(estimate_duration);
        dest.writeDouble(time_to_passenger);
        dest.writeParcelable(serviceObj, flags);
        dest.writeParcelable(driverObj, flags);
        dest.writeParcelable(note, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripObj> CREATOR = new Creator<TripObj>() {
        @Override
        public TripObj createFromParcel(Parcel in) {
            return new TripObj(in);
        }

        @Override
        public TripObj[] newArray(int size) {
            return new TripObj[size];
        }
    };

    public LatLng getLatLngDriver() {
        return latLngDriver;
    }

    public void setLatLngDriver(LatLng latLngDriver) {
        this.latLngDriver = latLngDriver;
    }

    public String getDriverFinished() {
        return driverFinished;
    }

    public void setDriverFinished(String driverFinished) {
        this.driverFinished = driverFinished;
    }

    public String getPassengerFinished() {
        return passengerFinished;
    }

    public void setPassengerFinished(String passengerFinished) {
        this.passengerFinished = passengerFinished;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public float getRateOfDriver() {
        return rateOfDriver;
    }

    public void setRateOfDriver(float rateOfDriver) {
        this.rateOfDriver = rateOfDriver;
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


    public String getPassengerConfirm() {
        return passengerConfirm;
    }

    public void setPassengerConfirm(String passengerConfirm) {
        this.passengerConfirm = passengerConfirm;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverAvatar() {
        return driverAvatar;
    }

    public void setDriverAvatar(String driverAvatar) {
        this.driverAvatar = driverAvatar;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public double getEstimate_duration() {
        return estimate_duration;
    }

    public void setEstimate_duration(double estimate_duration) {
        this.estimate_duration = estimate_duration;
    }

    public double getTime_to_passenger() {
        return time_to_passenger;
    }

    public void setTime_to_passenger(double time_to_passenger) {
        this.time_to_passenger = time_to_passenger;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public LatLng getLatLngPickup() {
        return latLngFrom;
    }

    public void setLatLngPickup(LatLng latLngFrom) {
        this.latLngFrom = latLngFrom;
    }

    public LatLng getLatLngDestination() {
        return latLngDestination;
    }

    public void setLatLngDestination(LatLng latLngDestination) {
        this.latLngDestination = latLngDestination;
    }

    public DriverObj getDriverObj() {
        return driverObj;
    }

    public void setDriverObj(DriverObj driverObj) {
        this.driverObj = driverObj;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public ServiceObj getServiceObj() {
        return serviceObj;
    }

    public void setServiceObj(ServiceObj serviceObj) {
        this.serviceObj = serviceObj;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public double getEstimateDistance() {
        return estimateDistance;
    }

    public void setEstimateDistance(double estimateDistance) {
        this.estimateDistance = estimateDistance;
    }

    public double getEstimateFare() {
        return estimateFare;
    }

    public void setEstimateFare(double estimateFare) {
        this.estimateFare = estimateFare;
    }
}
