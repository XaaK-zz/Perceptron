package net.greenvoss;

import junit.framework.Assert;

import org.junit.Test;

public class PerceptronTest {
	
	@Test
	public void simpleThresholdTest() {
		//create simple two node Perceptron
		Perceptron simple = new Perceptron(2);
		
		//set inputs to (1,0)
		simple.setInput(1, 1);
		simple.setInput(2, 0);
		
		//set weights to .5,-1,1
		//	this should cause the threshold to be -1
		simple.setWeight(0, .5f);
		simple.setWeight(1, -1f);
		simple.setWeight(2, 1f);
		
		Assert.assertEquals("Invalid Threshold value.", -1,simple.threshold());
	}
	
	@Test
	public void simpleThresholdTest2() {
		//create simple two node Perceptron
		Perceptron simple = new Perceptron(2);
		
		//set inputs to (1,0)
		simple.setInput(1, 1);
		simple.setInput(2, 0);
		
		//set weights to 1,-.75,1
		//	this should cause the threshold to be 1
		simple.setWeight(0, 1f);
		simple.setWeight(1, .75f);
		simple.setWeight(2, 1f);
		
		Assert.assertEquals("Invalid Threshold value.", 1,simple.threshold());
	}
	
	@Test
	public void trainerTestSingleRound() {
		//build a trainer object that overrides the random weights function 
		PerceptronTrainer trainer = new PerceptronTrainer() {
			@Override
			public void setRandomWeights(int inputNodes) {
				this.perceptron.setWeight(0,  0.1f);
				this.perceptron.setWeight(1,  0.1f);
				this.perceptron.setWeight(2, -0.3f);
			}
		};
		//create trainer with example settings
		trainer.init(2, .2f);
		trainer.trainOnDataRow(new int[] {0,0}, -1);
		
		//check that weights were updated correctly
		Assert.assertEquals("Invalid weight for node 0",-.3f,trainer.perceptron.getWeightValue(0));
		Assert.assertEquals("Invalid weight for node 1", .1f,trainer.perceptron.getWeightValue(1));
		Assert.assertEquals("Invalid weight for node 2",-.3f,trainer.perceptron.getWeightValue(2));
	}
	
	@Test
	public void trainerTestTwoRounds() {
		//build a trainer object that overrides the random weights function 
		PerceptronTrainer trainer = new PerceptronTrainer() {
			@Override
			public void setRandomWeights(int inputNodes) {
				this.perceptron.setWeight(0,  0.1f);
				this.perceptron.setWeight(1,  0.1f);
				this.perceptron.setWeight(2, -0.3f);
			}
		};
		//create trainer with example settings
		trainer.init(2, .2f);
		trainer.trainOnDataRow(new int[] {0,0}, -1);
		
		//check that weights were updated correctly
		Assert.assertEquals("Invalid weight for node 0",-.3f,trainer.perceptron.getWeightValue(0));
		Assert.assertEquals("Invalid weight for node 1", .1f,trainer.perceptron.getWeightValue(1));
		Assert.assertEquals("Invalid weight for node 2",-.3f,trainer.perceptron.getWeightValue(2));
		
		//do second example
		trainer.trainOnDataRow(new int[] {0,1}, 1);
		//check that weights were updated correctly
		Assert.assertEquals("Invalid weight for node 0",.1f,trainer.perceptron.getWeightValue(0));
		Assert.assertEquals("Invalid weight for node 1",.1f,trainer.perceptron.getWeightValue(1));
		Assert.assertEquals("Invalid weight for node 2",.1f,trainer.perceptron.getWeightValue(2));
		
	}
	
	@Test
	public void trainerTestThreeRounds() {
		//build a trainer object that overrides the random weights function 
		PerceptronTrainer trainer = new PerceptronTrainer() {
			@Override
			public void setRandomWeights(int inputNodes) {
				this.perceptron.setWeight(0,  0.1f);
				this.perceptron.setWeight(1,  0.1f);
				this.perceptron.setWeight(2, -0.3f);
			}
		};
		//create trainer with example settings
		trainer.init(2, .2f);
		trainer.trainOnDataRow(new int[] {0,0}, -1);
		
		//check that weights were updated correctly
		Assert.assertEquals("Invalid weight for node 0",-.3f,trainer.perceptron.getWeightValue(0));
		Assert.assertEquals("Invalid weight for node 1", .1f,trainer.perceptron.getWeightValue(1));
		Assert.assertEquals("Invalid weight for node 2",-.3f,trainer.perceptron.getWeightValue(2));
		
		//do second example
		trainer.trainOnDataRow(new int[] {0,1}, 1);
		//check that weights were updated correctly
		Assert.assertEquals("Invalid weight for node 0",.1f,trainer.perceptron.getWeightValue(0));
		Assert.assertEquals("Invalid weight for node 1",.1f,trainer.perceptron.getWeightValue(1));
		Assert.assertEquals("Invalid weight for node 2",.1f,trainer.perceptron.getWeightValue(2));
		
		//do third example
		trainer.trainOnDataRow(new int[] {1,1}, 1);
		//check that weights were updated correctly
		Assert.assertEquals("Invalid weight for node 0",.1f,trainer.perceptron.getWeightValue(0));
		Assert.assertEquals("Invalid weight for node 1",.1f,trainer.perceptron.getWeightValue(1));
		Assert.assertEquals("Invalid weight for node 2",.1f,trainer.perceptron.getWeightValue(2));
		
	}
}
