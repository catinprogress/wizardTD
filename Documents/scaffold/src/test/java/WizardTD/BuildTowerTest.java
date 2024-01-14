package WizardTD;
import processing.core.PApplet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class BuildTowerTest extends App {
    @BeforeEach
    public void startSketch() {
        configPath = "testconfig.json";
        loop();
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);
        noLoop();
    }

    @Override
    public void draw() {

    }

    @Test
    public void testConstruction() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        
        assertNotNull(build, "BuildTower object is null");
    }

    @Test
    public void testCorrectTowerCost() {
        BuildTower build = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        int exp_cost = json.getInt("tower_cost");

        assertEquals(exp_cost, build.getTowerCost(), "Incorrect tower cost retrieved");
    }

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
