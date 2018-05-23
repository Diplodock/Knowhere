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
        if (mapFragment.isAdded()) { // if the fragment is already in container
            ft.show(mapFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.main_frame, mapFragment, "A");
        }
        // Hide fragment B
        if (exploreFragment.isAdded()) { ft.hide(exploreFragment); }
        // Hide fragment C
        if (aboutFragment.isAdded()) { ft.hide(aboutFragment); }
        // Commit changes
        ft.commit();
    }

    protected void displayFragmentExplore() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (exploreFragment.isAdded()) { // if the fragment is already in container
            ft.show(exploreFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.main_frame, exploreFragment, "A");
        }
        // Hide fragment B
        if (mapFragment.isAdded()) { ft.hide(mapFragment); }
        // Hide fragment C
        if (aboutFragment.isAdded()) { ft.hide(aboutFragment); }
        // Commit changes
        ft.commit();
    }

    protected void displayFragmentAbout() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (aboutFragment.isAdded()) { // if the fragment is already in container
            ft.show(aboutFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.main_frame, aboutFragment, "A");
        }
        // Hide fragment B
        if (mapFragment.isAdded()) { ft.hide(mapFragment); }
        // Hide fragment C
        if (exploreFragment.isAdded()) { ft.hide(exploreFragment); }
        // Commit changes
        ft.commit();
    }
}
