package com.suusoft.locoindia.objects.orther;

import java.util.List;

public class RouteObject {

    private List<LegsObject> legs;

    public RouteObject(List<LegsObject> legs) {
        this.legs = legs;
    }

    public List<LegsObject> getLegs() {
        return legs;
    }
}
