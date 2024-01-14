package WizardTD;

import processing.core.PApplet;

public class ManaBar extends Shape {
    public static final int[] MANA = {97, 211, 212};
    public static final int[] BACKGROUND = {255, 255, 255};

    public ManaBar() {
        super(350, 12, 365, 20, MANA, BACKGROUND);
    }

    /**
     * Renders the text that shows how much mana the player currently has left and the current mana cap
     * @param app that invokes the subclass instance
     * @param current_amount current mana remaining
     * @param total_amount current mana cap
     */
    public void showAmountLeft(PApplet app, int current_amount, int total_amount) {
        String to_print = current_amount + "/" + total_amount;

        app.fill(0, 0, 0);
        app.textSize(height);
        app.textAlign(PApplet.CENTER);
        app.text(to_print, x+total_width/2, 30);
    }

    /**
     * Renders the text that labels the mana bar
     * @param app that invokes the subclass instance
     */
    public void showText(PApplet app) {
        app.fill(0, 0, 0);
        app.textSize(height);
        app.textAlign(PApplet.RIGHT);
        app.text("MANA:", x-3,30);
    } 
    
    /**
     * Sets the colour and width of the border to be drawn around the mana bar
     * @param app that invokes the subclass instance
     */
    public void setBorder(PApplet app) {
        app.stroke(0, 0, 0);
        app.strokeWeight(2);
    }
}
