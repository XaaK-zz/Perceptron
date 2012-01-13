package net.greenvoss;

import java.text.NumberFormat;

/**
 * Class to represent a single Perceptron node
 *
 */
public class Perceptron {
	int[] inputNodes;
	float[] weights;
	
	Perceptron(int numberofInputs) {
		//adjust for bias node
		inputNodes = new int[++numberofInputs];
		//set bias node
		inputNodes[0] = 1;
		//weights
		weights = new float[numberofInputs];
	}
	
	/**
	 * Sets the input data on one of the nodes
	 * 	Note: This does not allow the zero index to be set, as that is reserved for the bias node
	 * @param node Index of the input node to set with the value parameter
	 * @param value Data value to apply to the input node
	 */
	public void setInput(int node, int value) {
		if(node >= 1 && node < inputNodes.length) {
			this.inputNodes[node] = value;
		}
	}
	
	/**
	 * Set the weight for a given node
	 * @param node Index of the node to set the weight
	 * @param weight Weight value to set 
	 */
	public void setWeight(int node, float weight) {
		if(node >= 0 && node < weights.length) {
			this.weights[node] = weight;
		}
	}
	
	/**
	 * This method will apply a weight delta to the given weight node
	 * @param node Index of the node to update the weight
	 * @param delta Value to change the weight
	 */
	public void updateWeightValue(int node, float delta) {
		if(node >= 0 && node < weights.length) {
			this.weights[node] += delta;
		}
	}
	
	/**
	 * Retrieval method to extract the current weight for a node
	 * 	Note that this performs simple formatting on the number to avoid precision errors
	 * 		that came up in testing
	 * @param node Index of the node to retrieve the weight value for
	 * @return float value containing the weight of the given node
	 */
	float getWeightValue(int node){
		if(node >= 0 && node < weights.length) {
			NumberFormat x = NumberFormat.getInstance();
			return Float.parseFloat(x.format(this.weights[node]));
		}
		else {
			return Float.NaN;
		}
	}
	
	/**
	 * Method to perform the threshold function on the inputs/weights
	 * @return 
	 * 		1 if the sum of the inputs * weights is > 0
	 * 		0 if the sum of the inputs * weights is 0
	 * 		-1 if the sum of the inputs * weights is < 0
	 */
	public int threshold() {
		float total = this.getRawValue();
		if(total > 0)
			return 1;
		else if (total == 0.0f)
			return 0;
		else
			return -1;
	}
	
	/**
	 * Internal method to retrieve the value of the perceptron
	 */
	private float getRawValue() {
		float total = 0.0f;
		for(int x=0;x<inputNodes.length;x++) {
			total += (inputNodes[x] * this.getWeightValue(x));	
		}
		return total;
	}
	
}
