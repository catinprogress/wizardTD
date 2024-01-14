package WizardTD;

import processing.core.PApplet;

public final class UpgradeCost extends Shape {
    public static final int[] COLOUR = {255, 255, 255};
    public static final int UPGRADE_COST_MULTIPLIER = 10;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 18;
    public static final float X_COORD = GameplayActions.X + WIDTH/2;
    public static final float Y_COORD = 555 + HEIGHT/2;

    private String speed_string;
    private String damage_string;
    private String range_string;
    private String cost_string;
    private String total_string;

    private Shape show_upgrades;
    private Shape show_total;
    private int total_cost;
    
    private UpgradeRange range;
    private UpgradeDamage damage;
    private UpgradeSpeed speed;
    
    public UpgradeCost(UpgradeDamage damage, UpgradeRange range, UpgradeSpeed speed) {
        super(GameplayActions.X, 555, 80, 18, COLOUR, COLOUR);
        cost_string = "Upgrade cost";
        speed_string = "speed:";
        damage_string = "damage:";
        range_string = "range:";
        total_string = "Total:";

        this.range = range;
        this.damage = damage;
        this.speed = speed;
        total_cost = 0;

        show_upgrades = new Shape(GameplayActions.X, this.y+this.height, this.total_width, this.height, COLOUR, COLOUR);
        show_total = new Shape(GameplayActions.X, show_upgrades.getY() + this.height, this.total_width, this.height, COLOUR, COLOUR);

    }
    /**
     * Renders "Upgrade cost" on the screen
     * @param app that invokes the object
     */
    private void upgradeCostText(PApplet app) {
        app.fill(0, 0, 0);
        app.textSize(12);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text(cost_string, X_COORD, Y_COORD);
    }

    /**
     * Retrieves the total cost of all the upgrades that are selected when the tick method is invoked
     * @return total_cost
     */
    public int getTotalCost() {
        return total_cost;
    }

    public float getUpgradeBoxTotalHeight() {
        return show_upgrades.getHeight();
    }

    /**
     * Logic for changing the height of the size of the box that shows the cost of each upgrade option, according to how many options are selected
     * @param app that invokes the object
     */
    private void changeHeight(PApplet app) {
        if (damage.isSelected() && range.isSelected() && speed.isSelected()) {
            show_upgrades.setHeight(this.height*3);
        }
        else if (speed.isSelected() && range.isSelected()) {
            show_upgrades.setHeight(this.height*2);
        }
        else if (speed.isSelected() && damage.isSelected()) {
            show_upgrades.setHeight(this.height*2);
        }
        else if (range.isSelected() && damage.isSelected()) {
            show_upgrades.setHeight(this.height*2);
        }
        else if (speed.isSelected()) {
            show_upgrades.setHeight(this.height);
        }
        else if (range.isSelected()) {
            show_upgrades.setHeight(this.height);
        }
        else if (damage.isSelected()) {
            show_upgrades.setHeight(this.height);
        }
        show_total.setY(show_upgrades.getY() + show_upgrades.getHeight());
    }

