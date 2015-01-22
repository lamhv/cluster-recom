package bk.cluster;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;

import bk.cluster.kmean.Clustering;
import bk.cluster.kmean.KLCluster;
import bk.recommendation.Recommendation;

/**
 * Hello world!
 *
 */
public class App {
	private HashMap<String, HashMap<String, String>> userIdMovieRating = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, Vector> centroids = new HashMap<String, Vector>();

	public static void main1(String[] args) throws Exception {
		Recommendation.recommend();
	}
	
	public static void main(String[] args) throws Exception {
		App app = new App();
		app.readFile();

		// read the values (features) - generate vectors from input data
		List<NamedVector> vectors = app.getPoints();
		Clustering.cluster(vectors);
		
		// get test vector
		Vector testVector = app.generateTestVector();

		// List clusterId and calculate distance with testVector
		double closetDistance = Double.MAX_VALUE;
		String closetClusterId = "";
		
		Set<String> clusterIds = KLCluster.getClusterIds();
		Iterator<String> it = clusterIds.iterator();
		while (it.hasNext()) {
			String clusterId = it.next();
			Vector centroid = KLCluster.getCentroid(clusterId);
			
			double distance = centroid.getDistanceSquared(testVector);
			if (distance < closetDistance) {
				closetDistance = distance;
				closetClusterId = clusterId;
			}
			
			app.centroids.put(clusterId, centroid);
			
			System.out.println(String.format("ClusterId: %s. Vector: %s",
					clusterId, centroid.toString()));
		}
		
		// Inference which cluster that vector belong to
		System.out.println(String.format("Test vector belong to %s. Distacne: %f", closetClusterId, closetDistance));
		
		// Recommendation
		Recommendation.changeDataFromCluster(KLCluster.getVectors(closetClusterId));
		Recommendation.recommend();
	}
	
	private Vector generateTestVector() {
		String filePath = "/Users/youtech/Documents/Eclipse/Workspace_MahoutHadoop/dataMovie/lamhv_testClustering.txt";
		// double[][] result = new double[]
		try (InputStreamReader isr = new InputStreamReader(new FileInputStream(
				filePath), "UTF-8");
				BufferedReader br = new BufferedReader(isr)) {

			String userId = "";
			String movieId = "";
			String rating = "";
			HashMap<String, String> hm = new HashMap<String, String>();

			String line = br.readLine();
			while (line != null) {
				String[] inputItems = line.split("\\t", -2);

				userId = inputItems[0];
				movieId = inputItems[1];
				rating = inputItems[2];

				hm.put(movieId, rating);

				line = br.readLine();
			}

			System.out.println(hm);
			
			double[] ele = new double[1682];

			for (int i = 0; i < ele.length; i++) {
				movieId = String.valueOf(i + 1);
				if (hm.containsKey(movieId)) {
					ele[i] = Double.parseDouble(hm.get(movieId));
				} else {
					ele[i] = 0;
				}
			}

			Vector vec = new RandomAccessSparseVector(ele.length);
			vec.assign(ele);
			NamedVector nv = new NamedVector(vec, String.format("userTest_%s",
					userId));
			
			return nv;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private void readFile() {
		String filePath = "/Users/youtech/Documents/Eclipse/Workspace_MahoutHadoop/dataMovie/ml-100k/u1.base";
		// double[][] result = new double[]
		try (InputStreamReader isr = new InputStreamReader(new FileInputStream(
				filePath), "UTF-8");
				BufferedReader br = new BufferedReader(isr)) {

			// double[] ele = {1,1};
			// System.out.println(ele);

			String userId = "";
			String movieId = "";
			String rating = "";

			String line = br.readLine();
			while (line != null) {
				String[] inputItems = line.split("\\t", -2);

				userId = inputItems[0];
				movieId = inputItems[1];
				rating = inputItems[2];

				addInfo(userId, movieId, rating);

				line = br.readLine();
			}

			System.out.println(userIdMovieRating);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List getPoints() {
		List points = new ArrayList();

		Set<String> userIds = userIdMovieRating.keySet();
		for (String userId : userIds) {
			HashMap<String, String> movieRating = userIdMovieRating.get(userId);

			double[] ele = new double[1682];

			for (int i = 0; i < ele.length; i++) {
				String movieId = String.valueOf(i + 1);
				if (movieRating.containsKey(movieId)) {
					ele[i] = Double.parseDouble(movieRating.get(movieId));
				} else {
					ele[i] = 0;
				}
			}

			Vector vec = new RandomAccessSparseVector(ele.length);
			vec.assign(ele);
			NamedVector nv = new NamedVector(vec, String.format("user_%s",
					userId));
			points.add(nv);
		}

		return points;
	}

	private void addInfo(String userId, String movieId, String rating) {

		HashMap<String, String> userRating = userIdMovieRating.get(userId);

		if (userRating != null) {
			userRating.put(movieId, rating);
		} else {
			userRating = new HashMap<String, String>();
			userRating.put(movieId, rating);

			userIdMovieRating.put(userId, userRating);
		}
	}
}
