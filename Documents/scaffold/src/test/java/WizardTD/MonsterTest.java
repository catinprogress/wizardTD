package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

import processing.core.PImage;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import java.util.ArrayList;

public class MonsterTest extends App {
    @BeforeEach
    public void startSketch() {
        configPath = "testconfig.json";
        loop();
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);
        noLoop();
    }

   
    @Test
    public void testConstruction() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);

        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);
        assertNotNull(new_monster, "Monster object is null");
    }

    @Test
    public void testIsAliveWhenCreated() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);

        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);
        assertTrue(new_monster.isAlive(), "Monsters constructor did not initialise monster alive status to true");
    }

    @Test
    public void testCorrectHP() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);
        JSONArray monsters = wave_1.getJSONArray("monsters");
        int hp = monsters.getJSONObject(0).getInt("hp");
        
        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);

        assertEquals(hp, new_monster.getHP(), "Incorrect hp of monsters retrieved");
    }

    @Test
    public void testCorrectSpeed() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);
        JSONArray monsters = wave_1.getJSONArray("monsters");
        double speed = monsters.getJSONObject(0).getDouble("speed");
        
        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);

        assertEquals(speed, new_monster.getSpeed(), 0.01, "Incorrect speed of monsters retrieved");
    }

    @Test
    public void testCorrectArmour() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);
        JSONArray monsters = wave_1.getJSONArray("monsters");
        double armour = monsters.getJSONObject(0).getDouble("armour");
        
        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);

        assertEquals(armour, new_monster.getArmour(), 0.01, "Incorrect armour of monsters retrieved");
    }

    @Test
    public void testCorrectMana() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);
        JSONArray monsters = wave_1.getJSONArray("monsters");
        int mana = monsters.getJSONObject(0).getInt("mana_gained_on_kill");
        
        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);

        assertEquals(mana, new_monster.getManaGained(), "Incorrect mana_gained_on_kill of monsters retrieved");
    }

    @Test
    public void testCorrectQuantity() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);
        JSONArray monsters = wave_1.getJSONArray("monsters");
        int quantity = monsters.getJSONObject(0).getInt("quantity");
        
        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);

        assertEquals(quantity, new_monster.getQuantity(), "Incorrect quantity of monsters retrieved");
    }

    @Test
    public void testClone() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);

        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);
        Monsters clone = new_monster.clone();

        assertNotEquals(new_monster, clone, "clone method did not create a new instance of the cloned Monsters object");
    }

    @Test
    public void testKillWhenAlive() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);

        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);
        
        new_monster.kill();

        assertFalse(new_monster.isAlive(), "Monster alive status incorrect");  
    }

    @Test
    public void testQuantityAfterKill() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);

        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);
        int quantity = new_monster.getQuantity();
        
        new_monster.kill();

        assertEquals(quantity-1, new_monster.getCurrentQuantity(), "Current monster quantity did not decrement"); 
    }

    //positive test case --> damage is positive value
    @Test
    public void testAttackPositive() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);
        int damage = 20;

        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);
        new_monster.setSpawnedPath();
        double armour = new_monster.getArmour();
        float initial_hp = new_monster.getHP();

        new_monster.attack(damage);
        assertEquals((float)(initial_hp-(1-armour)*damage), new_monster.getHP(), 0.01, "Monster HP did not decrease the correct amount");
    }

    //negative test case --> damage is negative value
    @Test
    public void testAttackNegative() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);
        int neg_damage = -20;

        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);
        float initial_hp = new_monster.getHP();

        new_monster.attack(neg_damage);
        assertEquals(initial_hp, new_monster.getHP(), 0.001,"Monster HP should not change if damage is negative");
    }

    //test that monster spawns in valid spawn location
    @Test
    public void testSpawn() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);

        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);
        new_monster.setSpawnedPath();

        boolean location_valid = false;
        for (ArrayList<int[]> path : board.getValidPaths()) {
            int[] start = path.get(0);
            float x = start[0] + Monsters.OFFSET;
            float y = start[1] + Monsters.OFFSET;
            if ((Math.abs(new_monster.getX()-x) < 0.001) && ((new_monster.getY()-y) < 0.001)) {
                location_valid = true;
                break;
            }
        }
        
        assertTrue(location_valid, "Monster did not spawn in a valid spawn location");
    }

    //test that monster can move
    @Test
    public void testMove() {
        JSONArray waves = json.getJSONArray("waves");
        JSONObject wave_1 = waves.getJSONObject(0);

        Monsters new_monster = new Monsters(json, board, wave_1, gremlin, "gremlin", death_1, death_2, death_3, death_4, death_5);
        new_monster.setSpawnedPath();

        assertTrue(new_monster.move(), "move method did not move alive Monster");
    }
 /* 
    //test that monster moves on correct path and arrives at wizard house
    @Test
    public void testRespawnMonster() {

    }
    */
}   
