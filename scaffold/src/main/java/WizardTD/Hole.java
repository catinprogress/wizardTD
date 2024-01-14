package WizardTD;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
public class Hole extends GameplayActions {
    public static final int HOLE_COST = 100;
    public static final int HOLE_WAIT = 5;
    public static final int[] DESELECT = {62,71,114};

    private PImage hole;
    private ArrayList<int[]> path_tiles_coords;
    private int[] test;
    private ArrayList<int[]> holes_to_remove;
    private HashMap<int[], PImage> holes_placed;
    private boolean hole_placed;
    private int count;
    private Shape cost_show;
    private int current_cost;

    public Hole(PImage hole, Board board) {
        super(412, 'h', "H ", "Hole\ntrap");
        this.hole = hole;
        test = new int[2];
        this.path_tiles_coords = board.getPathTiles();
        holes_to_remove = new ArrayList<>();
        holes_placed = new HashMap<>();
        hole_placed = false;

        cost_show = new Shape(585, y + 2, 52, 18, COSTBOX, COSTBOX);
        current_cost = HOLE_COST;
    }

    /**
     * Retrieves the current cost of purchasing a hole
     * @return current_cost
     */
    public int getHoleCost() {
        return current_cost;
    }

    public HashMap<int[], PImage> getHolesPurchased() {
        return holes_placed;
    }

    /**
     * Renders a cross on top of the button when hole_placed is true to signify that the player cannot purchase a hole at the current time
     * @param app that instantiates this class
     */
    public void deselect(PApplet app) {
        if (hole_placed) {
            app.strokeWeight(3);
            app.stroke(DESELECT[0], DESELECT[1], DESELECT[2]);
            app.line(x, y, x + DIMENSION, y + DIMENSION);
            app.line(x, y+DIMENSION, x + DIMENSION, y);
        }  
    }

    /**
     * Checks if the current mouse coordinates are contained within a location of provided within a Collection of x,y coordinates.
     * Used to verify whether the mouse was clicked over a path tile or a purchased hole tile. If true, test is set to these coordinates.
     * @param app that insantiates the class
     * @param all_coords the Collection of int arrays containing 2 elements which correspond to x,y coordinates on the screen
     * @return true if mouse was clicked over valid tile, false otherwise
     */
    public boolean checkMouse(PApplet app, Collection<int[]> all_coords) {
        for (int[] coords : all_coords) {
            if (mouseClicked && (app.mouseX >= coords[0] && app.mouseX <= (coords[0] + WaveElements.PIXELSPERTILE)) && (app.mouseY >= coords[1] && app.mouseY <= (coords[1] + WaveElements.PIXELSPERTILE))) {
                test = coords;
                return true;  
            }
        }
        return false;
    }

    /**
     * Logic for checking if the user has selected the button and clicked their mouse over a valid path tile, in order to make a hole purchase.
     * If a hole is purchased, this method checks if 5 seconds have passed, to signify that the user can purchase another hole, however the cost will increase every use
     * @param app that instantiates this class
     * @param mana Mana object used to check if game has been lost yet
     * @param wave Wave object used to chekc if game has been won and retrieve spawned monsters to kill
     */
    public void tick(PApplet app, Mana mana, Wave wave, FastForward ff) {
        if (!mana.hasLost() && !wave.hasWon()) {
            int fps = WaveElements.FPS;
            if (ff.isSelected()) {
                fps = WaveElements.FPS/2;
            }
            if (hole_placed && app.frameCount%fps == 0) {
                if (count < HOLE_WAIT) { //if less than five seconds after hole purchased
                    count++;
                }
                else { // increase cost
                    hole_placed = false;
                    cost_show.setExternalColour(COSTBOX);
                    current_cost += HOLE_COST;
                }
            }
            else if (selected && !hole_placed && checkMouse(app, path_tiles_coords) && !checkMouse(app, holes_placed.keySet())) { //if button selected, and mouse is clicked over valid tile and user has waited at least 5 seconds after previous purchase
                if (mana.getCurrentMana() > current_cost) {
                    holes_placed.put(test, hole.get());
                    mana.changeCurrentMana(-current_cost); //reduce mana
                    hole_placed = true;
                    count = 0; //reset count so player has to wait another 5 seconds
                    cost_show.setExternalColour(DESELECT);
                }
            }
            
            for (Monsters current : wave.getSpawnedMonsters()) {
                for (int[] coords : holes_placed.keySet()) {
                    if ((coords[0] == (int)(current.getX() - Monsters.OFFSET)) && (coords[1] == (int)(current.getY() - Monsters.OFFSET))) {
                        mana.changeCurrentMana(current.getManaGained()*mana.getManaMultiplier()); //kill monster if in the middle of the hole
                        current.kill();
                        holes_to_remove.add(coords);
                    }
                }
            }
            showCost(app, cost_show, current_cost);

        for (int[] remove : holes_to_remove) {
            holes_placed.remove(remove, holes_placed.get(remove)); //remove purchased holes when monster falls into it
        }
    }     
    
    }

    /**
     * Renders purchased holes on screen in correct position
     * @param app that instantiates this class
     */
    public void draw(PApplet app) {
        for (int[] current : holes_placed.keySet()) {
            app.image(holes_placed.get(current), current[0], current[1]);
        }
    }
}
