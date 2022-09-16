package attilathehun.invitebruter;

import javax.sound.midi.SysexMessage;
import java.util.Arrays;

/**
 * By source array we understand an array of ints, designated to be performed
 * specific operations upon
 * This is a wrapper class meant to simplify those specific operations
 * Source array is a blueprint for a string, where each integer in the array represents a char
 * from a specified charset String
 * Therefore a value within a source array may never cross a specific boundary, which represents
 * the charset String's length
 */
public class SourceArrayVault {

    private int[] source;
    private static int boundary = - 1; //Regulates position swaps, must be positive, -1 indicates none has been set

    // Internal purposes only
    private SourceArrayVault() {}

    /**
     * Creates a new SourceArrayVault from a source array
     * @param array source array to be wrapped into the vault
     */
    public SourceArrayVault(int[] array) {
        this.source = array;
    }

    /**
     * Sets a boundary for the vaults, the boundary must be above zero
     * @param value the boundary, must be > 0
     */
    public static void setBoundary(int value) {
        if(value > 0) {
            SourceArrayVault.boundary = value;
        }
    }

    public static int getBoundary() {
        return boundary;
    }

    /**
     * Checks if the vault has a legitimate boundary set
     * @return true if the boundary is alright
     */
    static boolean hasBoundary(){
        return SourceArrayVault.boundary > 0;
    }

    /**
     * Sets the source array
     * @param array array to become the source one
     */
    private void inject(int[] array) {
        this.source = array;
    }

    /**
     * Adds one to the source array
     * @return true if successful
     */
    public boolean increase() {
        if(!SourceArrayVault.hasBoundary()) {
            return false;
        }
        final int value = 1;
        return this.add(SourceArrayVault.sourceArrayFromNumber(value));
    }

    /**
     * Adds a given possibilities count from the source array within
     * @param value possibilities to be added
     * @return true if successful
     */
    public boolean add(long value) {
        if(!SourceArrayVault.hasBoundary()) {
            return false;
        }

        return this.add(SourceArrayVault.sourceArrayFromNumber(value));
    }

    /**
     * Adds an integer array to the source array while obliging source array rules
     * @param array to me added to the source array
     * @return true if successful
     */
    public boolean add(int[] array) {
        if (!SourceArrayVault.hasBoundary()) {
            return false;
        }

        long arraysSum = SourceArrayVault.summarizeSourceArray(source) + SourceArrayVault.summarizeSourceArray(array);
        boolean hasEnoughCapacity = SourceArrayVault.summarizeMaxSourceArrayOfLength(source.length) - arraysSum > 0;

        if (!hasEnoughCapacity) {
            this.source = SourceArrayVault.prolongArray(source, SourceArrayVault.getLengthFromNumber(arraysSum));
        }

        int transitionValue = 0;

        for (int i = 0; i < source.length; i++) {
            array[i] += transitionValue;
            transitionValue = 0;
            transitionValue = (source[i] + array[i]) / SourceArrayVault.getBoundary();
            source[i] += (source[i] + array[i]) % SourceArrayVault.getBoundary();
        }

        return true;
    }

    /**
     * Removes one from the source array
     * @return true if successful
     */
    public boolean decrease() {
        if (!SourceArrayVault.hasBoundary()) {
            return false;
        }
        final int value = 1;
        return this.subtract(SourceArrayVault.sourceArrayFromNumber(value));
    }

    /**
     * Subtracts a given possibilities count from the source array within
     * @param value possibilities to be subtracted
     * @return true if successful
     */
    public boolean subtract(long value) {
        if(!SourceArrayVault.hasBoundary()) {
            return false;
        }

        return this.subtract(SourceArrayVault.sourceArrayFromNumber(value));
    }

    //TODO
    public boolean subtract(int[] array) {
        if(!SourceArrayVault.hasBoundary()) {
            return false;
        }

        return true;
    }

    /**
     * Extracts the source array from the wrapper
     * @return the source array
     */
    public int[] array() {
        return source;
    }

    /**
     * Checks if the array equals the array in the vault
     * @param array array to check
     * @return true if they match
     */
    public boolean equals(int[] array) {
        return Arrays.equals(source, array);
    }

    /**
     * Creeates a SourceArrayVault from possibilities count
     * @param number the number of possibilities
     * @return a SourceArrayVault equivalent to the possibilities count
     */
    public static SourceArrayVault fromNumber(long number) {
        SourceArrayVault result = new SourceArrayVault();
        int[] array = sourceArrayFromNumber(number);
        result.inject(array);
        return result;
    }

