package relationship;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.SimpleGraph;

public class ContactMap extends SimpleGraph<Integer, Integer[]> {
	
	int id = 0;

	public ContactMap(Class<? extends Integer[]> edgeClass) {
		super(edgeClass);
	}
	
	public int getId() {
		return id;		
	}
	
	public void setId(int id) {
		this.id = id;
	}

	/*
	public static void main(String[] arg) {
		ContactMap test = new ContactMap(Integer[].class);
		for (int i = 0; i < 100; i++) {
			test.addVertex(i+1);
		}

		
		test.addEdge(1, 2);
		test.addEdge(2, 3);		
		test.addEdge(3, 1);
		test.addEdge(4, 5);
		test.addEdge(6, 8);
		test.addEdge(8, 9);
		
		
		
		for(int i = 0; i < 100; i++) {
			int v1 = (int) (Math.random()*100+1); 
			int v2 = (int) (Math.random()*100+1);
			
			if(v1 != v2 && !test.containsEdge(v1, v2)) {
				test.addEdge(v1, v2);
			}						
		}
		
		Set<ContactMap> cluster = test.getContactCluster();
		System.out.println("Number of clusters = " + cluster.size());
		
	}
	*/

	/**
	 * 
	 */
	private static final long serialVersionUID = -1700147390041143788L;

	@Override
	public Integer[] addEdge(Integer sourceVertex, Integer targetVertex) {
		Integer[] e;
		if (sourceVertex < targetVertex) {
			e = new Integer[] { sourceVertex, targetVertex };			
		} else {
			e = new Integer[] { targetVertex, sourceVertex };			
		}

		if (super.addEdge(e[0], e[1], e)) {
			return e;
		} else {
			return super.addEdge(e[0], e[1]);
		}
	}

	public Set<ContactMap> getContactCluster() {
		Set<ContactMap> res = new HashSet<>();
		Set<Integer> allV = this.vertexSet();

		Integer[] inSubMap = new Integer[allV.size()];
		int inSubMapNext = 0;

		for (Integer v : allV) {
			int vInsert = Integer.MAX_VALUE;
			if (inSubMapNext > 0) {
				vInsert = Arrays.binarySearch(inSubMap, 0, inSubMapNext, v);
			}
			if (inSubMapNext == 0 || vInsert < 0) {
				Integer newV = v;
				int insertPt;

				// Not visited before
				if (inSubMapNext == 0) {
					insertPt = 0;
				} else {
					insertPt = ~vInsert;
					System.arraycopy(inSubMap, insertPt, inSubMap, insertPt+1, inSubMapNext - insertPt);
				}
				inSubMap[insertPt] = v;
				inSubMapNext++;

				// New contactMap
				ContactMap subMap = new ContactMap(Integer[].class);
				subMap.setId(newV);
				res.add(subMap);
				//System.out.println("Added subMap #" + subMap.getId());
				inSubMapNext = addToSubMapDFS(inSubMap, inSubMapNext, newV, subMap);

			}

		}

		return res;

	}

	private int addToSubMapDFS(Integer[] inSubMap, int inSubMapNext, Integer newV, 
			ContactMap subMap) {
		subMap.addVertex(newV);
		Set<Integer[]> edges = this.edgesOf(newV);
		for (Integer[] e : edges) {
			Integer neighbour = e[0].equals(newV) ? e[1] : e[0];
			int nInst = Arrays.binarySearch(inSubMap, 0, inSubMapNext, neighbour);
			if (nInst < 0) {
				System.arraycopy(inSubMap, ~nInst, inSubMap, ~nInst+1, inSubMapNext - ~nInst);
				inSubMap[~nInst] = neighbour;
				inSubMapNext++;
			}
			if (!subMap.containsVertex(neighbour)) {
				subMap.addVertex(neighbour);
			}
			Integer[] subMapEdge;
			if (newV < neighbour) {
				subMapEdge = new Integer[] { newV, neighbour };
			} else {
				subMapEdge = new Integer[] { neighbour, newV };
			}
			if (!subMap.containsEdge(subMapEdge[0], subMapEdge[1])) {
				subMap.addEdge(subMapEdge[0], subMapEdge[1], subMapEdge);
				//System.out.println("Add "+ subMapEdge[0] + "," + subMapEdge[1] 
				//		+ " to subMap #" + subMap.getId());
				inSubMapNext = addToSubMapDFS(inSubMap, inSubMapNext, neighbour, subMap);
			}
		}
		return inSubMapNext;
	}

}
