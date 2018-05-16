package availability;

import java.io.Serializable;
import person.AbstractIndividualInterface;
import relationship.RelationshipMap;
import random.RandomGenerator;

/**
 * <p>A general Availability object for all population type</p>
 *
 * @author Ben Hui
 */
public abstract class AbstractAvailability implements Serializable {

    /**
     * Random number generator
     */
    private RandomGenerator RNG;
    private RelationshipMap relationshipMap = null;

    public AbstractAvailability(RandomGenerator RNG) {
        this.RNG = RNG;
    }

    public void setRelationshipMap(RelationshipMap relationshipMap) {
        this.relationshipMap = relationshipMap;
    }

    protected RelationshipMap getRelationshipMap() {
        return this.relationshipMap;
    }

    protected boolean alreadyInRelationshipWith(AbstractIndividualInterface p1, AbstractIndividualInterface p2) {
        boolean res;
        res = (relationshipMap != null) && (relationshipMap.getEdge(p1.getId(), p2.getId()) != null);
        return res;
    }

    public void setRNG(RandomGenerator RNG) {
        this.RNG = RNG;        
    }

    protected RandomGenerator getRNG() {
        return RNG;
    }

    /**
     * Generate partnership. The generated partnership can be extracted through getPairing() method call.
     *
     * e.g.
     * <code>
     * int pairSize = avaiablity.generatePairing();
     * MovablePerson[][] initPairing = avaiablity.getPairing();
     * for (int i = 0; i < pairSize; i++){
     * ...
     * }
     * </code>
     *
     * @see Availability#getPairing()
     * @return Number of partnership generated
     */
    public abstract int generatePairing();

    /**
     * Return pairing generated from last generatePairing() method call.
     *
     * e.g.
     * <code>
     * int pairSize = avaiablity.generatePairing();
     * MovablePerson[][] initPairing = avaiablity.getPairing();
     * for (int i = 0; i < pairSize; i++){
     * ...
     * }
     * </code>
     *
     * @see Availability#generatePairing()
     * @return Pairing generated, or null if no pairing generated
     */
    public abstract AbstractIndividualInterface[][] getPairing();

    /**
     * Remove availability of input AbstractPerson.
     *
     * @param p AbstractPerson to be removed from availability
     * @return Return true if the AbstractPerson is removed successfully
     *
     */
    public abstract boolean removeMemberAvailability(AbstractIndividualInterface p);

    /**
     * If the member is available to forming new pair
     *
     * @param p AbstractPerson to be checked for availability
     * @return Return true if the AbstractPerson is availability to form new pairs.
     */
    public abstract boolean memberAvailable(AbstractIndividualInterface p);

    /**
     * Set population available population for pairing
     *
     * @param available An 2D array of AbstractPerson.
     *
     */
    public abstract void setAvailablePopulation(AbstractIndividualInterface[][] available);   
    
    /**
     * Set parameter of availability object.
     *
     * @param id parameter name
     * @param value parameter value
     * @return true if parameter is set correctly
     */
    public abstract boolean setParameter(String id, Object value);

    /**
     * Get parameter of availability object.
     *
     * @param id parameter availability
     * @return parameter to be return
     */
    public abstract Object getParameter(String id);
}
