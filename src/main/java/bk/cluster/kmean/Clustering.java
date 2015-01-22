package bk.cluster.kmean;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.Kluster;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class Clustering {
	public static void cluster(List<NamedVector> vectors) throws Exception {
		// specify the number of clusters
		int k = 10;

		// Create input directories for data
		File testData = new File("testdata");

		if (!testData.exists()) {
			testData.mkdir();
		}
		testData = new File("testdata/points");
		if (!testData.exists()) {
			testData.mkdir();
		}

		// Write initial centers
		Configuration conf = new Configuration();

		FileSystem fs = FileSystem.get(conf);

		// Write vectors to input directory
		writePointsToFile(vectors, "testdata/points/file1", fs, conf);

		Path path = new Path("testdata/clusters/part-00000");

		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path,
				Text.class, Kluster.class);

		for (int i = 0; i < k; i++) {
			Vector vec = vectors.get(i);

			// write the initial center here as vec
			Kluster cluster = new Kluster(vec, i,
					new EuclideanDistanceMeasure());
			writer.append(new Text(cluster.getIdentifier()), cluster);
		}

		writer.close();

		// Run K-means algorithm
		KMeansDriver.run(conf, new Path("testdata/points"), new Path(
				"testdata/clusters"), new Path("output"), 0.001, 10, true, 0,
				false);
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path(
				"output/" + Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000"),
				conf);
		IntWritable key = new IntWritable();

		// Read output values
		WeightedPropertyVectorWritable value = new WeightedPropertyVectorWritable();
		while (reader.next(key, value)) {
			System.out.println(value.toString() + " belongs to cluster "
					+ key.toString());
			
			// set data for KLCluster
			Vector vector = value.getVector();
			KLCluster.addInfoClusters(key.toString(), vector);
		}
		reader.close();
	}
	
	private static void writePointsToFile(List<NamedVector> points, String fileName,
			FileSystem fs, Configuration conf) throws IOException {

		Path path = new Path(fileName);
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path,
				Text.class, VectorWritable.class);
		long recNum = 0;
		VectorWritable vec = new VectorWritable();

		for (NamedVector point : points) {
			vec.set(point);
			recNum++;
			writer.append(new Text(point.getName()), vec);
		}

		writer.close();
	}
}
