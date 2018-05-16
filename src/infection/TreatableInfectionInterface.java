package infection;

import person.TreatablePersonInterface;


/**
 * An infection that is treatable. e.g. antibotic, vaccine etc.
 * @author Ben Hui
 */
public interface TreatableInfectionInterface {

    /**
     * Apply treatment to a SingleRelationshipTypePerson
     * @param treated the individual to be treated
     * @param time the time of treatment in (term of global time)
     * @return true if treatment is successful
     */
    public boolean applyTreatmentAt(TreatablePersonInterface treated,int time);
}
