package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

public class BuildTower extends GameplayActions {
    private JSONObject json;
    private int tower_cost;

    private ArrayList<int[]> grass_tiles_coords;
    private int[] coords;
    private HashMap<int[], Tower> purchased_towers;
    private HashMap<Integer, PImage> tower_images;
   
    private Mana mana;
    private UpgradeRange range;
    private UpgradeSpeed speed;
    private UpgradeDamage damage;
    private UpgradeCost cost_show;
    
    private Shape show_cost;
    private PImage ball;

    public BuildTower(Mana mana, JSONObject json, UpgradeRange range, UpgradeSpeed speed, UpgradeDamage damage, PImage tower_0, PImage tower_1, PImage tower_2, Board board, PImage ball) {
        super(152, 't', "T ", "Build\ntower");
        this.json = json;
        tower_images = new HashMap<>();
        tower_images.put(1, tower_0);
        tower_images.put(2, tower_1);
        tower_images.put(3, tower_2);
        this.mana = mana;
        grass_tiles_coords = board.getGrassTiles();
        purchased_towers = new HashMap<>();
        this.ball = ball;

        this.range = range;
        this.speed = speed;
        this.damage = damage;
        cost_show = new UpgradeCost(this.damage, this.range, this.speed);
        show_cost = new Shape(585, y+2, 52, 18, COSTBOX, COSTBOX);
        
        tower_cost = (int)GameElements.getNumericAmount("tower_cost", json);
        
    }
    
    /**
     * Retrieves the object's UpgradeRange instance
     * @return range
     */
    public UpgradeRange getRange() {
        return range;
    }

    /**
     * Retrieves the object's UpgradeDamage instance
     * @return damage
     */
    public UpgradeDamage getDamage() {
        return damage;
    }

    /**
     * Retrieves the object's UpgradeSpeed instance
     * @return speed
     */
    public UpgradeSpeed getSpeed() {
        return speed;
    }

    /**
     * Retrieves all the available tower level PImages as a HashMap where a PImage object maps to the corresponding level
     * @return tower_images
     */
    public HashMap<Integer, PImage> getTowerImages() {
        return tower_images;
    }

    /**
     * Retrieves the UpgradeCost object for this instance that shows the cost of all upgrades selected
     * @return cost_show
     */
    public UpgradeCost getCostShow() {
        return cost_show;
    }

    /**
     * Retrieves all the Tower objects that have been purchased as a HashMap, where a Tower object maps to its x,y coordinates on the screen
     * @return purchased_towers
     */
    public HashMap<int[], Tower> getPurchasedTowers() {
        return purchased_towers;
    }

    /**
     * Retrieves the object's Mana instance
     * @return mana
     */
    public Mana getMana() {
        return mana;
    }

    /**
     * Retrieves the cost to purchase a tower with no upgrades
     * @return tower_cost
     */
    public int getTowerCost() {
        return tower_cost;
    }

    /**
     * Contains the logic for building new towers and upgrading purchased ones, depending on which buttons have been selected by the player and the current mouse position.
     * Also executes the logic of each purchased tower object
     * @param app that instantiates this class
     * @param spawned_monsters all spawned monsters in the current wave
     * @param ff FastForward object used to check if game has been fastforwarded
     * @param p Pause object used to check if game has been paused
     * @param wave Wave object used to retrieve the currently spawned monsters
     */
    public void tick(PApplet app, ArrayList<Monsters> spawned_monsters, FastForward ff, Pause p, Wave wave) {
        if (!mana.hasLost() && !wave.hasWon()) {
            showCost(app, show_cost, tower_cost);
            if (selected && mouseClick(app, grass_tiles_coords) && !mouseClick(app, purchased_towers.keySet()) && (tower_cost < mana.getCurrentMana())) {
                //build tower was selected
                Tower new_tower = new Tower(json, tower_images, coords[0], coords[1], ball);
                new_tower.upgradeTower(mana, range, damage, speed);
                purchased_towers.put(coords, new_tower);
                mana.changeCurrentMana(-(tower_cost + new_tower.getCost()));
            }
            else if (!selected && mouseClick(app, purchased_towers.keySet()) && (range.isSelected() || damage.isSelected() || speed.isSelected())) {
                //build tower not selected
                Tower chosen_tower = purchased_towers.get(coords);
                chosen_tower.upgradeTower(mana, range, damage, speed);
                mana.changeCurrentMana(-chosen_tower.getCost());
            }

            for (Tower tower : purchased_towers.values()) {
                tower.tick(app, cost_show, spawned_monsters, mana, ff, p);
            }
        }

    }

    /**
     * Renders the purchased towers onto the screen
     * @param app that instantiates the class
     */
    public void draw(PApplet app) {
        for (Tower tower : purchased_towers.values()) {
            tower.draw(app);
        }
    }

    /**
     * Checks if the mouse has clicked a grass tile on the screen and sets the most recent coordinates of mouse click
     * @param app that instantiates the class
     * @return true if mouse has clicked a grass tile, false otherwise
     */
    private boolean mouseClick(PApplet app, Collection<int[]> all_coords) {
        for (int[] coord : all_coords) {
            if (mouseClicked && (app.mouseX >= coord[0] && app.mouseX <= (coord[0] + GameElements.PIXELSPERTILE)) && (app.mouseY >= coord[1] && app.mouseY <= (coord[1] + GameElements.PIXELSPERTILE))) {
                this.coords = coord;
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the mouse has clicked a purchased tower on the screen and sets the most recent coordinates of mouse click
     * @param app that instantiates the class
     * @return true if mouse has clicked an existing tower, false otherwise
     */
     /*
    private boolean mouseTowerTile(PApplet app) {
        for (int[] coord : purchased_towers.keySet()) {
            if (mouseClicked && (app.mouseX >= coord[0] && app.mouseX <= (coord[0] + GameElements.PIXELSPERTILE)) && (app.mouseY >= coord[1] && app.mouseY <= (coord[1] + GameElements.PIXELSPERTILE))) {
                this.coords = coord;
                return true;
            }
        }
        return false;
    } */

}
