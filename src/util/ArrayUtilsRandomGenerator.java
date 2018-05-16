package util;

import random.RandomGenerator;

/**
 * A list of array utility function using random generator
 * @author Ben Hui
 */
public class ArrayUtilsRandomGenerator {

    /**
     * Shuffle an array of type T. It is essentially the same as shuffle in Collection, but used RandomGenerator instead.
     *
     * @param <T>
     * @see java.util.Collections#shuffle(java.util.List, java.util.Random)
     * @param ent Array to be shuffled
     * @param rngEng Random number generator
     */
    public static <T> void shuffleArray(T[] ent, RandomGenerator rngEng) {
        shuffleArray(ent, ent.length, rngEng);
    }

    /**
     * Shuffle an array of type T between 0 and endPt-1. Location of any T after ent[endPt] (if exists) will not be changed. It is essentially the same as
     * shuffle in Collection, but used RandomGenerator instead.
     *
     * @param <T>
     * @see java.util.Collections#shuffle(java.util.List, java.util.Random)
     * @param ent Array to be shuffled
     * @param endPt The endPt of the shuffling. i.e. T at ent[0] to T[endPt-1] will be shuffled
     * @param rngEng Random number generator
     */
    public static <T> void shuffleArray(T[] ent, int endPt, RandomGenerator rngEng) {
        for (int i = endPt - 1; i > 0; i--) {
            int s = rngEng.nextInt(i + 1);
            T tempObj = ent[s];
            ent[s] = ent[i];
            ent[i] = tempObj;
        }
    }

    /**
     * Shuffle an array of int. It is essentially the same as shuffle in Collection, but used RandomGenerator instead.
     *
     * @see java.util.Collections#shuffle(java.util.List, java.util.Random)
     * @param ent Array to be shuffled
     * @param rngEng Random number generator
     */
    public static void shuffleArray(int[] ent, RandomGenerator rngEng) {
        shuffleArray(ent, ent.length, rngEng);
    }

    /**
     * Shuffle an array of int between 0 to endPt-1. Location of any int after ent[endPt] (if exists) will not be changed. It is essentially the same as shuffle
     * in Collection, but used RandomGenerator instead.
     *
     * @see java.util.Collections#shuffle(java.util.List, java.util.Random)
     * @param ent Array to be shuffled
     * @param endPt The endPt of the shuffling. i.e. int at ent[0] to ent[endPt-1] will be shuffled
     * @param rngEng Random number generator
     */
    public static void shuffleArray(int[] ent, int endPt, RandomGenerator rngEng) {
        for (int i = endPt - 1; i > 0; i--) {
            int s = rngEng.nextInt(i + 1);
            int tempObj = ent[s];
            ent[s] = ent[i];
            ent[i] = tempObj;
        }

    }

    /**
     * Randomly select number of elements from an input array of type T. I
     *
     * @param <T>
     * @param ent Input array
     * @param numToPick Number of element to be picked
     * @param RNG Random number generator
     * @return An array of picked entries
     */
    public static <T> T[] randomSelect(T[] ent, int numToPick, RandomGenerator RNG) {

        Class<? extends T[]> entClass = (Class<? extends T[]>) ent.getClass();
        T[] res = (T[]) java.lang.reflect.Array.newInstance(entClass.getComponentType(), numToPick);

        int[] indices = randomSelectIndex(ent, numToPick, 0, ent.length, RNG);
        for (int i = 0; i < indices.length; i++) {
            res[i] = ent[indices[i]];
        }
        return res;
    }

    /**
     * Generate indices for elements picked for an input array of type T. 
     *
     * @param <T>
     * @param ent Input array
     * @param numToPick Number of element to be picked
     * @param low Lower interval offset, the element will be chosen will be from [low,low+range-1]
     * @param range Higher interval offset, the element will be chosen will be from [low,low+range-1]
     * @param RNG Random number generator
     * @return An array of picked indices
     */
    public static <T> int[] randomSelectIndex(T[] ent, int numToPick, int low, int range, RandomGenerator RNG) {

        int[] res = new int[numToPick];
        
        int pt = 0;
        int test = low;
        int numToBeSel = numToPick;
        
        while(pt < res.length && test < low+range && numToBeSel > 0){           
            if(RNG.nextInt(low+range - test) < numToBeSel){
                res[pt] = test;
                pt++;
                numToBeSel--;
            }
            test++;            
        }                                     
        return res;
    }

}
