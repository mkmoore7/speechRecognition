package com.cse535.group2.semesterproject.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.cse535.group2.semesterproject.helpers.SpeechHelper;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class SpeakerService extends Service {
    private String speaker = null;
    private byte[] voice;
    private boolean recording = true;

    public void stopRecording(){
        recording = false;
        //rt.interrupt();
        //audioThread.audioRecord.stop();
        //audioThread.audioRecord.release();
        //audioThread.audioRecord = null;
    }
    public void resumeRecording(){
        recording = true;
        //if(rt!=null && !rt.isAlive()){
            //rt.start();
       // }

    }
    public SpeakerService() {
    }

    public class LocalBinder extends Binder{
        public SpeakerService getService(){
            return SpeakerService.this;
        }
    }

    SpeechHelper speechHelper;
    Thread rt;
    RecordThread audioThread;
    @Override
    public void onCreate() {
        super.onCreate();
        speechHelper = new SpeechHelper(getApplicationContext());
        //RecordSampleTask recordTask = new RecordSampleTask();
        //recordTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //audioThread = new RecordThread();
        //rt = new Thread(audioThread);
        //rt.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        LocalBinder binder = new LocalBinder();
        return binder;
    }

    private class RecordThread implements Runnable{
        private static final long MAX_SAMPLE_TIME = 5000; //30 secs = 30000 ms
        private static final String TAG = "RecordSampleTask>> ";
        public AudioRecord audioRecord;
        @Override
        public void run() {
            Looper.prepare();
            while(true && !Thread.interrupted()){
                Log.d(TAG, "run: Recording");
                int audioSource = MediaRecorder.AudioSource.DEFAULT;
                int sampleRate = 16000;
                int channelConfig = AudioFormat.CHANNEL_IN_MONO;
                int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
                int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
                byte[] buffer = new byte[bufferSize];
                List<Byte[]> bytes = new ArrayList<>();

                audioRecord = null;
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
                    Log.d(this.getClass().getName(), "doInBackground: "+e.getMessage());
                }


                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;

                publishProgress(bytes);
            }
        }
        private void publishProgress(List<Byte[]> bytes){
            Toast.makeText(getApplicationContext(), "progress Update", Toast.LENGTH_SHORT).show();

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

            SpeakerService.this.voice = wav_data;
            //speaker = speechHelper.identifyPerson(wav_data);
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

    public String getSpeaker(){
        return speaker;
    }

    private class IdentifyInput{
        public byte[] sound;
        public IdentifyInput(byte[] bytes){
            this.sound = bytes;
        }
    }
}
