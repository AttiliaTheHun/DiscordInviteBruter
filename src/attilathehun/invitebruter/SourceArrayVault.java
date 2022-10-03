package attilathehun.invitebruter;

import java.io.Serializable;
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
public class SourceArrayVault implements Serializable {

    private int[] source;
    private static int boundary = - 1; //Regulates position swaps, must be positive, -1 indicates none has been set, non-inclusive

    // Internal purposes only
    private SourceArrayVault() {}

    /**
     * Creates a new SourceArrayVault from a source array
     * @param array source array to be wrapped into the vault
     */
    public SourceArrayVault(int[] array) {
        if(!SourceArrayVault.isSourceArray(array)) {
            throw new NotASourceArrayException();
        }
        this.source = array;
    }

    /**
     * Sets a boundary for the vaults, the boundary must be above zero
     * @param value the boundary, must be > 0
     */
    public static void setBoundary(int value) {
        if(value > 0) {
            SourceArrayVault.boundary = value;
        } else {
            throw new IllegalArgumentException("The boundary must be a whole number above zero");
        }
    }

    public static int getBoundary() {
        if(!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
        }
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
        if(!SourceArrayVault.isSourceArray(array)) {
            throw new NotASourceArrayException();
        }
        this.source = array;
    }

    /**
     * Adds one to the source array
     */
    public void increase() {
        if(!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
        }
        //System.out.println("increase() call");
        this.add(1);
    }

    /**
     * Adds a given possibilities count from the source array within
     * @param value possibilities to be added
     */
    public void add(long value) {
        if(!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
        }

        this.add(SourceArrayVault.sourceArrayFromNumber(value));
    }

    /**
     * Adds an integer array to the source array while obliging source array rules
     * @param array to me added to the source array
     */
    public void add(int[] array) {
        if (!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
        }

        if(!SourceArrayVault.isSourceArray(array)) {
            throw new NotASourceArrayException();
        }

        //System.out.print("array: " + Arrays.toString(array));

        long arraysSum = SourceArrayVault.summarizeSourceArray(source) + SourceArrayVault.summarizeSourceArray(array);
        boolean hasEnoughCapacity = SourceArrayVault.summarizeMaxSourceArrayOfLength(source.length) - arraysSum > 0;

        if (!hasEnoughCapacity) {
            source = SourceArrayVault.prolongArray(source, SourceArrayVault.getLengthFromNumber(arraysSum));
        }
        if(source.length > array.length) {
            array = SourceArrayVault.prolongArray(array, source.length);
        }

        int transitionValue = 0;

        for (int i = source.length - 1; i > 0; i--) {
            array[i] += transitionValue;
            transitionValue = (source[i] + array[i]) / SourceArrayVault.getBoundary();
            source[i] = (source[i] + array[i]) % SourceArrayVault.getBoundary();
        }
        //System.out.println(" source: " + Arrays.toString(source));

    }

    /**
     * Removes one from the source array
     */
    public void decrease() {
        if (!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
        }
        final int value = 1;
        this.subtract(SourceArrayVault.sourceArrayFromNumber(value));
    }

    /**
     * Subtracts a given possibilities count from the source array within
     * @param value possibilities to be subtracted
     * @return true if successful
     */
    public void subtract(long value) {
        if(!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
        }

        this.subtract(SourceArrayVault.sourceArrayFromNumber(value));
    }

    //TODO
    public void subtract(int[] array) {
        if(!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
        }

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
     * Creates a copy of the vault
     * @return SourceArrayVault with identical source array
     */
    public SourceArrayVault copy() {
        return new SourceArrayVault(this.array());
    }

    /**
     * Static implementation of SourceArrayVault#copy()
     * @return SourceArrayVault with identical source array
     */
    public static SourceArrayVault copy(SourceArrayVault vault) {
        return new SourceArrayVault(vault.array());
    }

    /**
     * Creates a SourceArrayVault from possibilities count
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
            throw new MissingBoundaryException();
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
        //System.out.println("sourceArray: " + Arrays.toString(result) + " number: " + number);
        return result;
    }

    /**
     * Creates a SourceArrayVault from a String that represents a code calculated from a source array in the state
     * of the result source array
     * @param source String to convert into source array
     * @param charSet charset the *source* String is compliant to
     * @return a SourceArrayVault representing the value of the *source* String
     */
    public static SourceArrayVault fromString(String source, String charSet) {

        int[] array = new int[source.length()];
        char[] chars = source.toCharArray();
        for (int i = 0; i < array.length; i++) {
            int position = charSet.indexOf(chars[i]);
            if (position == -1) {
                throw new RuntimeException("cannot create a source array: Source String is not compliant to the charset provided");
            }
            array[i] = position;
        }
        return new SourceArrayVault(array);
    }

    /**
     * Helper method for SourceArrayVault#sourceArrayFromNumber()
     * @param number number of possibilities to create source array for
     * @return length of the source array
     */
    private static int getLengthFromNumber(long number) {
        if(!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
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
            throw new MissingBoundaryException();
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
        if (value > SourceArrayVault.getBoundary() - 1 || value < 0) {
            throw new OutOfBoundsException();
        }
        source[index] = value;
    }

    /**
     * Calculates total possibilities in the given array
     * @param array the array to summarize
     * @return total possibilities of that array
     */
    public static long summarizeSourceArray(int[] array) {
        if (!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
        }
        long sum = 0;
        if (!(array.length > 0)) {
            return sum;
        } else if (!(array.length > 1)) {
            return array[0];
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
        if (!SourceArrayVault.hasBoundary()) {
            throw new MissingBoundaryException();
        }
        int[] array = new int[length];
        //System.out.println("array: " + Arrays.toString(array));
        Arrays.fill(array, SourceArrayVault.getBoundary() - 1);
        //System.out.println("array: " + Arrays.toString(array));
        return SourceArrayVault.summarizeSourceArray(array);
    }

    /**
     * Increase array's length while preserving it as a source array
     * @param array target array
     * @param newLength length of the new array
     * @return array of the given length with values of the old array on correct indexes
     */
    public static int[] prolongArray(int[] array, int newLength) {
        int[] result = new int[newLength];
        final int lengthDifference = newLength - array.length;

        if (lengthDifference < 0) {
            return new int[]{};
        }

        for (int i = 0; i < result.length; i++) {
            if(i < lengthDifference) {
                result[i] = 0;
            } else {
                result[i] = array[i - lengthDifference];
            }
        }
        //System.out.println(Arrays.toString(array) + " : " + newLength + " : " + Arrays.toString(result));
        return result;
    }

    //TODO
    /**
     * Converts int array into a SourceArrayVault
     * @param array to be converted
     * @return SourceArrayVault with a legal source array within
     */
    public static SourceArrayVault convert(int[] array) {
        return null;
    }

    /**
     * Checks whether next increase() call will change source array's length
     * @return true if the source array is full and needs prolonging
     */
    public boolean isAtFinalPosition() {
        for (int i : source) {
            if (i != SourceArrayVault.getBoundary() - 1) {
                return false;
            }
        }
        return true;
    }

    public static class MissingBoundaryException extends RuntimeException {

        public MissingBoundaryException() {}

        public MissingBoundaryException(String s) {
            super(s);
        }
    }
    public static class OutOfBoundsException extends  RuntimeException {

        public OutOfBoundsException() {}

        public OutOfBoundsException (String s) {
            super(s);
        }
    }

    public static class NotASourceArrayException extends  RuntimeException {

        public NotASourceArrayException() {}

        public NotASourceArrayException(String s) {
            super(s);
        }
    }

}
