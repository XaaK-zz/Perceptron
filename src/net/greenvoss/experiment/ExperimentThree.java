package net.greenvoss.experiment;

import java.util.List;
import net.greenvoss.PerceptronTrainer;

/**
 * Class to represent Experiment Three - undertraining
 */
public class ExperimentThree extends ExperimentBase {

	/**
	 * Overridden execute method 
	 */
	public void execute(String trainingDataPath, String testingDataPath, int digit, boolean logging) {
		//create trainers 
		List<PerceptronTrainer> trainerList = this.getPerceptronTrainers(10, 64, 0.2f, digit);
		
		//Load training data
		List<String> fileData = this.getFileContents(trainingDataPath);
		
		//train on the data
		//	NOTE: We are forcing the max epoch to about 3/4 of the epoch's trained in experiment 1
		//		in order to undertrain  the perceptrons
		this.train(trainerList,fileData,
				new int[] {0,0,0,0,0,0,0,0,0,0},
				new int[] {2,3,5,1,2,12,3,1,3,8});
		
		//show results of the testing data
		List<ExperimentMetrics> metrics = this.calculateMetrics(trainerList, fileData);
		this.ReportResults(trainerList, metrics, "Experiment Three metrics on training data:");
		
		//done training - get testing metrics//////////////////////////
		//Load testing data
		List<String> testFileData = this.getFileContents(testingDataPath);
		metrics = this.calculateMetrics(trainerList, testFileData);
		
		//Done - we can now print out the metrics
		this.ReportResults(trainerList, metrics, "Experiment Three metrics on testing data:");
	}

}