    /**
     * Logic for rendering the order in which to display the different upgrades on screen, depending on which options were selected. The order of the text corresponds to the order in which the upgrade buttons are shown on screen.
     * @param app that invokes the object
     * @param range_level current range level of tower
     * @param damage_level current damage level of tower
     * @param speed_level current speed level of tower
     */
    private void upgradesSelectedText(PApplet app, int range_level, int damage_level, int speed_level) {
        int speed_cost = getUpgradeCost(speed_level);
        int range_cost = getUpgradeCost(range_level);
        int damage_cost = getUpgradeCost(damage_level);
        
        app.fill(0, 0, 0);
        app.textSize(12);
        app.textAlign(PApplet.LEFT, PApplet.BOTTOM);
        
        if (damage.isSelected() && range.isSelected() && speed.isSelected()) {
            total_cost = speed_cost + range_cost + damage_cost;
            app.text(range_string + "\n" + speed_string + "\n"  + damage_string, show_upgrades.getX() + 2, show_total.getY() - 1);

            app.textAlign(PApplet.RIGHT, PApplet.BOTTOM);
            app.text(range_cost + "\n" + speed_cost + "\n" +  damage_cost, show_upgrades.getX() + total_width - 2, show_total.getY() - 1);
        }
        else if (speed.isSelected() && range.isSelected()) {
            total_cost = speed_cost + range_cost;
            app.text(range_string + "\n" + speed_string , show_upgrades.getX() + 2, show_total.getY() - 1);

            app.textAlign(PApplet.RIGHT, PApplet.BOTTOM);
            app.text(range_cost + "\n" + speed_cost,  show_upgrades.getX() + total_width - 2, show_total.getY() - 1);
        }
        else if (speed.isSelected() && damage.isSelected()) {
            total_cost = speed_cost + damage_cost;
            app.text(speed_string + "\n" + damage_string, show_upgrades.getX() + 2, show_total.getY() - 1);

            app.textAlign(PApplet.RIGHT, PApplet.BOTTOM);
            app.text(speed_cost + "\n" + damage_cost,  show_upgrades.getX() + total_width - 2, show_total.getY() - 1);
        }
        else if (range.isSelected() && damage.isSelected()) {
            total_cost = range_cost + damage_cost;
            app.text(range_string + "\n" + damage_string, show_upgrades.getX() + 2, show_total.getY() - 1);

            app.textAlign(PApplet.RIGHT, PApplet.BOTTOM);
            app.text(range_cost + "\n" + damage_cost,  show_upgrades.getX() + total_width - 2, show_total.getY() - 1);
        }
        else if (range.isSelected()) {
            total_cost = range_cost;
            app.text(range_string, show_upgrades.getX() + 2, show_total.getY() - 1);

            app.textAlign(PApplet.RIGHT, PApplet.BOTTOM);
            app.text(range_cost,  show_upgrades.getX() + total_width - 2, show_total.getY() - 1);
        }
        else if (speed.isSelected()) {
            total_cost = speed_cost;
            app.text(speed_string, show_upgrades.getX() + 2, show_total.getY() - 1);

            app.textAlign(PApplet.RIGHT, PApplet.BOTTOM);
            app.text(speed_cost,  show_upgrades.getX() + total_width - 2, show_total.getY() - 1);
        } 
        else if (damage.isSelected()) {
            total_cost = damage_cost;
            app.text(damage_string, show_upgrades.getX() + 2, show_total.getY() - 1);

            app.textAlign(PApplet.RIGHT, PApplet.BOTTOM);
            app.text(damage_cost,  show_upgrades.getX() + total_width - 2, show_total.getY() - 1);
        }
    }

    /**
     * Renders the text and shape that displays the total cost of all upgrades selected
     * @param app that invokes the object
     */
    private void totalText(PApplet app) {
        app.fill(0, 0, 0);
        app.textSize(12);
        app.textAlign(PApplet.LEFT, PApplet.CENTER);
        app.text(total_string, show_total.getX() + 2, show_total.getY() + HEIGHT/2);

        app.textAlign(PApplet.RIGHT, PApplet.CENTER);
        app.text(total_cost, show_total.getX() + total_width - 2, show_total.getY() + HEIGHT/2);

    }

    /**
     * Logic for displaying the cost box when at least one of the upgrade options have been seletec
     * @param app that invokes the object
     * @param range_level current range level of tower
     * @param damage_level current damage level of tower
     * @param speed_level current speed level of tower
     */
    public void tick(PApplet app, int range_level, int damage_level, int speed_level) {
        app.strokeWeight(1);
        if (damage.isSelected() || speed.isSelected() || range.isSelected()) {
            drawRectangle(app);
            upgradeCostText(app);
        
            changeHeight(app);

            show_upgrades.drawRectangle(app);
            upgradesSelectedText(app, range_level, damage_level, speed_level);
            
            show_total.drawRectangle(app);    
            totalText(app);
        }
    }
    
    /**
     * Retrieves the cost of upgrading to a certain level. The upgrade cost is calculated by multiplying 10 by the current level and adding 20
     * @param current_level of an upgrade option
     * @return upgrade cost if current level is non-negative value, 0 otherwise
     */
    public static int getUpgradeCost(int current_level) {
        if (current_level >= 0) {
            int upgrade_cost = (current_level)*(UPGRADE_COST_MULTIPLIER) + 20;
            return upgrade_cost;
        }
        else {
            return 0;
        }
    }
}
