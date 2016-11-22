package com.cse535.group2.semesterproject.assistant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cse535.group2.semesterproject.R;
import com.cse535.group2.semesterproject.helpers.SpeechHelper;

import java.util.List;

public class AddPeopleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        Button btn_add = (Button) findViewById(R.id.btn_add);
        final ListView lst_people = (ListView) findViewById(R.id.lst_people);

        final SpeechHelper speechHelper = new SpeechHelper(this);

        new AsyncTask<Void,Void,Void>(){
            ArrayAdapter<String> nameAdapter;

            @Override
            protected Void doInBackground(Void... params) {
                List<String> names = speechHelper.getPeople();
                nameAdapter = new ArrayAdapter<String>(AddPeopleActivity.this,
                        R.layout.people_list_item,
                        R.id.people_list_item_textview, names);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                lst_people.setAdapter(nameAdapter);
            }
        }.execute();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enrollIntent = new Intent(AddPeopleActivity.this, EnrollPersonActivity.class);
                startActivity(enrollIntent);
                AddPeopleActivity.this.finish();
            }
        });



    }


}
