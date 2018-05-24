package com.example.user.Knowhere;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    private ListView listView;
    private StreetViewPanoramaView mStreetViewPanoramaView;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_explore, container, false);
        listView = v.findViewById(R.id.listView);
        // Inflate the layout for this fragment

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        databaseAccess.open();
        List<String> addresses = databaseAccess.getAddresses();
        databaseAccess.close();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, addresses);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String address = (String) listView.getItemAtPosition(i);
                Log.d(TAG, "onInfoWindowClick: ");
                StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
                LatLng latLng = new LatLng(getCoordinates(address).getLatitude(), getCoordinates(address).getLongitude());
                options.position(latLng);
                mStreetViewPanoramaView = new StreetViewPanoramaView(getActivity().getApplicationContext(), options);
                ViewGroup vg = (ViewGroup) v;
                final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END | Gravity.BOTTOM;
                final Button btok = new Button(getActivity().getApplicationContext());
                btok.setText("HIDE");
                ;
                btok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mStreetViewPanoramaView.animate().translationY(50000).setDuration(3000);
                        ((ViewGroup) v).removeView(btok);
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                ((ViewGroup) v).removeView(mStreetViewPanoramaView);
                                params.gravity = Gravity.START;
                            }
                        };
                        handler.postDelayed(runnable, 1000);
                    }
                });

                vg.setLayoutParams(params);
                vg.addView(mStreetViewPanoramaView);
                mStreetViewPanoramaView.onCreate(null);
                vg.addView(btok, params);
            }
        });


        return v;
    }

    protected Location getCoordinates(String addr) {
        Location targetLocation = new Location("");
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(addr, 1);
            if(addresses.size() > 0) {
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                targetLocation.setLatitude(latitude);
                targetLocation.setLongitude(longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return targetLocation;
    }
}
