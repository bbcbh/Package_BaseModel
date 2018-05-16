package relationship;

import org.jgrapht.EdgeFactory;

public class RelationshipEdgeFactory implements EdgeFactory<Integer, SingleRelationship>, java.io.Serializable {

    public RelationshipEdgeFactory(){
        super();
    }

    @Override
    public SingleRelationship createEdge(Integer arg0, Integer arg1) {
        Integer[] links = new Integer[2];
        links[0] = arg0;
        links[1] = arg1;
        return new SingleRelationship(links);
    }
}
