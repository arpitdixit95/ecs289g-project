/**
 * 
 */
package edu.ucdavis.cs.movieminer.taste;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;

import com.planetj.taste.common.TasteException;
import com.planetj.taste.correlation.UserCorrelation;
import com.planetj.taste.eval.RecommenderBuilder;
import com.planetj.taste.eval.RecommenderEvaluator;
import com.planetj.taste.impl.correlation.AveragingPreferenceInferrer;
import com.planetj.taste.impl.correlation.PearsonCorrelation;
import com.planetj.taste.impl.eval.RMSRecommenderEvaluator;
import com.planetj.taste.impl.model.netflix.NetflixDataModel;
import com.planetj.taste.impl.neighborhood.NearestNUserNeighborhood;
import com.planetj.taste.impl.recommender.CachingRecommender;
import com.planetj.taste.impl.recommender.GenericUserBasedRecommender;
import com.planetj.taste.model.DataModel;
import com.planetj.taste.neighborhood.UserNeighborhood;
import com.planetj.taste.recommender.Recommender;

import edu.ucdavis.cs.movieminer.taste.recommender.CompositeRecommender;
import edu.ucdavis.cs.movieminer.taste.recommender.LoggingRecommender;
import edu.ucdavis.cs.movieminer.taste.recommender.NoSuchElementRecommender;
import edu.ucdavis.cs.movieminer.taste.recommender.RandomRecommender;
import edu.ucdavis.cs.movieminer.taste.recommender.WeightedAverageRecommender;

/**
 * Requires the following file and directory
 * to be in the netflix_data_dir.  
 * 
 * movie_titles.txt (file)
 * training_set (directory containing movie ratings)
 * 
 * 
 * @author pfishero
 * @version $Id$
 */
public class TasteTest {
	public static final Logger logger = Logger.getLogger(TasteTest.class);


	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Usage:");
			System.out.println(" java TasteTest {netflix_data_dir} {k_neighbor_value}");
			System.exit(-1);
		} else {
			System.out.println("using command line args: "+args);
		}
		
		final String netflixDataDir = args[0];
		final int userNeighbors = Integer.parseInt(args[1]);
		final int itemNeighbors = Integer.parseInt(args[2]);
		logger.info("netflixDataDir="+netflixDataDir+
				" user_neighbors="+userNeighbors+
				" item_neighbors="+itemNeighbors);
		
		final DataModel myModel = new NetflixDataModel(new File(netflixDataDir));

//		Recommender recommender = new SlopeOneRecommender(myModel);
		//Recommender cachingRecommender = new CachingRecommender(recommender);
		
		
		RecommenderBuilder builder = new RecommenderBuilder() {
		    public Recommender buildRecommender(DataModel model) throws TasteException {
		    	// --- User-based recommender --
		    	// build and return the Recommender to evaluate here
				UserCorrelation userCorrelation = new PearsonCorrelation(model);
				// Optional:
				userCorrelation
						.setPreferenceInferrer(new AveragingPreferenceInferrer(model));

				UserNeighborhood neighborhood = new NearestNUserNeighborhood(userNeighbors,
						userCorrelation, model);

				Recommender userRecommender = new GenericUserBasedRecommender(model,
						neighborhood, userCorrelation);
				// -- end User-based Recommender
				
				// -- SlopeOneRecommender
				// Make a weighted slope one recommender
//				Recommender slopeOneRecommender = new SlopeOneRecommender(model);
				// -- end SlopeOneRecommender
				
				// -- Item-based recommender
				KnnItemBasedRecommender itemBasedRecommender = 
					new KnnItemBasedRecommender(
						model,
						new FileSystemResource("/Users/jbeck/simScore17K-150each.ser"));
				// -- end Item-based recommender
				
				Recommender compositeRecommender = 
							new CompositeRecommender(model, 
											userRecommender,
//											slopeOneRecommender,
											itemBasedRecommender
											).
										setWeights(
											0.40d, 
//											0.10d,
											0.60d
											);
				Recommender cachingRecommender = new CachingRecommender(new LoggingRecommender(new RandomRecommender(new NoSuchElementRecommender(new WeightedAverageRecommender(compositeRecommender,0.5,0.5)))));
				
				logger.info("composed userRecommender, itemBasedRecommender");
				
				return cachingRecommender;
		    }
		  };
//		Recommender recommender = builder.buildRecommender(myModel);
//		List<RecommendedItem> recommendations = recommender.recommend("6", 5);
//		for(RecommendedItem item : recommendations){
//			System.out.println(item.getItem()+": "+ item.getValue());
//		}
		RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
		double evaluation = evaluator.evaluate(builder, myModel, 0.9, 1.0);
		logger.info("evaluation = "+evaluation);
	}

}
