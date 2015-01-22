package bk.recommendation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;

public class Recommendation {
	public static void changeDataFromCluster(List<Vector> lst) {
		
		try
        {
            FileOutputStream fos = new FileOutputStream("output/dataset.csv");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            
            for (Vector vector: lst) {
    			NamedVector nv = (NamedVector) vector;
    			String itemId = nv.getName().substring(5);
    			for (int i = 0; i < vector.size(); i++) {
    				double value = vector.get(i);
    				
    				if (value > 0) {
    					String line = String.format("%s,%d,%f", itemId, i+1, value);
    					writer.write(line + "\n"); 
    				}
    			}
    		}
            writer.flush();
            
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
	}
	
	public static void recommend() throws Exception  {
		DataModel model = new FileDataModel(new File("/Users/youtech/Documents/Eclipse/Workspace_MahoutHadoop/cluster/output/dataset.csv"));
    	UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
    	UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
    	UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
    	List<RecommendedItem> recommendations = recommender.recommend(943, 3);
    	for (RecommendedItem recommendation : recommendations) {
    	  System.out.println(recommendation);
    	}
	}
}
