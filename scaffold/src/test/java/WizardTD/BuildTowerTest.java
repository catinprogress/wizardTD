package WizardTD;
import processing.core.PApplet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class BuildTowerTest extends App {
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
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        
        assertNotNull(build, "BuildTower object is null");
    }

    //check that correct tower cost is retrieved
    @Test
    public void testCorrectTowerCost() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int exp_cost = json.getInt("tower_cost");

        assertEquals(exp_cost, build.getTowerCost(), "Incorrect tower cost retrieved");
    }

    //check that new tower can be purchased when mouse clicks on a grass tile
    @Test
    public void testCreateNewTower() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int[] test_coords = board.getGrassTiles().get(3);
        mouseX = test_coords[0] + 2;
        mouseY = test_coords[1] + 5;
        build.setMouseClicked();
        build.setSelected();

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave);

        assertTrue(build.getPurchasedTowers().size() == 1, "New tower was not purchased when mouse clicked on grass tile");
    }

    //check that tower is not purchased when mouse clicks on invalid tile
    @Test
    public void testCreateNewTowerInvalidCoords() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int[] test_coords = board.getValidPaths().get(0).get(1);
        mouseX = test_coords[0] + 2;
        mouseY = test_coords[1] + 2;
        build.setMouseClicked();
        build.setSelected();

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave);

        assertTrue(build.getPurchasedTowers().isEmpty(), "New tower should not be placed when mouse clicked on non grass tile");
    }

    //check that mana decreases to correct amount after a tower is purchased
    @Test
    public void testPurchasingTowerCost() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int[] test_coords = board.getGrassTiles().get(3);
        mouseX = test_coords[0] + 2;
        mouseY = test_coords[1] + 5;
        build.setMouseClicked();
        build.setSelected();
        
        double exp_mana = build.getMana().getCurrentMana() - build.getTowerCost();

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave);

        assertEquals(exp_mana, build.getMana().getCurrentMana(), 0.01, "Mana did not decrease to correct value after a tower was purchased");
    }

    //test that you can't purchase a tower in a place where a tower is already placed
    @Test
    public void testPurchasingTowerUsedLocation() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int[] test_coords = board.getGrassTiles().get(3);
        mouseX = test_coords[0] + 2;
        mouseY = test_coords[1] + 5;
        build.setMouseClicked();
        build.setSelected();

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave); //create a tower

        int exp_size = build.getPurchasedTowers().size();

        mouseX = test_coords[0] + 2;
        mouseY = test_coords[1] + 5;
        build.setMouseClicked();

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave); //attempt to create a tower in same location

        assertEquals(exp_size, build.getPurchasedTowers().size(), "Towers cannot be placed over existing tiles");
    }

    //check that purchasing tower with upgrades selected sets the tower to correct upgrade level
    @Test
    public void testPurchasingTowerWithUpgrades() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int[] test_coords = board.getGrassTiles().get(3);
        mouseX = test_coords[0] + 20;
        mouseY = test_coords[1] + 30;
        
        build.setMouseClicked();
        build.setSelected();
        build.getRange().setSelected(); //select 2 upgrades
        build.getSpeed().setSelected();

        double exp_mana = build.getMana().getCurrentMana() - build.getTowerCost() - (2*UpgradeCost.getUpgradeCost(0)); //initial level = 0

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave);

        assertEquals(exp_mana, build.getMana().getCurrentMana(), 0.01, "Incorrect cost of purchase calculated for when upgrades are selected with tower purchase");
    }

    //check that already purchased towers can be upgraded by checking correct cost of upgrade is decreased
    @Test
    public void testUpgradePurchasedTowers() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int[] test_coords = board.getGrassTiles().get(5);
        mouseX = test_coords[0] + 3;
        mouseY = test_coords[1] + 7;
        build.setMouseClicked();
        build.setSelected();

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave); //purchase tower

        double exp_mana = build.getMana().getCurrentMana() - (2*UpgradeCost.getUpgradeCost(0));
        
        build.setSelected(); //unselect build tower
        build.setMouseClicked();
        build.getRange().setSelected();
        build.getSpeed().setSelected();

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave);

        assertEquals(exp_mana, build.getMana().getCurrentMana(), 0.01, "Incorrect cost of purchase calculated for upgrading existing towers");
    }

    //check that you can't upgrade an existing tower when build tower is selected
    @Test
    public void testUpgradeWhenBuildSelected() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int[] test_coords = board.getGrassTiles().get(5);
        mouseX = test_coords[0] + 3;
        mouseY = test_coords[1] + 7;
        build.setMouseClicked();
        build.setSelected();
        
        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave); //purchase tower

        double exp_mana = build.getMana().getCurrentMana(); //mana amount should not change when upgrade attempted with build tower still selected

        build.setMouseClicked();
        build.getRange().setSelected();
        build.getDamage().setSelected();

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave);

        assertEquals(exp_mana, build.getMana().getCurrentMana(), 0.01, "Mana value should not change if upgrade on existing tower is attempted with build tower still selected");
    }

    //test that you can't purchase a tower when build tower is not selected
    @Test
    public void testPurchaseTowerWhenNotSelected() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int[] test_coords = board.getGrassTiles().get(5);
        mouseX = test_coords[0] + CELLSIZE/2;
        mouseY = test_coords[1] + CELLSIZE/2;
        build.setMouseClicked(); //mouse clicked valid tile but build tower not selected

        build.tick(this, wave.getSpawnedMonsters(), ff, p, wave);

        assertTrue(build.getPurchasedTowers().isEmpty(), "Cannot purchase towers when build tower option is not selected");
    }

}
