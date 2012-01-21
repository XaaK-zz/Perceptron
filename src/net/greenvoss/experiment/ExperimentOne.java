package net.greenvoss.experiment;

import java.util.List;
import net.greenvoss.PerceptronTrainer;

/**
 * Class to represent Experiment One - baseline training
 */
public class ExperimentOne extends ExperimentBase {

	/**
	 * Overridden execute method 
	 */
	public void execute(String trainingDataPath, String testingDataPath, int digit, boolean logging) {
		//create trainers 
		List<PerceptronTrainer> trainerList = this.getPerceptronTrainers(10, 64, 0.2f, digit);
		
		//Load training data
		List<String> fileData = this.getFileContents(trainingDataPath);
		
		//train on the data
		this.train(trainerList,fileData,
				new int[] {0,0,0,0,0,0,0,0,0,0},
				new int[] {1000,1000,1000,1000,1000,1000,1000,1000,1000,1000});
		
		//show results of the testing data
		List<ExperimentMetrics> metrics = this.calculateMetrics(trainerList, fileData);
		this.ReportResults(trainerList, metrics,"Experiment One metrics on training data:");
		
		//done training - get testing metrics//////////////////////////
		//Load testing data
		List<String> testFileData = this.getFileContents(testingDataPath);
		metrics = this.calculateMetrics(trainerList, testFileData);
		
		//Done - we can now print out the metrics
		this.ReportResults(trainerList, metrics, "Experiment One metrics on testing data:");
	}

}
