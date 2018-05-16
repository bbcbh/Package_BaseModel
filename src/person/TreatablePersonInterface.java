package person;

/**
 * A interface for Person who can received treatment.
 * @author Ben Hui
 */
public interface TreatablePersonInterface extends AbstractIndividualInterface {

    int getLastTreatedAt();
    void setLastTreatedAt(int lastTreatedAt); 

}
