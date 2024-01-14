package WizardTD;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.omg.CosNaming._BindingIteratorImplBase;

import static org.junit.jupiter.api.Assertions.*;

import processing.core.PApplet;

public class TowerTest extends App {


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
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);

        assertNotNull(test_tower, "Tower object is null");
    }

    //test that correct range radius is read from config file
    @Test
    public void testCorrectRange() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, this.fireball);
        double exp_range = json.getDouble("initial_tower_range");

        assertTrue(Math.abs(exp_range - test_tower.getRadius()) < Math.pow(10, -4), "Incorrect tower radius was read from config file");
    }

    //test that correct firing speed is read from config file
    @Test
    public void testCorrectSpeed() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);
        double exp_speed = json.getDouble("initial_tower_firing_speed");

        assertTrue(Math.abs(exp_speed - test_tower.getSpeed()) < Math.pow(10, -4), "Incorrect tower firing speed was read from config file");
    }

    //test that correct damage is read from config file
    @Test
    public void testCorrectDamage() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);
        double exp_damage = json.getDouble("initial_tower_damage");

        assertTrue(Math.abs(exp_damage - test_tower.getDamage()) < Math.pow(10, -4), "Incorrect tower damage was read from config file");
    }

    //test that correct cost for respective tower level is retrieved after tower is upgraded
    @Test
    public void testCorrectCostRetrieved() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, this.fireball);
        
        int exp_cost = 40;
        u1.setSelected();
        u3.setSelected();

        test_tower.upgradeTower(mana, u1, u3, u2);
        assertEquals(exp_cost, test_tower.getCost(), "Incorrect cost retrieved after upgrade options were selected");      
    }

    //test that if no upgrade options are selected, the cost of the upgrade remains 0
    @Test
    public void testCostRemainsZero() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);
        int exp_cost = 0;

        test_tower.upgradeTower(mana, u1, u3, u2);
        assertEquals(exp_cost, test_tower.getCost(), "Cost of tower upgrade if no upgrade options are selected should remain 0");
    }

    //test that range level does not change if range option is not selected
    @Test
    public void testUnselectedRangeUpgradeLevel() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);
        int exp_level = 0;
        test_tower.upgradeTower(mana, u1, u3, u2);

        assertEquals(exp_level, test_tower.getRangeLevel(), "Tower range upgraded when upgrade range option not selected");
    }

    //test that damage level does not change if damage option is not selected
    @Test
    public void testUnselectedDamageUpgradeLevel() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);
        int exp_level = 0;
        test_tower.upgradeTower(mana,  u1, u3, u2);

        assertEquals(exp_level, test_tower.getDamageLevel(), "Tower damage upgraded when upgrade damage option not selected");
    }

    //test that speed level does not change if speed option is not selected
    @Test
    public void testUnselectedSpeedpgradeLevel() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);
        int exp_level = 0;
        test_tower.upgradeTower(mana,  u1, u3, u2);

        assertEquals(exp_level, test_tower.getSpeedLevel(), "Tower speed upgraded when upgrade speed option not selected");
    }

    //test that range upgrades to correct level if range option is selected
    @Test
    public void testUpgradeLevelRange() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);

        u1.setSelected();
        int exp_level = 1;
        test_tower.upgradeTower(mana,  u1, u3, u2);

        assertEquals(exp_level, test_tower.getRangeLevel(), "Tower range did not upgrade to the correct level");
    }

    //test that speed upgrades to correct level if speed option is selected
    @Test
    public void testUpgradeLevelSpeed() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);
        
        u2.setSelected();
        int exp_level = 1;
        test_tower.upgradeTower(mana,  u1, u3, u2);

        assertEquals(exp_level, test_tower.getSpeedLevel(), "Tower speed did not upgrade to the correct level");
    }

    //test that damage upgrades to correct level if damage option is selected
    @Test
    public void testUpgradeLevelDamage() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);

        u3.setSelected();
        int exp_level = 1;
        test_tower.upgradeTower(mana,  u1, u3, u2);

        assertEquals(exp_level, test_tower.getDamageLevel(), "Tower damage did not upgrade to the correct level");
    }

    //test that tower level upgrades after range, damage and speed all upgrade to the same level
    @Test
    public void testUpgradeTowerLevel() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);

        u1.setSelected();
        u2.setSelected();
        u3.setSelected();

        int exp_tower_level = 2;
        test_tower.upgradeTower(mana, u1, u3, u2);

        assertEquals(exp_tower_level, test_tower.getTowerLevel(), "Tower level did not increase after range, speed and damage upgraded to same level");
    }

    //test multiple upgrades to range level
    @Test
    public void testMultipleRangeUpgrades() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);

        u1.setSelected();
        test_tower.upgradeTower(mana, u1, u3, u2);
        test_tower.upgradeTower(mana,  u1, u3, u2);

        int exp_level = 2;

        assertEquals(exp_level, test_tower.getRangeLevel(), "Tower range level did not increase correctly after range was upgraded twice");
    }
    
    //test that multiple upgrades to one level doesn't increase overall tower level
    @Test
    public void testMultipleTowerUpgrades() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);
        int exp_level = test_tower.getTowerLevel();

        u1.setSelected();
        test_tower.upgradeTower(mana,  u1, u3, u2);
        test_tower.upgradeTower(mana,  u1, u3, u2);

        assertEquals(exp_level, test_tower.getTowerLevel(), "Tower level should remain the same if not all upgrade options are upgraded to the same level");
    }

    //test that there are no fired fireballs when towers are created
    @Test
    public void initialFireballsFired() {
        Tower test_tower = new Tower(json, b.getTowerImages(), 20, 20, fireball);

        assertTrue(test_tower.getFireballsFired().isEmpty(), "No fireballs should be fired when a tower is instantiated");
    }

    //Test that fireballs can be created to fire when monster is within range of the tower at the correct speed
    @Test
    public void testFiringFireballs() {
        int stop_at = frameCount + (5*FPS);
        while (frameCount != stop_at) { //run game for 5 seconds to spawn monsters
            loop();
        }
        noLoop();

        Monsters test_monster = wave.getSpawnedMonsters().get(0);
        int x = (int)test_monster.getX() + 2; //initial radius of tower range is 96
        int y = (int)test_monster.getY() + 2;

        Tower tower = new Tower(json, b.getTowerImages(), x, y, fireball); //create fireball to fire
        frameCount = (int)(Math.round(FPS/tower.getSpeed())); //number of frames between fireball being fired

        tower.tick(this, b.getCostShow(), wave.getSpawnedMonsters(), mana, ff, p);

        assertTrue(tower.getFireballsFired().size() > 0, "Fireball objects should be fired when a monster is within range");
    }

    //Test that fireball can attack alive monsters
    @Test
    public void testAttackingAliveMonsters() {
        int stop_at = frameCount + (5*FPS);
        while (frameCount != stop_at) {
            loop();
        }
        noLoop();

        Monsters test_monster = wave.getSpawnedMonsters().get(0);
        int x = (int)test_monster.getX() + 2; //initial radius of tower range is 96
        int y = (int)test_monster.getY() + 2;
        float initial_hp = test_monster.getHP();

        Tower tower = new Tower(json, b.getTowerImages(), x, y, fireball);
        frameCount = (int)(Math.round(FPS/tower.getSpeed()));

        tower.tick(this, b.getCostShow(), wave.getSpawnedMonsters(), mana, ff, p);
        Fireball to_fire = tower.getFireballsFired().get(0);

        while (!to_fire.targetReached()) { //run logic until fireball reaches its target
            tower.tick(this, b.getCostShow(), wave.getSpawnedMonsters(), mana, ff, p);
        }

        tower.tick(this, b.getCostShow(), wave.getSpawnedMonsters(), mana, ff, p); //attack monster if alive

        assertTrue(test_monster.getHP() < initial_hp , "Fireball object should damage monster and reduce its HP when reached");
    }

    //Test that mana is reduced after fireball kills a monster
    @Test
    public void testAttackingKilledMonsters() {
        int stop_at = frameCount + (5*FPS);
        while (frameCount != stop_at) {
            loop();
        }
        noLoop();

        Monsters test_monster = wave.getSpawnedMonsters().get(0);
        int x = (int)test_monster.getX() + 2; //initial radius of tower range is 96
        int y = (int)test_monster.getY() + 2;

        Tower tower = new Tower(json, b.getTowerImages(), x, y, fireball);
        frameCount = (int)(Math.round(FPS/tower.getSpeed()));

        tower.tick(this, b.getCostShow(), wave.getSpawnedMonsters(), mana, ff, p);
        Fireball to_fire = tower.getFireballsFired().get(0);
        Monsters to_kill = tower.getFireballs().get(to_fire);

        while (!to_fire.targetReached()) { //fire fireball until fireball reaches its target
            to_fire.fire();
        }

        to_kill.kill(); //kill monster when fireball reaches it
        double initial_mana = mana.getCurrentMana();
        
        tower.tick(this, b.getCostShow(), wave.getSpawnedMonsters(), mana, ff, p); //increase mana if fireball reaches killed monster

        assertTrue(mana.getCurrentMana() > initial_mana, "Fireball object should increase current mana if monster is killed when reached");
    }

    //test that fireball that has reached its target is no longer fired/drawn on screen
    @Test
    public void testRemovingFiredFireballs() {
        int stop_at = frameCount + (5*FPS);
        while (frameCount != stop_at) { //run game for 5 seconds to spawn monsters
            loop();
        }
        noLoop();

        Monsters test_monster = wave.getSpawnedMonsters().get(0);
        int x = (int)test_monster.getX() + 2; //initial radius of tower range is 96
        int y = (int)test_monster.getY() + 2;

        Tower tower = new Tower(json, b.getTowerImages(), x, y, fireball);
        frameCount = (int)(Math.round(FPS/tower.getSpeed()));

        tower.tick(this, b.getCostShow(), wave.getSpawnedMonsters(), mana, ff, p); 
        Fireball to_fire = tower.getFireballsFired().get(0);

        while (!to_fire.targetReached()) { //run logic until fireball reaches its target
            tower.tick(this, b.getCostShow(), wave.getSpawnedMonsters(), mana, ff, p);
        }
        
        tower.tick(this, b.getCostShow(), wave.getSpawnedMonsters(), mana, ff, p); //fireball should be removed from arraylist of fireballs to draw on screen

        assertFalse(tower.getFireballsFired().contains(to_fire), "Fireball object should not be fired again after it has reached its target");
    }
}
