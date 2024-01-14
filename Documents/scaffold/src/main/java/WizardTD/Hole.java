package WizardTD;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.HashMap;

import org.checkerframework.checker.units.qual.h;

public class Hole extends GameplayActions {
    public static final int HOLE_COST = 100;
    public static final int HOLE_WAIT = 5;
    public static final int[] DESELECT = {187,131,155};

    private PImage hole;
    private ArrayList<int[]> path_tiles_coords;
    private int[] test;
    private ArrayList<int[]> holes_placed_coords;
    private ArrayList<int[]> holes_to_remove;
    private HashMap<int[], PImage> holes_placed;
    private boolean hole_placed;
    private int count;
    private Shape cost_show;
    private String to_print;
    private int current_cost;

    public Hole(PImage hole, Board board) {
        super(412, 'h', "H ", "Hole\ntrap");
        this.hole = hole;
        test = new int[2];
        this.path_tiles_coords = board.getPathTiles();
        holes_placed_coords = new ArrayList<>();
        holes_to_remove = new ArrayList<>();
        holes_placed = new HashMap<>();
        hole_placed = false;

        cost_show = new Shape(585, y + 2, 52, 18, COSTBOX, COSTBOX);
        current_cost = HOLE_COST;
    }

    public PImage getHole() {
        return hole;
    }

    public void nextHole(PApplet app) {
        app.fill(0, 0, 0);
        app.textSize(FONTSIZE);
        app.textAlign(PApplet.LEFT);
        app.text(to_print, x + DIMENSION + 10, y);
    }

    public void deselect(PApplet app) {
        if (hole_placed) {
            app.strokeWeight(3);
            app.stroke(DESELECT[0], DESELECT[1], DESELECT[2]);
            app.line(x, y, x + DIMENSION, y + DIMENSION);
            app.line(x, y+DIMENSION, x + DIMENSION, y);
        }  
    }

    public boolean checkMouse(PApplet app, ArrayList<int[]> coords_to_check) {
        for (int[] coords : coords_to_check) {
            if (mouseClicked && (app.mouseX >= coords[0] && app.mouseX <= (coords[0] + GameElements.PIXELSPERTILE)) && (app.mouseY >= coords[1] && app.mouseY <= (coords[1] + GameElements.PIXELSPERTILE))) {
                test = coords;
                return true;  
            }
        }
        return false;
    }

    public void tick(PApplet app, Mana mana, ArrayList<Monsters> spawned_monsters, Wave wave) {
        if (!mana.hasLost() && !wave.hasWon()) {
            if (hole_placed && app.frameCount%(GameElements.FPS) == 0) {
                if (count < HOLE_WAIT) {
                    count++;
                }
                else {
                    hole_placed = false;
                    cost_show.setExternalColour(COSTBOX);
                    current_cost += HOLE_COST;
                }
            }
            else if (selected && !hole_placed && checkMouse(app, path_tiles_coords) && !checkMouse(app, holes_placed_coords)) {
                if (mana.getCurrentMana() > HOLE_COST) {
                    holes_placed_coords.add(test);
                    holes_placed.put(test, hole.get());
                    mana.changeCurrentMana(-HOLE_COST);
                    hole_placed = true;
                    count = 0;
                    cost_show.setExternalColour(DESELECT);
                }
            }
            
            for (Monsters current : spawned_monsters) {
                for (int[] coords : holes_placed_coords) {
                    if (Math.abs((coords[0] + GameElements.PIXELSPERTILE/2) - (current.getX() + Monsters.DIMENSIONS/2)) <= 0.5 && Math.abs((coords[1] + GameElements.PIXELSPERTILE/2) - (current.getY() + Monsters.DIMENSIONS/2)) <= 0.5) {
                        mana.changeCurrentMana(current.getManaGained());
                        current.kill();
                        holes_to_remove.add(coords);
                    }
                }
            }
            showCost(app, cost_show, current_cost);

        for (int[] remove : holes_to_remove) {
            holes_placed_coords.remove(remove);
            holes_placed.remove(remove, holes_placed.get(remove));
        }
    }     

        
    }

    public void draw(PApplet app) {
        for (int[] current : holes_placed.keySet()) {
            app.image(holes_placed.get(current), current[0], current[1]);
        }
    }
}
