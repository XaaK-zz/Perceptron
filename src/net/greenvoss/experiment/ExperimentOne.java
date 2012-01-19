package net.greenvoss.experiment;

import java.util.List;
import net.greenvoss.PerceptronTrainer;

public class ExperimentOne extends ExperimentBase {

	List<PerceptronTrainer> trainerList;
	
	public void execute(String trainingDataPath, String testingDataPath, int digit) {
		//create trainers 
		trainerList = this.getPerceptronTrainers(10, 64, 0.2f);
		
		//Load training data
		List<String> fileData = this.getFileContents(trainingDataPath);
		
		//train on the data
		this.train(trainerList,fileData,digit,0,1000);
		//show results of the testing data
		List<ExperimentMetrics> metrics = this.calculateMetrics(trainerList, fileData, digit);
		this.ReportResults(trainerList, metrics,"Experiment One metrics on training data:");
		
		//done training - get testing metrics//////////////////////////
		//Load testing data
		List<String> testFileData = this.getFileContents(testingDataPath);
		metrics = this.calculateMetrics(trainerList, testFileData, digit);
		
		//Done - we can now print out the metrics
		this.ReportResults(trainerList, metrics, "Experiment One metrics on testing data:");
	}

}
