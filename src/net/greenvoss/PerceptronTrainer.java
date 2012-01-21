package net.greenvoss;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.greenvoss.experiment.ExperimentBase;
import net.greenvoss.experiment.ExperimentMetrics;

/**
 * Class that is responsible for training a Perceptron
 */
public class PerceptronTrainer {
	
	/**
	 * Perceptron this trainer owns and will update based on training data
	 */
	Perceptron perceptron;
	
	/**
	 * Learning rate for the training algorithm
	 */
	float learningRate;
	
	/**
	 * Data map used to store experiment-specific information
	 * 	This was added in order to keep the Perceptron/PerceptronTrainer code 
	 * 	free of any code that was specific to the digit experiments 
	 */
	HashMap<String,Object> dynamicData = new HashMap<String,Object>();
	
	/**
	 * Number of epochs this Perceptron was trained
	 */
	int epochsTrained = 0;
	
	/**
	 * Basic constructor
	 */
	public PerceptronTrainer(){}
	
	/**
	 * Constructs and inits the Perceptron with the passed weights
	 * @param inputNodes Number of input nodes to the underlying Perceptron
	 * @param learningRate Learning rate for the training algorithm
	 * @param weights Array of weights to assign to the Perceptron input nodes
	 */
	public PerceptronTrainer(int inputNodes, float learningRate, float[] weights){
		//init the perceptron
		this.init(inputNodes, learningRate);
		//set weights
		for(int x=0;x<weights.length;x++){
			this.perceptron.setWeight(x, weights[x]);
		}
	}
	
	/**
	 * Initializes the object with random weights
	 * @param inputNodes Number of input nodes to the underlying Perceptron
	 * @param learningRate Learning rate for the training algorithm
	 */
	public void init(int inputNodes, float learningRate) {
		this.learningRate = learningRate;
		this.perceptron = new Perceptron(inputNodes);
		this.setRandomWeights(inputNodes);
	}
	
	/**
	 * Sets dynamic data into this trainer, used for experiment-specific needs
	 * @param name Name value to store in the hash
	 * @param data Value to store in the hash
	 */
	public void setDynamicData(String name, Object data){
		this.dynamicData.put(name, data);
	}
	
	/**
	 * Gets dynamic data from this object
	 * @param name Name value to use to retrieve the value from the dynamic data
	 * @return Object retrieved from the dynamic data
	 */
	public Object getDynamicData(String name){
		return this.dynamicData.get(name);
	}
	
	/**
	 * Returns the epochs trained for this Perceptron 
	 */
	public int getEpochsTrained(){
		return this.epochsTrained;
	}
	
	/**
	 * Sets the underlying Perceptron weights to small random values in the range of -1 to 1
	 * @param inputNodes Number of input nodes on the perceptron
	 */
	void setRandomWeights(int inputNodes) {
		Random rand = new Random();
		//set random weights
		for(int x=0;x<inputNodes;x++){
			//set weights randomly between -1.0 and 1.0
			this.perceptron.setWeight(x, (rand.nextBoolean() ? -1 : 1) * rand.nextFloat());
		}
	}
	
	/**
	 * This method submits a single set of data inputs to the perceptron, 
	 * 		calls the threshold function, compares it to the target class
	 * 		and if it does not match then updates the weights based on the learning
	 * 		algorithm
	 * @param data Array of data values to pass to the Perceptron
	 * @param targetClass Target class this data should produce  
	 */
	public void trainOnDataRow(int[] data, int targetClass) {
		///if the target does not equal the output we need to train
		EvaluateResult result = this.evaluateOnDataRow(data, targetClass);
		if(!result.result) {
			for(int x=0;x<=data.length;x++){
				//Apply perceptron gradient decent learning rule:
				//	LearningRate * x_i * (t^k - o^k) 
				//	Note: the x vs x-1 is just adjusting for the bias node
				this.perceptron.updateWeightValue(x, this.learningRate * 
						(x==0 ? 1 : data[x-1]) * (targetClass - result.rawOutput));
			}
		}
	}
	
