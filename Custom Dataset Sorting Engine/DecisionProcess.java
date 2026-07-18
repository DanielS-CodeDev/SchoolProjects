/*
 * Author: Daniel Shapiro
 * Course: CSC 345, Spring 2026
 * Program name: DecisionProcess.java
 * External Packages: java.util.HashMap, java.util.Arrays
 * Purpose: This class implements the decision process Logic does when fending
 *      off attacks from Lord Bug. Logic takes in attacks from the slowest in
 *      speed to the fastest. Based on those attacks speed logic knows the
 *      damage amount and type through a HashMap. Here are the 3 decisions that
 *      Logic uses to defend himself.
 *      1. If the attack is lightning Logic will try to block it with their
 *          sheild if available, else teleport away and print a message that
 *          Logic teleported away
 *      2. If the attack is Fire or Ice, Logic will use their shield unless
 *          there is an attack > 500,000 REMIAING in A2 OR Logic will use
 *          their shield if no more lightning attacks remain in A3, otherwise
 *          Logic will use there armour.
 *      3. This is overpowers all other decisions. Regardless of attack if the
 *          total damage is >= 1.2 million in damage taken or will be taken,
 *          Logic will see if the shield can be used if not Logic teleports
 *          away and a message is printed that Logic teleported
 *      If the full round is completed then a message is printed out out how
 *      much damage Logic took.
 * Class Constructor: Defines the fields of the class. There are many fields
 *      for this class that are already set. The three that are set by the
 *      constructor is 'attackHMap' is a Hash Map, keys are doubles (Speed)
 *      which are unique and the value is the Attacks object. Each list
 *      'A1', 'A2' and 'A3'; double[], int[] and String[] respectively are also
 *      defined.
 * Class methods list:
 *      performRound()
 *      decisions(attackSpeed, attackDamage, attackType)
 *      logicUsedShield()
 *      findNextDamage()
 *      findNextType()
 *      damageTakenWithArmour(damage)
 *      useShield(currentDamage)
 *      removeSpeed()
 *      removeDamage(damage)
 *      removeType(type)
 *      toString()
 */
import java.util.HashMap;
import java.util.Arrays;

public class DecisionProcess {

    private HashMap<Double, Attacks> attackHMap;  // HashMap of attacks
    private double[] A1; // array of speeds, slowest to fastest
    private int[] A2; // array of damages, highest to lowest
    private String[] A3; // array of types, "lightning", "ice", "fire"
    private int shieldMax = 5; // set the shield usage maximum
    private int shieldUsage = 0; // amount of times Logic uses the shield
    private double armourAbsorbPerc = 0.3; // percent aroumor takes from damage
    private double maxDamageIntake = 1200000.0; // max damage taken 1.2 mill
    private double totalDamageTaken = 0; // Logic curr taken amount of damage

    /*
     * DecisionProcess(attackHMap, A1, A2, A3) -- Constructor of the class.
     *      Returns Nothing. Takes in HashMap<Double, Attacks> 'attackHMap',
     *      double[] 'A1', int[] 'A2' and String[] 'A3'. Defines the fields of
     *      the class.
     */
    public DecisionProcess(HashMap<Double, Attacks> attackHMap, double[] A1, 
                                                    int[] A2, String[] A3) {
        // Set the fields of the class
        this.attackHMap = attackHMap;
        this.A1 = A1;
        this.A2 = A2;
        this.A3 = A3;
    }

