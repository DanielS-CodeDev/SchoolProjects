/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: Attacks.java
 * External Packages: N/A
 * Purpose: The class holds the type (String: Fire, Ice or Lightning), damage 
 *      (integer: [0, 1 million]) and speed (float: [0, 1]) for each attack
 *      Lord Bug performs.
 * Class Constructor: The constructor of the class defines all the fields of
 *      the class. 
 * Class methods list:
 *      getType()
 *      getDamageAmount()
 *      getSpeedAmount()
 *      toString()
 */

public class Attacks {

    private String type; // type of attack: Fire, Ice, Lightning
    private int damageAmount; // amount of damge: int [0, 1 million]
    private double speedAmount; // speed of attack: double [0, 1]

    /*
     * Attacks(type, strDamage, strSpeed) -- Constructor of the class. Returns
     *      nothing. Defines the fields of the class. Takes in three Strings
     *      'type', 'strDamage' and 'strSpeed'. Sets the fields with there
     *      respective types the fields take.
     */
    public Attacks(String type, String strDamage, String strSpeed) {

        this.type = type; // set the field type
        this.damageAmount = Integer.valueOf(strDamage); // set damage as an int
        this.speedAmount = Double.valueOf(strSpeed); // set speed as a float
    }

    /*
     * Below are simple getters for the fields of the class. Returns the value
     * for that specific field.
     */

    public String getType() {
        return this.type;
    }

    public int getDamageAmount() {
        return this.damageAmount;
    }

    public double getSpeedAmount() {
        return this.speedAmount;
    }

    /*
     * toString() -- No parameters. Returns a String representation of the
     *      Attacks class. String will look like:
     *      "[Type, Damage Amount, Speed Amount]".
     */
    @Override
    public String toString() {
        String attackStr = "[";     // String rep of the obj
        attackStr += this.getType() + ", ";
        attackStr += this.getDamageAmount() + ", ";
        attackStr += this.getSpeedAmount() + "]";

        return attackStr;
    }

}
