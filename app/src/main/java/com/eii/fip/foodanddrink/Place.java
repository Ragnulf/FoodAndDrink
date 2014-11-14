package com.eii.fip.foodanddrink;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class Place{
    public String vicinity;
    public float[] geometry; //array(0 => lat, 1 => lng)
    public String id;
    public String name;
    public float rating;
    public String reference;
    public String[] types;

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public float[] getGeometry() {
        return geometry;
    }

    public void setGeometry(float[] geometry) {
        this.geometry = geometry;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public double GetDistance()
    {

    /*    float[] ret=geometry;

        Location.distanceBetween(Double.valueOf(),
                Double.valueOf(PreferenceManageur.getInstance().getLongitude()),
                geometry[0],geometry[1],ret);//locA.distanceBetween(locB);
        return (int)ret[1];*/
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude( Double.valueOf(PreferenceManageur.getInstance().getLatitude()));
        locationA.setLongitude( Double.valueOf(PreferenceManageur.getInstance().getLongitude()));
        Location locationB = new Location("B");
        locationB.setLatitude(geometry[0]);
        locationB.setLongitude(geometry[0]);
        distance = locationA.distanceTo(locationB);
        return distance;
    }



}