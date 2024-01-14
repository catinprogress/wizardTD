package WizardTD;

import processing.core.PApplet;

public class NextWaveText {
    public static final float X = 10;
    public static final float Y = 30;
    public static final float FONTSIZE = 25;
    
    private int next_wave;
    private int seconds;
    private int last_wave;
    private String to_print;

    public NextWaveText(int seconds, int last_wave) {
        this.last_wave = last_wave;
        this.seconds = seconds;
        
        next_wave = 1;
        to_print = "";
    }
    /**
     * Decreases the duration until the next wave by 1 if current duration is above 0.
     */
    public void decrement() {
        if (seconds > 0) {
            seconds--;
        }   
    }

    /**
     * Increments the next wave value to display on screen
     */
    public void setNextWave() {
        if (next_wave <= last_wave) { 
            next_wave++;
        }
    }
    
    /**
     * Invoked at the start of a new wave to change the duration until the next wave. The value must be non-negative in order to be displayed on screen.
     * @param new_seconds amount to set the new duration to
     */
    public void setSeconds(int new_seconds) {
        if (new_seconds >= 0) {
            seconds = new_seconds;
        }
        else {
            next_wave = last_wave;
        }
    }

    /**
     * Logic for deciding what text to display on the screen. For any text to be displayed, the next wave cannot be the greater than the last wave and the durationt to display must be non-negative
     * @param app that invokes this object
     */
    public void tick (PApplet app) {
        if (next_wave <= last_wave && seconds >= 0) {
            to_print = "Wave " + next_wave + " starts: " + seconds;
        }
        else { //last wave has started
            to_print = "";
        }   
    }

    /**
     * Renders the text to display on screen
     * @param app
     */
    public void draw(PApplet app) {
        app.fill(0, 0, 0);
        app.textSize(FONTSIZE);
        app.textAlign(PApplet.LEFT);
        app.text(to_print, X, Y);
    } 
}
