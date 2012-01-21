package net.greenvoss;

import java.util.List;

import net.greenvoss.experiment.ExperimentBase;
import net.greenvoss.experiment.ExperimentFactory;

/**
 * Main entry-point for the application.
 * 	Contains the main method and handles command-line arguments
 */
public class PerceptronDriver {

	static void usage() {
		System.console().printf("Perceptron.jar <trainingData.tra> <testingData.tes> <digit>\n"+ 
				"\t\ttrainingData.tra: File path to the training data file.\n" + 
				"\t\ttestingData.tes: File path to the testing data file.\n" + 
				"\t\tdigit: single digit to use as the test basis.\n" + 
				"\t\t[-verbose]: optional flag to turn on debugging.\n");
	}
	
	/**
	 * @param args Command line parameters
	 * 		0: fileName of training data file
	 * 		1: fileName of testing data file
	 * 		2: digit to use to compare against others
	 */
	public static void main(String[] args) {
		boolean verboseMode = false;
		if(args.length < 3) {
			usage();
			return;
		}
		if(args.length >=4){
			if(args[3] == "-verbose"){
				verboseMode = true;
			}
		}
		//get the list of experiments
		List<ExperimentBase> list = ExperimentFactory.getExperiments();
		//iterate over them and execute the experiment
		for(ExperimentBase experiment : list){
			experiment.execute(args[0], args[1], Integer.parseInt(args[2]),verboseMode);
		}
	}

}
