package net.greenvoss;

public class PerceptronDriver {

	static void usage() {
		System.console().printf("Perceptron.jar <trainingData.tra> <testingData.tes> <digit>\n"+ 
				"trainingData.tra: File path to the training data file.\n" + 
				"testingData.tes: File path to the testing data file.\n" + 
				"digit: single digit to use as the test basis.\n");
	}
	
	/**
	 * @param args Command line parameters
	 * 		0: fileName of training data file
	 * 		1: fileName of testing data file
	 * 		2: digit to use to compare against others
	 */
	public static void main(String[] args) {
		if(args.length < 3) {
			usage();
			return;
		}
	}

}
