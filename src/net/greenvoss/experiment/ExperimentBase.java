package net.greenvoss.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.greenvoss.PerceptronTrainer;

import org.apache.commons.io.FileUtils;

/**
 * Base class for all experiments we will execute with the Perceptron library
 *	This class contains an execute method that will be overridden by the specific 
 *		Experiment classes, as well as some utility methods useful for all experiments
 */
public class ExperimentBase {
	
	final static double TRAINING_CONSTANT = 0.01;
	public final static String DYNAMIC_PROPERTY_DIGIT = "DigitTarget";
	
	/**
	 * Common method used to execute the derived experiments
	 * @param trainingDataPath Path to the training data file
	 * @param testingDataPath Path to the testing data file
	 * @param digit Digit to use in the experiment
	 * @param logging Flag to turn on logging to the console
	 */
	public void execute(String trainingDataPath, String testingDataPath, int digit, boolean logging) {
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
	public int[] getRowContents(String row) {
		String[] temp = row.split("[,]");
		int[] intArray = new int[temp.length];
		for(int x=0;x<temp.length;x++){
			intArray[x] = Integer.parseInt(temp[x]);
		}
		return intArray;
	}
	
	/*
	 * Utility method to get a PerceptronTrainer.
	 * 	This is overridden in the unit tests to return derived PerceptronTrainers
	 */
	PerceptronTrainer getTrainer(){
		return new PerceptronTrainer(); 
	}
	
	/**
	 * Returns a list of PerceptronTrainers defined by the input parameters
	 * @param numberOfTrainers Number of trainers to construct
	 * @param inputSize Size of the underlying perceptron input layer 
	 * @param learningRate Learning rate of the training algorithm
	 * @return List of PerceptronTrainers - initialized with the passed in data and 
	 * 		set to random weights
	 */
	List<PerceptronTrainer> getPerceptronTrainers(int numberOfTrainers, int inputSize, float learningRate) {
		List<PerceptronTrainer> list = new ArrayList<PerceptronTrainer>();
		for(int x=0;x<numberOfTrainers;x++){
			PerceptronTrainer trainer = getTrainer();
			trainer.init(inputSize, learningRate);
			trainer.setDynamicData(DYNAMIC_PROPERTY_DIGIT, (Integer)x);
			list.add(trainer);
		}
		return list;
	}
	
	/**
	 * Calculates the confusion matrix data and accuracy for a given set of trainers and input data
	 * @param trainerList List of PerceptronTrainers to test
	 * @param fileData List of test data in CSV format, with the target digit as the last value
	 * @param targetDigit Target digit we are testing on
	 * @return List of ExperimentMetrics objects, containing the detailed results of the trainers 
	 * 		against the fileData data rows.
	 */
	List<ExperimentMetrics> calculateMetrics(List<PerceptronTrainer> trainerList, List<String> fileData, int targetDigit) {
		List<ExperimentMetrics> metrics = new ArrayList<ExperimentMetrics>();
		//calculate accuracy on the sets
		for(PerceptronTrainer trainer : trainerList){
			ExperimentMetrics metric = trainer.calculateMetrics(fileData, targetDigit, this);
			metrics.add(metric);
		}
		
		return metrics;
	}

	/**
	 * Core training method.  Accepts list of PerceptronTrainers and FileData and iterates over the
	 * 	file data until the accuracy on the trainers stabilizes (or we have tried for 1000 times)
	 * @param trainerList List of PerceptronTrainer objects to train against the data
	 * @param fileData List of String objects containing the CSV data from the training data - 
	 * 		this must have the target digit as the last element in each row
	 * @param targetDigit Digit we are testing against
	 * @param maxEpochs Max number of times we will run the training data
	 * @return Number of epochs trained
	 */
	void train(List<PerceptronTrainer> trainerList, List<String> fileData, 
			int targetDigit, int[] minEpochsList, int[] maxEpochsList){
		
		//for(PerceptronTrainer trainer : trainerList){
		for(int x=0;x<trainerList.size();x++) {
			PerceptronTrainer trainer = trainerList.get(x);
			trainer.train(this, fileData, targetDigit, minEpochsList[x], maxEpochsList[x]);
		}
	}

	/**
	 * Extracted logic to determine if the training epochs should continue or not
	 */
	public boolean shouldKeepTraining(int epochs, int minEpochs, int maxEpochs,
			double lastAccuracy, double currentAccuracy){
		if(epochs < minEpochs){
			return true;
		}
		else if(epochs >= maxEpochs){
			return false;
		}
		else if(currentAccuracy == 0.0 || lastAccuracy == 0.0){
			return true;
		}
		else if(Math.abs(currentAccuracy - lastAccuracy) > TRAINING_CONSTANT){
			return true;
		}
		else {
			return false;
		}
			
	}
	
	/**
	 * Method used to display the results of an experiment.
	 * 	Can also be overridden in the unit tests to check for the final results of a test
	 * @param trainerList Final list of PerceptronTrainers after an experiment is complete
	 * @param metrics Final list of metrics, calculated on the training data
	 * @param epochs Number of training runs executed
	 * @param epochs String to print before the results
	 */
	void ReportResults(List<PerceptronTrainer> trainerList, List<ExperimentMetrics> metrics, 
			String header){
		System.out.println(header);
		for(int x=0;x<metrics.size();x++){
			ExperimentMetrics metric = metrics.get(x);
			PerceptronTrainer trainer = trainerList.get(x);
			System.out.println("Metrics for digit: " + x + " after " + trainer.getEpochsTrained() + " epochs.");
			System.out.println("TruePositives: " + metric.TruePositives);
			System.out.println("TrueNegatives: " + metric.TrueNegatives);
			System.out.println("FalsePositives: " + metric.FalsePositives);
			System.out.println("FalseNegatives: " + metric.FalseNegatives);
			System.out.println("Accuracy: " + metric.getAccuracy());
		}
		System.out.println("-----------------------------------------------");
	}
	
	public boolean isDataRowPositiveTrainingSample(int[] rowData, int targetDigit){
		if(rowData[rowData.length-1] == targetDigit) {
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isDataRowNegativeTrainingSample(int[] rowData, PerceptronTrainer perceptron){
		if(rowData[rowData.length-1] == (Integer)perceptron.getDynamicData(DYNAMIC_PROPERTY_DIGIT)) {
			return true;
		}
		else{
			return false;
		}
	}
}
