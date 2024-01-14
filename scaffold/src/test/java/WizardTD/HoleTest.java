package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import processing.core.PApplet;
import java.util.ArrayList;

public class HoleTest extends App {
    @BeforeEach
    public void startSketch() {
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "simplemap.json";
        setup();
        delay(1000);
    }
    
    @Test
    public void testHoleConstruction() {
        Hole hole = new Hole(this.hole, board);

        assertNotNull(hole, "Hole object is null");
    }

    //test logic for conditions of purchasing a hole 
    @Test
    public void testNewHolePlaced() {
        Hole hole = new Hole(this.hole, board);
        ArrayList<int[]> path_coords = board.getPathTiles();
        int[] test_coords = path_coords.get(5);
        
        mouseX = test_coords[0]; //conditions to purchase hole
        mouseY = test_coords[1];
        hole.setMouseClicked();
        hole.setSelected();
        double exp_mana = mana.getCurrentMana() - hole.getHoleCost();

        hole.tick(this, mana, wave, ff);

        assertEquals(exp_mana, mana.getCurrentMana(), 0.01, "New hole was not purchased when hole button was selected");
    }

    //logic for testing that after a hole is purchased, a new hole cannot be purchased again until after 5 seconds where the cost of the hole will increase
    @Test
    public void testHolePlacedReset() {
        Hole hole = new Hole(this.hole, board);
        ArrayList<int[]> path_coords = board.getPathTiles();
        int[] test_coords = path_coords.get(10);
        
        mouseX = test_coords[0];
        mouseY = test_coords[1];
        hole.setMouseClicked();
        hole.setSelected();

        hole.tick(this, mana, wave, ff); //purchase a hole

        int exp_cost = hole.getHoleCost() + Hole.HOLE_COST; //cost of new hole

        hole.setSelected();

        frameCount = 0;
        for (int i = 0; i <= Hole.HOLE_WAIT; i++) { //wait over 5 seconds to set new hole cost
            hole.tick(this, mana, wave, ff);
            frameCount += FPS;
        }

        assertEquals(exp_cost, hole.getHoleCost(), "Cost of hole should increase 5 seconds after a hole is placed");
    }
    
    //logic for testing that if the player attempts to purchase a hole before 5 seconds after previous purchase, no change should occur
    @Test
    public void testHolePlacedTime() {
        Hole hole = new Hole(this.hole, board);
        ArrayList<int[]> path_coords = board.getPathTiles();
        int[] test_coords = path_coords.get(10);
        
        mouseX = test_coords[0];
        mouseY = test_coords[1];
        hole.setMouseClicked();
        hole.setSelected();

        hole.tick(this, mana, wave, ff); //purchase a hole

        int exp_cost = hole.getHoleCost(); //cost of new hole

        frameCount = 0;
        for (int i = 0; i < Hole.HOLE_WAIT - 2; i++) { //wait less than 5 seconds
            hole.tick(this, mana, wave, ff);
            frameCount += FPS;
        }

        assertEquals(exp_cost, hole.getHoleCost(), "New hole cannot be purchased until 5 seconds after previous hole is placed");
    }

    //test that after a monster is killed, the hole that it fell into disappears
    @Test
    public void testMonsterKill() {
        ArrayList<ArrayList<int[]>> valid_paths = board.getValidPaths();
        ArrayList<int[]> path = valid_paths.get(0); //only one valid path for this map
        mouseX = path.get(0)[0]; //place hole at the spawn site
        mouseY = path.get(0)[1];
        int exp_size = h.getHolesPurchased().size(); //previous size
        h.setMouseClicked();
        h.setSelected();
        h.tick(this, mana, wave, ff);

        h.setSelected();

        int current_size = wave.getTotalMonsters();
        
        while (wave.getTotalMonsters() >= current_size) {
            loop();
        }
        noLoop();

        assertEquals(exp_size, h.getHolesPurchased().size(), "Purchased hole did not disappear after a monster was killed");
    }

}
