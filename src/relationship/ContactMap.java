package relationship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.SimpleGraph;

public class ContactMap extends SimpleGraph<Integer, Integer[]> {
	
	int id = 0;

	public ContactMap() {
		super(Integer[].class);
	}
	
	public int getId() {
		return id;		
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String toFullString() {
		StringWriter wri = new StringWriter();
		PrintWriter pWri = new PrintWriter(wri);
		Set<Integer[]> edges = this.edgeSet();		
		for(Integer[] e : edges) {			
			for(int i = 0; i < e.length; i++) {
				if(i > 0) {
					pWri.print(',');
				}
				pWri.print(e[i]);
			}
			pWri.println();			
		}
		pWri.close();
		return wri.toString();			
	}
	
	public static ContactMap ContactMapFromFullString(String str) throws IOException {		
		ContactMap map = new ContactMap();
		BufferedReader reader = new BufferedReader(new StringReader(str));
		String line;
		while((line = reader.readLine())!= null) {			
			String[] ent = line.split(",");
			Integer[] edge = new Integer[ent.length];
			for(int i = 0; i < ent.length; i++) {
				edge[i] = Integer.parseInt(ent[i]);
			}
			if(!map.containsVertex(edge[0])) {
				map.addVertex(edge[0]);
			}
			if(!map.containsVertex(edge[1])) {
				map.addVertex(edge[1]);
			}			
			map.addEdge(edge[0], edge[1], edge);						
		}
	
		reader.close();
		
		return map;
		
		
	}
	
	

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
				ContactMap subMap = new ContactMap();
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
			subMapEdge = Arrays.copyOf(e, e.length);
			
			subMapEdge[0] = newV < neighbour?  newV: neighbour;
			subMapEdge[1] = newV < neighbour?  neighbour : newV;
			
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
