package com.example.todo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import data.Constant;
import data.DatabaseManager;
import setting.SettingActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int labelId;
    private int sortBy;
    DatabaseManager databaseManager;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();

        FloatingActionButton fab_1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewItemActivity(MainActivity.this);
            }
        });

        //
        FloatingActionButton fab_2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Try", Toast.LENGTH_SHORT).show();
                startPhotographActivity(MainActivity.this);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void init() {
        sharedPreferences = getSharedPreferences(Constant.SHARED_SETTING, Context.MODE_APPEND);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getInt(Constant.DEFAULT_LABEL_ID_SETTING, -1) < 0) {
            editor.putInt(Constant.DEFAULT_LABEL_ID_SETTING, Constant.DEFAULT_LABEL_ID).commit();
        }

        databaseManager = new DatabaseManager(this);
        labelId = sharedPreferences.getInt(Constant.DEFAULT_LABEL_ID_SETTING, Constant.DEFAULT_LABEL_ID);
        sortBy = Constant.SORT_BY_DEFAULT;
        startListFragment(labelId, sortBy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startListFragment(labelId, sortBy);
    }

    public void startListFragment(int labelId, int sortBy) {
        ListFragment listFragment = new ListFragment();
        listFragment.setListFragment(databaseManager, labelId, sortBy);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
    }

    public void startNewItemActivity(Context context) {
        Intent intent = new Intent(context, NewItemActivity.class);
        intent.putExtra(Constant.NEW_OR_EDIT, Constant.NEW);
        context.startActivity(intent);
    }

    //
    public void startPhotographActivity(Context context) {
        Intent intent = new Intent(context, PhotographActivity.class);
        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_by_default:
                sortBy = Constant.SORT_BY_DEFAULT;
                break;
            case R.id.sort_by_date:
                sortBy = Constant.SORT_BY_DATE;
                break;
            case R.id.sort_by_importance:
                Toast.makeText(this, "Still waiting......", Toast.LENGTH_LONG).show();
                break;
        }
        startListFragment(labelId, sortBy);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.label:
                labelId = Constant.DEFAULT_LABEL_ID;
                startListFragment(labelId, sortBy);
                break;
            case R.id.label1:
                labelId = Constant.LABEL_ID_1;
                startListFragment(labelId, sortBy);
                break;
            case R.id.label2:
                labelId = Constant.LABEL_ID_2;
                startListFragment(labelId, sortBy);
                break;
            case R.id.label_have_done:
                labelId = Constant.LABEL_ID_HAVE_DONE;
                startListFragment(labelId, sortBy);
                break;
            case R.id.add_todo_set_group:
                Toast.makeText(this, "Still waiting......", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Still waiting......", Toast.LENGTH_LONG).show();
                break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                startListFragment(Constant.DEFAULT_LABEL_ID, sortBy);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseManager.closeDatabase();
    }
}