	/**
	 * Runs the threshold output function on the weights&inputs and returns true if the output
	 * 	matches the passed targetClass and false otherwise
	 * @param data Array of data to use to test the perceptron
	 * @param targetClass Value that should be outout by the threshold function
	 * @return EvaluateResult This returns a EvaluateResult object becuase we need both the the 
	 * 		result from the evaluation (true/false) as well as the raw output from the threshold
	 */
	public EvaluateResult evaluateOnDataRow(int[] data, int targetClass) {
		//first send the data into the perceptron
		for(int x=0;x<data.length;x++){
			this.perceptron.setInput(x+1, data[x]);
		}
		//get the actual output from the threshold
		int output = this.perceptron.threshold();
		//check if the target does not equal the output 
		if(targetClass != output) {
			return new EvaluateResult(false,output);
		}
		else {
			return new EvaluateResult(true,output);
		}
	}
	
	/**
	 * Returns the weight value for a node on the contained Perceptron object
	 * @param node Node to return the weight value for
	 * @return Weight value of the requested node
	 */
	public float getWeightValue(int node){
		return this.perceptron.getWeightValue(node);
	}
	
	/**
	 * Generates the Confusion Matrix for the current perceptron, 
	 * 	given a set of data and an Experiment
	 * @param fileData Data to evaluate against the Perceptron network
	 * @param experiment Experiment object containing the specific business logic needed
	 * 		for evaluating if a row is a valid sample or not
	 * @return ExperimentMetrics object containing the results of the current Perceptron network
	 * 		against the fileData
	 */
	public ExperimentMetrics calculateMetrics(List<String> fileData, ExperimentBase experiment) {
		ExperimentMetrics metric = new ExperimentMetrics();
		//calculate accuracy on the Perceptron with the passed in data
		for(String fileRow : fileData) {
			int[] rowData = experiment.getRowContents(fileRow);
			//Note: Experiment tells us is this is a Positive/Negative training example
			//	This the Perceptron code doesn't need to know anything about the data
			if(experiment.isDataRowPositiveTrainingSample(rowData, this)) {
				if(this.evaluateOnDataRow(rowData, 1).result) {
					metric.TruePositives++;
				}
				else{
					metric.FalseNegatives++;
				}
			}
			else if(experiment.isDataRowNegativeTrainingSample(rowData, this)) {
				if(this.evaluateOnDataRow(rowData, -1).result){
					metric.TrueNegatives++;
				}
				else{
					metric.FalsePositives++;
				}
			}
		}
		
		return metric;
	}
	
	/**
	 * Train on the provided data until the accuracy stops improving.
	 * This will update the Perceptron weights according to the gradient descent learning rule 
	 * @param experiment Experiment object detailing the business rules for the data
	 * @param fileData The rows of data to train against
	 * @param minEpochs Minimum number of training runs
	 * @param maxEpochs Maximum number of training runs
	 * @return Number of epochs trained on the data before convergence is found
	 */
	public int train(ExperimentBase experiment, List<String> fileData,  
			int minEpochs, int maxEpochs){
		double lastAccuracy = 0.0;
		double currentAccuracy = 100.0;
		
		//get the current accuracy count
		ExperimentMetrics metrics = this.calculateMetrics(fileData,experiment);
		currentAccuracy = metrics.getAccuracy();
		
		//keep looping until we stablize or have run for maxEpochs times
		//	Note: the experiment determines the exact business logic
		while(experiment.shouldKeepTraining(epochsTrained, minEpochs, maxEpochs, lastAccuracy, currentAccuracy)) {
			lastAccuracy = currentAccuracy;
			for(String fileRow : fileData) {
				int[] rowData = experiment.getRowContents(fileRow);
				if(experiment.isDataRowPositiveTrainingSample(rowData, this)) {
					this.trainOnDataRow(rowData, 1);
				}
				else if(experiment.isDataRowNegativeTrainingSample(rowData, this)) {
					this.trainOnDataRow(rowData, -1);
				}
			}
			//calculate the metrics for this run
			metrics = this.calculateMetrics(fileData,experiment);
			currentAccuracy = metrics.getAccuracy();
			
			//update the epoch counter
			epochsTrained++;
		}//end while loop
		
		return epochsTrained;
	}
	
	/**
	 * Container class to hold both the evaluation result and threshold output
	 */
	public class EvaluateResult {
		/**
		 * Result of the threshold function
		 */
		public boolean result;
		
		/*
		 * Raw output of the summation of the node - useful to keep for training
		 */
		public int rawOutput;
		
		/**
		 * Constructor for an EvaluateResult object
		 * @param r Result of the threshold call
		 * @param output Raw output of the summation
		 */
		public EvaluateResult(boolean r, int output) {
			result = r;
			rawOutput = output;
		}
	}
}
