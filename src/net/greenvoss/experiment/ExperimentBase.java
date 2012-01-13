package net.greenvoss.experiment;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
	String[] getRowContents(String row) {
		return row.split("[,]");
	}
}
