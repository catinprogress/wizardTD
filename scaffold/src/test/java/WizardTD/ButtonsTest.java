package WizardTD;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;

import static org.junit.jupiter.api.Assertions.*;



public class ButtonsTest extends App {
    @Override
    public void draw() {

    }

    @Test
    public void testUpgradeRangeConstruction() {
        UpgradeRange range = new UpgradeRange();

        assertNotNull(range, "UpgradeRange object is null");
    }

    @Test
    public void testUpgradeSpeedConstruction() {
        UpgradeSpeed speed = new UpgradeSpeed();

        assertNotNull(speed, "UpgradeSpeed object is null");
    }

    @Test
    public void testUpgradeDamageConstruction() {
        UpgradeDamage damage = new UpgradeDamage();

        assertNotNull(damage, "UpgradeDamage object is null");
    }

    @Test
    public void testManaPoolSpellConstruction() {
        ManaPoolSpell mana_pool = new ManaPoolSpell(100);

        assertNotNull(mana_pool, "ManaPoolSpell object is null");
    }

    @Test
    public void testPauseConstruction() {
        Pause pause = new Pause();

        assertNotNull(pause, "Pause object is null");
    }

    @Test
    public void testFastForwardConstruction() {
        FastForward fast_foward = new FastForward();

        assertNotNull(fast_foward, "FastForward object is null");
    }

  

    //Test the buttons are initialised to unselected state when instantiated
    @Test
    public void testUpgradeRangeSelected() {
        UpgradeRange range = new UpgradeRange();

        assertFalse(range.isSelected(), "Buttons should be initialised to unselected when instantiated");
    }

    @Test
    public void testUpgradeSpeedSelected() {
        UpgradeSpeed speed = new UpgradeSpeed();

        assertFalse(speed.isSelected(), "Buttons should be initialised to unselected when instantiated");
    }

    @Test
    public void testUpgradeDamageSelected() {
        UpgradeDamage damage = new UpgradeDamage();

        assertFalse(damage.isSelected(), "Buttons should be initialised to unselected when instantiated");
    }

    
    @Test
    public void testBuildTowerSelected() { 
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";  
        setup();
        delay(1000);
        
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);

