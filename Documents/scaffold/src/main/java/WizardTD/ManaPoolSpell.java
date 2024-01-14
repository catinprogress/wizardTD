package WizardTD;

import processing.core.PApplet;

public class ManaPoolSpell extends GameplayActions {    
    private Shape show_cost;
    private boolean was_selected;
    private int cost;

    public ManaPoolSpell(int cost) {
        super(360, 'm', "M ", "Mana pool\ncost: " + cost);
        this.cost = (int)cost;
        show_cost = new Shape(585, y+2, 52, 18, COSTBOX, COSTBOX);
    }
    
    /**
     * Allows user to change the current cost of the mana pool spell
     * @param cost amount to change cost to, must be positive value
     */
    public void updateCost(int cost) {
        if (cost > 0) {
            this.cost = cost;
            sub_text = "Mana pool\ncost: " + cost;
        }
    }

    /**
     * Overrides the superclass GameplayActions method and is used to change the previous selected state of the button rather than the current state of the button, and is
     * invoked by a Mana instance after the button is pressed. Used to prevent mana value from changing when player unselects the button.
     */
    public void setSelected() {
        if (was_selected) {
            was_selected = false;
        }
        else {
            was_selected = true;
        }
    }

    /**
     * Retrieves the previous state of the button. Invoked by Mana instance to prevent undesired change in mana amount and mana pool spell cost when the player unselects the button.
     * @return true if button was previously pressed, false otherwise
     */
    public boolean wasSelected() {
        return was_selected;
    }

    /**
     * Logic for changing the previous selected state of the button to false.
     * When the button is unselected and was previously selected, the previous selected state is set to false.
     * Also displays the cost of the mana pool spell when mouse hovers over the button.
     * @param app that invokes the subclass
     */
    public void tick(PApplet app) {
        if (!selected && was_selected) {
            was_selected = false;
        }
        showCost(app, show_cost, cost);
    }
}
