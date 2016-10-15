package com.jianqingc.nectar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jianqingc.nectar.controller.HttpRequestController;
import com.jianqingc.nectar.fragment.InstanceFragment;
import com.jianqingc.nectar.fragment.OverviewFragment;
import com.jianqingc.nectar.R;
import com.jianqingc.nectar.fragment.VolumeFragment;
import com.jianqingc.nectar.fragment.AccessAndSecurityFragment;
import com.jianqingc.nectar.fragment.ImageFragment;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nectar Cloud");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("nectar_android", 0);



        //toolbar.setTitleTextColor();
        FloatingActionButton fabRight = (FloatingActionButton) findViewById(R.id.fabRight);
        FloatingActionButton fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabRight.setVisibility(View.GONE);
        fabRight.setEnabled(false);
        fabLeft.setVisibility(View.GONE);
        fabLeft.setEnabled(false);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        TextView tenantNameInNavHeader = (TextView) hView.findViewById(R.id.tenantNameInNavHeader);
        tenantNameInNavHeader.setText(sharedPreferences.getString("tenantName", "error in retrieving tenantName from shared preference"));
        TextView userNameInNavHeader = (TextView) hView.findViewById(R.id.userNameInNavHeader);
        userNameInNavHeader.setText(sharedPreferences.getString("username", "error in retrieving username from shared preference"));

        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        FragmentManager manager = getSupportFragmentManager();
        OverviewFragment overviewFragment = new OverviewFragment();
        manager.beginTransaction().replace(R.id.relativelayout_for_fragment, overviewFragment, overviewFragment.getTag()).commit();

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
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
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
        FragmentManager manager = getSupportFragmentManager();
        if (id == R.id.nav_overview) {
            Toast.makeText(MainActivity.this, "overview", Toast.LENGTH_SHORT).show();
            OverviewFragment overviewFragment = new OverviewFragment();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment, overviewFragment, overviewFragment.getTag()).commit();
        } else if (id == R.id.nav_instances) {
            Toast.makeText(MainActivity.this, "instances", Toast.LENGTH_SHORT).show();
            InstanceFragment instanceFragment = new InstanceFragment();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment, instanceFragment, instanceFragment.getTag()).commit();
        } else if (id == R.id.nav_volumes) {
            VolumeFragment volumeFragment = new VolumeFragment();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment, volumeFragment, volumeFragment.getTag()).commit();
        } else if (id == R.id.nav_images) {
            ImageFragment imageFragment = new ImageFragment();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment, imageFragment, imageFragment.getTag()).commit();
        } else if (id == R.id.nav_accessAndSecurity) {
            AccessAndSecurityFragment accessAndSecurityFragment = new AccessAndSecurityFragment();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment, accessAndSecurityFragment, accessAndSecurityFragment.getTag()).commit();
        } else if (id == R.id.nav_networkTopology) {

        } else if (id == R.id.nav_networks) {

        } else if (id == R.id.nav_routers) {

        }else if (id == R.id.nav_about) {

        }else if (id == R.id.nav_signout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure to sign out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            SharedPreferences sharedPreferences =  getApplicationContext().getSharedPreferences("nectar_android", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isSignedOut",true);
                            editor.apply();
                            startActivity(i);
                            Toast.makeText(getApplicationContext(), "Successfully Signed Out!", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.jianqingc.nectar/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);

    }

    @Override
    public void onStop() {
        super.onStop();
        this.finish();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.jianqingc.nectar/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
