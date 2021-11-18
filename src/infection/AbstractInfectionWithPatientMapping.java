package infection;

import java.util.HashMap;
import random.RandomGenerator;

/**
 * An extension of AbstractInfection infection class with storage of patient 
 * specific parameters 
 * 
 * @author Ben Hui
 */
public abstract class AbstractInfectionWithPatientMapping extends AbstractInfection implements java.io.Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 843019464793283504L;
	// A mapping of patient id and infection specific parameter
    protected final HashMap<Integer, double[]> currentlyInfected; 
    
    public AbstractInfectionWithPatientMapping(RandomGenerator RNG) {
        super(RNG);
        currentlyInfected = new HashMap<>();
    }

    public HashMap<Integer, double[]> getCurrentlyInfected() {
        return currentlyInfected;
    }   
    
    
    
}
