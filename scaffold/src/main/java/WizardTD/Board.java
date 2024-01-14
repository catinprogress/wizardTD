package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;
import processing.data.JSONObject;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

public class Board {
    public static final int BOARD_WIDTH = 20;
    private int initial_x; 
    private int initial_y;
    private int wizard_i;
    private int wizard_j;

    private PImage grass;
    private PImage shrub;
    private PImage horizontal_path;
    private PImage vertical_path;
    private PImage right_down_path;
    private PImage right_up_path;
    private PImage left_down_path;
    private PImage left_up_path;
    private PImage cross_path;
    private PImage t_path;
    private PImage flipped_t_path;
    private PImage right_t_path;
    private PImage left_t_path;
    private PImage wizard_house;

    private ArrayList<String> map_as_string;
    private int[][][] tile_coords;
    private int[][] path_array;
    private HashMap<int[], PImage> paths;
    private HashMap<int[], PImage> elements;
    private int[] wizard_coords;
    private ArrayList<ArrayList<int[]>> all_valid_paths;

    public Board(PApplet app, PImage grass, PImage shrub, PImage horizontal_path, PImage right_down_path, PImage cross_path, PImage t_path, PImage wizard_house, JSONObject json, int height){
        initial_x = 0;
        initial_y = height - BOARD_WIDTH*RetrieveValues.PIXELSPERTILE;

        this.grass = grass;
        this.shrub = shrub;
        this.wizard_house = wizard_house;

        this.horizontal_path = horizontal_path;
        vertical_path = rotateImageByDegrees(horizontal_path, 90, app);

        this.right_down_path = right_down_path;
        right_up_path = rotateImageByDegrees(right_down_path, 90, app);
        left_up_path = rotateImageByDegrees(right_down_path, 180, app);
        left_down_path = rotateImageByDegrees(right_down_path, 270, app);

        this.cross_path = cross_path;

        this.t_path = t_path;
        flipped_t_path = rotateImageByDegrees(t_path, 180, app);
        right_t_path = rotateImageByDegrees(t_path, 90, app);
        left_t_path = rotateImageByDegrees(t_path, 270, app);
        
        map_as_string = getMap(json);
        tile_coords = setCoords();
        path_array = set2DPathArray();
        wizard_coords = getWizardHouse();
        paths = getPaths(app);
        elements = getElements();
        all_valid_paths = findShortestPathstoFollow();
    }

     /**
     * Retrieves the layout of map for the current level provided in the config JSONObject and stores each row as a String in an ArrayList
     * @return lines_in_file
     */
    protected ArrayList<String> getMap(JSONObject json) {
        File map_file = new File(json.getString("layout"));
        ArrayList<String> lines_in_file = new ArrayList<String>();

        try {
            Scanner scan = new Scanner(map_file);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                while (line.length() != BOARD_WIDTH) {
                    line += " ";
                }
                lines_in_file.add(line);
            }
            scan.close();         
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        }
            
