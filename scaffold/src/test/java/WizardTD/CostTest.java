package WizardTD;

import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import static org.junit.jupiter.api.Assertions.*;

public class CostTest extends App {
    @Override
    public void draw() {

    }

    @Test
    public void testConstruction() {
        UpgradeCost cost = new UpgradeCost(u3, u1, u2);
        assertNotNull(cost, "UpgradeCost object is null");
    }

    //test that the upgrade cost for towers with no initial upgrades is equal to 20
    @Test
    public void testInitialUpgradeCostValue() {
        int exp_cost = 20;
        
        assertEquals(exp_cost, UpgradeCost.getUpgradeCost(0), "Initital cost for upgrade should be 20");
    }
    
    //test that the upgrade cost multiplier value is correct and calculates the correct upgrade cost value for a certain level
    @Test
    public void testUpgradeCostValueMultiplier() {
        int level = 2;
        int cost_multiplier = 10;
        int exp_cost = level*cost_multiplier + 20;
        
        assertEquals(exp_cost, UpgradeCost.getUpgradeCost(level), "The upgrade cost retrieved is incorrect");
    }

    //test that negative level values return a cost value of 0
    @Test
    public void testNegativeValueUpgradeLevel() {
        assertEquals(0, UpgradeCost.getUpgradeCost(-1), "Negative upgrade levels should return a value of 0");
    }

    //test that the height of the middle box of the cost box is the same as the other title box when 1 upgrade is selected
    @Test
    public void testCorrectBoxHeightOneUpgrade() {
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        UpgradeCost cost = new UpgradeCost(u3, u1, u2);

        float exp_height = cost.getHeight();
        u1.setSelected();

        cost.tick(this, 0, 0, 0);

        assertEquals(exp_height, cost.getUpgradeBoxTotalHeight(), 0.001, "Height of upgrade cost box for a single upgrade is incorrect");
    }

    //test that box height doubles when 2 upgrades are selected
    @Test
    public void testCorrectBoxHeightTwoUpgrades() {
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        UpgradeCost cost = new UpgradeCost(u3, u1, u2);

        float exp_height = cost.getHeight()*2;
        u1.setSelected();
        u3.setSelected();

        cost.tick(this, 1, 0, 2);

        assertEquals(exp_height, cost.getUpgradeBoxTotalHeight(), 0.001, "Height of upgrade cost box for two upgrades selected is incorrect");
    }

    //test that box height triples when 3 upgrades are selected
    @Test
    public void testCorrectBoxHeightThreeUpgrades() {
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        UpgradeCost cost = new UpgradeCost(u3, u1, u2);

        float exp_height = cost.getHeight()*3;
        u1.setSelected();
        u2.setSelected();
        u3.setSelected();

        cost.tick(this, 2, 4, 5);

        assertEquals(exp_height, cost.getUpgradeBoxTotalHeight(), 0.001, "Height of upgrade cost box for all upgrades selected is incorrect");
    }
    
    //test that the correct total upgrade cost is calculated for when 1 upgrade option is selected
    @Test
    public void testTotalCostCalculationOneUpgrade() {
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        UpgradeCost cost = new UpgradeCost(u3, u1, u2);
        int speed_level = 3;
        int exp_cost = UpgradeCost.getUpgradeCost(speed_level);
        
        u2.setSelected();
        cost.tick(this, 0, 2, speed_level);

        assertEquals(exp_cost, cost.getTotalCost(), "Incorrect total cost calculated for a single upgrade selected");
    }

    //test that the correct total upgrade cost is calculated for when 2 upgrade options are selected
    @Test
    public void testTotalCostCalculationTwoUpgrades() {
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        UpgradeCost cost = new UpgradeCost(u3, u1, u2);
        int speed_level = 0;
        int damage_level = 5;
        int exp_cost = UpgradeCost.getUpgradeCost(speed_level) + UpgradeCost.getUpgradeCost(damage_level);
        
        u2.setSelected();
        u3.setSelected();
        cost.tick(this, 2, damage_level, speed_level);

        assertEquals(exp_cost, cost.getTotalCost(), "Incorrect total cost calculated for two upgrades selected");
    }

    //test that the correct total upgrade cost is calculated for when 3 upgrade options are selected
    @Test
    public void testTotalCostCalculationThreeUpgrades() {
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        UpgradeCost cost = new UpgradeCost(u3, u1, u2);
        int range_level = 0;
        int damage_level = 5;
        int speed_level = 1;
        int exp_cost = UpgradeCost.getUpgradeCost(range_level) + UpgradeCost.getUpgradeCost(damage_level) + UpgradeCost.getUpgradeCost(speed_level);
        
        u1.setSelected();
        u3.setSelected();
        u2.setSelected();
        cost.tick(this, range_level, damage_level, speed_level);

        assertEquals(exp_cost, cost.getTotalCost(), "Incorrect total cost calculated for all upgrades selected");
    }

    //test that tick method ignores negative upgrade level values when calculating total cost
    @Test
    public void testTotalCostCalculationNegativeLevel() {
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);

        UpgradeCost cost = new UpgradeCost(u3, u1, u2);
        int range_level = 0;
        int damage_level = -1;
        int speed_level = 1;
        int exp_cost = UpgradeCost.getUpgradeCost(range_level) +  UpgradeCost.getUpgradeCost(speed_level);
        
        u1.setSelected();
        u3.setSelected();
        u2.setSelected();
        cost.tick(this, range_level, damage_level, speed_level);

        assertEquals(exp_cost, cost.getTotalCost(), "Negative level values should be ignored and not be calculated in total cost");
    }


}
