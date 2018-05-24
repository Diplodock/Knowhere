package com.example.user.Knowhere;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private static final int REQUEST_CODE = 1000;

    private MapFragment mapFragment;
    private ExploreFragment exploreFragment;
    private AboutFragment aboutFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mMainFrame = findViewById(R.id.main_frame);
        mMainNav = findViewById(R.id.main_nav);

        mapFragment = new MapFragment();
        exploreFragment = new ExploreFragment();
        aboutFragment = new AboutFragment();

        setFragment(mapFragment);


        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_map:
                        displayFragmentMap();
                        return true;

                    case R.id.nav_explore:
                        exploreFragment.onDetach();
                        displayFragmentExplore();
                        return true;

                    case R.id.nav_profile:
                        displayFragmentAbout();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    protected void displayFragmentMap() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (mapFragment.isAdded()) {
            ft.show(mapFragment);
        } else {
            ft.add(R.id.main_frame, mapFragment, "A");
        }

        if (exploreFragment.isAdded()) { ft.hide(exploreFragment); }

        if (aboutFragment.isAdded()) { ft.hide(aboutFragment); }

        ft.commit();
    }

    protected void displayFragmentExplore() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (exploreFragment.isAdded()) {
            ft.show(exploreFragment);
            ft.detach(exploreFragment);
            ft.attach(exploreFragment);
        } else {
            ft.add(R.id.main_frame, exploreFragment, "A");
            ft.detach(exploreFragment);
            ft.attach(exploreFragment);
        }

        if (mapFragment.isAdded()) { ft.hide(mapFragment); }

        if (aboutFragment.isAdded()) { ft.hide(aboutFragment); }

        ft.commit();
    }

    protected void displayFragmentAbout() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (aboutFragment.isAdded()) {
            ft.show(aboutFragment);
        } else {
            ft.add(R.id.main_frame, aboutFragment, "A");
        }

        if (mapFragment.isAdded()) { ft.hide(mapFragment); }

        if (exploreFragment.isAdded()) { ft.hide(exploreFragment); }

        ft.commit();
    }
}
