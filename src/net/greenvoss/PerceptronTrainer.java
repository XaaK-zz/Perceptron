package net.greenvoss;

import java.util.Random;

public class PerceptronTrainer {
	
	Perceptron perceptron;
	float learningRate;
	
	public void init(int inputNodes, float learningRate) {
		this.learningRate = learningRate;
		this.perceptron = new Perceptron(inputNodes);
		this.setRandomWeights(inputNodes);
	}
	
	public void setRandomWeights(int inputNodes) {
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
	 * @param data
	 * @param targetClass
	 */
	public void trainOnDataRow(int[] data, int targetClass) {
		//first send the data into the perceptron
		for(int x=0;x<data.length;x++){
			this.perceptron.setInput(x+1, data[x]);
		}
		//get the actual output from the threshold
		int output = this.perceptron.threshold();
		//if the target does not equal the output we need to train
		if(targetClass != output) {
			for(int x=0;x<=data.length;x++){
				//Apply perceptron gradient decent learning rule:
				//	LearningRate * x_i * (t^k - o^k) 
				this.perceptron.updateWeightValue(x, this.learningRate * (x==0 ? 1 : data[x-1]) * (targetClass - output));
			}
		}
	}
	
	
}
