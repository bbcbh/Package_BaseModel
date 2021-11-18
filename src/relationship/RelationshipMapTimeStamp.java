package relationship;

/**
 *
 * @author Ben Hui
 */
public class RelationshipMapTimeStamp extends RelationshipMap{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 194676637800794005L;

	@Override
    public SingleRelationship addEdge(Integer arg0, Integer arg1) {
        SingleRelationshipTimeStamp rel = new SingleRelationshipTimeStamp(new Integer[]{arg0, arg1});
        super.addEdge(arg0, arg1, rel);
        return rel;
    }
    
}
