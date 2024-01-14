package WizardTD;

import processing.core.PApplet;

public abstract class GameplayActions extends Shape {
    public static final int[] BACKGROUND = {130, 116, 79};
    public static final int[] CLICKED = {147,193,235};
    public static final int[] HOVER = {206, 206, 206};
    
    public static final int FONTSIZE = 25;
    public static final int SUBFONTSIZE = 11;
    
    public static final int X = 648;
    public static final int DIMENSION = 40;
    
    public final float MAIN_X = x + DIMENSION/2;
    public final float MAIN_Y = y + DIMENSION/2 - 3;
    public final float SUB_X = x + DIMENSION + 8;
    public final float SUB_Y = y + 15;
    
    public static final int COSTSIZE = 10;
    public static final int[] COSTBOX = {233,226,255};
    public static final int COST_X = X - 37;
    public final float COST_Y = y + 11;

    protected boolean selected; //remember last click
    protected boolean mouseClicked;
    protected static boolean key_was_pressed;
    
    //protected static char last_key;
    protected char key_flag;
    
    protected String main_text;
    protected String sub_text;
    protected UpgradeCost cost;


    public GameplayActions(float y, char key_flag, String main_text, String sub_text) {
        super(X, y, DIMENSION, DIMENSION, HOVER, BACKGROUND);
        selected = false;
        mouseClicked = false;
        key_was_pressed = false;
        this.key_flag = key_flag;
        this.main_text = main_text;
        this.sub_text = sub_text;
    }

    /**
     * Called by subclass that extends PApplet class when a mouse click event occurs
     */
    public void setMouseClicked() {
        mouseClicked = true;
    }

    /**
     * Retrieves the status of a mouse click event
     * @return mouseClicked; true if mouse was clicked, false otherwise
     */
    public boolean getMouseClicked() {
        return mouseClicked;
    }

    /**
     * Retrieves the status of a key press event
     * @return key_was_pressed; true if a key was pressed, false otherwise
     */
    public static boolean keyWasPressed() {
        return key_was_pressed;
    }

    /**
     * Called by a subclass of PApplet to change status of key press event
     * @param flag boolean value indicating whether key was pressed or not
     */
    public static void setKeyWasPressed(boolean flag) {
        key_was_pressed = flag;
    }

    /**
     * Indicates whether the current subclass instance has been selected or not
     * @return selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Retrieves the key as char type that is associated with a GameplayAction subclass
     * @return key_flag
     */
    public char getKey() {
        return key_flag;
    }

    /**
     * Checks if the current mouse position is at the current instance's button
     * @param app that instantiates the subclass
     * @return true if mouse position is at the button, false otherwise
     */
    protected boolean mouse(PApplet app) {
        if ((app.mouseX >= x && app.mouseX <= x+DIMENSION) && (app.mouseY >= y && app.mouseY <= y+DIMENSION)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Logic for rendering the colour of the buttons on the screen, depending on the button's previous state and the current mouse/key event
     * Condition to change the colour of the button depends on whether the game has ended or not
     * @param app that instantiates the subclass
     * @param mana Mana object that indicates whether the player has lost the game
     * @param wave Wave object that indicates whether the player has won the game
     */
    //logic for rendering the colour of the buttons on the screen, depending on the button's previous state and mouse/key actions
    protected void setButtons(PApplet app, Mana mana, Wave wave) {
        if (!mana.hasLost() && !wave.hasWon()) {
            app.strokeWeight(2);
            if (key_was_pressed && (app.key == key_flag)) {
                external_width = total_width;
                if (!selected) {
                    external_colour = CLICKED;
                    selected = true;
                }
                else if (selected) {
                    selected = false;
                    external_width = 0;
                    
                } 
                key_was_pressed = false; 
            }
            else if (mouse(app)) {
                external_width = total_width;
                if (mouseClicked && !selected) {
                    external_colour = CLICKED;
                    selected = true;
                }
                else if (mouseClicked && selected) {
                    selected = false;
                    external_colour = HOVER;
                }
                else if (!selected && (app.key != key_flag)) {
                    external_colour = HOVER;
                }
                else if (selected && (app.key != key_flag)) {
                    external_colour = CLICKED;
                }    
            }
            else if (!selected) {
                setExternalWidth(0);
            }            
            mouseClicked = false;
        }   
    }

    /**
     * Renders the button and its labels on the screen
     * @param app that instantiates the subclass
     * @param mana Mana object to be used by setButtons method
     * @param wave Wave object to be used by setButtons method
     */
    public void drawButtons(PApplet app, Mana mana, Wave wave) {
        setButtons(app, mana, wave);
        drawRectangle(app);
        mainText(app);
        subText(app);
    }
    
    /**
     * Used to change the current state of the button. If was not previously selected, selected becomes true, and false otherwise
     */
    public void setSelected() {
        if (!selected) {
            selected = true;
        }
        else {
            selected = false;
        }
    }

    /**
     * Renders the main text that goes inside the button
     * @param app that instantiates the subclass
     */
    protected void mainText(PApplet app) {
        app.fill(0, 0, 0);
        app.textSize(FONTSIZE);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text(main_text, MAIN_X, MAIN_Y);
    }

    /**
     * Renders the side text description that goes beside the button
     * @param app that instantiates the subclass
     */
    protected void subText(PApplet app) {
        app.fill(0, 0, 0);
        app.textSize(SUBFONTSIZE);
        app.textAlign(PApplet.LEFT);
        app.text(sub_text, SUB_X, SUB_Y);
    }
    
    /**
     * Logic for rendering the box that displays the cost of some gameplay actions on the screen
     * Cost is displayed when mouse hovers over the button
     * @param app that instantiates the subclass
     * @param show_cost Shape object that draws the cost box
     * @param amount the amount to display
     */
    protected void showCost(PApplet app, Shape show_cost, int amount) {
        if (mouse(app) && !mouseClicked) {
            app.strokeWeight(1);
            show_cost.drawRectangle(app);
            
            app.fill(0, 0, 0);
            app.textSize(COSTSIZE);
            app.textAlign(PApplet.CENTER, PApplet.CENTER);
            app.text("Cost: " + amount, COST_X, COST_Y);
        }
    }

}
