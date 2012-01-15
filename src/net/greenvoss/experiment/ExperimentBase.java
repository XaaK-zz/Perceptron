package net.greenvoss.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.greenvoss.PerceptronTrainer;

import org.apache.commons.io.FileUtils;

/**
 * Base class for all experiments we will execute with the Perceptron library
 *	This class contains an execute method that will be overridden by the specific 
 *		Experiment classes, as well as some utility methods useful for all experiments
 */
public class ExperimentBase {
	
	/**
	 * Common method used to execute the derived experiments
	 * @param trainingDataPath Path to the training data file
	 * @param testingDataPath Path to the testing data file
	 * @param digit Digit to use in the experiment
	 */
	public void execute(String trainingDataPath, String testingDataPath, int digit) {
		return;
	}
	
	/**
	 * Utility method for getting the file contents of a passed file
	 * 	Note: this method will be overridden in unit tests to make testing easier
	 * @param file Path to a file to extract data for
	 * @return List of Strings containing the data in the file
	 */
	List<String> getFileContents(String file) {
		try {
			return FileUtils.readLines(new File(file));
		} catch (IOException e) {
			System.err.printf("Error reading from %s - %s\n",file,e);
			return null;
		}
	}
	
	/**
	 * Utility method to split a row into an array or strings 
	 * 	based on a CSV format
	 * Note: This is split out to make testing/mocking easier
	 * @param row String containing csv data
	 * @return Array of string values
	 */
	int[] getRowContents(String row) {
		String[] temp = row.split("[,]");
		int[] intArray = new int[temp.length];
		for(int x=0;x<temp.length;x++){
			intArray[x] = Integer.parseInt(temp[x]);
		}
		return intArray;
	}
	
	PerceptronTrainer getTrainer(){
		return new PerceptronTrainer(); 
	}
	
	List<PerceptronTrainer> getPerceptronTrainers(int numberOfTrainers, int inputSize, float learningRate) {
		List<PerceptronTrainer> list = new ArrayList<PerceptronTrainer>();
		for(int x=0;x<numberOfTrainers;x++){
			PerceptronTrainer trainer = getTrainer();
			trainer.init(inputSize, learningRate);
			list.add(trainer);
		}
		return list;
	}
	
	List<ExperimentMetrics> calculateMetrics(List<PerceptronTrainer> trainerList, List<String> fileData, int targetDigit) {
		List<ExperimentMetrics> metrics = new ArrayList<ExperimentMetrics>();
		//calculate accuracy on the sets
		for(int x=0;x<trainerList.size();x++) {
			if(x != targetDigit){
				ExperimentMetrics metric = new ExperimentMetrics();
				PerceptronTrainer current = trainerList.get(x);
				for(String fileRow : fileData) {
					int[] rowData = this.getRowContents(fileRow);
					if(rowData[rowData.length-1] == targetDigit) {
						if(current.evaluateOnDataRow(rowData, 1)) {
							metric.TruePositives++;
						}
						else{
							metric.FalseNegatives++;
						}
					}
					else if(rowData[rowData.length-1] == x) {
						if(current.evaluateOnDataRow(rowData, -1)){
							metric.TrueNegatives++;
						}
						else{
							metric.FalsePositives++;
						}
					}
				}
				metrics.add(metric);
			}
			else {
				//need to add a dummy metric in here to keep counts accurate
				metrics.add(new ExperimentMetrics() {
					@Override
					public float getAccuracy() {
						return 1;
					}
				});
			}
		}
		return metrics;
	}

	/**
	 * Core training method.  Accepts list of PerceptronTrainers and FileData and iterates over the
	 * 	file data until the accuracy on the trainers hits 90% (or we have tried for 1000 times)
	 * @param trainerList
	 * @param fileData
	 * @param targetDigit
	 * @return Number of epochs trained
	 */
	int train(List<PerceptronTrainer> trainerList, List<String> fileData, 
			int targetDigit, double accuracyTarget, int maxEpochs){
		int epoch = 0;
		double accuracy = 0.0;
		List<ExperimentMetrics> metrics = null;
		
		while(accuracy < accuracyTarget && epoch < maxEpochs) {
			for(String fileRow : fileData) {
				int[] rowData = this.getRowContents(fileRow);
				//check the last element in the row
				if(rowData[rowData.length-1] == targetDigit) {
					//Positive example (i.e. same digit)
					for(int x=0;x<trainerList.size();x++) {
						trainerList.get(x).trainOnDataRow(rowData, 1);
					}
				}
				else {
					//Negative example (i.e. some other digit)
					trainerList.get(rowData[rowData.length-1]).trainOnDataRow(rowData, -1);
				}
			}
			//calculate the metrics for this run
			metrics = this.calculateMetrics(trainerList, fileData, targetDigit);
			
            for(int i=0; i < metrics.size() ; i++)
            	accuracy = accuracy + metrics.get(i).getAccuracy();
            
             //calculate average value
            accuracy = (accuracy / metrics.size()) * 100;
            
			//update the epoch counter
			epoch++;
			//ensure we don't loop forever...
			if(epoch > 1000)
				accuracy = 100;
		}//end while loop
		
		return epoch;
	}
	
	/**
	 * Method used to display the results of an experiment.
	 * 	Can also be overridden in the unit tests to check for the final results of a test
	 * @param trainerList Final list of PerceptronTrainers after an experiment is complete
	 * @param metrics Final list of metrics, calculated on the training data
	 * @param epochs 
	 */
	void ReportResults(List<PerceptronTrainer> trainerList, List<ExperimentMetrics> metrics, int epochs){
		for(int x=0;x<metrics.size();x++){
			ExperimentMetrics metric = metrics.get(x);
			System.out.printf("Metrics for digit: %i after %i epochs.\n ",x,epochs);
			System.out.printf("TruePositives: %i\n",metric.TruePositives);
			System.out.printf("TrueNegatives: %i\n",metric.TrueNegatives);
			System.out.printf("FalsePositives: %i\n",metric.FalsePositives);
			System.out.printf("FalseNegatives: %i\n",metric.FalseNegatives);
			System.out.printf("Accuracy: %f\n",metric.getAccuracy());
		}
	}
}