        assertFalse(build.isSelected(), "Buttons should be initialised to unselected when instantiated");
    }


    @Test
    public void testManaPoolSpellSelected() {
        ManaPoolSpell mana_pool = new ManaPoolSpell(100);

        assertFalse(mana_pool.isSelected(), "Buttons should be initialised to unselected when instantiated");
    }

    @Test
    public void testPauseSelected() {
        Pause pause = new Pause();

        assertFalse(pause.isSelected(), "Buttons should be initialised to unselected when instantiated");
    }

    @Test
    public void testFastForwardSelected() {
        FastForward fast_foward = new FastForward();

        assertFalse(fast_foward.isSelected(), "Buttons should be initialised to unselected when instantiated");
    }

    
    @Test
    public void testHoleSelected() {
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";  
        setup();
        delay(1000);
        Hole test_hole = new Hole(hole, board);

        assertFalse(test_hole.isSelected(), "Buttons should be initialised to unselected when instantiated");
    }

    //Test static variable key_was_pressed sets to the correct boolean value
    @Test
    public void testSetKeyWasPressed() {
        GameplayActions.setKeyWasPressed(true);

        assertTrue(GameplayActions.keyWasPressed(), "key_was_pressed static variable was not set to the correct boolean value");
    }

    //Test that mouseClicked() method sets mouseClick to correct boolean value
    @Test
    public void testMouseClicked() {
        UpgradeRange range = new UpgradeRange();

        range.setMouseClicked();
        assertTrue(range.getMouseClicked(), "mouseClicked variable was not set to the correct boolean value");
    }

    //Test that each key has the correct key flag

    @Test
    public void testUpgradeRangeKey() {
        UpgradeRange range = new UpgradeRange();

        assertEquals( '1', range.getKey(), "Incorrect key flag for UpgradeRange subclass");
    }

    @Test
    public void testUpgradeSpeedKey() {
        UpgradeSpeed speed = new UpgradeSpeed();

        assertEquals( '2', speed.getKey(), "Incorrect key flag for UpgradeSpeed subclass");
    }

    @Test
    public void testUpgradeDamagKey() {
        UpgradeDamage damage = new UpgradeDamage();

        assertEquals('3', damage.getKey(), "Incorrect key flag for UpgradeDamage subclass");
    }

    
    @Test
    public void testBuildTowerKey() {   
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";  
        setup();
        delay(1000);

        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);

        assertEquals('t', build.getKey(), "Incorrect key flag for BuildTower subclass");
    }
    

    @Test
    public void testManaPoolSpellKey() {
        ManaPoolSpell mana_pool = new ManaPoolSpell(100);

        assertEquals('m', mana_pool.getKey(), "Incorrect key flag for ManaPoolSpell subclass");
    }

    @Test
    public void testPauseKey() {
        Pause pause = new Pause();

        assertEquals('p', pause.getKey(), "Incorrect key flag for Pause subclass");
    }

    @Test
    public void testFastForwardKey() {
        FastForward fast_foward = new FastForward();

        assertEquals('f', fast_foward.getKey(), "Incorrect key flag for FastForward subclass");
    }

     
    @Test
    public void testHoleKey() { 
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        Hole test_hole = new Hole(hole, board);

        assertEquals('h', test_hole.getKey(), "Incorrect key flag for Hole subclass");
    }
    
    //Test that setSelected sets selected variable to correct boolean value

    @Test
    public void testSetSelected() {
        Pause pause = new Pause();
        pause.setSelected(); //true
        pause.setSelected(); //false

        assertFalse(pause.isSelected(), "Button was not set to the correct boolean value");
    }

    //Test logic for toggling buttons when the correct key is pressed 
    @Test
    public void testKeyPressSelect() {
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        UpgradeRange range = new UpgradeRange();
        GameplayActions.setKeyWasPressed(true);
        key = range.getKey();

        range.drawButtons(this, mana, wave);

        assertTrue(range.isSelected(), "Button was not set to selected after key was pressed");
    }

    @Test
    public void testKeyPressUnselect() {
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";  
        setup();
        delay(1000);

        UpgradeRange range = new UpgradeRange();
        GameplayActions.setKeyWasPressed(true);
        key = range.getKey();

        range.drawButtons(this, mana, wave);

        GameplayActions.setKeyWasPressed(true);
        key = range.getKey();

        range.drawButtons(this, mana, wave);
        assertFalse(range.isSelected(), "Button was not unselected after key was pressed a second time");
    }

    //Test logic for toggling buttons when incorrect key is pressed
    @Test
    public void testIncorrectKeyPress() {   
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        UpgradeSpeed speed = new UpgradeSpeed();
        GameplayActions.setKeyWasPressed(true);
        key = 'z';

        speed.drawButtons(this, mana, wave);
        assertFalse(speed.isSelected(), "Invalid key press event should not toggle button");
    }

    //Test logic for toggling buttons when mouse clicks over button coordinates
    @Test
    public void testMouseSelect() {  
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";  
        setup();
        delay(1000);

        Pause pause = new Pause();
        pause.setMouseClicked();
        mouseX = (int)pause.getX();
        mouseY = (int)pause.getY();

        pause.drawButtons(this, mana, wave);
        assertTrue(pause.isSelected(), "Button was not selected after mouse was clicked");
    }

    @Test 
    public void testMouseUnselect() {
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";  
        setup();
        delay(1000);

        Pause pause = new Pause();
        pause.setMouseClicked();
        mouseX = (int)pause.getX();
        mouseY = (int)pause.getY();

        pause.drawButtons(this, mana, wave); //selected = true
        pause.setMouseClicked();

        pause.drawButtons(this, mana, wave); //selected = false
        assertFalse(pause.isSelected(), "Button was not unselected after mouse was clicked a second time");
    }

    //Test logic for toggling buttons when mouse is clicked somewhere that is not where the button is on the screen
    @Test
    public void testInvalidMouseCoords() { 
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";  
        setup();
        delay(1000);

        ManaPoolSpell pool = new ManaPoolSpell(300);
        pool.setMouseClicked();
        mouseX = (int)pool.getX() - 20;
        mouseY = (int)pool.getY() - 5;

        pool.drawButtons(this, mana, wave);
        assertFalse(pool.isSelected(), "Button should not be selected when mouse is not clicked in the correct location");
    }

    //Test logic for toggling buttons using both key and mouse
    @Test
    public void testMouseSelectAndKeyUnselect() {
        PApplet.runSketch(new String[] { "App" }, this);
        configPath = "testconfig.json";  
        setup();
        delay(1000);

        FastForward fast_forward = new FastForward();
        fast_forward.setMouseClicked();
        mouseX = (int)fast_forward.getX() + GameplayActions.DIMENSION;
        mouseY = (int)fast_forward.getY() + 3;
        
        fast_forward.drawButtons(this, mana, wave); //selected with mouse

        key = fast_forward.getKey();
        GameplayActions.setKeyWasPressed(true);

        fast_forward.drawButtons(this, mana, wave); //unselected with key

        assertFalse(fast_forward.isSelected(), "Button presses should be toggled and untoggled with key press or mouse click");
    }
    
}