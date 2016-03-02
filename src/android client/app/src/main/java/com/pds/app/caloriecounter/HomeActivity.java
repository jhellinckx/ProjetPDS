package com.pds.app.caloriecounter;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.FrameLayout;
import android.view.View;

import org.json.simple.JSONObject;

import java.io.IOException;

import static org.calorycounter.shared.Constants.network.*;
public abstract class HomeActivity extends NotifiableActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected FrameLayout frameLayout;
    protected View v;

    protected void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void handleMessage(JSONObject msg){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        init();
        frameLayout = (FrameLayout)findViewById(R.id.content_layout);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Hom/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent cameractivity = new Intent(HomeActivity.this, ScanningActivity.class);

            startActivity(cameractivity);
        } else if (id == R.id.nav_data) {
            Intent dataactivity = new Intent(HomeActivity.this, PersonalDataActivity.class);

            startActivity(dataactivity);

        } else if (id == R.id.nav_recommend) {
            Intent recommendactivity = new Intent(HomeActivity.this, RecommendationActivity.class);

            startActivity(recommendactivity);

        } else if (id == R.id.nav_rating) {
            Intent ratingactivity = new Intent(HomeActivity.this, RatingActivity.class);

            startActivity(ratingactivity);

        } else if (id == R.id.nav_history) {
            Intent historyactivity = new Intent(HomeActivity.this, HistoryActivity.class);

            startActivity(historyactivity);

        } else if (id == R.id.nav_logout){
            onLogout();
            Intent logActivity = new Intent(HomeActivity.this, LogActivity.class);
            startActivity(logActivity);
        } else if (id == R.id.nav_calendar){
            Intent calendarActivity = new Intent(HomeActivity.this, CalendarActivity.class);
            startActivity(calendarActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onLogout(){
        JSONObject logoutRequest = networkJSON(LOG_OUT_REQUEST,new JSONObject());
        send(logoutRequest);
    }
}
