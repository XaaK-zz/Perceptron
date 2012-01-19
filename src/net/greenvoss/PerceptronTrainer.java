package net.greenvoss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.greenvoss.experiment.ExperimentBase;
import net.greenvoss.experiment.ExperimentMetrics;

public class PerceptronTrainer {
	
	Perceptron perceptron;
	float learningRate;
	HashMap<String,Object> dynamicData = new HashMap<String,Object>();
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
	 * @param name Name value to use to retrieve the value from the hash
	 * @return Object retrieved from the hash
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
		if(!this.evaluateOnDataRow(data, targetClass)) {
			for(int x=0;x<=data.length;x++){
				//Apply perceptron gradient decent learning rule:
				//	LearningRate * x_i * (t^k - o^k) 
				this.perceptron.updateWeightValue(x, this.learningRate * (x==0 ? 1 : data[x-1]) * (targetClass - this.perceptron.threshold()));
			}
		}
	}
	
	/**
	 * Runs the threshold output function on the weights&inputs and returns true if the output
	 * 	matches the passed targetClass and false otherwise
	 * @param data Array of data to use to test the perceptron
	 * @param targetClass Value that should be outout by the threshold function
	 * @return Boolean flag indicating if the threshold found the correct class.
	 */
	public boolean evaluateOnDataRow(int[] data, int targetClass) {
		//first send the data into the perceptron
		for(int x=0;x<data.length;x++){
			this.perceptron.setInput(x+1, data[x]);
		}
		//get the actual output from the threshold
		int output = this.perceptron.threshold();
		//check if the target does not equal the output 
		if(targetClass != output) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public float getWeightValue(int node){
		return this.perceptron.getWeightValue(node);
	}
	
	public ExperimentMetrics calculateMetrics(List<String> fileData, int targetDigit, ExperimentBase experiment) {
		ExperimentMetrics metric = new ExperimentMetrics();
		//calculate accuracy on the Perceptron with the passed in data
		for(String fileRow : fileData) {
			int[] rowData = experiment.getRowContents(fileRow);
			if(experiment.isDataRowPositiveTrainingSample(rowData, targetDigit)) {
				if(this.evaluateOnDataRow(rowData, 1)) {
					metric.TruePositives++;
				}
				else{
					metric.FalseNegatives++;
				}
			}
			else if(experiment.isDataRowNegativeTrainingSample(rowData, this)) {
				if(this.evaluateOnDataRow(rowData, -1)){
					metric.TrueNegatives++;
				}
				else{
					metric.FalsePositives++;
				}
			}
		}
		
		return metric;
	}
	
	public int train(ExperimentBase experiment, List<String> fileData, int targetDigit, 
			int minEpochs, int maxEpochs){
		double lastAccuracy = 0.0;
		double currentAccuracy = 100.0;
		
		//get the current accuracy count
		//currentAccuracy = getAccuracy(trainerList, fileData, targetDigit);
		ExperimentMetrics metrics = this.calculateMetrics(fileData,targetDigit,experiment);
		currentAccuracy = metrics.getAccuracy();
		
		//keep looping until we stablize or have run for 1000 times
		while(experiment.shouldKeepTraining(epochsTrained, minEpochs, maxEpochs, lastAccuracy, currentAccuracy)) {
			lastAccuracy = currentAccuracy;
			for(String fileRow : fileData) {
				int[] rowData = experiment.getRowContents(fileRow);
				if(experiment.isDataRowPositiveTrainingSample(rowData, targetDigit)) {
					//Positive example (i.e. same digit)
					this.trainOnDataRow(rowData, 1);
				}
				else if(experiment.isDataRowNegativeTrainingSample(rowData, this)) {
					//Negative example (i.e. some other digit)
					this.trainOnDataRow(rowData, -1);
				}
			}
			//calculate the metrics for this run
			metrics = this.calculateMetrics(fileData,targetDigit,experiment);
			currentAccuracy = metrics.getAccuracy();
			
			//update the epoch counter
			epochsTrained++;
		}//end while loop
		
		return epochsTrained;
	}
	
}
