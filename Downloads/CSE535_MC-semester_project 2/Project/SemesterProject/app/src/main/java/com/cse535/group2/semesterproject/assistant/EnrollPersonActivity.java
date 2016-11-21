package com.cse535.group2.semesterproject.assistant;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cse535.group2.semesterproject.R;
import com.cse535.group2.semesterproject.helpers.SpeechHelper;
import com.cse535.group2.semesterproject.waveview.WaveView;

import org.apache.commons.lang3.ArrayUtils;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EnrollPersonActivity extends AppCompatActivity {
    private static final String TAG = "EnrollPersonActivity>>";
    private static HashMap<String, UUID> people = new HashMap<>();
    Button btn_submit;
    byte[] voice;
    WaveView mWaveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_person);

        Button btn_record = (Button) findViewById(R.id.btn_record);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        final EditText txt_name = (EditText) findViewById(R.id.txt_name);

        final SpeechHelper speechHelper = new SpeechHelper(this);
        mWaveView = (WaveView) findViewById(R.id.wave_view);

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mWaveView.setVisibility(WaveView.VISIBLE);
//                try{
//                    FileInputStream fis = new FileInputStream(getExternalFilesDir(null).toString() + "/hillary.wav");
//
//                    byte[] bytes = IOUtils.toByteArray(fis);
//
//                    EnrollPersonActivity.this.voice = bytes;
//                    btn_submit.setVisibility(Button.VISIBLE);
//
//                    return;
//                }catch (Exception e){
//                    Log.d(TAG, "onClick: "+e.getMessage());
//                }


                RecordSampleTask recordTask = new RecordSampleTask();
                recordTask.execute();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Call Speech API
                String name = txt_name.getText().toString();
                new AsyncTask<byte[],Void,Void>(){
                    String name;
                    Boolean success = false;
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        name = txt_name.getText().toString();
                    }

                    @Override
                    protected Void doInBackground(byte[]... params) {
                        if(!name.trim().equals("")){
                            speechHelper.addPerson(name, params[0]);
                            success = true;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if(success){
                            EnrollPersonActivity.this.finish();
                        }else{
                            Toast.makeText(EnrollPersonActivity.this, "Please enter a valid name",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute(EnrollPersonActivity.this.voice);
            }
        });


        List<Animator> animators = new ArrayList<>();

        // horizontal animation.
        // wave waves infinitely.
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(1000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());

        // vertical animation.
        // water level increases from 0 to center of WaveView
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
        mWaveView, "waterLevelRatio", 0f, 0.5f);
        waterLevelAnim.setDuration(10000);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        animators.add(waterLevelAnim);

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
        mWaveView, "amplitudeRatio", 0f, 0.05f);
        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);
        amplitudeAnim.setDuration(5000);
        amplitudeAnim.setInterpolator(new LinearInterpolator());
        animators.add(amplitudeAnim);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);

        mWaveView.setShowWave(true);
        animatorSet.start();
    }


    private class RecordSampleTask extends AsyncTask<Void, Void, List<Byte[]>>{
        private static final long MAX_SAMPLE_TIME = 5000; //30 secs = 30000 ms
        private static final String TAG = "RecordSampleTask>> ";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btn_submit.setVisibility(Button.GONE);
            mWaveView.setVisibility(WaveView.VISIBLE);
            Toast.makeText(EnrollPersonActivity.this, "Starting Recoding", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        protected void onPostExecute(List<Byte[]> bytes) {
            super.onPostExecute(bytes);
            mWaveView.setVisibility(WaveView.GONE);
            Toast.makeText(EnrollPersonActivity.this, "Stopped Recoding", Toast.LENGTH_SHORT)
                    .show();

            List<Byte> byteList = new ArrayList<>();
            for(int i=0;i<bytes.size();i++){
                Byte[] innerBytes = bytes.get(i);
                for(int j=0;j<innerBytes.length;j++){
                    byteList.add(innerBytes[j]);
                }
            }


            long totalAudioLen = byteList.size();
            long totalDataLen = totalAudioLen + 36;
            long longSampleRate = 16000;
            int channels = 1;
            long byteRate = 16 * 16000 * channels/8;

            byte[] header = getWaveFileHeader(totalAudioLen,totalDataLen,longSampleRate,channels,byteRate);
            byte[] data = ArrayUtils.toPrimitive(byteList.toArray(new Byte[byteList.size()]));

            byte[] wav_data = new byte[header.length + data.length];

            for(int i=0;i<header.length;i++){
                wav_data[i] = header[i];
            }

            for(int i=0;i<data.length;i++){
                wav_data[i+header.length] = data[i];
            }


            // TODO Remove file saving code

            try {
                FileOutputStream fos =
                        new FileOutputStream(getExternalFilesDir(null).toString() + "/SAMPLE.wav");
                fos.write(wav_data);
                fos.flush();
                fos.close();
            }catch (Exception e){
                Log.d(TAG, "onPostExecute: "+e.getMessage());
            }

            btn_submit.setVisibility(Button.VISIBLE);

        }

        @Override
        protected List<Byte[]> doInBackground(Void... params) {
            int audioSource = MediaRecorder.AudioSource.DEFAULT;
            int sampleRate = 16000;
            int channelConfig = AudioFormat.CHANNEL_IN_MONO;
            int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
            int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
            byte[] buffer = new byte[bufferSize];
            List<Byte[]> bytes = new ArrayList<>();

            AudioRecord audioRecord = null;
            audioRecord =
                    new AudioRecord(audioSource,sampleRate,
                            channelConfig,audioEncoding,bufferSize);




            long startTime = System.currentTimeMillis();
            long currTime = startTime;

            audioRecord.startRecording();

            try{
                //File cacheDir = new File(EnrollPersonActivity.this.getCacheDir().getPath())
                //FileOutputStream fileOutputStream = new FileOutputStream(cacheDir+"/"+uid.toString());
                while(currTime-startTime<MAX_SAMPLE_TIME){
                    audioRecord.read(buffer,0,bufferSize);
                    bytes.add(ArrayUtils.toObject(buffer));
                    currTime = System.currentTimeMillis();
                }
            }catch (Exception e){
                Log.d(TAG, "doInBackground: "+e.getMessage());
            }


            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;

            return bytes;
        }

        /**
         * http://www.edumobile.org/android/audio-recording-in-wav-format-in-android-programming/
         * @param totalAudioLen
         * @param totalDataLen
         * @param longSampleRate
         * @param channels
         * @param byteRate
         * @return
         */
        private byte[] getWaveFileHeader( long totalAudioLen,
                long totalDataLen, long longSampleRate, int channels,
                long byteRate) {

            byte[] header = new byte[44];

            header[0] = 'R'; // RIFF/WAVE header
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';
            header[4] = (byte) (totalDataLen & 0xff);
            header[5] = (byte) ((totalDataLen >> 8) & 0xff);
            header[6] = (byte) ((totalDataLen >> 16) & 0xff);
            header[7] = (byte) ((totalDataLen >> 24) & 0xff);
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';
            header[12] = 'f'; // 'fmt ' chunk
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';
            header[16] = 16; // 4 bytes: size of 'fmt ' chunk
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;
            header[20] = 1; // format = 1
            header[21] = 0;
            header[22] = (byte) channels;
            header[23] = 0;
            header[24] = (byte) (longSampleRate & 0xff);
            header[25] = (byte) ((longSampleRate >> 8) & 0xff);
            header[26] = (byte) ((longSampleRate >> 16) & 0xff);
            header[27] = (byte) ((longSampleRate >> 24) & 0xff);
            header[28] = (byte) (byteRate & 0xff);
            header[29] = (byte) ((byteRate >> 8) & 0xff);
            header[30] = (byte) ((byteRate >> 16) & 0xff);
            header[31] = (byte) ((byteRate >> 24) & 0xff);
            header[32] = (byte) (2 * 16 / 8); // block align
            header[33] = 0;
            header[34] = 16; // bits per sample
            header[35] = 0;
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';
            header[40] = (byte) (totalAudioLen & 0xff);
            header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
            header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
            header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

            return header;
        }


    }
}
