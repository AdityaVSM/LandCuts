package com.example.landcuts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.landcuts.Models.Land;
import com.example.landcuts.R;

import java.util.ArrayList;

public class LandViewAdapter extends ArrayAdapter<Land> {

    public LandViewAdapter(@NonNull Context context, ArrayList<Land> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_land_info_card, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Land currentLandPosition = getItem(position);

        assert currentLandPosition != null;

        TextView land_name = currentItemView.findViewById(R.id.land_name);
        TextView location = currentItemView.findViewById(R.id.location);
        TextView price = currentItemView.findViewById(R.id.price);
        TextView available_cuts = currentItemView.findViewById(R.id.no_of_available_cuts);


        land_name.setText(currentLandPosition.getName());
        location.setText(currentLandPosition.getLocation());
        price.setText(String.valueOf(currentLandPosition.getPrice()));
        available_cuts.setText(String.valueOf(currentLandPosition.getNo_of_available_cuts()));

        // then return the recyclable view
        return currentItemView;
    }

}
