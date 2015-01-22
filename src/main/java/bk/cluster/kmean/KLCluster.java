package bk.cluster.kmean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;

public class KLCluster {
	private static HashMap<String, List<Vector>> infoClusters = new HashMap<String, List<Vector>>();
	
	public static void addInfoClusters(String clusterId, Vector vector) {
		if (infoClusters.containsKey(clusterId)) {
			List<Vector> lst = infoClusters.get(clusterId);
			lst.add(vector);
		} else {
			List<Vector> lst = new ArrayList<Vector>();
			lst.add(vector);
			infoClusters.put(clusterId, lst);
		}
	}
	
	public static List<Vector> getVectors(String clusterId) {
		return infoClusters.get(clusterId);
	}
	
	public static Vector getCentroid(String clusterId) {
		
		List<Vector> lst = infoClusters.get(clusterId);

		Vector centroidVector = new RandomAccessSparseVector(1682);
		
		for (Vector vector: lst) {
			centroidVector = centroidVector.plus(vector);
		}
		
		centroidVector = centroidVector.divide(lst.size());
		
		// refine value in vector
		double[] ele = new double[1682];
		for (int i = 0; i < 1682; i++) {
			ele[i] = Double.parseDouble(String.format("%.2f", centroidVector.get(i)));
		}
		
		Vector refineCentroidVector = new RandomAccessSparseVector(ele.length);
		refineCentroidVector.assign(ele);
		
		NamedVector centroidVectorName = new NamedVector(refineCentroidVector, String.format("centroid_%s",
				clusterId));
		
		return centroidVectorName;
	}
	
	public static Set<String> getClusterIds() {
		return infoClusters.keySet();
	}
}
