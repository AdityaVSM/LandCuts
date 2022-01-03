package com.example.landcuts.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.landcuts.Adapters.LandViewAdapter;
import com.example.landcuts.Models.Land;
import com.example.landcuts.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        final ArrayList<Land> arrayList = new ArrayList<>();

        // add all the values from 1 to 15 to the arrayList
        // the items are of the type NumbersView
        arrayList.add(new Land("Land1","Arizona",10000));
        arrayList.add(new Land("Land2","UK",30000));
        arrayList.add(new Land("Land3","Bengaluru",3000));
        arrayList.add(new Land("Land4","Delhi",14230));
        arrayList.add(new Land("Land5","Davanagere",901010));
        arrayList.add(new Land("Land6","California",42000));

        LandViewAdapter numbersArrayAdapter = new LandViewAdapter(getActivity().getApplicationContext(), arrayList);

        ListView diff_land_list_view = view.findViewById(R.id.diff_land_list_view);

        diff_land_list_view.setAdapter(numbersArrayAdapter);

        return view;
    }


}