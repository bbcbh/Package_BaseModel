package relationship;

/**
 * An extension of SingleRelationship, with time stamp recording the start time of the relationship
 *
 * @author Ben Hui
 */
public class SingleRelationshipTimeStamp extends SingleRelationship {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8441896876051714971L;
	int relStartTime;

    public SingleRelationshipTimeStamp(Integer[] links) {
        super(links);
    }

    public int getRelStartTime() {
        return relStartTime;
    }

    public void setRelStartTime(int relStartTime) {
        this.relStartTime = relStartTime;
    }

}
