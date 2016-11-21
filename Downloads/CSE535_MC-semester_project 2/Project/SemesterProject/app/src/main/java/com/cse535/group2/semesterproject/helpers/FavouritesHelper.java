package com.cse535.group2.semesterproject.helpers;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by anique on 11/20/16.
 */

public class FavouritesHelper {
    private static final String TAG = "FavouritesHelper>>";

    private static final long TIME_LIMIT = 1000*60*60*24*10; //3 days


    public FavouritesHelper(){
        loadFavourites();
    }

    private class Favourite implements Serializable{
        int topicId;
        String text;
        long time;
        long numClicks;

        public Favourite(int topicId, String text, long time){
            this.topicId = topicId;
            this.text = text;
            this.time = time;
            this.numClicks = 1;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj.getClass() == Favourite.class){
                Favourite that = (Favourite) obj;
                if(that.topicId == this.topicId && that.text == this.text){
                    return true;
                }
            }
            return false;
        }
    }

    private ArrayList<Favourite> clicks;

    private void loadFavourites(){
        // TODO Implement
        try {
            File location = new File(Environment.getDataDirectory(), "favourites.dat");
            if (location.exists()) {
                SerializationUtils.deserialize(new FileInputStream(location));
            } else {
                clicks = new ArrayList<>();
            }
        }catch (Exception e){
            Log.d(TAG, "loadFavourites: ");
        }
    }

    /**
     *
     * @param topicId The ID of the topic as an integer
     * @param text The actual text in the vocabulary
     */
    public void registerClick(int topicId, String text){
        long time = System.currentTimeMillis();
        for(Favourite fav: clicks){
            if(fav.topicId == topicId && fav.text.equals(text)){
                fav.numClicks += 1;
                fav.time = time;
                saveFavourites();
                return;
            }
        }
        Favourite click = new Favourite(topicId, text, time);
        clicks.add(click);
        saveFavourites();
    }

    private void saveFavourites(){
        try {
            File location = new File(Environment.getDataDirectory(), "favourites.dat");
            FileOutputStream outputStream = new FileOutputStream(location);
            SerializationUtils.serialize(clicks,outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            Log.d(TAG, "saveFavourites: "+e.getMessage());
        }

    }

    public void resetFavourites(){
        clicks.clear();
        saveFavourites();
    }

    public String[] getFavourites(int topicId){
        //Sort by num clicks
        Collections.sort(clicks, new Comparator<Favourite>() {
            @Override
            public int compare(Favourite o1, Favourite o2) {
                long currTime = System.currentTimeMillis();
                if(currTime - o1.time>TIME_LIMIT){
                    o1.numClicks = 0;
                }
                if(currTime - o2.time>TIME_LIMIT){
                    o2.numClicks = 0;
                }
                if(o1.numClicks<o2.numClicks){
                    return -1;
                }else if(o1.numClicks>o2.numClicks){
                    return 1;
                }else{
                    return 0;
                }

            }
        });

        List<String> favourites = new ArrayList<>();
        for(int i=0;i<clicks.size();i++){
            if(clicks.get(i).topicId == topicId){
                favourites.add(clicks.get(i).text);
            }
        }

        Collections.reverse(favourites);
        return favourites.toArray(new String[favourites.size()]);



    }
}
