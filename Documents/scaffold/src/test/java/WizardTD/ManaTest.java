package WizardTD;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class ManaTest extends App {

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
        Mana test_mana = new Mana(json);

        assertNotNull(test_mana, "Mana object is null");
    }

    @Test
    public void testCorrectInitialMana() {
        Mana test_mana = new Mana(json);
        double exp_mana = json.getDouble("initial_mana");

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.001, "Incorrect initial mana value retrieved");
        
    }

    @Test
    public void testCorrectInitialManaCap() {
        Mana test_mana = new Mana(json);
        double exp_mana = json.getDouble("initial_mana_cap");

        assertEquals(exp_mana, test_mana.getCurrentManaCap(), 0.001, "Incorrect initial mana cap value retrieved"); 
    }

    @Test
    public void testIncreaseMana() {
        Mana test_mana = new Mana(json);
        double exp_mana = test_mana.getCurrentMana() + 100;
        test_mana.changeCurrentMana(100);

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.001, "Current mana did not increase to correct value");
    }

    @Test
    public void testDecreaseMana() {
        Mana test_mana = new Mana(json);
        double exp_mana = test_mana.getCurrentMana() - 20;
        test_mana.changeCurrentMana(-20);

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.001, "Current mana did not decrease to correct value");
    }

    @Test
    public void testIncreaseManaBeyondManaCap() {
        Mana test_mana = new Mana(json);
        double exp_mana = test_mana.getCurrentMana();
        test_mana.changeCurrentMana(10 + test_mana.getCurrentManaCap());

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.001, "Current mana cannot be increased to exceed current mana cap");
    }

    @Test
    public void testDecreaseManaToZeroInvalid() {
        Mana test_mana = new Mana(json);
        double exp_mana = test_mana.getCurrentMana();
        test_mana.changeCurrentMana(-(test_mana.getCurrentMana() + 20));

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.001, "Current mana cannot decrease less than or equal to 0 with changeCurrentMana method");
    }

    @Test
    public void testDecreaseManaToBelowZero() {
        Mana test_mana = new Mana(json);
        test_mana.decreaseMana(test_mana.getCurrentMana() + 20);

        assertFalse(test_mana.getCurrentMana() < 0, "Mana value cannot be decreased to negative value");
    }

    @Test
    public void testDecreaseManaMinimumValue() {
        Mana test_mana = new Mana(json);
        test_mana.decreaseMana(test_mana.getCurrentMana() + 20);

        assertTrue(test_mana.getCurrentMana() == 0, "Minimum mana value that mana can be decreased to with decreaseMana method is 0");
    }

    

}
