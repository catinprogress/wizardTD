package WizardTD;

import processing.core.PApplet;
import processing.data.JSONObject;

public class Mana extends GameElements{
    private double mana_pool_spell_cost_increase_per_use;
    private double mana_pool_spell_cap_multiplier;
    private double mana_pool_spell_mana_gained_multiplier;

    private double mana_pool_cost;
    private double mana_gained_per_second;

    private ManaBar mana_bar;
    private ManaPoolSpell mana_pool;
    
    private double current_mana;
    private double current_mana_cap;
   
    private boolean has_lost;
    private int times_mana_cast;

    public Mana (JSONObject json) {
        super(json);
        current_mana = getNumericAmount("initial_mana", json);
        current_mana_cap = getNumericAmount("initial_mana_cap", json);
        mana_gained_per_second = getNumericAmount("initial_mana_gained_per_second", json);

        mana_bar = new ManaBar();
        mana_bar.setExternalWidth(current_mana/current_mana_cap);
        
        mana_pool_cost = getNumericAmount("mana_pool_spell_initial_cost", json);
        mana_pool_spell_cost_increase_per_use = getNumericAmount("mana_pool_spell_cost_increase_per_use", json);
        mana_pool_spell_cap_multiplier = getNumericAmount("mana_pool_spell_cap_multiplier", json);
        mana_pool_spell_mana_gained_multiplier = getNumericAmount("mana_pool_spell_mana_gained_multiplier", json);

        mana_pool = new ManaPoolSpell((int)mana_pool_cost);
        times_mana_cast = 0;
    }

    /**
     * Retrieves the current mana amount that the player has
     * @return current_mana
     */
    public double getCurrentMana() {
        return current_mana;
    }
    
    /**
     * Retrieves the current mana cap that the player has. This value is the maximum amount of mana that the player can have
     * @return current_mana_cap
     */
    public double getCurrentManaCap() {
        return current_mana_cap;
    }

    /**
     * Allows the user to change the current mana amount.
     * The minimum value that the mana can decrease to for this method is above 0, and
     * the maximum value that the mana can increase to is the current mana cap.
     * Often invoked to purchase items, to prevent players from purposely trying to lose the game by spending all their mana.
     * @param cost value to increase/decrease mana by. If decreasing, cost must be a negative value
     */
    public void changeCurrentMana(double cost) {
        if (cost < 0) {
            if ((cost + current_mana) > 0) { //decrease mana
                current_mana += cost;
                double decrement = (-cost)/current_mana_cap;
                this.mana_bar.decreaseExternalWidth(decrement);
            }
        }
        else if (cost >= 0) { //increase mana
            if ((cost + current_mana) <= current_mana_cap) {
                current_mana += cost;
                double increment = cost/current_mana_cap;
                this.mana_bar.increaseExternalWidth(increment);
            }
        }
    }

    /**
     * Allows the user to decrease the mana to a minimum value of 0. 
     * Often invoked when monsters reach the wizard's house, as this allows the game to be lost
     * @param amount to decrease mana by. Value must be a positive amount
     */
    public void decreaseMana(double amount) {
        if (amount > 0) {
            if (current_mana - amount <= 0) {
                this.mana_bar.setExternalWidth(0);
                current_mana = 0;
            }
            else {
                double decrement = amount/current_mana_cap;
                current_mana -= amount;
                this.mana_bar.decreaseExternalWidth(decrement);
            }
        }
    }

    /**
     * Retrieves the mana pool spell instance for the game
     * @return mana_pool
     */
    public ManaPoolSpell getManaPoolSpell() {
        return mana_pool;
    }

    /**
     * Indicates whether the player has lost the game or not.
     * @return true if player has run out of mana, false otherwise
     */
    public boolean hasLost() {
        return has_lost;
    }

    /**
     * Logic for playing the mana pool spell and changing the current mana values accordingly.
     * Also allows mana to increase per second when the game is not paused, and changes the player's current lose status if mana decreases to 0.
     * @param app that instantiates the class
     * @param pause Pause object used to check if game has been paused
     * @param ff FastForward object used to check if game has been fastforwarded
     * @param wave Wave object used to check whether player has won the game or not
     */
    public void tick(PApplet app, Pause pause, FastForward ff, Wave wave) {
        if (current_mana <= 0) {
            has_lost = true;
        }
        else {
            has_lost = false;
        }
        if (!has_lost && !wave.hasWon()) {
            mana_pool.tick(app);
            int fps = FPS;
            if (ff.isSelected()) {
                fps = (int)(FPS/2);
            }

            if (mana_pool.isSelected() && !mana_pool.wasSelected() && mana_pool_cost < current_mana) {
                times_mana_cast++;
                current_mana -= mana_pool_cost;
                current_mana_cap += current_mana_cap*(mana_pool_spell_cap_multiplier-1)*(times_mana_cast);
                mana_gained_per_second += mana_gained_per_second*(mana_pool_spell_mana_gained_multiplier-1)*(times_mana_cast);
                mana_bar.setExternalWidth(current_mana/current_mana_cap);
                mana_pool_cost += mana_pool_spell_cost_increase_per_use;
                mana_pool.updateCost((int)mana_pool_cost);
                mana_pool.setSelected();  
            }
            
            if (!pause.isSelected()) {
                if (app.frameCount%fps == 0) {
                    changeCurrentMana(mana_gained_per_second);
                }
            }
        }
        
    }
    
    /**
     * Renders the mana pool spell button and the mana bar on the screen
     * @param app that instantiates the class
     * @param wave Wave object that is invoked by the mana pool spell object
     */
    public void draw(PApplet app, Wave wave) {
        this.mana_pool.drawButtons(app, this, wave);
        
        this.mana_bar.setBorder(app);
        this.mana_bar.drawRectangle(app); 
        
        this.mana_bar.showAmountLeft(app, (int)current_mana, (int)current_mana_cap);
        this.mana_bar.showText(app);
    }

}
