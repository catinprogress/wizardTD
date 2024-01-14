package WizardTD;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;

import java.util.ArrayList;

public class Monsters extends WaveElements implements Cloneable {
    public static final int DIMENSIONS = 20;
    public static final float OFFSET = (PIXELSPERTILE-DIMENSIONS)/2;
    
    private PImage type;
    private int hp;
    private double speed;
    private double armour;
    private int mana_gained_on_kill;
    private int quantity;
    private int current_quantity;
    private float current_hp;
    private double initial_speed;

    private float x;
    private float y;
    private int x_index;
    private int y_index;

    private PImage death_1;
    private PImage death_2;
    private PImage death_3;
    private PImage death_4;
    private PImage death_5;

    private int count_4_frames;
    private int counts_to_next;
    private int count;
    
    private boolean is_alive;
    private boolean respawn;
    private boolean speed_up;
    
    private ArrayList<ArrayList<int[]>> all_valid_paths;
    private ArrayList<int[]> valid_path;

    private HealthBar health_bar;
   
    public Monsters(JSONObject json, Board board, JSONObject current_wave, PImage type, String type_as_string, PImage death_1, PImage death_2, PImage death_3, PImage death_4, PImage death_5) {
        super(json);

        this.death_1 = death_1;
        this.death_2 = death_2;
        this.death_3 = death_3;
        this.death_4 = death_4;
        this.death_5 = death_5;
        count_4_frames = 0;
        respawn = false;
        
        hp = (int)setMonsterAttribute(current_wave, type_as_string, "hp");
        initial_speed = setMonsterAttribute(current_wave, type_as_string, "speed");
        speed = initial_speed;
        counts_to_next = (int)Math.round(PIXELSPERTILE/speed);
        count = 0;
        armour = setMonsterAttribute(current_wave, type_as_string, "armour");
        mana_gained_on_kill = (int)setMonsterAttribute(current_wave, type_as_string, "mana_gained_on_kill");
        quantity = (int)setMonsterAttribute(current_wave, type_as_string, "quantity");
        this.type = type;
        
        current_hp = hp;
        current_quantity = quantity;
        is_alive = true;
        speed_up = false;

        all_valid_paths = board.getValidPaths();
        valid_path = new ArrayList<int[]>();
        health_bar = null;
    }

    /**
     * Overrides the cloneable interface method to create a new monster object that is a copy of the current Monster instance but points to a different location in memory.
     * Allows monsters to be cloned with the same initial attribute values but as separate entities.
     */
    @Override
    public Monsters clone() {
        Monsters new_monster = null;
        try {
            new_monster = (Monsters)super.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Monsters not cloneable!");
        }

        return new_monster;
    }

    /**
     * Invoked when the monster's HP decreases to 0 and changes it's state to dead, to prevent is from respawning.
     * When the current instance is killed, the remaining quantity of monsters of this type for the current wave is also decremented.
     */
    public void kill() {
        if (is_alive) {
            is_alive = false;
            if (current_quantity > 0) {
                current_quantity--;
            }
            else {
                current_quantity = 0;
            }   
        }    
    }

    /**
     * Invoked to reduce the monster's HP when it is attacked by a fireball and change the health bar accordingly.
     * If the monster's HP reduces to 0 or below, the kill() method is invoked to change its alive status.
     * @param damage amount of damage that is inflicted on monster, disregarding its current armour value
     */
    public void attack(double damage) {
        if (damage > 0) {
            current_hp -= (float)((1 - armour)*damage);
            if (current_hp <= 0) {
                health_bar.setExternalWidth(0);
                kill();
            }
            else {
                health_bar.setExternalWidth(current_hp/hp);
            }
        }
    }

    /**
     * Invoked when Monsters are instantiated to set the path that it will follow to the wizard's house when it is spawned/respawned
     */
    public void setSpawnedPath() {
        int index = random.nextInt(all_valid_paths.size());
        valid_path = all_valid_paths.get(index);  //set the path that the monster object will follow   

        x = valid_path.get(0)[0]; //first element of path list is spawn location
        x_index = valid_path.get(0)[0]; //index allows increment to next coordinate by speed
        
        y = valid_path.get(0)[1];
        y_index = valid_path.get(0)[1];

        if (!respawn) {
            health_bar = new HealthBar(this);
        }
    }

    /**
     * Invoked when monster reaches the wizard's house to indicate whether it needs to be respawned or not
     */
    public void setRespawn() {
        if (!respawn) {
            respawn = true;
        }
        else {
            respawn = false;
        }   
    }
    /**
     * Logic for increasing the current monster's speed when fast forward option is selected. When invoked, must call slowdown() method to revert monster's speed to normal.
     */
    public void speedup() {
        if (!speed_up) {
            speed = initial_speed*2;
            counts_to_next = (int)Math.round(PIXELSPERTILE/speed);
            count = Math.round(count/2);
            speed_up = true;
        }
    }

    /**
     * Logic for reverting the current monster's speed to initial speed after the fast forward option is unselected.
     */
    public void slowdown() {
        if (speed_up) {
            speed = initial_speed;
            counts_to_next = (int)Math.round(PIXELSPERTILE/speed);
            count = count*2;
            speed_up = false;
        }
    }

