package WizardTD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import processing.data.JSONArray;
import processing.data.JSONObject;

public abstract class WaveElements implements RetrieveValues{
    protected JSONObject json;
    protected Random random;

    public WaveElements (JSONObject json) {
        this.json = json;
        random = new Random();
    }

    /**
     * Retrieves the wave JSONObject in the given game config file at a given wave number
     * @param waves JSONArray of all waves in the game
     * @param wave_num wave number to consider
     * @return the current wave
     */
    protected JSONObject getCurrentWave(JSONArray waves, int wave_num) {      
        if (wave_num > waves.size() || wave_num < 1) {
            System.err.println("Invalid wave number!");
            return null;
        }

        if (!(waves.get(wave_num-1) instanceof JSONObject)) {
            System.err.println("Wave number is not of type JSONObject!");
            return null;
        }

        return waves.getJSONObject(wave_num-1);    
    }

    /**
     * Retrieves the name of the monster types to spawn for the current wave and stores them in an ArrayList
     * @param monsters JSONArray of all monster objects for a given wave
     * @return types
     */
    protected ArrayList<String> getMonsterTypes(JSONArray monsters) {
        ArrayList<String> types = new ArrayList<String>();

        for (int i = 0; i < monsters.size(); i++) {
            try {
                JSONObject current = monsters.getJSONObject(i);
                types.add(current.getString("type"));
            }
            catch (RuntimeException e) {
                System.err.println("No monsters exist for wave " + (i+1) + "!");
                return null;
            }
        }
        return types;
    }

    /**
     * Retrieves the total number of monsters to spawn for a given wave
     * @param monsters JSONArray of all monter objects for a given wave
     * @return total_monsters
     */
    protected int getTotalMonsters(JSONArray monsters) {
        int total_monsters = 0;
        for (int i = 0; i < monsters.size(); i++) {
            try {
                JSONObject current = monsters.getJSONObject(i);
                total_monsters += current.getInt("quantity");
            }
            catch (RuntimeException e) {
                System.err.println("Key: 'quantity' doesn't exist, or value is not an integer!");
            }
        }

        if (total_monsters <= 0) {
            System.err.println("No monsters to spawn for this wave!");
        }

        return total_monsters;
    }
    
    /**
     * Reads a JSONObject to check if it contains a JSONArray object that maps to a given key
     * @param object the JSONObject to retrieve from
     * @param key String that indicates the key value of the array object
     * @return the JSONArray
     */
    protected JSONArray readJSONArray(JSONObject object, String key) {
        JSONArray arr = null;
        try {
            arr = object.getJSONArray(key);
        }
        catch (RuntimeException e) {
            System.err.println("Key: " + key + " does not exist, or value is not a JSONArray!");
            return null;
        }

        if (arr == null || arr.size() <= 0) {
            System.err.println(key + " JSONArray cannot be read");
            return null;
        }
        
        return arr;
    }

    /**
     * Creates a hashamp that maps the index of a given monster type in a JSONArray of all monsters for a given wave to it's type as a String
     * @param monsters JSONArray for a given wave
     * @return indexes
     */
    private HashMap<String, Integer> indexOfMonsterType(JSONArray monsters) {
        HashMap<String, Integer> indexes = new HashMap<>();
        
        for (int i = 0; i < monsters.size(); i++) {
            try {
                JSONObject current = monsters.getJSONObject(i);
                String type = current.getString("type");
                indexes.put(type, i);
            }
            catch (RuntimeException e) {
                System.err.println("Key 'type' for monsters JSONArray  doesn't exist!");
                return null;
            }
        }

        if (indexes.size() == 0) {
            System.err.println("No monster types exist for this wave!");
            return null;
        }

        return indexes;
    }

    /**
     * Reads the value of the specified monster attribute from the JSONObject config file
     * @param current_wave JSONObject of current wave to read
     * @param type String indicating the type of the monster
     * @param key String indicating the attribute to retrieve the value from 
     * @return value
     */
    protected double setMonsterAttribute(JSONObject current_wave, String type, String key) {
        JSONArray monsters = readJSONArray(current_wave, "monsters");
        HashMap<String, Integer> indexes = indexOfMonsterType(monsters);

        double value = -1;

        try {
            JSONObject monster = monsters.getJSONObject(indexes.get(type));
            value = monster.getDouble(key);
        }
        catch (RuntimeException e) {
            System.err.println("Key: " + key + " doesn't exist, or value is not numeric!");
        }

        if (value <= 0) {
            System.err.println("Initial " + key + " cannot be less than or equal 0!");
        }
        return value;
    }

}