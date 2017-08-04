package com.duvitech.appstream;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.view.Menu;
import android.view.MenuItem;

import com.duvitech.appstream.provider.DuvitechMediaRouteProvider;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MediaRouteSelector mSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a route selector for the type of routes that we care about.
        mSelector =
                new MediaRouteSelector.Builder().addControlCategory(MediaControlIntent
                        .CATEGORY_LIVE_AUDIO).addControlCategory(MediaControlIntent
                        .CATEGORY_LIVE_VIDEO).addControlCategory(MediaControlIntent
                        .CATEGORY_REMOTE_PLAYBACK).addControlCategory(DuvitechMediaRouteProvider
                        .CATEGORY_DUVITECH_ROUTE).build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Be sure to call the super class.
        super.onCreateOptionsMenu(menu);

        // Inflate the menu and configure the media router action provider.
        getMenuInflater().inflate(R.menu.media_router_menu, menu);

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mSelector);

        // Return true to show the menu.
        return true;
    }

}
