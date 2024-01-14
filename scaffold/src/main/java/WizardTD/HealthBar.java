package WizardTD;

public class HealthBar extends Shape {
    public static final int[] HEALTH = {122, 251, 103};
    public static final int[] DAMAGE = {234, 51, 35};
    private Monsters monster;
    
    public HealthBar(Monsters monster) {
        super(monster.getX(), monster.getY(), WaveElements.PIXELSPERTILE, WaveElements.PIXELSPERTILE/10, HEALTH, DAMAGE);
        this.monster = monster;
        has_stroke = false;
    }
    
    /**
     * Logic for ensuring that the healthbar of a given monster moves with the monster's current position on the screen and remains above its image
     */
    public void tick() {
        this.x = monster.getX() - Monsters.OFFSET;
        this.y = monster.getY() - Monsters.OFFSET;
    }
}
