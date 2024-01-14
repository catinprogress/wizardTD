package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;

import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;

public class Tower implements RetrieveValues {
    private PImage tower;
    private HashMap<Integer, PImage> tower_images;
    
    private int tower_level;
    private int range_level;
    private int damage_level;
    private int speed_level;
   
    private int x;
    private int y;
    private int cost;
    
    private double initial_damage;
    private double current_radius;
    private double current_damage;
    private double current_speed;
    
    private boolean speed_up;
    
    private PImage ball;
    private ArrayList<Fireball> to_remove; //fireballs that have reached their target and need to be removed
    private HashMap<Fireball, Monsters> fireballs; //maps monster object to a fireball
    private ArrayList<Fireball> to_fire; //fireballs to be fired
    private ArrayList<Fireball> fired; //fireballs that have been fired

    private Random random;

    public Tower(JSONObject json, HashMap<Integer, PImage> tower_images, int x, int y, PImage ball) {
        this.tower_images = tower_images;  
        this.tower = tower_images.get(1);      
        range_level = 0;
        damage_level = 0;
        speed_level = 0;
        this.ball = ball;
        speed_up = false;

        current_radius = getNumericAmount("initial_tower_range", json);
        current_speed = getNumericAmount("initial_tower_firing_speed", json);
        initial_damage = getNumericAmount("initial_tower_damage", json);
        current_damage = initial_damage;

        tower_level = 1;
        fireballs = new HashMap<Fireball, Monsters>();
        to_remove = new ArrayList<>();
        to_fire = new ArrayList<>();
        fired = new ArrayList<>();

        cost = 0;

        if (x >= 0 && x <= 640) {
            this.x = x;
        }
        else {
            this.x = 0;
        }

        if (y >= 40 && y <= 680) {
            this.y = y;
        }
        else {
            this.y = 40;
        }
        random = new Random();   
    }
    
    /**
     * Retrieves the tower's current range level
     * @return range_level
     */
    public int getRangeLevel() {
        return range_level;
    }

    /**
     * Retrieves the tower's current radius of its firing range
     * @return current_radius
     */
    public double getRadius() {
        return current_radius;
    }

    /**
     * Retrieves the current speed at which the tower shoots fireballs
     * @return current_speed
     */
    public double getSpeed() {
        return current_speed;
    }

    /**
     * Retrieves the current damage that a fireball can inflict on a monster
     * @return current_damage
     */
    public double getDamage() {
        return current_damage;
    }
    
    /**
     * Retrieves the tower's current damage level
     * @return damage_level
     */
    public int getDamageLevel() {
        return damage_level;
    }

    /**
     * Retrieves the tower's current speed level
     * @return speed_level
     */
    public int getSpeedLevel() {
        return speed_level;
    }

    /**
     * Retrieves the next tower level that the tower can be upgraded to once the range, speed and damage all upgrade to at least this value
     * @return tower_level
     */
    public int getTowerLevel() {
        return tower_level;
    }

    /**
     * Retrieves all the Fireball objects that have been fired at a monster that is within range as an ArrayList
     * @return to_fire
     */
    public ArrayList<Fireball> getFireballsFired() {
        return fired;
    }

    /**
     * Retrieves all the Fireball objects and the Monsters object that they are targeted to attack as a hashmap.
     * key: Fireball object to fire, value: Monster object to attack
     * @return fireballs
     */
    public HashMap<Fireball, Monsters> getFireballs() {
        return fireballs;
    }

    /**
     * Increases the current range level and radius by 32
     */
    private void upgradeRange() {
        range_level++;
        current_radius += UpgradeRange.RANGE_RADIUS_UPGRADE;
    }
    
    /**
     * Increases the current damage level and damage inflicted by 1/2 of initial tower damage
     */
    private void upgradeDamage() {
        damage_level++;
        current_damage += UpgradeDamage.DAMAGE_MULTIPLIER_UPGRADE*initial_damage;
    }

    /**
     * Increases the current speed level and current firing speed by 1/2 the towers current firing speed
     */
    private void upgradeSpeed() {
        speed_level++;
        current_speed = UpgradeSpeed.FIRING_SPEED_UPGRADE*current_speed;
    }