    /**
     * Creates a source array for given number of possibilities
     * @param number the number of possibilities to create source array for
     * @return source array
     */
    private static int[] sourceArrayFromNumber(long number) {
        if(!SourceArrayVault.hasBoundary()) {
            return new int[]{};
        }
        int[] result = new int[SourceArrayVault.getLengthFromNumber(number)];
        long left = number;

        for (int i = 0; i < result.length; i++) {

            long requiredForCurrentIndex = 0;

            if (i == result.length - 1) {
                result[i] += left;
                continue;
            } else if (i == result.length - 2) {
                requiredForCurrentIndex = (long) SourceArrayVault.getBoundary() + SourceArrayVault.getBoundary() - 1;
            } else {
                requiredForCurrentIndex =  (long) Math.pow(SourceArrayVault.getBoundary(), (result.length - 1 - i)) + SourceArrayVault.getBoundary() - 1;
            }
            //System.out.println("req: " + requiredForCurrentIndex + " left: " + left);
            if (requiredForCurrentIndex > left) {
                continue;
            }

            long interCalculation = (requiredForCurrentIndex - (SourceArrayVault.getBoundary() - 1));
            result[i] = (int) (left / (interCalculation));
            left -= interCalculation * result[i];

        }
        return result;
    }

    /**
     * Helper method for SourceArrayVault#sourceArrayFromNumber()
     * @param number number of possibilities to create source array for
     * @return length of the source array
     */
    private static int getLengthFromNumber(long number) {
        if(!SourceArrayVault.hasBoundary()) {
            return 0;
        }
        int length = 1;
        while (true) {
            //System.out.println("number % length * boundary: " + number + " % " + length + " * " + SourceArrayVault.getBoundary());
            if (number > SourceArrayVault.summarizeMaxSourceArrayOfLength(length)) {
                length++;
            } else {
                break;
            }
        }
        //System.out.println("Length calculated: " + length);
        return length;
    }

    /**
     * Checks if array is a source array, or better, if it could be
     * @param array array to be checked
     * @return whther it is a source array
     */
    public static boolean isSourceArray(int[] array) {
        if(!SourceArrayVault.hasBoundary()) {
            return false; // there is no source array without a boundary
        }

        for (int i : array) {
            if(i > SourceArrayVault.getBoundary()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the source array's length for easier access
     * @return length of the source array
     */
    public int length() {
        return source.length;
    }

    /**
     * SourceArrayVault#get() alias
     * @param index in the source array
     * @return value on the index
     */
    public int val(int index) {
        return get(index);
    }

    /**
     * Returns value on the specified index within the source array for easier access
     * @param index index in the source array
     * @return value on the index
     */
    public int get(int index) {
        return source[index];
    }

    /**
     * Sets given value on a given index in the source array for easy data manipulation
     * @param index position in the source array to edit
     * @param value the value to assign
     */
    public void set(int index, int value) {
        source[index] = value;
    }

    /**
     * Calculates total possibilities in the given array
     * @param array the array to summarize
     * @return total possibilities of that array
     */
    public static long summarizeSourceArray(int[] array) {
        long sum = 0;
        if(!(array.length > 1)) {
            return sum;
        }
        for (int i = 0; i < array.length; i++) {

            if (i == array.length - 1) {
                sum += array[i];
            } else {
                //System.out.println("array[i]: " + array[i] + " boundary: " + SourceArrayVault.getBoundary() + " array.length - 1 - i: " + (array.length - 1) + "-" + i);
                sum += array[i] * Math.pow(SourceArrayVault.getBoundary(), array.length - 1 - i);
            }
        }
        return sum;
    }

    /**
     * Calculates total possibilites that a source array of target length can have
     * @param length length of the hypothetical array
     * @return maximum possibilities of such array
     */
    public static long summarizeMaxSourceArrayOfLength(int length) {
        int[] array = new int[length];
        //System.out.println("array: " + Arrays.toString(array));
        Arrays.fill(array, SourceArrayVault.getBoundary() - 1);
        //System.out.println("array: " + Arrays.toString(array));
        return SourceArrayVault.summarizeSourceArray(array);
    }

    /**
     * Increase array's length while preserving it as a source array
     * @param array target array
     * @param newLenght length of the new array
     * @return array of the given length with values of the old array on correct indexes
     */
    public static int[] prolongArray(int[] array, int newLenght) {
        int[] result = new int[newLenght];
        final int lengthDifference = newLenght - array.length;

        if (lengthDifference < 0) {
            int[] empty = {};
            return empty;
        }

        for (int i = 0; i < result.length; i++) {
            if(i < lengthDifference) {
                result[i] = 0;
            } else {
                result[i] = array[i];
            }
        }
        return result;
    }

}
