package relationship;

import infection.AbstractInfection;
import java.util.Map;
import person.AbstractIndividualInterface;

/**
 * <p>An object representing a relationship, in a population
 * where there is only one type of relationship possible.
 * This will be used as an edge in RelationshipMap.</p>
 *
 * <p>A SingleRelationship object consists of 3 three fields: Two MovablePerson id
 * involved in the relationship, the length of this relationship (in days),
 *  and number of days until this relationship is expired.</p>
 *
 * @see population.MovablePerson
 * @see population.RelationshipMap
 * 
 * @author Ben Hui
 */
public class SingleRelationship implements java.io.Serializable {

    /**
     * The two SingleRelationshipTypePerson involved in this relationship
     */
    protected Integer[] partners;
    /**
     * The length of this relationship.
     */
    protected double durations = 0;
    /**
     * Time until the relation is expired
     */
    protected double timeUntilExpire = 0;

    /**
     * Constructor of SingleRelationship object
     * @param links two SingleRelationshipTypePerson objects linked by this relationship
     */
    public SingleRelationship(Integer[] links) {
        this.partners = links;
    }

    /**
     * Return SingleRelationshipTypePerson Id linked by this relationship
     *
     * @return the SingleRelationshipTypePerson id involved in this relationship
     */
    public Integer[] getLinks() {
        return this.partners;
    }

    /**
     * Return SingleRelationshipTypePerson Id (as an integer)
     * linked by this relationship
     * 
     * @return the SingleRelationshipTypePerson id involved in this relationship, as int
     */
    public int[] getLinksValues() {
        int[] res = {this.partners[0], this.partners[1]};
        return res;
    }

    /**
     * Return AbstractPerson linked by this relationship
     * @param <T> 
     * @param dataMap a AbstractPerson mapping by id
     * @return AbstractPerson involved in this relationship
     */
    public <T extends AbstractIndividualInterface> AbstractIndividualInterface[] getLinks(Map<Integer, T> dataMap) {
        AbstractIndividualInterface[] res = new AbstractIndividualInterface[partners.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = dataMap.get(partners[i]);
        }
        return res;
    }

    /**
     * Get length of this relationship
     * @return length of this relationship
     */
    public double getDurations() {
        return durations;
    }

    /**
     * Set length of this relationship
     * @param durations length of this relationship
     */
    public void setDurations(double durations) {
        this.durations = durations;
        this.timeUntilExpire = durations;
    }

    /**
     * Increment time for this relationship
     * @param deltaT time increment
     * @return time until the relationship is expired
     */
    public double incrementTime(double deltaT) {
        this.timeUntilExpire -= deltaT;
        return this.timeUntilExpire;
    }

    /**
     * Return if either person in input array has any symptoms
     * @param pList Abstract person involved 
     * @param infList infection list
     * @return true if any person has symptoms
     */
    public static boolean hasSymptoms(AbstractIndividualInterface[] pList, AbstractInfection[] infList) {
        for (AbstractInfection infection : infList) {
            for (AbstractIndividualInterface p1 : pList) {
                if (infection.hasSymptoms(p1)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Perform acts between the AbstractPerson involved. It will
     * update A SingleRelationshipTypePerson's contact history and infection status.
     *
     * @param r the AbstractPerson involved
     * @param timestamp time when act took place
     * @param infList infection list
     * @param transmitable non-biological factors (e.g. usage of condom) that influence if infection can be transmitted
     * @return if infection transmission is successful, store as [inf_id][src]
     */
    public static boolean[][] performAct(AbstractIndividualInterface[] r, int timestamp, AbstractInfection[] infList, boolean[] transmitable) {
        boolean[][] sucTransmission =  new boolean[infList.length][r.length];
        for (int target = 0; target < r.length; target++) {
            int src = (target + 1) % r.length;           
            for (int infType = 0; infType < infList.length; infType++) {
                sucTransmission[infType][src] = infList[infType].couldTransmissInfection(r[src], r[target]) && transmitable[infType];
                if (sucTransmission[infType][src]) {
                    r[target].setLastActInfectious(infType,true);
                }
            }
        }

        return sucTransmission;
    }

    @Override
    public String toString() {
        return Double.toString(durations);
    }
}