    /*
     * performRound() -- Takes in no parameters. Returns nothing. Loops through
     *      A1 and goes through the decision process of what Logic should do.
     *      To determine the decisions, method calls decisions(). If
     *      decisions() returns true it means that the round ends. If the round
     *      went thr normal length then the message of how much Logic took in
     *      damage is printed out: "Logic took X Damage".
     */
    // this will loop over speed and do the round
    public void performRound() {

        // Loop over A1 (the speeds) to know what is the next attack
        for (int i = 0; i < this.A1.length; i++) {

            // get Attack object
            Attacks currAttack = attackHMap.get(this.A1[i]);

            // Get all of the data of the current attack
            double currAttackSpeed = currAttack.getSpeedAmount();
            int currAttackDamage = currAttack.getDamageAmount();
            String currAttackType = currAttack.getType();

            // Perform the decisions of what Logic should do
            boolean endRound = this.decisions(currAttackSpeed, 
                                            currAttackDamage, currAttackType);

            // Remove the current attacks from the lists
            this.removeSpeed();
            this.removeDamage(currAttackDamage);
            this.removeType(currAttackType);

            // If endRound is True exit round
            if (endRound) {
                return;
            }
        }

        // Determine if the totalDamageTaken is a whole number
        if (totalDamageTaken % 1 == 0) {
            String X = String.format("%.0f", this.totalDamageTaken);
            System.out.print("Logic took " + X + " Damage");
        }

        // It is not a whole number
        else {
            // Print out the total damage taken by logic in the round
            String X = String.format("%.1f", this.totalDamageTaken);
            System.out.print("Logic took " + X + " Damage");
        }
    } 

    /*
     * decisions(attackSpeed, attackDamage, attackType) -- Takes in a double
     *      'attackSpeed', integer 'attackDamage' and String 'attackType' and
     *      goes through the decision process of how logic should handle the
     *      attacks of Lord Bug. If logic were to use their shield but that
     *      would be >= 1.2 million then Logic uses their shield. If Logic can
     *      not since Logic used up all of their uses of the shield then Logic
     *      teleports away with no damage taken and then a boolean, true is
     *      returned to end the round, else false. If the attack is a Lightning
     *      attack then Logic goes through the same process as stated above.
     *      If the there are no more damages above 500,00 OR there are no more
     *      Lightning attacks then Logic will see if Logic can use their shield
     *      if so then no damage is taken, if not then logic uses armour. All
     *      else Logic will use there armour. If Logic teleported away a
     *      message is printed out: "Logic had to Teleport away".
     */
    // return a boolean if need to break
    public boolean decisions(double attackSpeed, int attackDamage, String attackType) {

        // Set the attack type to lowercase
        String attackTLower = attackType.toLowerCase();

        // If damage that would be used with armour >= max damage in take
        // Or is type is lightning use shield
        if (this.useShield(attackDamage) || attackTLower.equals("lightning")) {

            // Return bool, used to see is Logic used shield
            return logicUsedShield();
        }

        // get the next damage in sorted list (A2)
        double nextDamage = this.findNextDamage();

        // get the next type in sorted list (A3)
        String nextType = this.findNextType();

        // If no attacks left above 500,000 OR no attacks left type lightning
        // Logic uses shield
        if ((nextDamage < 500000) || (!nextType.equals("lightning"))) {

            // If logic out of shield use teleport and end round
            if (this.shieldMax == this.shieldUsage) {
                this.totalDamageTaken += this.damageTakenWithArmour(attackDamage);
                return false; // do not end round
            }
            this.shieldUsage += 1; // So no damage taken
            return false; // do not end round
        }

        // Logic will use his armour
        this.totalDamageTaken += this.damageTakenWithArmour(attackDamage);
        return false; // do not end round
    }

    /*
     * logicUsedShield() -- No parameters. Returns a boolean true if Logic used
     *      all of the shields and teleported away so the round needs to end or
     *      false that logic used a shield but still has some remaining so
     *      round is not over.
     */
    private boolean logicUsedShield() {
        // If logic out of shield use teleport and end round
        if (this.shieldMax == this.shieldUsage) {
            System.out.print("Logic had to Teleport away");
            return true; // must end round
            
        }
        this.shieldUsage += 1; // So no damage taken
        return false; // do not end round
    }

    /*
     * findNextDamage() -- No parameters. Loops over A2 and finds the next
     *      available damage in the list and returns the integer, damage value.
     *      -1.0 is returned if that damage was not found.
     */
    public int findNextDamage() {

        // Loop over the A2 int[] array
        for (int i = 0; i < this.A2.length; i++) {
            int currDamage = A2[i]; // get current value in A2

            // If 'currDamage' != TOMBSTONE return the value
            if (currDamage != -1) {
                return currDamage;
            }
        }
        return -1; // If not found return -1;
    }

