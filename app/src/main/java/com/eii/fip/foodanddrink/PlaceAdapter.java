package com.eii.fip.foodanddrink;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by jonathan on 06/11/14.
 */
public class PlaceAdapter extends ArrayAdapter<Place> {
    public Context context;
    public int layoutResourceId;
    public ArrayList<Place> places;

    public PlaceAdapter(Context context, int layoutResourceId, ArrayList<Place> places) {
        super(context, layoutResourceId, places);
        this.layoutResourceId = layoutResourceId;
        this.places = places;
    }


}