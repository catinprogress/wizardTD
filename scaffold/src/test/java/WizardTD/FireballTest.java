package WizardTD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import processing.core.PApplet;

public class FireballTest extends App {
    

    @BeforeEach
    public void startSketch() {
        configPath = "testconfig.json";
        PApplet.runSketch(new String[] { "App" }, this);
        setup();
        delay(1000);
    }

    @Override
    public void draw() {

    }

    //test that Fireball object can be constructed with valid arguments passed in
    @Test
    public void testConstruction() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);

        assertNotNull(fire_test, "Fireball object is null");
    }

    // test that fireball objects all have default speed of 5 pixels per frame
    @Test
    public void testConstantFireballSpeedValue() {
        assertEquals(5, Fireball.PIXELS_PER_FRAME, "Fireball is not moving 5 pixels per frame");
    }

     @Test
    public void testTargetReachedSetToFalse() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);

        assertFalse(fire_test.targetReached(), "Fireball target reached variable is not initialised to false");
    }

    //positive test --> test the number of loops it will take for fireball to reach the target location
    @Test
    public void testCountsToNext() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);
        double exp_dist = Math.sqrt(10*10 + 10*10);
        int exp_counts_to_next = (int)Math.round(exp_dist/5);

        assertEquals(exp_counts_to_next, fire_test.getCountsToNext(), "Incorrect number of loops to move fireball until target is reached");
    }

    //test x coordinate of fireball moves in correct direction after 1 frame
    @Test
    public void testFireballMovementX() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);
        double exp_dist = Math.sqrt(10*10 + 10*10);

        float num = (float)(exp_dist/5);
        float x_inc = (10 - 20)/num;
        float exp_x = 10 - x_inc;
       
        fire_test.fire(); //first coordinate is initial x

        assertEquals(exp_x, fire_test.getX(), 3, "Incorrect x-coordinate distance travelled in 1 frame");
    }

    // test y coordinate of fireball moves in correct direction after 1 frame
    @Test
    public void testFireballMovementY() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);
        double exp_dist = Math.sqrt(10*10 + 10*10);
        float num = (float)exp_dist/5;
        float y_inc = (10 - 20)/num;
        float exp_y = 10 - y_inc;

        fire_test.fire(); //first coordinate is initial y

        assertEquals(exp_y, fire_test.getY(), 3, "Incorrect y-coordinate distance travelled in 1 frame");
    }

    //test that x coordinate of fireball reaches target in the correct amount of frames
    @Test
    public void testFireballReachesTargetX() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);
        int counts_to_next = fire_test.getCountsToNext();

        for (int i = 0; i <= counts_to_next; i++) {
            fire_test.fire();
        }
    
        assertEquals(20, fire_test.getX(), 3, "Target x coordinate not reached");
    }

    //test that y coordinate of fireball reaches target in the correct amount of frames
    @Test
    public void testFireballReachesTargetY() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);
        int counts_to_next = fire_test.getCountsToNext();

        for (int i = 0; i <= counts_to_next; i++) {
            fire_test.fire();
        }

         assertEquals(20, fire_test.getY(), 0.5, "Target y coordinate not reached");
    }

    // test that target reached boolean value is set to true after initial x and y coorindates reach target x and y coordinates
    @Test
    public void testFireballTargetReached() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);
        int counts_to_next = fire_test.getCountsToNext();

        for (int i = 0; i <= counts_to_next+1; i++) {
            fire_test.fire();
        }

        assertTrue(fire_test.targetReached(), "Target reached flag variable does not return correct boolean");
    }

    // test that disappear method returns x coorindate to initial x coordinate
    @Test
    public void testFireballDisappearsX() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);

        fire_test.disappear();

        assertEquals(10, fire_test.getX(), 0.001, "disappear method did not return fireball to iniital x coordinate");
    }
    
    // test that disappear method returns y coordinate to initial y coordinate
    @Test
    public void testFireballDisappearsY() {
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);

        fire_test.disappear();

        assertEquals(10, fire_test.getY(), 0.001, "disappear method did not return fireball to iniital y coordinate");
    }

    // test that fast forward toggles the current speed of fireball movement to twice the default speed
    @Test
    public void testFastForwardSpeed() {
        ff.setSelected(); //set fast forward option to be true
        int exp_speed = Fireball.PIXELS_PER_FRAME*2; //speed should double
        
        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);
    
        assertEquals(exp_speed, fire_test.getSpeed(), "Speed of fireball movement did not double after fast forward is toggled");
    }

    //test that fireball reaches target in half the time
    @Test
    public void testFastForwardCounts() {
        ff.setSelected(); //set fast forward option to be true

        Fireball fire_test = new Fireball(10, 10, 20, 20, fireball, ff);
        double exp_dist = Math.sqrt(10*10 + 10*10);
        int exp_counts = (int)Math.round(exp_dist/(Fireball.PIXELS_PER_FRAME*2)); //exp counts

        assertEquals(exp_counts, fire_test.getCountsToNext(), "Number of times fireball should be fired to reach target should halve after fast forward is toggled");
    }


}
