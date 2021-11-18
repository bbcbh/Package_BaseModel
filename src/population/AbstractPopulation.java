package population;

import infection.AbstractInfection;
import person.AbstractIndividualInterface;
import relationship.RelationshipMap;
import relationship.SingleRelationship;
import util.PersonClassifier;

/**
 * An abstract object specifying entire population modelled.
 * 
 * @author Ben Hui
 */
public abstract class AbstractPopulation implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8962185006821536165L;
	private int globalTime = 0;
    private AbstractInfection[] infList; // Infection to be modelled in this population

    public abstract boolean setParameter(String identifier, int id, Object value);

    public abstract Object getValue(String identifier, int id);

    public abstract void advanceTimeStep(int deltaT);

    public abstract void initialise();

    protected void resetGlobalTime() {
        globalTime = 0;
    }
    
    protected void setGlobalTime(int t){
        globalTime = t;
    }
    protected abstract SingleRelationship formRelationship(AbstractIndividualInterface[] pair, RelationshipMap relMap, int d);

    public AbstractInfection[] getInfList() {
        return infList;
    }

    protected void setInfList(AbstractInfection[] infList) {
        this.infList = infList;
    }

    protected void incrementTime(int deltaT) {
        globalTime += deltaT;
    }

    public int getGlobalTime() {
        return globalTime;
    }

    
    /**
     * Return all person object stored in current population
     * @return All AbstractPerson contained in this population
     */
    public abstract AbstractIndividualInterface[] getPop();

    /**
     * Return population size
     * @return Size of the population
     */
    public int getPopSize() {
        return getPop() == null ? -1 : getPop().length;
    }

    /**
     * Set AbstractPerson within this population.
     * @param pop Population to be set
     */
    protected abstract void setPop(AbstractIndividualInterface[] pop);

    /**
     * Set infection instantly for selected individuals based on person type. 
     * Infection status will be set using population internal RNG (i.e. varies across intervention)
     *
     * @param infId infection id
     * @param infClassifer person type classifier
     * @param preval list of prevalence based on person type
     * @param preExposeMax number of days of pre-exposure.
     * @return number of newly infected 
     */
    public abstract int[] setInstantInfection(int infId, PersonClassifier infClassifer, float[] preval, int preExposeMax);

   
}
