package net.greenvoss.experiment;

import java.util.List;
import net.greenvoss.PerceptronTrainer;

/**
 * Class to represent Experiment Two - overtraining
 */
public class ExperimentTwo extends ExperimentBase {

	/**
	 * Overridden execute method 
	 */
	public void execute(String trainingDataPath, String testingDataPath, int digit, boolean logging) {
		//create trainers 
		List<PerceptronTrainer> trainerList = this.getPerceptronTrainers(10, 64, 0.2f, digit);
		
		//Load training data
		List<String> fileData = this.getFileContents(trainingDataPath);
		
		//train on the data
		//	NOTE: We are forcing the min epoch to twice the number trained in experiment one 
		//	in order to overtrain the perceptrons
		this.train(trainerList,fileData,
				new int[] {6,8,16,0,6,34,8,4,8,26},
				new int[] {1000,1000,1000,1000,1000,1000,1000,1000,1000,1000});
		
		//show results of the testing data
		List<ExperimentMetrics> metrics = this.calculateMetrics(trainerList, fileData);
		this.ReportResults(trainerList, metrics, "Experiment Two metrics on training data:");
		
		//done training - get testing metrics//////////////////////////
		//Load testing data
		List<String> testFileData = this.getFileContents(testingDataPath);
		metrics = this.calculateMetrics(trainerList, testFileData);
		
		//Done - we can now print out the metrics
		this.ReportResults(trainerList, metrics, "Experiment Two metrics on testing data:");
	}

}
