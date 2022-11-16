package relationship;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

import org.jgrapht.graph.DirectedMultigraph;

public class TransmissionMap extends DirectedMultigraph<Integer, Integer[]> {

	private static final long serialVersionUID = -1700110334758702189L;
	
	long id = 0;
	
	public TransmissionMap() {
		super(Integer[].class);
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
	public String toFullString() {
		StringWriter wri = new StringWriter();
		PrintWriter pWri = new PrintWriter(wri);
		Set<Integer[]> edges = this.edgeSet();
		for (Integer[] e : edges) {
			for (int i = 0; i < e.length; i++) {
				if (i > 0) {
					pWri.print(',');
				}
				pWri.print(e[i]);
			}
			pWri.println();
		}
		pWri.close();
		return wri.toString();
	}

	public static TransmissionMap TransmissonMapFromFullString(String str) throws IOException {
		TransmissionMap map = new TransmissionMap();
		BufferedReader reader = new BufferedReader(new StringReader(str));
		String line;

		while ((line = reader.readLine()) != null) {
			String[] ent = line.split(",");
			Integer[] edge = new Integer[ent.length];

			if (edge.length > 2) {
				for (int i = 0; i < ent.length; i++) {
					edge[i] = Integer.parseInt(ent[i]);
				}
				if (!map.containsVertex(edge[0])) {
					map.addVertex(edge[0]);
				}
				if (!map.containsVertex(edge[1])) {
					map.addVertex(edge[1]);
				}
				map.addEdge(edge[0], edge[1], edge);
			}
		}

		reader.close();

		return map;

	}

	


}
