package com.dzondza.vasya.diagnostix;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.AndroidFragment;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.BatteryFragment;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.CamerasFragment;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.DirectoriesFragment;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.DisplayFragment;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.InstalledAppsFragment;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.NetworkFragment;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.SensorsFragment;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.SystemFragment;
import com.dzondza.vasya.diagnostix.NavigationDrawerContent.DeviceFragment;


/**
 * Program main activity
 * creates and initializes toolbar, DrawerLayout
 */

public class MainScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        navigationDrawer();
    }


    private void navigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        toggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        //sets text to navigationView header's textViews
        View header = navigationView.getHeaderView(0);
        TextView headerUpper = header.findViewById(R.id.text_header_upper);
        headerUpper.setText(Build.BRAND);
        TextView headerLower = header.findViewById(R.id.text_header_lower);
        headerLower.setText(Build.MODEL);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_fragment_container,
                new DeviceFragment()).commit();
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_device));
    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_item_developer:
                intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                startActivity(intent);
                return true;
            case R.id.menu_item_accounts:
                intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                startActivity(intent);
                return true;
            case R.id.menu_item_security:
                intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(intent);
                return true;
            case R.id.menu_item_search:
                intent = new Intent(Settings.ACTION_SEARCH_SETTINGS);
                startActivity(intent);
                return true;
            case R.id.menu_item_sound:
                intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
                startActivity(intent);
                return true;
            case R.id.menu_item_exit:
                finish();
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       // selects drawerLayout's item
        Fragment mFragment = null;
        switch (item.getItemId()) {
            case R.id.nav_system:
                mFragment = new SystemFragment();
                break;
            case R.id.nav_device:
                mFragment = new DeviceFragment();
                break;
            case R.id.nav_installed_app:
                mFragment = new InstalledAppsFragment();
                break;
            case R.id.nav_display:
                mFragment = new DisplayFragment();
                break;
            case R.id.nav_network:
                mFragment = new NetworkFragment();
                break;
            case R.id.nav_android:
                mFragment = new AndroidFragment();
                break;
            case R.id.nav_battery:
                mFragment = new BatteryFragment();
                break;
            case R.id.nav_camera:
                mFragment = new CamerasFragment();
                break;
            case R.id.nav_sensors:
                mFragment = new SensorsFragment();
                break;
            case R.id.nav_directories:
                mFragment = new DirectoriesFragment();
                break;
            case R.id.nav_share:
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                intentShare = Intent.createChooser(intentShare, getString(R.string.app_name));
                startActivity(intentShare);
        }

        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        if (mFragment != null) {
            mTransaction.addToBackStack(null);
            mTransaction.replace(R.id.activity_main_fragment_container, mFragment).commit();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}