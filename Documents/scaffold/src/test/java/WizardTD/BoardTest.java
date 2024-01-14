package WizardTD;

import processing.core.PApplet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


public class BoardTest extends App {
    
    @BeforeEach
    public void create() {
        loop();
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);
    }

    @Test
    public void testConstruction() {
        Board board = new Board(this, grass, shrub, wizard_house, horizontal_path, right_down_path, t_path, cross_path, json, 680);
        assertNotNull(board, "Board object is null");
    }

}