    /**
     * Renders purple circles at the top of the tower to indicate the current range level. Once all upgrades have been increased to this level, the purple circles disappear until the range is upgraded again
     * @param app that invokes this object
     */
    private void showRangeLevel(PApplet app) {
        app.noFill();
        app.strokeWeight(1);
        app.stroke(219, 54, 231);
        if (range_level >= tower_level) {
            int begin = tower_level-1;
            for (int i = begin; i < range_level; i++) {
                app.ellipse(x+3+(6*i), y+3, 6, 6);
            }
        }
    }

    /**
     * Renders purple cross at the bottom of the tower to indicate the current damage level. Once all upgrades have been increased to this level, the purple crosses disappear until the damage is upgraded again
     * @param app that invokes this object
     */
    private void showDamageLevel(PApplet app) {
        app.strokeWeight(1);
        app.stroke(219, 54, 231);
        if (damage_level >= tower_level) {
            int begin = tower_level-1;
            for (int i = begin; i < damage_level; i++) {
                app.line(x + (i*6), y+24, x + ((i+1)*6), y+30);
                app.line(x + ((i+1)*6), y+24, x + (i*6), y+30);
            }
        }
    }
    
    /**
     * Outlines a blue square around the tower where the thickness of the outline indicates the current speed level. Once all upgrades have been increased to this level, the square outline disappears until the speed is upgraded again
     * @param app that invokes this object
     */
    private void showSpeedLevel(PApplet app) {  
        app.stroke(136, 179, 249);
        app.strokeWeight(speed_level);
        if (speed_level >= tower_level) {
            app.rect(x+4, y+4, 24, 24);
        }
    }

    /**
     * Renders a pastel purple circle outline around the tower that indicates the current range of the tower's fireballs.
     * @param app
     */
    private void showRadius(PApplet app) {
        app.noFill();
        app.strokeWeight(1);
        app.stroke(255, 255, 85);
        app.ellipse(x+UpgradeRange.CENTRE_OFFSET, y+UpgradeRange.CENTRE_OFFSET, (float)current_radius*2, (float)current_radius*2);
    }
    
    /**
     * Allows the user to increase the next tower level to upgrade to if the range, speed and damage have all been upgraded to the current tower level value.
     */
    private void increaseTowerLevel() {
        if (range_level >= tower_level && speed_level >= tower_level && damage_level >= tower_level) {
            tower_level++;
            if (tower_level <= 3) {
                tower = tower_images.get(tower_level);
            }
        }
    }

    /**
     * Retrieves the x coordinate of the tower on screen according to the position of left edge of the tower image
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the y coordinate of the tower on screen according to the position of the top edge fo the tower image
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Retrieves the cost value of the selected upgrades
     * @return cost
     */
    public int getCost() {
        return cost;
    }
    