        return lines_in_file;
    }

     /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle, PApplet app) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = app.createImage(newWidth, newHeight, PApplet.RGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
        
    /**
     * Calculates the leftmost x,y pixel coordinates of each tile on the board, according to their respective row and column position
     * row and column number is relative to board width
     * x, y coordinate depends on the number of pixels per tile
     * @return coord_array
     */
    private int[][][] setCoords() {
        int[][][] coord_array = new int[BOARD_WIDTH][BOARD_WIDTH][2];
        int x;
        int y;

        for (int i = 0; i < BOARD_WIDTH; i++) {
            y = initial_y + (RetrieveValues.PIXELSPERTILE*i);
            for (int j = 0; j < BOARD_WIDTH; j++) {
                x = initial_x + (RetrieveValues.PIXELSPERTILE*j);  
                coord_array[i][j][0] = x;
                coord_array[i][j][1] = y;

            }    
        } 
        return coord_array;
    }

    /**
     * Creates a 2D array to determine the location of a path tile
     * path or wizard house represented by the value 0
     * all other path elements represented by the value 1
     * @return path_array
     */
    private int[][] set2DPathArray() {
        int[][] path_array = new int[BOARD_WIDTH][BOARD_WIDTH];

        for (int i = 0; i < BOARD_WIDTH; i++) {
            String line = map_as_string.get(i); //get ith row in map
            for (int j = 0; j < BOARD_WIDTH; j++) { //get jth column in map
                if (line.charAt(j) == 'X') { 
                    path_array[i][j] = 0;
                }
                else if (line.charAt(j) == 'W') {
                    path_array[i][j] = 0;
                    wizard_i = i;
                    wizard_j = j;
                }

                else {
                    path_array[i][j] = 1;
                }
            }
        }
        return path_array;
    }

    /**
     * Retrieves an ArrayList containing the leftmost x, y pixel coordinates of each grass tile on the board by invoking the getTileCoords method
     * @return grass_tile coordinates
     */
    public ArrayList<int[]> getGrassTiles() {
        return getTileCoords(' ');
    }

    /**
     * Creates an ArrayList containing the leftmost x, y pixel coordinates of the tile that corresponds to a given char symbol in the map txt file
     * @param symbol char that represents a certain tile element
     * @return tiles_coords
     */
    private ArrayList<int[]> getTileCoords(char symbol) {
        ArrayList<int[]> tiles_coords = new ArrayList<>();

        for (int i = 0; i < BOARD_WIDTH; i++) {
            String line = map_as_string.get(i); //get ith row in map
            for (int j = 0; j < BOARD_WIDTH; j++) { //get jth column in map
                if (line.charAt(j) == symbol) { 
                    int[] coords = {tile_coords[i][j][0], tile_coords[i][j][1]};
                    tiles_coords.add(coords);
                }
            }
        }
        return tiles_coords;
    }

    /**
     * Retrieves an ArrayList containing the leftmost x, y pixel coordinates of each path tile on the board by invoking the getTileCoords method
     * @return path_tile coordinates
     */
    public ArrayList<int[]> getPathTiles() {
        return getTileCoords('X');
    }

    /**
     * Finds the x, y pixel coordinates of the wizard house
     * calculates the coordinate offset for centering the wizard house on its tile
     * @return coords
     */
    private int[] getWizardHouse() {
        int[] coords = new int[2];
        for (int i = 0; i < BOARD_WIDTH; i++) { //get rows in map
            String line = map_as_string.get(i); //current line in map
            for (int j = 0; j < BOARD_WIDTH; j++) { //get column in map
                if (line.charAt(j) == 'W') {
                    coords[0] = tile_coords[i][j][0] - 8; //offset to center wizard house
                    coords[1] = tile_coords[i][j][1] - 8;
                }
            }
        }
        return coords;
    }

    /**
     * Renders the image of each tile onto the screen at their respective coordinates
     * @param app that instantiates this class
     */
    public void draw(PApplet app) {
        for (Map.Entry<int[], PImage> entry: paths.entrySet()) {
            int[] coords = entry.getKey();
            app.image(entry.getValue(), coords[0], coords[1]);
        }

        for (Map.Entry<int[], PImage> entry: elements.entrySet()) {
            int[] coords = entry.getKey();
            app.image(entry.getValue(), coords[0], coords[1]);
        }        
    }  

    /**
     * Renders wizard house on screen; invoked separately as it must called after all other board objects to be shown on top
     * @param app that instantiates the class
     */
    public void drawWizard(PApplet app) {
        app.image(this.wizard_house, wizard_coords[0], wizard_coords[1]);
    }
    
    /**
     * Creates hashmap that maps a path PImage to their respective path coordinate
     * Path image to put is calculated by neighbouring tiles and whether they are paths or not 
     * @param app that instantiantes this class
     * @return path_map
     */
    private HashMap<int[], PImage> getPaths(PApplet app) {
        HashMap<int[], PImage> path_map = new HashMap<int[], PImage>();

        for (int i = 1; i < BOARD_WIDTH-1; i++) { //get path images for centre of map
            for (int j = 1; j < BOARD_WIDTH-1; j++) {               
                if (path_array[i][j] == 0) {
                    PImage to_put = new PImage();
                    if (path_array[i-1][j] == 1 & path_array[i+1][j] == 1) { //i-1 = row above, i+1 = row below
                        to_put = horizontal_path;
                    }
                    else if (path_array[i-1][j] == 0 & path_array[i+1][j] == 0) {
                        if (path_array[i][j+1] == 1 & path_array[i][j-1] == 1) { //j+1 = column to right, j-1 = column to left
                            to_put = vertical_path;
                        }
                        else if (path_array[i][j+1] == 1 & path_array[i][j-1] == 0) {
                            to_put = right_t_path;
                        }
                        else if (path_array[i][j+1] == 0 & path_array[i][j-1] == 1) {
                            to_put = left_t_path;
                        }
                        else {
                            to_put = cross_path;
                       }
                    }
                    else if (path_array[i-1][j] == 1 & path_array[i+1][j] == 0) {
                        if (path_array[i][j+1] == 1 & path_array[i][j-1] == 1) {
                            to_put = vertical_path;
                        }
                        else if (path_array[i][j+1] == 1 & path_array[i][j-1] == 0) {
                            to_put = right_down_path;
                        }
                        else if (path_array[i][j+1] == 0 & path_array[i][j-1] == 1) {
                            to_put = left_down_path;
                        }
                        else {
                            to_put = t_path;
                        }   
                    }
                    else {
                        if (path_array[i][j+1] == 1 & path_array[i][j-1] == 1) {
                            to_put = vertical_path;
                        }
                        else if (path_array[i][j+1] == 1 & path_array[i][j-1] == 0) {
                            to_put = right_up_path;
                        }
                        else if (path_array[i][j+1] == 0 & path_array[i][j-1] == 1) {
                            to_put = left_up_path;
                        }
                        else {
                            to_put = flipped_t_path;
                        }
                    
                    } 
                    
                    path_map.put(tile_coords[i][j], to_put);
                }
            }
        }

        //get edges of board
        for (int j = 1; j < BOARD_WIDTH-1; j++) {
            if (path_array[0][j] == 0) {
                PImage to_put = getBorderRows(path_array[0], true, path_array[1], j, app);
                path_map.put(tile_coords[0][j], to_put);
            }

            if (path_array[BOARD_WIDTH-1][j] == 0) {
                PImage to_put = getBorderRows(path_array[BOARD_WIDTH-1], false, path_array[BOARD_WIDTH-2], j, app);
                path_map.put(tile_coords[BOARD_WIDTH-1][j], to_put);
            }

            if (path_array[j][0] == 0) {
                PImage to_put = getBorderColumns(path_array, 0, j, 1, true, app);
                path_map.put(tile_coords[j][0], to_put);
            }        

            if (path_array[j][BOARD_WIDTH-1] == 0) {
                PImage to_put = getBorderColumns(path_array, BOARD_WIDTH-1, j, -1, false, app);
                path_map.put(tile_coords[j][BOARD_WIDTH-1], to_put);
            }           
        }

        for (int i = 0; i < BOARD_WIDTH; i += (BOARD_WIDTH-1)) {
            for (int j = 0; j < BOARD_WIDTH; j += (BOARD_WIDTH-1)) {
                if (path_array[i][j] == 0) {
                    path_map.put(tile_coords[i][j], cross_path);
                }
            }
        }

        return path_map;
    }

    /**
     * Retrieves the path image to place on the top and bottom edges of the board
     * @param row coordinates of each tile in the row
     * @param is_top boolean value of whether row is top or bottom
     * @param row_below coordinates of adjacent row
     * @param column_index index of column at the given row coordinate
     * @param app that instantiates this class
     * @return the path PImage to place
     * 
     */
    private PImage getBorderRows(int[] row, boolean is_top, int[] row_below, int column_index, PApplet app) {
        PImage to_put = new PImage();
        int rotate_by = 0;
        if (row[column_index+1] == 1 & row[column_index-1] == 1) {
            to_put = vertical_path;      
        }
        else if (row[column_index+1] == 0 & row[column_index-1] == 0) {
            if (row_below[column_index] == 0) {
                to_put = cross_path;
            }
            else {
                to_put = flipped_t_path;
                rotate_by = 180;
            }
        }
        else if (row[column_index+1] == 0 & row[column_index-1] == 1) {
            if (row_below[column_index] == 0) {
                to_put = left_t_path;
                rotate_by = 90;
            }
            else {
                to_put = left_up_path;
                rotate_by = 90;
            }
        }
        else if (row_below[column_index] == 0) {
                to_put = right_t_path;
        }
        else {
            to_put = right_up_path;
        }
        
        if (is_top) {
            return to_put;
        }
        return rotateImageByDegrees(to_put, rotate_by, app);
        
    }

    /**
     * Retrieves the path image to place on the left and right edges of the board
     * @param column 2D path array that indicates paths as 0
     * @param column_num y position of current coordinate
     * @param row_index x position of current row
     * @param adjacent_column index of adjacent column
     * @param is_left boolean value of whether column is on the left or right edge
     * @param app that instantiates the class
     * @return the path PImage to place
     */

    //get the images to put on the left and right edges of the board
     private PImage getBorderColumns(int[][] column, int column_num, int row_index, int adjacent_column, boolean is_left, PApplet app) {
        PImage to_put = new PImage();
        int rotate_by = 0;
        
        if (column[row_index-1][column_num] == 1 & column[row_index+1][column_num] == 1) {
            to_put = horizontal_path;     
        }
        else if (column[row_index-1][column_num] == 0 & column[row_index+1][column_num] == 0) {
            if (column[row_index][column_num+adjacent_column] == 0) {
                to_put = cross_path;
            }
            else {
                to_put = right_t_path; //180
                rotate_by = 180;
            }  
        }
        else if (column[row_index-1][column_num] == 0 & column[row_index+1][column_num] == 1) {
            if (column[row_index][column_num+adjacent_column] == 0) {
                to_put = flipped_t_path;
            }
            else {
                to_put = right_up_path; //90
                rotate_by = 90;
            }
        }
        else if (column[row_index-1][column_num] == 1 & column[row_index+1][column_num] == 0) {
            if (column[row_index][column_num+adjacent_column] == 0) {
                to_put = t_path;
            }
            else {
                to_put = right_down_path; //270
                rotate_by = 270;
            }
        }

        if (is_left) {
            return to_put;
        }
        return rotateImageByDegrees(to_put, rotate_by, app);
    }

    /**
     * Creates a hashmap that maps the PImage of all grass and shrub tiles to their respective x,y pixel coordinates
     * @return elements_map
     */
    private HashMap<int[], PImage> getElements() {
        HashMap<int[], PImage> elements_map = new HashMap<int[], PImage>();

        for (int i = 0; i < BOARD_WIDTH; i++) { //get rows in map
            String line = map_as_string.get(i); //current line in map
            for (int j = 0; j < BOARD_WIDTH; j++) { //get column in map
                if (line.charAt(j) == ' ' || line.charAt(j) == 'W') {
                    elements_map.put(tile_coords[i][j], this.grass);
                }
                else if (line.charAt(j) == 'S') {
                    elements_map.put(tile_coords[i][j], this.shrub);
                }
            }
        }
        return elements_map;
    }
    
    /**
     * Retrieves an ArrayList containing coordinates of all valid monster spawn locations for a given map
     * Spawn locations are determined by whether coordinates on the edge of the map that have a connected path to the wizard's house
     * @return spawn_locations
     */
    public ArrayList<int[]> getSpawnLocations() {
        ArrayList<int[]> spawn_locations = new ArrayList<int[]>();
        boolean[][] path_visited = new boolean[BOARD_WIDTH][BOARD_WIDTH];

        findSpawnLocations(wizard_i, wizard_j, spawn_locations, path_visited);
     
        if (spawn_locations.size() < 1) {
            System.err.println("No spawn locations for this level!");
        }
        return spawn_locations;
    }
    
    /**
     * Recursively follows all paths starting from the wizard's house to the edge of the board to find all valid spawn locations
     * Base case: reach the edge of the board
     * Recursive relation: if one of the surrounding tiles is a path
     * @param i the starting row position of the tile
     * @param j the starting column position of the tile
     * @param spawn_locations ArrayList to append valid spawn location positions
     * @param path_visited 2D array of same size as board to remember whether path was traversed already by previous method call
     */
    private void findSpawnLocations(int i, int j, ArrayList<int[]> spawn_locations, boolean[][] path_visited) {
        if ((i == 0 || i == 19 || j == 0 || j == 19) && path_array[i][j] == 0) { //base case = if at the edge of the board and on a path
            path_visited[i][j] = true;
            int[] array_coords = {i, j};
            spawn_locations.add(array_coords);
            return;
        }
        else if (path_array[i][j] == 0 || path_array[i][j] == 2) { //recursive relation = if current tile is a path and one of the surrounding tiles is also a path, go to the next path
            path_visited[i][j] = true;
            if (path_array[i][j+1] == 0 && !path_visited[i][j+1]) {
                findSpawnLocations(i, j+1, spawn_locations, path_visited);
                }
            if (path_array[i][j-1] == 0 && !path_visited[i][j-1]) {
                findSpawnLocations(i, j-1, spawn_locations, path_visited);
                }
            if (path_array[i+1][j] == 0 && !path_visited[i+1][j]) {
                findSpawnLocations(i+1, j, spawn_locations, path_visited);                
                }
            if (path_array[i-1][j] == 0 && !path_visited[i-1][j]) {
                findSpawnLocations(i-1, j, spawn_locations, path_visited);  
            }  
        }  
    }
    
    /**
     * Finds the shortest path to the wizard house and stores in a nested ArrayList
     * If multiple paths of the same length are found, they are all added to the list
     * @return all_valid_paths
     */
    private ArrayList<ArrayList<int[]>> findShortestPathstoFollow() {
        ArrayList<int[]> spawn_locations = getSpawnLocations();
        ArrayList<ArrayList<int[]>> all_valid_paths = new ArrayList<>();
        PathFinding.Point end = new PathFinding.Point(wizard_j, wizard_i, null);
         
        for (int[] location : spawn_locations) {        
            PathFinding.Point start = new PathFinding.Point(location[1], location[0], null);
            List<PathFinding.Point> path = PathFinding.FindPath(path_array, start, end);

            if (path != null) {
                ArrayList<int[]> valid_path = new ArrayList<>();
                valid_path.add(tile_coords[location[0]][location[1]]);
                for (PathFinding.Point point : path) {
                    int[] coords = tile_coords[point.y][point.x];
                    valid_path.add(coords);
                }
                all_valid_paths.add(valid_path);
            }
        }

        if (all_valid_paths.size() == 0 || all_valid_paths == null) {
            System.err.println("No valid paths to follow");
            System.exit(1);
        }

        int smallest = all_valid_paths.get(0).size(); //get shortest path
        for (int i = 1; i < all_valid_paths.size(); i++) {
            if (all_valid_paths.get(i).size() < smallest) {
                smallest = all_valid_paths.get(i).size();
                all_valid_paths.remove(i-1);
            }
            else if (all_valid_paths.get(i).size() > smallest) {
                all_valid_paths.remove(i);
            }
        }
        return all_valid_paths;
    }

    /**
     * Retrieves nestest ArrayList containing all shortest paths to the wizard house
     * @return all_valid_path
     */
    public ArrayList<ArrayList<int[]>> getValidPaths() {
        return all_valid_paths;
    }
    
}