    /*
     * findNextType() -- No parameters. Loops over A3 and finds the next
     *      available type in the list and returns the String, type value.
     *      "*" is returned if that type was not found.
     */
    public String findNextType() {

        // Loop over the A3 String[] array
        for (int i = 0; i < this.A3.length; i++) {
            String currType = A3[i].toLowerCase(); // get current type in A3

            // If 'currType' != TOMBSTONE return the value
            if (currType != "*") {
                return currType;
            }
        }
        return "*"; // If not found return "*";
    }

    /*
     * damageTakenWithArmour(damage) -- Takes in a integer 'damage' and
     *      calculates the amount of damage Logic will take if Logic uses
     *      the armour. A double is returned of the amount Logic will have to 
     *      take.
     */
    private double damageTakenWithArmour(int damage) {
        return damage * (1 - this.armourAbsorbPerc);
    }

    /*
     * useShield(currentDamage) -- Takes in an integer'currentDamage' and
     *      returns a boolean if Logic should used their shield if the damage
     *      that they will take will their armour puts them at or over the
     *      maxDamgeInTake. If so, then true is returned. False otherwise.
     */
    private boolean useShield(int currentDamage) {

        // double of the amount of damage that would be taken using armour
        double ifUsedArmour = this.damageTakenWithArmour(currentDamage);
        return (this.totalDamageTaken + ifUsedArmour) >= this.maxDamageIntake;
    }

    /*
     * removeSpeed() -- No parameters. Removes the first speed from A1 and
     *      returns a double of the removed speed. The old spot in A1 array
     *      gets a TOMBSTONE of -1, a value speed will never get so arrays are
     *      never resized.
     */
    public double removeSpeed() {

        double TOMBSTONE = -1.0; // Value will never happen here to say removed
        double removed = -1;

        // Loop until first speed to be removed
        for (int i = 0; i < this.A1.length; i++) {

            if (this.A1[i] != -1.0) {
                removed = A1[i]; // get the removed speed
                this.A1[i] = TOMBSTONE; // set TOMBSTONE of 'removed' speed
                break;
            }
        }
        return removed;
    }

    /*
     * removeDamage(damage) -- Takes in an integer 'damage' and loops over
     *      A2 and sets a TOMBSTONE of -1 which will never be a valid damage
     *      at the first occurence of 'damage'.
     */
    public void removeDamage(int damage) {

        int TOMBSTONE = -1; // Value will never happen here to say removed

        // Loop over the while A2 int[]
        for (int i = 0; i < this.A2.length; i++) {

            // If damage set TOMBSTONE and break
            if (this.A2[i] == damage) {
                this.A2[i] = TOMBSTONE; // set TOMBSTONE
                break; // break out of loop
            }
        }
    }

    /*
     * removeType(type) -- Takes in an String 'type' and loops over
     *      A3 and sets a TOMBSTONE of "*" which will never be a valid type
     *      at the first occurence of 'type'.
     */
    public void removeType(String type) {

        String TOMBSTONE = "*"; // Value will never happen here to say removed
        String loweredType = type.toLowerCase(); // Lowercase type

        // Loop over the while A3 String[]
        for (int i = 0; i < this.A3.length; i++) {

            // If type set TOMBSTONE and break
            if (this.A3[i].equals(loweredType)) {
                this.A3[i] = TOMBSTONE; // set TOMBSTONE
                break; // break out of loop
            }
        }
    }

    /*
     * toString() -- No parameters. Returns a String representation of A1, A2
     *      A3. This method is used for Debugging.
     */
    @Override
    public String toString() {

        return Arrays.toString(this.A1) + "\n" + Arrays.toString(this.A2)
                                             + "\n" + Arrays.toString(this.A3);
    }
}