package com.fokakefir.musicplayer.gui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.fragment.AlbumsFragment;
import com.fokakefir.musicplayer.gui.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private SearchFragment searchFragment;
    private AlbumsFragment albumsFragment;

    private BottomNavigationView bottomNav;

    // endregion

    // region 2. Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.searchFragment = new SearchFragment();
        this.albumsFragment = new AlbumsFragment();

        this.bottomNav = findViewById(R.id.bottom_navigation);
        this.bottomNav.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, this.searchFragment).commit();
    }

    // endregion

    // region 3. Fragments

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.isChecked()) {
            return true;
        } else {
            if (item.getItemId() == R.id.nav_search) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, this.searchFragment).commit();
                getSupportActionBar().setTitle("YouTube");
                return true;
            } else if (item.getItemId() == R.id.nav_albums) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, this.albumsFragment).commit();
                getSupportActionBar().setTitle("Albums");
                return true;
            }
        }
        return false;
    }

    // endregion

}