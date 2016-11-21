package com.cse535.group2.semesterproject.assistant;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cse535.group2.semesterproject.R;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechRecognizerActivity extends AppCompatActivity {

    private TextView txtSpeechInput;
    private Button btnSpeak;
    TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_recognizer);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (Button) findViewById(R.id.btnSpeak);
        info = (TextView) findViewById(R.id.info);

        btnSpeak.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                promptSpeechInput();
                //Toast.makeText(SpeechRecognizerActivity.this, "ButtonClicked", Toast.LENGTH_SHORT).show();
                info.setText("Recording");
            }
        });

    }


    public void promptSpeechInput(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!");

        try{
            startActivityForResult(i, 100);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(SpeechRecognizerActivity.this, "Sorry, your device does not support Speech Recognition", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent i){
        Toast.makeText(SpeechRecognizerActivity.this, "startActivityForResult called", Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, i );
        switch(requestCode){
            case 100: {
                if(resultCode ==RESULT_OK && i!= null) {
                    ArrayList<String> results = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(results.get(0));

                }
                break;
            }
        }
    }
}
