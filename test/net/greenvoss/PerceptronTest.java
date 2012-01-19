package net.greenvoss;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import net.greenvoss.experiment.ExperimentBase;
import net.greenvoss.experiment.ExperimentMetrics;

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
	public void shouldEvaluateDataRow() {
		//create simple two node Perceptron
		PerceptronTrainer simple = new PerceptronTrainer(2,0.2f,new float[] {.5f,-1f,1f});
		
		//set inputs to (1,0)

		//set weights to .5,-1,1
		//	this should cause the threshold to be -1
		
		Assert.assertTrue("value for 1,0",simple.evaluateOnDataRow(new int[] {1,0},-1));
	
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
	public void trainerTestSingleRound2() {
		ExperimentBase experiment = new ExperimentBase(){
			@Override
			public boolean isDataRowNegativeTrainingSample(int[] rowData,
					PerceptronTrainer perceptron) {
				return true;
			}
			
			@Override
			public boolean isDataRowPositiveTrainingSample(int[] rowData,
					int targetDigit) {
				return false;
			}
		};
		PerceptronTrainer trainer = new PerceptronTrainer(2,.2f,new float[] {0.1f,0.1f,-0.3f});
		
		
		//create trainer with example settings
		List<String> fileData = new ArrayList<String>();
		fileData.add("0,0");
		trainer.train(experiment, fileData, -1, 0, 1000);
		
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
	
	@Test
	public void shouldReturnDynamicData(){
		PerceptronTrainer trainer = new PerceptronTrainer();
		trainer.setDynamicData("test", 11);
		
		Assert.assertEquals("Invalid value returned from dynamic data map.", 11, trainer.getDynamicData("test"));
	}
	
	@Test
	public void shouldReturnValidMetrics(){
		PerceptronTrainer trainer = new PerceptronTrainer(){
			@Override
			public boolean evaluateOnDataRow(int[] data, int targetClass) {
				if(data[2] == 1){
					return true;
				}
				else {
					return false;
				}
					
			}
		};
		
		ExperimentBase experiment = new ExperimentBase(){
			@Override
			public boolean isDataRowNegativeTrainingSample(int[] rowData,
					PerceptronTrainer perceptron) {
				if(rowData[1] == 1){
					return true;
				}
				else {
					return false;
				}
			}
			
			@Override
			public boolean isDataRowPositiveTrainingSample(int[] rowData,
					int targetDigit) {
				if(rowData[0] == 1){
					return true;
				}
				else {
					return false;
				}
			}
		};
		
		List<String> fileData = new ArrayList<String>();
		fileData.add("1,0,1");	//successful positive training sample
		fileData.add("1,0,0");	//unsuccessful positive training sample
		fileData.add("0,1,1");	//successful negative training sample
		fileData.add("0,1,0");	//unsuccessful negative training sample
		fileData.add("1,0,1");	//successful positive training sample
		fileData.add("1,0,1");	//successful positive training sample
		
		ExperimentMetrics metrics = trainer.calculateMetrics(fileData, 0, experiment);
		
		Assert.assertEquals("Invalid TruePositives", 3, metrics.TruePositives);
		Assert.assertEquals("Invalid TrueNegatives", 1, metrics.TrueNegatives);
		Assert.assertEquals("Invalid FalsePositives", 1, metrics.FalsePositives);
		Assert.assertEquals("Invalid FalseNegatives", 1, metrics.FalseNegatives);
	}
}
