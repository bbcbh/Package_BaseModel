package relationship;

import java.util.*;
import org.jgrapht.graph.SimpleGraph;
import person.AbstractIndividualInterface;

/**
 * <p>
 * A simple graph for representing relationships as a network map.</p>
 *
 * <p>
 * For now vertices are stored as SingleRelationshipTypePerson ID, and edges are stored as SingleRelationship.</p>
 *
 * <p>
 * In addition, it contains a SET which stored available SingleRelationshipTypePerson ID by gender.</p>
 *
 * <p>
 * This class is an extension of SimpleGraph class from <a href="http://www.jgrapht.org/">JGraphT</a> library. See
 * <a href="http://www.jgrapht.org/javadoc/">http://www.jgrapht.org/javadoc/</a> for Java API documentation</p>
 *
 *
 * @see population.person.AbstractPerson
 * @see population.RelationshipMap.SingleRelationship
 *
 * @author Ben Hui
 */
public class RelationshipMap extends SimpleGraph<Integer, SingleRelationship> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2554179596604852689L;

	ArrayList<SingleRelationship> relArr;

    /**
     * Two internal set that store all MovablePerson in the map as an array, separated by gender. Note this map is synchronized, so it is thread safe.
     *
     */
    @SuppressWarnings("unchecked")
	private final Set<Integer>[] personByGender = new Set[INDEX_TOTAL];
    /**
     * List index for male
     */
    public static final int INDEX_MALE = 0;
    /**
     * List index for female
     */
    public static final int INDEX_FEMALE = INDEX_MALE + 1;
    /**
     * Total number of lists
     */
    public static final int INDEX_TOTAL = INDEX_FEMALE + 1;

    /**
     * Create a relationship map with no entry
     */
    public RelationshipMap() {
        super(SingleRelationship.class);
        for (int i = 0; i < personByGender.length; i++) {
            personByGender[i] = Collections.synchronizedSet(new HashSet<Integer>());
        }
        relArr = new ArrayList<>();

    }

    /**
     * Set persons by gender for this relationship map. This method used in order to take advantage of array-based operation. (e.g. in combination with Matlab).
     * In most iterative based operations (e.g. in pure Java), it might be more economical to use addAvailablePerson and remove AvailablePerson instead
     *
     * @param available the SingleRelationshipTypePerson id for available
     * @param isMale if the person available is male
     * @see RelationshipMap#addAvailablePerson(int, boolean)
     * @see RelationshipMap#removeAvailablePerson(int, boolean)
     *
     */
    public void setPersonByGender(int[] available, boolean isMale) {
        int size = available == null ? 0 : available.length;
        int arrayIndex = isMale ? INDEX_MALE : INDEX_FEMALE;
        personByGender[arrayIndex] = Collections.synchronizedSet(new HashSet<Integer>(size));
        for (int ent = 0; ent < size; ent++) {
            personByGender[arrayIndex].add(available[ent]);
        }
    }

    /**
     * A convenient method to generate Id-SingleRelationshipTypePerson mapping using HashMap.
     *
     * @param <T>
     * @param localData input AbstractPerson array
     * @return A ID- map based on input SingleRelationshipTypePerson array.
     */
    /*
     public static Map<Integer, AbstractPerson> generateDataMap(AbstractPerson[] localData) {
     HashMap localMap = new HashMap<Integer, AbstractPerson>(localData.length);
     for (int i = 0; i < localData.length; i++) {
     localMap.put(new Integer(localData[i].getId()), localData[i]);
     }
     return localMap;
     }
     */
    public static <T extends AbstractIndividualInterface> Map<Integer, T> generateDataMap(T[] localData) {
        HashMap<Integer, T> localMap = new HashMap<Integer, T>(localData.length);
        for (T mapEnt : localData) {
            localMap.put(mapEnt.getId(), mapEnt);
        }
        return localMap;
    }
    /*
     public static Map<Integer, population.person.SingleRelationshipTypePerson> generateDataMap(population.person.SingleRelationshipTypePerson[] localData) {
     HashMap localMap = new HashMap<Integer, population.person.SingleRelationshipTypePerson>(localData.length);
     for (int i = 0; i < localData.length; i++) {
     localMap.put(new Integer(localData[i].getId()), localData[i]);
     }
     return localMap;
     }
     */

    /**
     * Add an edge to graph. It will create vertex if not existed.
     *
     * @param p1 the id of SingleRelationshipTypePerson
     * @param p2 the id of SingleRelationshipTypePerson
     * @return SingleRelationship edge added
     */
    public SingleRelationship addEdge(int p1, int p2) {
        Integer p_1 = p1;
        Integer p_2 = p2;
        if (!this.containsVertex(p_1)) {
            super.addVertex(p_1);
        }
        if (!this.containsVertex(p_2)) {
            super.addVertex(p_2);
        }

        SingleRelationship r = this.addEdge(p_1, p_2);

        if (r != null) {
            relArr.add(r);
        }
        return r;
    }

    @Override
    public boolean removeEdge(SingleRelationship rel) {
        boolean removed = super.removeEdge(rel);
        Integer[] pids = rel.getLinks();
        for (Integer pid : pids) {
            if (super.degreeOf(pid) == 0) {
                super.removeVertex(pid);
            }
        }
        if (removed) {
            relArr.remove(rel);
        }
        return removed;
    }

    @Override
    public SingleRelationship removeEdge(Integer arg0, Integer arg1) {
        SingleRelationship r = super.getEdge(arg0, arg1);
        if (r != null) {
            removeEdge(r);
        }
        return r;
    }

    @Override
    public boolean removeVertex(Integer v) {
        System.err.println(getClass().getName()
                + ": Direct removal not recommended. Please use removeVertex(int personId, boolean isMale)");
        boolean res;
        synchronized (this) {
            res = super.removeVertex(v);
        }
        
        if(res){
            for (Set<Integer> personCollections : this.personByGender) {
                personCollections.remove(v);
            }                        
        }
        
        return res;

    }

    /**
     * Remove a SingleRelationshipTypePerson from the map and from internal list.
     *
     * @param person person to be removed
     * @return return true if the AbstractPerson is removed successfully.
     */
    public boolean removeVertex(AbstractIndividualInterface person) {
        return removeVertex(person.getId(), person.isMale());
    }

    /**
     * Remove a AbstractPerson from the map and from internal list.
     *
     * @param personId the AbstractPerson Id
     * @param isMale if the AbstractPerson is male
     * @return return true if the AbstractPerson is removed successfully.
     */
    public boolean removeVertex(int personId, boolean isMale) {
        boolean res;
        synchronized (this) {
            res = super.removeVertex(personId);
        }
        if (res) {
            int arrayIndex = isMale ? INDEX_MALE : INDEX_FEMALE;
            synchronized (this.personByGender[arrayIndex]) {
                res = this.personByGender[arrayIndex].remove(personId);
            }
        }
        return res;
    }

    public boolean addAvailablePerson(AbstractIndividualInterface p) {
        return this.addAvailablePerson(p.getId(), p.isMale());

    }

    public boolean addAvailablePerson(int personId, boolean isMale) {
        int arrayIndex = isMale ? INDEX_MALE : INDEX_FEMALE;
        boolean res;
        synchronized (this.personByGender[arrayIndex]) {   // Probably not necessary as personByGender[i] is synchronized anyway
            res = this.personByGender[arrayIndex].add(personId);
        }
        return res;
    }

    public boolean removeAvailablePerson(AbstractIndividualInterface p) {
        return this.removeAvailablePerson(p.getId(), p.isMale());
    }

    public boolean removeAvailablePerson(int personId, boolean isMale) {
        int arrayIndex = isMale ? INDEX_MALE : INDEX_FEMALE;
        boolean res;
        synchronized (this.personByGender[arrayIndex]) {  // Probably not necessary as personByGender[i] is synchronized anyway
            res = this.personByGender[arrayIndex].remove(personId);
        }
        return res;
    }

    /**
     * Return all AbstractPerson available in map, ordered by input comparator
     *
     * @param comparator input comparator. If null, it will return unordered.
     * @param personMap a Map of AbstractPerson, with AbstractPerson id as key
     * @return all AbstractPerson within the map order by input comparator.
     */
    public <T extends AbstractIndividualInterface> AbstractIndividualInterface[][] getPersonsAvailable(Comparator<AbstractIndividualInterface> comparator, Map<Integer, T> personMap) {
        AbstractIndividualInterface[][] res = new AbstractIndividualInterface[2][];

        for (int i = 0; i < res.length; i++) {
            AbstractIndividualInterface[] ent = new AbstractIndividualInterface[this.personByGender[i].size()];
            Integer[] pid = new Integer[ent.length];
            pid = this.personByGender[i].toArray(pid);
            for (int p = 0; p < pid.length; p++) {
                // For now all in local data
                ent[p] = personMap.get(pid[p]);
            }
            if (comparator != null) {
                Arrays.sort(ent, comparator);
            }
            res[i] = ent;
        }
        return res;
    }

    /**
     * Return the id of all AbstractPerson available in map
     *
     * @return the id of all AbstractPerson within the map, separated by gender
     */
    public int[][] getPersonAvailableByIndex() {
        int[][] res = new int[INDEX_TOTAL][];
        for (int i = 0; i < res.length; i++) {
            res[i] = new int[this.personByGender[i].size()];
            Integer[] pid = new Integer[this.personByGender[i].size()];
            pid = this.personByGender[i].toArray(pid);
            for (int p = 0; p < pid.length; p++) {
                res[i][p] = pid[p].intValue();
            }
        }
        return res;
    }

    public SingleRelationship[] getRelationshipArray() {
        return relArr.toArray(new SingleRelationship[relArr.size()]);
    }

}