    /**
     * Logic for moving the spawned monster along its set path and changing its x,y coordinates according to its current speed.
     * This method only has effect when the current monster is alive and it has not reached the wizard's house.
     * @return true if monster's position has changed, false otherwise
     */
    public boolean move() {
        if (is_alive) {
            for (int i = 0; i < (valid_path.size()-1); i++) {
                if (valid_path.get(i)[0] == x_index && valid_path.get(i)[1] == y_index) {  //x and y index remembers the next closest tile that they are incrementing towards
                    if (count == counts_to_next) {
                        count = 0;
                        x_index = valid_path.get(i+1)[0];
                        x = x_index;
                        y_index = valid_path.get(i+1)[1];
                        y = y_index;
                    }
                    else {
                        count++;
                        if ((x_index - valid_path.get(i+1)[0]) == -PIXELSPERTILE) {
                            x += speed;
                        }
                        else if ((x_index - valid_path.get(i+1)[0]) == PIXELSPERTILE) {
                            x -= speed;
                        }
                        else if ((y_index - valid_path.get(i+1)[1]) == -PIXELSPERTILE) {
                            y += speed;
                        }
                        else if ((y_index - valid_path.get(i+1)[1]) == PIXELSPERTILE) {
                            y -= speed;
                        }
                    }
                    return true;       
                }    
            }
        }
        return false;
    }

    /**
     * Renders the death animation on screen after the monster dies. Each death image lasts for 4 frames and monster's position on the screen does not change during this method call.
     * @param app that invokes this object
     * @param ff FastForward object used to increase the speed at which the monster's death animation lasts when fast forward is selected
     */
    public void deathAnimation(PApplet app, FastForward ff) {
        count_4_frames++;
        if (ff.isSelected()) {
            count_4_frames++;
        }
        if (!is_alive) {
            if (count_4_frames <= 4) {
                app.image(death_1, x+OFFSET, y+OFFSET);
            }
            else if (count_4_frames <= 8) {
                app.image(death_2, x+OFFSET, y+OFFSET);
            }
            else if (count_4_frames <= 12) {
                app.image(death_3, x+OFFSET, y+OFFSET);
            }
            else if (count_4_frames <= 16) {
                app.image(death_4, x+OFFSET, y+OFFSET);
            }
            else if (count_4_frames <= 20) {
                app.image(death_5, x+OFFSET, y+OFFSET);
            }
            else {
                app.image(death_5, x+OFFSET, y+OFFSET);
                count_4_frames = -1;
            }
        }
        else {
            count_4_frames = 0;
        }   
    }
    /**
     * Indicates whether the current monster's death animation has completed executing after it dies to prevent the object from being used or called again.
     * @return true if death animation has been completed, false otherwise
     */
    public boolean disappeared() {
        if (count_4_frames == -1) {
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the monster has reached the wizard house, and if alive, decreases the wizard's current mana by the current HP amount.
     * Allows the monster to be respawned to a valid spawn location when the object is inv oked again.
     * @param mana Mana object whose mana needs to be decreased
     */
    public void respawnMonster(Mana mana) {
        int last_index = valid_path.size() - 1;
        if ((x_index == valid_path.get(last_index)[0]) && (y_index == valid_path.get(last_index)[1]) && is_alive) {
            mana.decreaseMana(current_hp);
            setSpawnedPath();
            respawn = true;
        }
    }

    /**
     * Renders the monster and its healthbar on screen in their current position
     * @param app that invokes this object
     */
    public void draw(PApplet app) {
        app.image(type, x+OFFSET, y+OFFSET);
        this.health_bar.tick();
        this.health_bar.drawRectangle(app);
    }

    /**
     * Indicates whether the monster needs to be respawned or not.
     * @return true if monster needs to be respawned, false otherwise
     */
    public boolean getRespawn() {
        return respawn;
    }

    /**
     * Retrieves current x position of monster as shown on screen.
     * @return x + offset value used to centre image in the middle of the path
     */
    public float getX() {
        return x+OFFSET;
    }

    /**
     * Retrieves current y position of mosnter as shown on screen
     * @return y + offset value used to centre image in the middle of the path
     */
    public float getY() {
        return y+OFFSET;
    }

    /**
     * Retrieves current quantity of monsters of the current instance's type for the current wave
     * @return current quantity
     */
    public int getCurrentQuantity() {
        return current_quantity;
    }

    /**
     * Retrieves the initial quantity of monsters of the current instance's type for the current wave
     * @return initial quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves monster's current alive status
     * @return true if alive, false otherwise
     */
    public boolean isAlive() {
        return is_alive;
    }

    /**
     * Retrieves monster's current hp
     * @return current hp
     */
    public float getHP() {
        return current_hp;
    }

    /**
     * Retrieves monster's current speed, which represents how many pixels it moves per frame
     * @return current speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Retrieves the proportion of damage the monster can take when attacked
     * @return armour, where 1-armour indicates the proportion of damage that is inflicted after an attack
     */
    public double getArmour() {
        return armour;
    }

    /**
     * Retrieves the amount of mana that can be gained from the monster after it is killed
     * @return mana gained on kill
     */
    public int getManaGained() {
        return mana_gained_on_kill;
    }
}
