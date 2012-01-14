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
	
	ExperimentMetrics calculateMetrics(List<PerceptronTrainer> trainerList, String[] fileData, int targetDigit) {
		ExperimentMetrics metrics = new ExperimentMetrics();
		//calculate accuracy on the sets
		for(int x=0;x<trainerList.size();x++) {
			if(x != targetDigit){
				PerceptronTrainer current = trainerList.get(x);
				for(String fileRow : fileData) {
					int[] rowData = this.getRowContents(fileRow);
					if(rowData[rowData.length-1] == targetDigit) {
						if(current.evaluateOnDataRow(rowData, 1)) {
							metrics.TruePositives++;
						}
						else{
							metrics.FalseNegatives++;
						}
					}
					else if(rowData[rowData.length-1] == x) {
						if(current.evaluateOnDataRow(rowData, -1)){
							metrics.TrueNegatives++;
						}
						else{
							metrics.FalsePositives++;
						}
					}
				}
			}
		}
		return metrics;
	}
}
