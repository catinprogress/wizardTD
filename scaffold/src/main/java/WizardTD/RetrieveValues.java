package WizardTD;

import processing.data.JSONObject;

interface RetrieveValues {
    public static final int PIXELSPERTILE = 32;
    public static final int FPS = 60;
    /**
     * Reads and retrieves a numeric value from a given JSONObject key
     * @param key String indicating the value to retrieve from
     * @param obj the JSONObject to read
     * @return amount
     */
    public default double getNumericAmount(String key, JSONObject obj) {
        double amount = -1;
        try{
            amount = obj.getDouble(key);
        }
        catch(RuntimeException e) {
            System.err.println("Key: '" + key + "' does not exist, or value is not numeric!");
        }

        if (amount <= 0) {
            System.err.println("The initial amount cannot be less than or equal to 0");
        }

        return amount;
    }

    /**
     * Finds the distance between two points on the screen
     * @param start_x initial x coordinate
     * @param start_y initial y coordinate
     * @param end_x destination x coordinate
     * @param end_y destination y coordinate
     * @return distance
     */
    public default double findDistanceBetweenTwoPoints(float start_x, float start_y, float end_x, float end_y) {
        float x_dist = Math.abs(start_x - end_x);
        float y_dist = Math.abs(start_y - end_y);
        double distance = Math.hypot(x_dist, y_dist);

        return distance;
    }


}
