package com.suusoft.locoindia.objects;

/**
 * Created by SuuSoft.com on 11/09/2016.
 */
public class DealCateObj {

    public static final String MY_FAVORITES = "1";
    public static final String TRANSPORT = "2";
    public static final String FOOD_AND_BEVERAGES = "3";
    public static final String LABOR = "4";
    public static final String TRAVEL = "5";
    public static final String SHOPPING = "6";
    public static final String NEWS_AND_EVENTS = "7";
    public static final String OTHER = "8";

    public static final String All = "9";

    private String id, name, description;

    public DealCateObj(String id, String name){
        this.id = id;
        this.name = name;
        this.description = "";
    }

    public DealCateObj(String id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return super.toString();
    }


}
