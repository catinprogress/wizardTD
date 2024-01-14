package WizardTD;

import processing.core.PApplet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


public class AppTest extends App {
    @BeforeEach
    public void create() {
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";
        setup();
        delay(1000); 
    }

    @Test
    public void testBoardConstruction() {
        noLoop();
        Board board = new Board(this, grass, shrub, wizard_house, horizontal_path, right_down_path, t_path, cross_path, json, 680);
        assertNotNull(board, "Board object is null");
    }

    //check that mouse click event toggles mouseClicked variable for buttons
    @Test
    public void testMouseClick() {
        redraw();
        mouseClicked();

        assertTrue(ff.getMouseClicked() && p.getMouseClicked() && h.getMouseClicked() && mana.getManaPoolSpell().getMouseClicked() && u1.getMouseClicked() && u2.getMouseClicked() && u3.getMouseClicked() && b.getMouseClicked());
    }

    //check that key press sets the static variable to indicate a key press
    @Test
    public void testKeyPress() {
        redraw();
        keyPressed();

        assertTrue(GameplayActions.keyWasPressed());
    }

    //check that an unpressed key doesn't set the static variable to indicate a key press
    @Test
    public void testKeyUnpressed() {
        redraw();
        keyReleased();

        assertFalse(GameplayActions.keyWasPressed());
    }

    //check that after monsters attack wizard house and mana runs out, game can be restarted
    @Test
    public void gameRestart() {
        Mana old_mana = mana;
        while (!old_mana.hasLost()) {
            loop();
        }
        key = 'r';
        keyPressed();

        delay(1000);
        noLoop();

        assertFalse(old_mana == mana, "Game was not restarted after player lost and player pressed r key");
    }

}
