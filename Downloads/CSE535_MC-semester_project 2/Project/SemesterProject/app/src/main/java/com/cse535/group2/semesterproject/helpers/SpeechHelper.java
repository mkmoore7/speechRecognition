package com.cse535.group2.semesterproject.helpers;

/**
 * Created by anique on 11/14/16.
 */
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationRestClient;
import com.microsoft.cognitive.speakerrecognition.contract.EnrollmentStatus;
import com.microsoft.cognitive.speakerrecognition.contract.identification.CreateProfileResponse;
import com.microsoft.cognitive.speakerrecognition.contract.identification.EnrollmentOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.IdentificationOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.Profile;
import com.microsoft.cognitive.speakerrecognition.contract.identification.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SpeechHelper {
    private static final String API_KEY = "f16112bacc834455adabfc21f9cc41a4";
    private SpeakerIdentificationRestClient client;
    private static final String TAG = "SpeechHelper>>";
    List<Profile> profiles;
    Map<String, UUID> people;
    Context context;


    public SpeechHelper(Context con){
        this.context = con;
        client = new SpeakerIdentificationRestClient(API_KEY);
        //resetProfiles();
        loadProfiles();
    }

    /**
     *
     * @param voice The PCM-16 BIT, 16Khz, Mono audio sample of the person
     * @return the UUID of the person or null if enrollment fails
     */
    public UUID addPerson(String name, byte[] voice){
        if(people.get(name) != null){
            Toast.makeText(context, "Person with this name already exists!", Toast.LENGTH_LONG)
                    .show();
            return null;
        }

        UUID uid = createProfile();
        int maxIterations = 10;


        int iterations = 0;
        if(uid != null){
            try{


                OperationLocation operationLocation =
                        client.enroll(new ByteArrayInputStream(voice), uid);
                //con.deleteFile(audioFilePath);

                while(true){
                    iterations++;
                    if(iterations>maxIterations){
                        break;
                    }
                    Thread.sleep(5000);
                    EnrollmentOperation enrollmentOperation =
                            client.checkEnrollmentStatus(operationLocation);



                    if(enrollmentOperation.processingResult!=null &&
                            enrollmentOperation.processingResult.enrollmentStatus
                                    == EnrollmentStatus.ENROLLED){
                        people.put(name, uid);
                        savePeople();
                        return uid;

                    }else if(enrollmentOperation.status == Status.FAILED){
                        break;
                    }

                }

            }catch(Exception e){
                Log.d(TAG, "addPerson: "+e.getMessage());
            }
        }

        return null;
    }

    public void removePerson(String name){
        UUID uid = people.get(name);
        try{
            client.deleteProfile(uid);
            people.remove(name);
            savePeople();
        }catch(Exception e){
            Log.d(TAG, "removePerson: "+ e.getMessage());
        }
    }

    /**
     * Given a 30second audio sample, identify the person
     * @param voice the 16-bit PCM, 16kHz, Mono audio sample byte array
     */
    public String identifyPerson(byte[] voice){
        List<UUID> uids = new ArrayList<>();
        List<String> names = getPeople();;
        for(String name: names){
            UUID uid = people.get(name);
            if(uid != null){
                uids.add(uid);
            }
        }
        try{

            OperationLocation operationLocation =
                    client.identify(new ByteArrayInputStream(voice),uids);

            int maxIterations = 10;
            int iterations = 0;
            while(true){
                iterations++;
                if(iterations>maxIterations){
                    break;
                }
                Thread.sleep(5000);
                IdentificationOperation identificationOperation =
                        client.checkIdentificationStatus(operationLocation);



                if(identificationOperation.status== Status.SUCCEEDED){
                    for(String name:names){
                        UUID uid = people.get(name);
                        if(uid.compareTo(identificationOperation.processingResult.identifiedProfileId) == 0){
                            return name;
                        }
                    }
                }else if(identificationOperation.status == Status.FAILED){
                    break;
                }

            }

        }catch (Exception e){
            Log.d(TAG, "identifyPerson: "+ e.getMessage());
        }

        return null;

    }

    private void loadProfiles(){
        loadPeople();
        try{
            this.profiles = client.getProfiles();
        }catch(Exception e){
            Log.d(TAG, "resetProfiles: " + e.getMessage());
        }
    }

    private void loadPeople(){
        String path = context.getFilesDir().getPath() + "/people.dat";
        File mapFile = new File(path);
        if(mapFile.exists()){
            try {
                this.people = SerializationUtils.deserialize(new FileInputStream(mapFile));
            }catch (Exception e){
                Log.d(TAG, "loadPeople: "+e.getMessage());
            }
        }else{
            this.people = new HashMap<String, UUID>();
        }

    }

    private void savePeople() throws Exception{

        String path = context.getFilesDir().getPath() + "/people.dat";
        File mapFile = new File(path);
        FileUtils.writeByteArrayToFile(mapFile,SerializationUtils.serialize((HashMap<String, UUID>)people));
    }

    public List<String> getPeople(){
        Set<String> keySet = people.keySet();

        String[] names = (String[])keySet.toArray(new String[keySet.size()]);
        return Arrays.asList(names);
    }

    public void resetProfiles(){
        try{
            List<Profile> profiles = client.getProfiles();
            for(Profile profile: profiles){
                client.deleteProfile(profile.identificationProfileId);
            }
            people.clear();
            savePeople();

        }catch(Exception e){
            Log.d(TAG, "resetProfiles: " + e.getMessage());
        }
    }

    private UUID createProfile(){
        try{
            CreateProfileResponse response = client.createProfile("en-US");
            return response.identificationProfileId;
        }catch(Exception e){
            Log.d(TAG, "createProfile: " + e.getMessage());
            return null;
        }
    }

}
