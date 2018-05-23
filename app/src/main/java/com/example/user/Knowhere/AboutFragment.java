package com.example.user.Knowhere;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import static com.example.user.Knowhere.LocationsDB.DBNAME;


public class AboutFragment extends Fragment {
    private Button button;

    public AboutFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        button = v.findViewById(R.id.btbdclear);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getApplicationContext().deleteDatabase(DBNAME);
                Toast.makeText(getActivity().getApplicationContext(),"Database is clear \n Restart the app", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

}
