package WizardTD;

import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wave extends GameElements{
    private double pre_wave_pause;
    private double duration;
    private int current_wave_number;
    private int final_wave;
    private double next_pre_wave_pause;
    private int wave_framecount;

    private int total_monsters_current_wave;
    private int total_monsters;
    private int frames_per_spawn;
    private int double_frames;
    private int frame_track;

    private ArrayList<String> monster_types;
    private ArrayList<Monsters> monsters_to_spawn;
    private ArrayList<Monsters> spawned_monsters;
    private ArrayList<Monsters> to_remove;
    private HashMap<String, PImage> types;

    private JSONObject current_wave;
    private JSONArray waves;
    private NextWaveText next_wave_text;
    private Mana mana;
    private Board board;
    
    private PImage death_1;
    private PImage death_2;
    private PImage death_3;
    private PImage death_4;
    private PImage death_5;

    private boolean has_won;

    public Wave(Mana mana, JSONObject json, Board board, HashMap<String, PImage> types, PImage death_1, PImage death_2, PImage death_3, PImage death_4, PImage death_5) {
        super(json);
        this.board = board;
        waves = readJSONArray(json, "waves");
        current_wave_number = 1;
        current_wave = getCurrentWave(waves, current_wave_number);
        final_wave = waves.size();
        duration = getNumericAmount("duration", current_wave);
        total_monsters_current_wave = getTotalMonsters(readJSONArray(current_wave, "monsters"));
        total_monsters = total_monsters_current_wave;
        pre_wave_pause = getNumericAmount("pre_wave_pause", current_wave);
        this.types = types;
        wave_framecount = 0;
        this.mana = mana;
        to_remove = new ArrayList<>(); 
        has_won = false;

        this.death_1 = death_1;
        this.death_2 = death_2;
        this.death_3 = death_3;
        this.death_4 = death_4;
        this.death_5 = death_5;
        
        monster_types = getMonsterTypes(readJSONArray(current_wave, "monsters"));
        frames_per_spawn = setFramesPerSpawn(FPS);
        double_frames = setFramesPerSpawn(FPS/2);
        monsters_to_spawn = getMonstersToSpawn(types, board);
        spawned_monsters = new ArrayList<Monsters>();
        
        next_pre_wave_pause = getNumericAmount("pre_wave_pause", getCurrentWave(waves, current_wave_number+1));
        next_wave_text = new NextWaveText((int)pre_wave_pause, waves.size());
    }

    /**
     * Retrieves an ArrayList containing all the monster that have been currently spawned
     * @return spawned monsters
     */
    public ArrayList<Monsters> getSpawnedMonsters() {
        return spawned_monsters;
    }

    /**
     * Retrieves the total number of waves for this level
     * @return final_wave
     */
    public int getFinalWaveNumber() {
        return final_wave;
    }

    /**
     * Calculates the number of frames in between each monster spawn, where the value depends on the duration of the wave and the total monsters to spawn
     * @param FPS frames per second
     * @return frames_per_spawn
     */
    private int setFramesPerSpawn(double FPS) {
        double seconds = duration/total_monsters_current_wave;
        int frames_per_spawn;
        if (FPS > 0) {
            frames_per_spawn = (int)(seconds*FPS);
        }
        else {
            frames_per_spawn = (int)(seconds*GameElements.FPS);
        }

        return frames_per_spawn;
    }

    /**
     * Retrieves the number of frames in between each monster spawn
     * @return frames_per_spawn
     */
    public int getFramesPerSpawn() {
        return frames_per_spawn;
    }

    /**
     * Retrieves the duration of the current wave, which is the period in which monsters can be spawned
     * @return duration
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Retrieves total monsters that are alive currently
     * @return total_monsters
     */
    public int getTotalMonsters() {
        return total_monsters;
    }

    /**
     * Retrieves total monsters that exist in the current wave, excluding those that were carried on from previous waves
     * @return total_monsters_current_wave
     */
    public int getTotalMonstersCurrentWave() {
        return total_monsters_current_wave;
    }

    /**
     * Creates an ArrayList containing all the Monsters types and their quantities to spawn for the current wave
     * @param types HashMap where the PImage of a monster maps to its type indicated a String
     * @param board Board object to set valid paths for monsters to follow
     * @return monsters
     */
    private ArrayList<Monsters> getMonstersToSpawn(HashMap<String, PImage> types, Board board){
        ArrayList<Monsters> monsters = new ArrayList<Monsters>();
        
        for (Map.Entry<String, PImage> current : types.entrySet()) {
            if (monster_types.contains(current.getKey())) {
                Monsters new_monster = new Monsters(json, board, current_wave, current.getValue(), current.getKey(), death_1, death_2, death_3, death_4, death_5);
                for (int i = 0; i < new_monster.getQuantity(); i++) {      
                    Monsters clone = new_monster.clone();
                    clone.setSpawnedPath();              
                    monsters.add(clone); //create new monster objects of the correct quantity
                }
            }
        }
        return monsters;
    }

    /**
     * Allows user to spawn monster objects randomly.
     * @param monsters_to_spawn contains all monsters to spawn for a wave to remove from after they have been randomly spawned
     * @param spawned_monsters contains ArrayList of already spawned monsters to append to
     */
    private void randomMonsterSpawn(ArrayList<Monsters> monsters_to_spawn, ArrayList<Monsters> spawned_monsters) {
        int index = random.nextInt(monsters_to_spawn.size());
        spawned_monsters.add(monsters_to_spawn.get(index));
        monsters_to_spawn.remove(index);
    }

    /**
     * Logic for spawning monsters at the correct frame count and displaying them on screen if they are still alive.
     * Also shows the text that indicates the time until the next wave begins. 
     * The player can only continue to the next wave if they have enough mana remaining.
     * They can can only win the game if they get to the last wave and all monsters have been spawned and killed
     * @param app that instantiates this class
     * @param pause Pause object used to stop any movement when pause is selected
     * @param ff FastForward object used to speed up all movement when fast forward is selected
     */
    public void tick(PApplet app, Pause pause, FastForward ff) { 
        if (current_wave_number == final_wave && total_monsters == 0) {
            has_won = true;
        }
        else if (!pause.isSelected() && !mana.hasLost()) {
            wave_framecount++;
            this.next_wave_text.tick(app);
            frame_track = app.frameCount%frames_per_spawn; //track when to spawn monsters
            int fps = FPS;
            if (ff.isSelected()) {
                frame_track = app.frameCount%(double_frames);
                fps = (int)(FPS/2);
            }

            if ((int)pre_wave_pause > 0 && wave_framecount < fps*pre_wave_pause) {
                pre_wave_pause--;
                next_wave_text.decrement();
            }
            else if (pre_wave_pause > 0 && wave_framecount >= fps*pre_wave_pause) { //first pre_wave_pause check
                pre_wave_pause = 0;
                this.next_wave_text.setNextWave();
                this.next_wave_text.setSeconds((int)next_pre_wave_pause + (int)duration);
            }
            else if ((pre_wave_pause == 0) && (app.frameCount%fps == 0)) {
                if (duration > 0) {
                    duration--;
                }
                else if (next_pre_wave_pause > 0 ) {
                    next_pre_wave_pause--;
                }
                next_wave_text.decrement();
            }

            if (next_pre_wave_pause > 0 || current_wave_number == final_wave) {
                if (duration >= 0 && frame_track == 0 && monsters_to_spawn.size() > 0 ) { //logic to make sure monsters are spawned at the correct increments
                    randomMonsterSpawn(monsters_to_spawn, spawned_monsters);
                }

                for (Monsters current : spawned_monsters) {
                    if (ff.isSelected()) {
                        current.speedup();
                    }
                    else {
                        current.slowdown();
                    }
                    
                    if (current.isAlive() && !current.getRespawn()) {  
                        current.draw(app); 
                        current.move();
                        current.respawnMonster(mana);
                    }
                    else if (!current.isAlive()) {  
                        total_monsters_current_wave--; 
                        if (current.disappeared()) {
                            to_remove.add(current);
                            total_monsters--;
                        }
                        else {
                            current.draw(app);
                            current.deathAnimation(app, ff); 
                        }
                    }
                    else if (current.isAlive() && current.getRespawn() && frame_track == 0) {
                        current.setRespawn();
                    }
                                        
                }
                for (Monsters remove: to_remove) {
                    spawned_monsters.remove(remove);
                }
            } 
            else if (duration == 0 && next_pre_wave_pause == 0) {
                if (current_wave_number < final_wave) {
                    current_wave_number++;
                    newWave(app);    
                }
            }
        }
        else {
            for (Monsters current : spawned_monsters) {
                if (!current.disappeared()) {
                    current.draw(app);
                }
            }    
        }
    }
    
    /**
     * Renders the next wave text on screen
     * @param app that instantiates the class
     */
    public void draw(PApplet app) {
        this.next_wave_text.draw(app);
    }

    /**
     * Indicates whether the game has been won yet or not
     * @return has_won; true if all monsters have been killed after the final wave begins, false otherwise
     */
    public boolean hasWon() {
        return has_won;
    }

    /**
     * Allows the user to start a new wave by re-initialising the wave attributes for the next wave. 
     * This method should be invoked when the player has not yet reached the final wave and the duration of the current wave has ended with mana still remaining
     * @param app that instantiates the class
     */
    private void newWave(PApplet app) {
        current_wave = getCurrentWave(waves, current_wave_number);
        duration = getNumericAmount("duration", current_wave);
        total_monsters_current_wave = getTotalMonsters(readJSONArray(current_wave, "monsters"));
        total_monsters += total_monsters_current_wave;
        
        monster_types = getMonsterTypes(readJSONArray(current_wave, "monsters"));
        frames_per_spawn = setFramesPerSpawn(FPS);
        double_frames = setFramesPerSpawn(FPS/2);
        monsters_to_spawn = getMonstersToSpawn(types, board);

        if (current_wave_number < final_wave) {
            next_pre_wave_pause = getNumericAmount("pre_wave_pause", getCurrentWave(waves, current_wave_number + 1));
        }
        else {
            next_pre_wave_pause = 0;
        }
        next_wave_text.setNextWave();
        next_wave_text.setSeconds((int)next_pre_wave_pause + (int)duration);
    }

    /**
     * Renders the text that indicates the end of the game and whether the player has won or lost. 
     * The text to display depends on whether the player has killed all monsters to spawn for each wave and whether they have mana remaining
     * @param app that instantiates this class
     */
    public void displayWin(PApplet app) {
        String to_print = "";
        int[] colour;
        if (has_won) {
            to_print = "YOU WIN";
            colour = new int[]{255, 255, 255};
        }
        else if (mana.hasLost()) {
            to_print = "YOU LOST";
            colour = new int[]{0, 0, 0};
        }
        else {
            return;
        }

        app.fill(colour[0], colour[1], colour[2]);
        app.textSize(150);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text(to_print, 760/2, 680/2-50);

        app.textSize(40);
        app.text("Press 'r' to restart", 760/2, 680/2+50);
    }

    /**
     * For testing purposes to check the functionality tick() method; sets pre_wave_pause to 0.
     */
    public void setPreWavePauseZero() {
        pre_wave_pause = 0;
    }

    /**
     * For testing purposes to check the functionality tick() method; sets duration to 0.
     */
    public void setDurationZero() {
        duration = 0;
    }

    /**
     * For testing purposes to check the functionality tick() method; sets bext_pre_wave_pause to 0.
     */
    public void setNextPreWavePauseZero() {
        next_pre_wave_pause = 0;
    }

    /**
     * For testing purposes to check the functionality tick() method; sets total_monsters to 0.
     */
    public void setTotalMonstersZero() {
        total_monsters = 0;
    }

    /**
     * Retrieves the current wave number.
     * @return current_wave_number
     */
    public int getCurrentWaveNumber() {
        return current_wave_number;
    }

    /**
     * Retrieves the current wave framecount, which increments every time the tick() method is called and the game has not ended or been paused
     * @return wave_framecount
     */
    public int getWaveFrameCount() {
        return wave_framecount;
    }



}