    /**
     * Checks whether the mouse is positioned over the current tower object
     * @param app that invokes this object
     * @return true if mouse is at the tower location, false otherwise
     */
    private boolean mouseHover(PApplet app) {
        if ((app.mouseX >= x && app.mouseX <= x+WaveElements.PIXELSPERTILE) && (app.mouseY >= y && app.mouseY <= y+WaveElements.PIXELSPERTILE)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Logic for showing the current firing range of the tower on screen when the mouse hovers over the tower image, and shooting fireballs at the correct speed when monsters are within range
     * @param app that invokes this object
     * @param cost_show UpgradeCost object used to show the cost of upgrades selected
     * @param spawned_monsters ArrayList of monsters that have been spawned in the current wave
     * @param mana Mana object used to decrease the current mana after a monster is killed by a fireball
     * @param ff FastForward object used to increase firing speed when fast forward is selected
     * @param p Pause object used to stop firing fireballs when pause is selected
     */
    public void tick(PApplet app, UpgradeCost cost_show, ArrayList<Monsters> spawned_monsters, Mana mana, FastForward ff, Pause p) {
        if (mouseHover(app) && !app.mousePressed) { //if mouse is hovering over tower, show upgrade cost and range radius 
            showRadius(app);
            
            app.noStroke(); //fill in topbar and sidebar after radius of tower is shown
            app.fill(130, 116, 79);
            app.rect(0, 0, App.WIDTH, App.TOPBAR);
            app.rect(640, 0, App.SIDEBAR, App.HEIGHT);
            
            cost_show.tick(app, range_level, damage_level, speed_level); //show cost of upgrade
        }

        if (!p.isSelected()) { //if not paused, shoot fireballs
            if (ff.isSelected() && !speed_up) { //increase speed if fast forward selected
                current_speed = 2*current_speed;
                speed_up = true;
            }
            else if (!ff.isSelected() && speed_up) {
                current_speed = current_speed/2;
                speed_up = false;
            }
            
            if (app.frameCount%((int)Math.round(WaveElements.FPS/current_speed)) == 0) { //check that fireballs are being shot at the correct speed
                for (Monsters monster : spawned_monsters) {
                    double distance = findDistanceBetweenTwoPoints(x + UpgradeRange.CENTRE_OFFSET, y + UpgradeRange.CENTRE_OFFSET, monster.getX() + Monsters.DIMENSIONS/2, monster.getY() + Monsters.DIMENSIONS/2);
                    if (distance <= current_radius) {//if monster is in tower range, create a fireball object to to fire
                        Fireball fireball = new Fireball(x + UpgradeRange.CENTRE_OFFSET, y + UpgradeRange.CENTRE_OFFSET, monster.getX() + Monsters.DIMENSIONS/2, monster.getY() + Monsters.DIMENSIONS/2, ball, ff);
                        fireballs.put(fireball, monster); //key: fireball object, value: monster object to shoot at
                        to_fire.add(fireball); //list of fireball objects to fire
                        break;
                    }
                }
                if (to_fire.size() > 0) { //choose an aribitrary monster to shoot at
                    int index = random.nextInt(to_fire.size());
                    Fireball current = to_fire.get(index);
                    fired.add(current);
                    to_fire.remove(current);
                }
            }
            
            for ( Fireball chosen : fired ) { //iterate through fireballs that have been fired
                Monsters to_kill = fireballs.get(chosen);  
                if (!chosen.targetReached()) {
                    chosen.fire();
                }
                else {
                    chosen.disappear();
                    if (!to_kill.isAlive()) { //mana increases if monster is killed
                        mana.changeCurrentMana(to_kill.getManaGained()*mana.getManaMultiplier());
                    }
                    else { //decrease monster hp
                        to_kill.attack(current_damage);
                    } 
                    to_remove.add(chosen);
                }
                chosen.draw(app);
            }

            for (Fireball remove : to_remove) { //remove fireballs who've reached their target
               fired.remove(remove);
               fireballs.remove(remove, fireballs.get(remove));
            }
                     
            }                 
        }
    
    /**
     * Renders the tower on screen and the respective range, damage and speed level indicators
     * @param app that invokes this object
     */
    public void draw(PApplet app) {
        app.image(tower, x, y);

        showRangeLevel(app);
        showDamageLevel(app);
        showSpeedLevel(app);
    }

    /**
     * Logic for upgrading the tower when the player has enough mana for the next level they wish to upgrade to. The player can only purchase as many upgrades as they have mana, but they cannot purchase upgrades to deliberately lose the game.
     * @param mana Mana object used to check if wizard has enough mana for a purchase
     * @param range UpgradeRange pbject used to check if range option was selected
     * @param damage UpgradeDamage object used to check if damage option was selected
     * @param speed UpgradeSpeed object used to check if speed option was selected
     */
    public void upgradeTower(Mana mana, UpgradeRange range, UpgradeDamage damage, UpgradeSpeed speed) {
        cost = 0;
        if (range.isSelected()) {
            int range_cost = UpgradeCost.getUpgradeCost(range_level);
            if (mana.getCurrentMana() > range_cost) {
                cost += range_cost;
                upgradeRange();
            }
        }
        if (speed.isSelected()) {
            int speed_cost = UpgradeCost.getUpgradeCost(speed_level); 
            if (mana.getCurrentMana() > (cost + speed_cost)) {
                cost += speed_cost;
                upgradeSpeed();
            }
        }
        if (damage.isSelected()) {
            int damage_cost = UpgradeCost.getUpgradeCost(damage_level);
            if (mana.getCurrentMana() > (cost + damage_cost)) {
                cost += damage_cost;
                upgradeDamage();
            }
        }
        increaseTowerLevel(); 
    }
}

