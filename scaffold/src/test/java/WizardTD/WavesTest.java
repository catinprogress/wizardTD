package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import static org.junit.jupiter.api.Assertions.*;

public class WavesTest extends App {

    @BeforeEach
    public void startSketch() {  
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";
        setup();
        delay(1000);
    }

    @Override
    public void draw() {

    }

    @Test
    public void testConstruction() {
        Wave wave = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);

        assertNotNull(wave, "Wave object is null");
    }

    @Test
    public void testMonstersToSpawn() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        int exp_monster_quantity = 8;

        assertEquals(exp_monster_quantity, wave_1.getTotalMonstersCurrentWave(), "Incorrect number of monsters to spawn retrieved for current wave");
    }

    //tests whether the amount of monsters to spawn per frame is accurate to the given duration and total quantity of monsters to spawn for wave 1
    @Test
    public void testFramesPerSpawn() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        int exp_frames_per_spawn = (int)((double)10/8*FPS);

        assertEquals((int)exp_frames_per_spawn, wave_1.getFramesPerSpawn(), "Number of frames per monster spawn is incorrect");
    }
    
    //tests whether monsters can be spawned at the correct framerate
    @Test
    public void testRandomMonsterSpawn() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        frameCount = wave_1.getFramesPerSpawn();
        wave_1.setPreWavePauseZero();
        
        wave_1.tick(this, p, ff);

        frameCount = wave_1.getFramesPerSpawn()*2;
        wave_1.tick(this, p, ff);
        
        int exp_spawned_monster_size = 2;

        assertEquals(exp_spawned_monster_size, wave_1.getSpawnedMonsters().size(), "Monsters are not spawning in correct amount of frames");
    }

    //test that new wave is initialized after the end of each wave
    @Test
    public void testNewWave() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);

        wave_1.setDurationZero();
        wave_1.setNextPreWavePauseZero();
        wave_1.setPreWavePauseZero();
        
        wave_1.tick(this, p, ff);
        
        int exp_wave_number = 2;

        assertEquals(exp_wave_number, wave_1.getCurrentWaveNumber(), "New wave was not initialised after previous wave duration ended");
    }

     //test that new wave is not initialized after the final wave
    @Test
    public void testNoNewWaveAfterFinalWave() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        int exp_wave_number = wave_1.getFinalWaveNumber();

        for (int i = 0; i <= exp_wave_number; i++) {
            wave_1.setDurationZero();
            wave_1.setNextPreWavePauseZero();
            wave_1.setPreWavePauseZero();
            
            wave_1.tick(this, p, ff); //wave ??
        }

        assertEquals(exp_wave_number, wave_1.getCurrentWaveNumber(), "A new wave should not be initialized after the final wave");
    }

    //test win is possible
    @Test
    public void testWin() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        
         for (int i = 0; i < wave_1.getFinalWaveNumber()-1; i++) {
            wave_1.setDurationZero();
            wave_1.setNextPreWavePauseZero();
            wave_1.setPreWavePauseZero();
            
            wave_1.tick(this, p, ff);
        }

        wave_1.setDurationZero();
        wave_1.setNextPreWavePauseZero();
        wave_1.setPreWavePauseZero();
        wave_1.setTotalMonstersZero(); //kill all monsters

        wave_1.tick(this, p, ff);

        assertTrue(wave_1.hasWon(), "Player did not win the game after final wave monsters have been killed");
    }

    //test that pause does not run any movement by checking that the wave framecount (which only increments when pause.isSelected() is true) does not increment
    @Test
    public void testPauseSelected() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);

        p.setSelected();
        int current_frame = wave_1.getWaveFrameCount();
        
        wave_1.tick(this, p, ff); 

        assertEquals(current_frame, wave_1.getWaveFrameCount(), "No movement should occur after pause button is selected");
    }

    //test that wave executes movement when pause is not selected by checking that the wave framecount (which only increments when pause.isSelected() is true) does increment
    @Test
    public void testPauseNotSelected() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);

        int exp_frame_count = wave_1.getWaveFrameCount() + 1;
        
        wave_1.tick(this, p, ff);

        assertEquals(exp_frame_count, wave_1.getWaveFrameCount(), "Movement should occur when pause is not selected");
    }

    //test that wave resumes movement when pause is not selected by checking that the wave framecount (which only increments when pause.isSelected() is true) increments twice after 2 unselected pause states
    @Test
    public void testPauseUnselected() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);

        int exp_frame_count = wave_1.getWaveFrameCount();
        
        wave_1.tick(this, p, ff);
        exp_frame_count++;

        p.setSelected();

        wave_1.tick(this, p, ff);

        p.setSelected();

        wave_1.tick(this, p, ff);
        exp_frame_count++;

        assertEquals(exp_frame_count, wave_1.getWaveFrameCount(), "Movement should resume when pause is unselected");
    }

    //test that movement of wave doubles in speed when fast forwards is selected
    @Test
    public void testFastForwardSelected() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        double current_duration = wave_1.getDuration(); //8.0
        wave_1.setPreWavePauseZero();
        
        ff.setSelected();
        frameCount = 30;

        wave_1.tick(this, p, ff); 
        current_duration--;

        assertEquals(current_duration, wave_1.getDuration(), 0.01, "Duration of wave should decrement every 30 frames if fast forward is selected");
    }

    //test that movement of wave remains the same when fast forward is not selected
    @Test
    public void testFastForwardNotSelected() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        double current_duration = wave_1.getDuration();
        wave_1.setPreWavePauseZero();

        frameCount = 60;

        wave_1.tick(this, p, ff); 
        current_duration--;

        assertEquals(current_duration, wave_1.getDuration(), 0.01, "Duration of wave should decrement every 60 frames if fast forward not selected");
    }

    //test that movement of wave reverts back to normal after fast forward is unselected
    @Test
    public void testFastForwardUnselected() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        double current_duration = wave_1.getDuration();
        wave_1.setPreWavePauseZero();
        frameCount = 60;

        wave_1.tick(this, p, ff); 
        current_duration--; 

        ff.setSelected(); //true
        frameCount = 90;

        wave_1.tick(this, p, ff); 
        current_duration--; //6

        ff.setSelected(); //false
        frameCount = 120;

        wave_1.tick(this, p, ff);
        current_duration--;

        assertEquals(current_duration, wave_1.getDuration(), 0.01, "Speed of duration decrement per second should revert back to 60 frames after fast forward is unselected");
    }

    //test that killed monsters are removed from arraylist of monsters to spawn
    @Test
    public void testRemovingKilledMonsters() {
        Wave wave_1 = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        int exp_total = wave_1.getTotalMonsters();
        wave_1.setPreWavePauseZero();

        frameCount = wave_1.getFramesPerSpawn();

        wave_1.tick(this, p, ff); //1 spawned monster
        Monsters monster = wave_1.getSpawnedMonsters().get(0);
        monster.kill();
        
        frameCount++;

        for (int i = 0; i <= 21; i++) { //4 frames per death image, 5 images in total --> 4*5 = 20
            wave_1.tick(this, p, ff);
            frameCount++;
        }
        exp_total--;

        assertEquals(exp_total, wave_1.getTotalMonsters(), "Monster should be removed from total remaining monsters after it is killed");
        
    }
    
}
