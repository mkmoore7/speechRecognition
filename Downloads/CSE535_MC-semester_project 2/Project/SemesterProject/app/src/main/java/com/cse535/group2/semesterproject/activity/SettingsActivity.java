package com.cse535.group2.semesterproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cse535.group2.semesterproject.R;
import com.cse535.group2.semesterproject.assistant.AddPeopleActivity;

/**
 * Created by jose on 11/13/16.
 */

public class SettingsActivity extends AppCompatActivity{
    private static final String LOG_TAG = "SettingsActivity";
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main_page);

        Log.d(LOG_TAG, "onCreate");

        Button btn_add_people = (Button)findViewById(R.id.btn_settings_add_person);

        btn_add_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent addPeopleIntent = new Intent(SettingsActivity.this, AddPeopleActivity.class);
                startActivity(addPeopleIntent);
            }
        });

//        btn_identify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, mService.getSpeaker(), Toast.LENGTH_SHORT).show();
//
//
//
//            }
//        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, mMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cancel:
                Log.d(LOG_TAG, "menu_cancel selected");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
