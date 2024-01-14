package WizardTD;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import processing.data.JSONArray;
import processing.data.JSONObject;

public abstract class GameElements implements Cloneable{
    public static final int PIXELSPERTILE = 32;
    public static final int BOARD_WIDTH = 20;
    public static final int FPS = 60;
    protected JSONObject json;
    protected Random random;

    public GameElements (JSONObject json) {
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
            System.exit(1);
        }

        if (!(waves.get(wave_num-1) instanceof JSONObject)) {
            System.err.println("Wave number is not of type JSONObject!");
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
        }

        if (arr == null || arr.size() <= 0) {
            System.err.println(key + " JSONArray cannot be read");
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
            }
        }

        if (indexes.size() == 0) {
            System.err.println("No monster types exist for this wave!");
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

    /**
     * Retrieves the layout of map for the current level provided in the config JSONObject and stores each row as a String in an ArrayList
     * @return lines_in_file
     */
    protected ArrayList<String> getMap() {
        File map_file = new File(json.getString("layout"));
        ArrayList<String> lines_in_file = new ArrayList<String>();

        try {
            Scanner scan = new Scanner(map_file);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                while (line.length() != BOARD_WIDTH) {
                    line += " ";
                }
                lines_in_file.add(line);
            }
            scan.close();         
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        }
            
        return lines_in_file;
    }

    /**
     * Reads and retrieves a numeric value from a given JSONObject key
     * @param key String indicating the value to retrieve from
     * @param obj the JSONObject to read
     * @return amount
     */
    public static double getNumericAmount(String key, JSONObject obj) {
        double amount = -1;
        try{
            amount = obj.getDouble(key);
        }
        catch(RuntimeException e) {
            System.err.println("Key: '" + key + "' does not exist, or value is not numeric!");
        }

        if (amount <= 0) {
            System.err.println("The initial amount cannot be less than or equal to 0");
        }

        return amount;
    }

    /**
     * Finds the distance between two points on the screen
     * @param start_x initial x coordinate
     * @param start_y initial y coordinate
     * @param end_x destination x coordinate
     * @param end_y destination y coordinate
     * @return distance
     */
    public static double findDistanceBetweenTwoPoints(float start_x, float start_y, float end_x, float end_y) {
        float x_dist = Math.abs(start_x - end_x);
        float y_dist = Math.abs(start_y - end_y);
        double distance = Math.hypot(x_dist, y_dist);

        return distance;
    }
}