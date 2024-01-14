package WizardTD;
import processing.core.PApplet;


public class Shape {
    protected float x;
    protected float y;
    
    protected float total_width;
    protected float height;
    protected float external_width;
    
    protected boolean has_stroke;

    protected int[] external_colour;
    protected int[] fill_colour;

    public Shape(float x, float y, float width, float height, int[] external_colour, int[] fill_colour) {
        this.x = x;
        this.y = y;
        total_width = width;
        this.height = height;
        external_width = width;
        this.has_stroke = true;
        this.external_colour = external_colour;
        this.fill_colour = fill_colour;
    }

    /**
     * Retrieves the x coordinate of the shape from the leftmost edge.
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * Retrieves the y coordinate of the shape from the topmost edge.
     * @return y
     */
    public float getY() {
        return y;
    }

    /**
     * Allows the user to change the current y coordinate of the shape, given that it is a non-negative value and less than or equal to the height of the screen.
     * @param new_y
     */
    public void setY(float new_y) {
        if (new_y >= 0 && new_y <= App.HEIGHT) {
            y = new_y;
        }
    }

    /**
     * Allows the user to change the height of the shape given that the new height value is a positive number
     * @param new_height of shape
     */
    public void setHeight(float new_height) {
        if (new_height > 0) {
            height = new_height;
        }
    }

    /**
     * Retrieves the current height of the shape
     * @return height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Renders a rectangle on screen with two layers of colour, the background fill colour is initially set to be completely covered by the external colour layer.
     * Used to indicate the proportion of some entity, like a monster's health or the current mana.
     * Can also be used for shapes that switch between different states, such as button clicks.
     * @param app
     */
    //render the rectangular shape on screen, with background fill colour behind the external colour
    public void drawRectangle(PApplet app) {
        app.stroke(0, 0, 0);
        if (!has_stroke) {
            app.noStroke();
        }
        app.fill(fill_colour[0], fill_colour[1], fill_colour[2]);
        app.rect(x, y, total_width, height); //background rectangle

        app.fill(external_colour[0], external_colour[1], external_colour[2]);
        app.rect(x, y, external_width, height); //external rectangnle
    }

    /**
     * Decreases the current width of the external layer of colour by a desired proportion
     * @param decrement_proportion the proportion to decrease the external colour layer by
     */
    public void decreaseExternalWidth(double decrement_proportion) {
        float width_decrement = (float)(external_width - (decrement_proportion*total_width));
        
        if (external_width >= 0 && (width_decrement < total_width)) {
            external_width = width_decrement;
        }
    }
    
    /**
     * Increases the current width of the external layer of colour by a desired proportion.
     * @param increment_proportion the proportion to increase the external colour layer by
     */
    public void increaseExternalWidth(double increment_proportion) {
        float width_increment = (float)((increment_proportion*total_width)+external_width);
        if (external_width >= 0 && (width_increment <= total_width)) {
            external_width = width_increment;
        }
    }

    /**
     * Allows the user to input a set proportion of which the external layer of colour fills the shape relative to the total width of the shape
     * @param proportion the desired proportion of the width of the external layer colour in comparason to the total width of the shape
     */
    public void setExternalWidth(double proportion) {
        float width_decrement = (float)((proportion)*external_width);
        
        if (external_width > 0 && (width_decrement <= total_width)) {
            external_width = width_decrement;
        }
    }

    /**
     * Allows the user to change the colour of the outer layer of the shape
     * @param rgb an int array of length 3, where the values correspond to R, G, B values
     */
    public void setExternalColour(int[] rgb) {
        external_colour = rgb;
    }
}