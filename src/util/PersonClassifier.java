package util;

import java.io.Serializable;
import person.AbstractIndividualInterface;

/**
 * Simple utility function to classify an AbstractPerson
 * @author Ben Hui
 */
public interface PersonClassifier extends Serializable {
    public int classifyPerson(AbstractIndividualInterface p);    
    public int numClass();
    
}
