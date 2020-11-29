package com.gift.project.gifttaxi.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.gift.project.gifttaxi.R;
import com.google.android.material.textfield.TextInputEditText;

public class AddressSearchFragment extends Fragment {
    private View currentView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_address_search, container, false);
        fragmentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        this.currentView = fragmentView;
        TextInputEditText sourceTextInput = fragmentView.findViewById(R.id.source_input);
        sourceTextInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                expandLayout(view);
            }
        });
        return fragmentView;
    }


    public void expandLayout(View clicked) {
        LinearLayout.LayoutParams mapParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mapParams.weight = 10;
        View mapView = this.getView().getRootView().findViewById(R.id.map_view);
        mapView.setLayoutParams(mapParams);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.TOP;
        params.weight = 0;
        this.currentView.setLayoutParams(params);
    }
}
