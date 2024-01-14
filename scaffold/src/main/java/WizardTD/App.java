package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import java.util.*;

public class App extends PApplet {
    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static final int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static final int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;

    protected String configPath;
    protected JSONObject json;
    protected Board board;
    protected Wave wave;
    protected PImage shrub;
    protected PImage grass;
    protected PImage wizard_house;
    protected PImage horizontal_path;
    protected PImage right_down_path;
    protected PImage t_path;
    protected PImage cross_path;
    protected PImage gremlin;
    protected PImage death_1;
    protected PImage death_2;
    protected PImage death_3;
    protected PImage death_4;
    protected PImage death_5;
    protected PImage worm;
    protected PImage beetle;
    protected PImage tower_0;
    protected PImage tower_1;
    protected PImage tower_2;
    protected PImage fireball;
    protected PImage hole;
    protected HashMap<String, PImage> monster_types;
    protected Mana mana;
    protected FastForward ff;
    protected Pause p;
    protected BuildTower b;
    protected UpgradeRange u1;
    protected UpgradeSpeed u2;
    protected UpgradeDamage u3;
    protected Hole h;
    protected char last_key;

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
        
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
        this.json = loadJSONObject(configPath);
        this.grass = this.loadImage("src/main/resources/WizardTD/grass.png");
        this.shrub = this.loadImage("src/main/resources/WizardTD/shrub.png");
        this.wizard_house = this.loadImage("src/main/resources/WizardTD/wizard_house.png");
        this.horizontal_path = this.loadImage("src/main/resources/WizardTD/path0.png");
        this.right_down_path = this.loadImage("src/main/resources/WizardTD/path1.png");
        this.t_path = this.loadImage("src/main/resources/WizardTD/path2.png");
        this.cross_path = this.loadImage("src/main/resources/WizardTD/path3.png");
        this.gremlin = this.loadImage("src/main/resources/WizardTD/gremlin.png");
        this.beetle = this.loadImage("src/main/resources/WizardTD/beetle.png");
        this.worm = this.loadImage("src/main/resources/WizardTD/worm.png");
        this.death_1 = this.loadImage("src/main/resources/WizardTD/gremlin1.png");
        this.death_2 = this.loadImage("src/main/resources/WizardTD/gremlin2.png");
        this.death_3 = this.loadImage("src/main/resources/WizardTD/gremlin3.png");
        this.death_4 = this.loadImage("src/main/resources/WizardTD/gremlin4.png");
        this.death_5 = this.loadImage("src/main/resources/WizardTD/gremlin5.png");
        this.tower_0 = this.loadImage("src/main/resources/WizardTD/tower0.png");
        this.tower_1 = this.loadImage("src/main/resources/WizardTD/tower1.png");
        this.tower_2 = this.loadImage("src/main/resources/WizardTD/tower2.png");
        this.fireball = this.loadImage("src/main/resources/WizardTD/fireball.png");
        this.hole = this.loadImage("src/main/resources/WizardTD/hole.png");

        monster_types = new HashMap<String, PImage>();
        monster_types.put("gremlin", gremlin);
        monster_types.put("beetle", beetle);
        monster_types.put("worm", worm);
    
        this.board = new Board(this, grass, shrub, horizontal_path, right_down_path, cross_path, t_path, wizard_house, json, HEIGHT);
        this.mana = new Mana(json);
        
        this.p = new Pause();  
        this.wave = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
        this.ff = new FastForward();
        this.u1 = new UpgradeRange();
        this.u2 = new UpgradeSpeed();
        this.u3 = new UpgradeDamage();
        this.b = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
        this.h = new Hole(hole, board);
    }


    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(){
        GameplayActions.setKeyWasPressed(true);
        if (key == 'r' && (mana.hasLost() || wave.hasWon())) { //restart game 
            this.board = new Board(this, grass, shrub, horizontal_path, right_down_path, cross_path, t_path, wizard_house, json, HEIGHT);
            this.mana = new Mana(json);
            this.p = new Pause();  
            this.wave = new Wave(mana, json, board, monster_types, death_1, death_2, death_3, death_4, death_5);
            this.ff = new FastForward();
            this.u1 = new UpgradeRange();
            this.u2 = new UpgradeSpeed();
            this.u3 = new UpgradeDamage();
            this.b = new BuildTower(mana, json, u1, u2, u3, tower_0, tower_1, tower_2, board, fireball);
            this.h = new Hole(hole, board);
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }
    
    @Override
    public void mouseClicked() {
        ff.setMouseClicked();
        p.setMouseClicked();
        b.setMouseClicked();
        u1.setMouseClicked();
        u2.setMouseClicked();
        u3.setMouseClicked();
        mana.getManaPoolSpell().setMouseClicked();
        h.setMouseClicked();
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        this.background(130, 116, 79); 
        this.board.draw(this);
        this.mana.tick(this, p, ff, wave);
        
        this.h.draw(this);
        
        
        this.wave.tick(this, p, ff);
        this.board.drawWizard(this);
        this.b.tick(this, wave.getSpawnedMonsters(), ff, p, wave);
        this.b.draw(this);  
        this.wave.draw(this);
        this.h.tick(this, mana, wave, ff);
        
        this.mana.draw(this, wave);
        this.b.drawButtons(this, mana, wave);
        this.ff.drawButtons(this, mana, wave);
        this.p.drawButtons(this, mana, wave);
        this.u1.drawButtons(this, mana, wave);
        this.u2.drawButtons(this, mana, wave);
        this.u3.drawButtons(this, mana, wave);
        this.h.drawButtons(this, mana, wave);
        this.h.deselect(this);
             
        
        this.wave.displayWin(this);
    }

    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

}
