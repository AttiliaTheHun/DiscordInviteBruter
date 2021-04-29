/*
 * This class servers as a serializable data model that can be easily
 * stored inside a file and deserialized back without the need of
 * third-party libraries
 */
package attilathehun.invitebruter;

import java.io.Serializable;

public class Model implements Serializable {
    private int[][] lowerRanges;
    private int[][] upperRanges;

    public Model(int[][] lowerRanges, int[][] upperRanges){
        this.lowerRanges = lowerRanges;
        this.upperRanges = upperRanges;
    }

    public int[][] getLowerRanges() {
        return lowerRanges;
    }

    public int[][] getUpperRanges() {
        return upperRanges;
    }
}
