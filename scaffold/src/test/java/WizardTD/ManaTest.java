package WizardTD;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class ManaTest extends App {

    @BeforeEach
    public void startSketch() {
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";
        setup();
        delay(1000);
    }

    @Test
    public void testConstruction() {
        Mana test_mana = new Mana(json);

        assertNotNull(test_mana, "Mana object is null");
    }

    //check correct mana values are read from config file
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

    //check that mana can be increased to correct amount
    @Test
    public void testIncreaseMana() {
        Mana test_mana = new Mana(json);
        double exp_mana = test_mana.getCurrentMana() + 100;
        test_mana.changeCurrentMana(100);

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.001, "Current mana did not increase to correct value");
    }

    //check that mana can be decreased to correct amount
    @Test
    public void testDecreaseMana() {
        Mana test_mana = new Mana(json);
        double exp_mana = test_mana.getCurrentMana() - 20;
        test_mana.changeCurrentMana(-20);

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.001, "Current mana did not decrease to correct value");
    }

    //check that you can't increase mana beyond mana cap
    @Test
    public void testIncreaseManaBeyondManaCap() {
        Mana test_mana = new Mana(json);
        double exp_mana = test_mana.getCurrentMana();
        test_mana.changeCurrentMana(10 + test_mana.getCurrentManaCap());

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.001, "Current mana cannot be increased to exceed current mana cap");
    }

    //check that player cant' cause themselves to die by overpurchasing items
    @Test
    public void testDecreaseManaToZeroInvalid() {
        Mana test_mana = new Mana(json);
        double exp_mana = test_mana.getCurrentMana();
        test_mana.changeCurrentMana(-(test_mana.getCurrentMana() + 20));

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.001, "Current mana cannot decrease less than or equal to 0 with changeCurrentMana method");
    }

    //check that mana cannot be decreased to below 0 when monsters attack wizard house
    @Test
    public void testDecreaseManaToBelowZero() {
        Mana test_mana = new Mana(json);
        test_mana.decreaseMana(test_mana.getCurrentMana() + 20);

        assertFalse(test_mana.getCurrentMana() < 0, "Mana value cannot be decreased to negative value");
    }

    //test that decreasing below current mana value sets mana to 0
    @Test
    public void testDecreaseManaMinimumValue() {
        Mana test_mana = new Mana(json);
        test_mana.decreaseMana(test_mana.getCurrentMana() + 20);

        assertTrue(test_mana.getCurrentMana() == 0, "Minimum mana value that mana can be decreased to with decreaseMana method is 0");
    }

    //test that mana pool spell purchase works
    @Test 
    public void testManaPoolIncrease() {
        Mana test_mana = new Mana(json);
        double mana_pool_cost = json.getDouble("mana_pool_spell_initial_cost");
        
        GameplayActions.setKeyWasPressed(true);
        key = test_mana.getManaPoolSpell().getKey(); //select mana pool button
    
        double exp_mana = test_mana.getCurrentMana() - mana_pool_cost;

        test_mana.draw(this, wave); //execute mana pool button press logic
        test_mana.tick(this, p, ff, wave); //cost should decrease

        assertEquals(exp_mana, test_mana.getCurrentMana(), 0.01, "Mana pool spell did not get purchased after button was selected");
    }
   

}
