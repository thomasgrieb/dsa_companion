package de.thomasinc.dsaapp;
import android.content.Context;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Utility helper class that implements various methods needed in several places of the app
 */

public class Util {

    /**
     * Reads the skills from the json file saved in the apps' assets folder
     * @param context application context needed for filepath
     * @return {@link String} of skills
     */
    public static String readSkillsJson(Context context){
        String json = null;
        try {
            InputStream file = context.getAssets().open("skills.json");
            int size = file.available();
            byte[] buffer = new byte[size];
            file.read(buffer);
            file.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    /**
     * Uses the {@link String} generated in {@link #readSkillsJson(Context)} to generate a
     *  {@link JSONObject}, where the keys are the skill categories and its values the skills of
     *  the category. Creates {@link ArrayList} of skill categories.
     * @param context application context needed for filepath
     * @return String-array of skill categories
     */
    public static String[] getSkillKats(Context context){
        ArrayList<String> kats = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(readSkillsJson(context));
            Iterator<String> it= obj.keys();
            while(it.hasNext()){
                kats.add(it.next());
            }
        } catch (JSONException er){
            er.printStackTrace();
        }
        return kats.toArray(new String[5]);
    }

    /**
     * Reads the skills of a specific category from a {@link JSONObject} generated from the String
     *  from {@link #readSkillsJson(Context)}. Collects them in a {@link HashMap}, where the name of
     *  the skill as {@link String} is the key and a {@link Skill} object its value.
     * @param context application context needed for filepath
     * @param kat
     * @return {@link HashMap} object containing all skills of category kat as
     *  {@link String}:{@link Skill} pairs
     */
    public static HashMap<String,Skill> getSkillsOfCat(Context context, String kat){
        HashMap<String,Skill> skillmap = new HashMap<>();
        try{
            JSONObject obj = new JSONObject(readSkillsJson(context)).getJSONObject(kat);
            Iterator<String> it = obj.keys();
            String entry;
            Formula f;
            String[] fAr;
            while (it.hasNext()){
                entry = it.next();
                fAr = obj.getString(entry).split("-");
                f = new Formula(fAr[0],fAr[1],fAr[2]);
                skillmap.put(entry,new Skill(entry,f));
            }
        } catch (JSONException er){
            er.printStackTrace();
        }
        return skillmap;
    }

    /**
     * Creates a {@link HashMap} containing {@link Skill}:{@link Integer}(0) pairs for every
     *  existing skill in skill.json in order to initialize an empty characters skill sheet.
     * Uses the {@link #readSkillsJson(Context)} method to get a list of categories, loops over the
     *  result and uses the {@link #getSkillsOfCat(Context, String)} method to create a
     *  {@link Skill} array of all skills in that category. Lastly, the list is appended to a
     *  predefined {@link HashMap} and each key is assigned the value 0.
     * The method is supposed to be used after creating a new profile through the profile creating
     *  functionality in order to complete the creation of the corresponding {@link Character}
     *  object.
     * @param context
     * @return {@link HashMap} of {@link Skill}:{@link Integer} pairs
     */
    public static HashMap<Skill,Integer> initializeSkillValueMap(Context context){
        HashMap<Skill,Integer> skillValueMap = new HashMap<>();
        try{
            JSONObject obj = new JSONObject((readSkillsJson(context)));
            Iterator<String> itCat = obj.keys();
            Skill[] skillsOfCat;
            while (itCat.hasNext()){
                skillsOfCat = getSkillsOfCat(context,itCat.next()).values().toArray(new Skill[0]);
                for (int i = 0; i< skillsOfCat.length;i++){
                    skillValueMap.put(skillsOfCat[i], 0);
                }
            }
        } catch (JSONException er){
            er.printStackTrace();
        }
        return skillValueMap;
    }

    /**
     * Reads existing charfile (json) from the  appropriate file folder and creates corresponding
     *  {@link Character} object.
     * @param context application context needed for filepath
     * @return {@link Character} object with the saved attributes
     */
    public static Character readCharFromJson(Context context) {
        ArrayList<Integer> l = new ArrayList<>();
        String json = null;
        try {
            InputStream file = context.openFileInput("myCharacter.json"); //const-class?
            int size = file.available();
            byte[] buffer = new byte[size];
            file.read(buffer);
            file.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject obj = new JSONObject(json);
            Iterator<String> it = obj.keys();
            while (it.hasNext()) {
                l.add(obj.getInt(it.next()));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return new Character.CharBuilder("placeholder")
                .mu(l.get(0))
                .kl(l.get(1))
                .in(l.get(2))
                .ch(l.get(3))
                .ff(l.get(4))
                .ge(l.get(5))
                .ko(l.get(6))
                .kk(l.get(7))
                .build();
    }

    /**
     * Writes a Character object to a json file, derives the filename from the charactername
     * @param context
     * @param c
     */
    public static void writeCharToJson(Context context, Character c) {

    }

    /**
     * Function for checking whether a EditText-Object is empty or not
     * @param text a single {@link EditText} object
     * @return boolean, false if not empty, true if empty
     */
    public static boolean checkIfEmpty(EditText text) {
        if (text.getText().toString().trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Function for checking whether an Array of EditText-Objects includes any unfilled fields
     * @param array array of {@link EditText} objects
     * @return boolean, false if none empty, true if any empty
     */
    public static boolean checkIfAnyEmptyArray(EditText[] array) {
        for (int i = 0; i < 8; i++) {
            if (Util.checkIfEmpty(array[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param context application context needed for filepath
     * @return boolean indicating whether any charfile exists (true if it exists)
     */
    public static boolean checkIfCharExists(Context context) {
        return context.getFileStreamPath("myCharacter.json").exists();
    }

    public static int largerThan(int x,int y){
        int z = 0;
        if(x > y){
            z = x-y;
        }
        return z;
    }
}
