package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;

public class Fireball implements RetrieveValues {
    public static final int PIXELS_PER_FRAME = 5;

    private float x_vel;
    private float y_vel;
    private float initial_x;
    private float initial_y;
    private float target_x;
    private float target_y;
    private int speed;
    
    private int counts_to_next;
    private int count;
    
    private ArrayList<float[]> coords;
    private double distance;

    private boolean target_reached;
    
    private PImage ball; 

    public Fireball(float initial_x, float initial_y, float target_x, float target_y, PImage ball, FastForward ff) {
        this.initial_x = initial_x;
        this.initial_y = initial_y;
        target_reached = false;
        this.target_x = target_x;
        this.target_y = target_y;
        this.ball = ball;
        speed = PIXELS_PER_FRAME;
        if (ff.isSelected()) {
            speed = 2*PIXELS_PER_FRAME;
        }
        distance = findDistanceBetweenTwoPoints(initial_x, initial_y, target_x, target_y);
        
        counts_to_next = (int)Math.round(distance/speed);
        coords = findPathToTarget();
        
        count = 1;
    }

    /**
     * Creates an Arraylist containing the x,y coordinates of the path for the fireball to follow according to the current speed and distance they have to travel to reach the target
     * @return coords
     */
    private ArrayList<float[]> findPathToTarget() {
        float num = (float)(distance/speed);
        float x_inc = (initial_x - target_x)/num;
        float y_inc = (initial_y - target_y)/num;
        
        ArrayList<float[]> coords = new ArrayList<float[]>();
        float[] coord = {initial_x, initial_y};
        coords.add(coord);
        
        for (int i = 1; i < counts_to_next; i++) {
            float x_coord = coords.get(i-1)[0] - x_inc;
            float y_coord = coords.get(i-1)[1] - y_inc;
            float[] new_coord = {x_coord, y_coord};
            coords.add(new_coord);
        }
        coord[0] = target_x;
        coord[1] = target_y;
        coords.add(coord);

        return coords;
    }

    /**
     * Logic for moving the fireball across the screen until they reach their target.
     * Current position of fireball depends on the number of times this method has been called.
     * If target is reached, target_reached is set to true.
     * @return true if fireball was fired, false otherwise
     */
    public boolean fire() {
        if (count <= counts_to_next) {
            x_vel = coords.get(count)[0];
            y_vel = coords.get(count)[1];
            count++;
            return true;
        }
        else {
            target_reached = true;
            return false;
        }
    }

    /**
     * Retrieves current x coordinate of fireball
     * @return x_vel
     */
    public float getX() {
        return x_vel;
    }

    /**
     * Retrieves current speed of fireball movement per frame
     * @return speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Retrieves current y coordinate of fireball
     * @return y_vel
     */
    public float getY() {
        return y_vel;
    }

    /**
     * Returns the fireball to tower position so their image doesn't remain on the screen after target is reached
     */
    public void disappear() {
        x_vel = initial_x;
        y_vel = initial_y;
    }
    
    /**
     * Retrieves the total number of times to call the move() method to reach target destination
     * @return counts_to_next
     */
    public int getCountsToNext() {
        return counts_to_next;
    }

    /**
     * Retrieves boolean value that indicates whether target destination has been reached
     * @return target_reached
     */
    public boolean targetReached() {
        return target_reached;
    }

    /**
     * Renders the fireball PImage onto the screen at its current position
     * @param app that instantiates the object
     */
    public void draw(PApplet app) {
        app.image(ball, x_vel, y_vel);
    }
}