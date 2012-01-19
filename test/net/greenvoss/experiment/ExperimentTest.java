package net.greenvoss.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import net.greenvoss.PerceptronTrainer;

import org.junit.Test;

public class ExperimentTest {
	
	@Test
	public void shouldLoadFileLines() throws Exception{
		//create file
		File tempFile = File.createTempFile("tmp", null, new File(".")); 
		tempFile.deleteOnExit();
		FileWriter fw = new FileWriter(tempFile);
		BufferedWriter out = new BufferedWriter(fw);
		out.write("test1,test2,test3\n");
		out.write("test4,test5,test6\n");
		out.close();
		
		ExperimentBase experimentBase = new ExperimentBase();
		List<String> fileData = experimentBase.getFileContents(tempFile.getAbsolutePath());
		
		Assert.assertEquals("Did not read file correctly.", 2, fileData.size());
		Assert.assertEquals("Incorrect line 1.", "test1,test2,test3", fileData.get(0));
		Assert.assertEquals("Incorrect line 2.", "test4,test5,test6", fileData.get(1));
	}
	
	@Test
	public void shouldSplitRow() {
		ExperimentBase experimentBase = new ExperimentBase();
		int[] rowData = experimentBase.getRowContents("0,1,2,3");
		
		Assert.assertEquals("Did not parse row correctly.", 4, rowData.length);
		Assert.assertEquals("Incorrect data 0.", 0, rowData[0]);
		Assert.assertEquals("Incorrect data 1.", 1, rowData[1]);
		Assert.assertEquals("Incorrect data 2.", 2, rowData[2]);
		Assert.assertEquals("Incorrect data 3.", 3, rowData[3]);
		
	}
	
	@Test
	public void shouldGetSetofTrainers() {
		ExperimentBase experimentBase = new ExperimentBase();
		List<PerceptronTrainer> list = experimentBase.getPerceptronTrainers(5, 3, .2f);
		
		Assert.assertNotNull("list should not be null.",list);
		Assert.assertEquals("Invalid list size", 5,list.size());
		for(int x=0;x<5;x++){
			Assert.assertNotNull("Null list element - " + x,list.get(x));
		}
	}
	@Test
	public void testMetricsAccuracyCalculation() {
		ExperimentMetrics metrics = new ExperimentMetrics();
		metrics.TruePositives = 5;
		metrics.TrueNegatives = 5;
		metrics.FalsePositives = 5;
		metrics.FalseNegatives = 5;
		
		Assert.assertEquals("Invalid Accuracy", 50.0f,metrics.getAccuracy());
	}
	
	@Test
	public void shouldCalculateAccuracyCorrectly(){
		List<PerceptronTrainer> list = new ArrayList<PerceptronTrainer>();
		//first create list of trainers that will return the data we want
		PerceptronTrainer trainer = new PerceptronTrainerMock();
		trainer.setDynamicData(ExperimentBase.DYNAMIC_PROPERTY_DIGIT, 0);
		list.add(trainer);
		trainer = new PerceptronTrainerMock();
		trainer.setDynamicData(ExperimentBase.DYNAMIC_PROPERTY_DIGIT, 1);
		list.add(trainer);
		trainer = new PerceptronTrainerMock();
		trainer.setDynamicData(ExperimentBase.DYNAMIC_PROPERTY_DIGIT, 2);
		list.add(trainer);
		
		//now construct list of data to get accuracy measurements on
		//	This list combined with the mock Perceptron trainer makes this test work
		List<String> fileData = new ArrayList<String>();
		fileData.add("1,0,0");	//correctly identified as zero (because the first element is the correct class (1 vs -1))
		fileData.add("0,0,0");	//incorrectly identified
		fileData.add("1,1,0");
		fileData.add("1,1,0");
		fileData.add("-1,2,1");	//The first four lines are evaluated for each of the trainers (since digit 0 is the selected digit)
		fileData.add("0,0,1");
		fileData.add("0,2,1");
		fileData.add("0,2,1");
		fileData.add("-1,2,2");
		fileData.add("-1,0,2");
		fileData.add("-1,2,2");
		fileData.add("0,2,2");
		
		ExperimentOne experimentOne = new ExperimentOne();
		List<ExperimentMetrics> metrics = experimentOne.calculateMetrics(list, fileData, 0);
		
		Assert.assertNotNull("Metrics should not be null",metrics);
		Assert.assertEquals("Invalid number of metrics.",3,metrics.size());
		Assert.assertEquals("Invalid TruePositives for digit 1",3,metrics.get(1).TruePositives);
		Assert.assertEquals("Invalid TrueNegatives for digit 1",1,metrics.get(1).TrueNegatives);
		Assert.assertEquals("Invalid FalsePositives for digit 1",3,metrics.get(1).FalsePositives);
		Assert.assertEquals("Invalid FalseNegatives for digit 1",1,metrics.get(1).FalseNegatives);
		Assert.assertEquals("Invalid Accuracy for digit 1",50.0f,metrics.get(1).getAccuracy());
		
		Assert.assertEquals("Invalid TruePositives for digit 2",3,metrics.get(2).TruePositives);
		Assert.assertEquals("Invalid TrueNegatives for digit 2",3,metrics.get(2).TrueNegatives);
		Assert.assertEquals("Invalid FalsePositives for digit 2",1,metrics.get(2).FalsePositives);
		Assert.assertEquals("Invalid FalseNegatives for digit 2",1,metrics.get(2).FalseNegatives);
		Assert.assertEquals("Invalid Accuracy for digit 2",75.0f,metrics.get(2).getAccuracy());
		
	}
	
	@Test
	public void testTraining(){
		ExperimentOne experimentOne = new ExperimentOne();
		List<PerceptronTrainer> list = new ArrayList<PerceptronTrainer>();
		//first create list of trainers 
		PerceptronTrainer trainer = new PerceptronTrainer(2,0.2f,new float[] {.5f,.5f,.5f});
		trainer.setDynamicData(ExperimentBase.DYNAMIC_PROPERTY_DIGIT, 0);
		list.add(trainer);
		trainer = new PerceptronTrainer(2,0.2f,new float[] {.5f,.5f,.5f});
		trainer.setDynamicData(ExperimentBase.DYNAMIC_PROPERTY_DIGIT, 1);
		list.add(trainer);
		trainer = new PerceptronTrainer(2,0.2f,new float[] {.5f,.5f,.5f});
		trainer.setDynamicData(ExperimentBase.DYNAMIC_PROPERTY_DIGIT, 2);
		list.add(trainer);
		
		//now construct list of data to train on
		List<String> fileData = new ArrayList<String>();
		fileData.add("1,0,0");	
		fileData.add("0,0,0");	
		fileData.add("1,1,0");
		fileData.add("1,1,0");
		fileData.add("-1,2,1");	
		fileData.add("0,0,1");
		fileData.add("0,2,1");
		fileData.add("0,2,1");
		fileData.add("-1,2,2");
		fileData.add("-1,0,2");
		fileData.add("-1,2,2");
		fileData.add("0,2,2");
		
		//train data
		//experimentOne.train(list, fileData, 0,0,1000);
		experimentOne.train(list,fileData,0,
				new int[] {0,0,0},
				new int[] {1000,1000,1000});
		
		
		//ensure the weights have been modified
		Assert.assertTrue("Invalid weight.",list.get(1).getWeightValue(0) != .5f);
		Assert.assertTrue("Invalid weight.",list.get(1).getWeightValue(1) != .5f);
		Assert.assertTrue("Invalid weight.",list.get(1).getWeightValue(2) != .5f);
		
		Assert.assertTrue("Invalid weight.",list.get(2).getWeightValue(0) != .5f);
		Assert.assertTrue("Invalid weight.",list.get(2).getWeightValue(1) != .5f);
		Assert.assertTrue("Invalid weight.",list.get(2).getWeightValue(2) != .5f);
	}
	
	@Test
	public void simpleExperimentOneTest() {
		ExperimentOne experimentOne = new ExperimentOne() {
			int fileContentsCall = 0;
			@Override
			List<String> getFileContents(String file) {
				if(fileContentsCall == 0) {
					//return some sample data (just 2 records for digit 0 and 1 for digit 1)
					List<String> list = new ArrayList<String>();
					list.add("0,1,6,15,12,1,0,0,0,7,16,6,6,10,0,0,0,8,16,2,0,11,2,0,0,5,16,3,0,5,7,0,0,7,13,3,0,8,7,0,0,4,12,0,1,13,5,0,0,0,14,9,15,9,0,0,0,0,6,14,7,1,0,0,0");
					list.add("0,0,10,16,6,0,0,0,0,7,16,8,16,5,0,0,0,11,16,0,6,14,3,0,0,12,12,0,0,11,11,0,0,12,12,0,0,8,12,0,0,7,15,1,0,13,11,0,0,0,16,8,10,15,3,0,0,0,10,16,15,3,0,0,0");
					list.add("0,0,2,13,9,0,0,0,0,0,14,11,12,7,0,0,0,6,16,1,0,16,0,0,0,5,12,0,0,11,5,0,0,8,13,0,0,8,7,0,0,1,16,0,0,9,8,0,0,0,13,3,6,16,1,0,0,0,3,16,14,4,0,0,0");
					list.add("0,0,0,3,16,11,1,0,0,0,0,8,16,16,1,0,0,0,0,9,16,14,0,0,0,1,7,16,16,11,0,0,0,9,16,16,16,8,0,0,0,1,8,6,16,7,0,0,0,0,0,5,16,9,0,0,0,0,0,2,14,14,1,0,1");
					list.add("0,0,9,13,1,0,0,0,0,0,8,16,6,0,0,0,0,0,7,16,10,0,0,0,0,0,13,16,10,0,0,0,0,0,9,16,14,0,0,0,0,0,0,7,16,5,0,0,0,0,3,9,16,13,8,5,0,0,4,15,16,16,16,16,1");
					list.add("0,0,0,0,10,13,0,0,0,0,0,0,15,16,0,0,0,0,0,7,16,14,0,0,0,3,12,16,16,13,0,0,0,3,11,9,16,9,0,0,0,0,0,0,16,9,0,0,0,0,0,0,15,12,0,0,0,0,0,0,8,15,2,0,1");
					fileContentsCall++;
					return list;
				}
				else {
					//return testing data (3 digit 0 and 3 digit 1)
					List<String> list = new ArrayList<String>();
					list.add("0,0,5,13,9,1,0,0,0,0,13,15,10,15,5,0,0,3,15,2,0,11,8,0,0,4,12,0,0,8,8,0,0,5,8,0,0,9,8,0,0,4,11,0,1,12,7,0,0,2,14,5,10,12,0,0,0,0,6,13,10,0,0,0,0");
					list.add("0,0,1,9,15,11,0,0,0,0,11,16,8,14,6,0,0,2,16,10,0,9,9,0,0,1,16,4,0,8,8,0,0,4,16,4,0,8,8,0,0,1,16,5,1,11,3,0,0,0,12,12,10,10,0,0,0,0,1,10,13,3,0,0,0");
					list.add("0,0,3,13,11,7,0,0,0,0,11,16,16,16,2,0,0,4,16,9,1,14,2,0,0,4,16,0,0,16,2,0,0,0,16,1,0,12,8,0,0,0,15,9,0,13,6,0,0,0,9,14,9,14,1,0,0,0,2,12,13,4,0,0,0");
					list.add("0,0,0,12,13,5,0,0,0,0,0,11,16,9,0,0,0,0,3,15,16,6,0,0,0,7,15,16,16,2,0,0,0,0,1,16,16,3,0,0,0,0,1,16,16,6,0,0,0,0,1,16,16,6,0,0,0,0,0,11,16,10,0,0,1");
					list.add("0,0,0,0,14,13,1,0,0,0,0,5,16,16,2,0,0,0,0,14,16,12,0,0,0,1,10,16,16,12,0,0,0,3,12,14,16,9,0,0,0,0,0,5,16,15,0,0,0,0,0,4,16,14,0,0,0,0,0,1,13,16,1,0,1");
					list.add("0,0,0,2,16,16,2,0,0,0,0,4,16,16,2,0,0,1,4,12,16,12,0,0,0,7,16,16,16,12,0,0,0,0,3,10,16,14,0,0,0,0,0,8,16,12,0,0,0,0,0,6,16,16,2,0,0,0,0,2,12,15,4,0,1");
					fileContentsCall++;
					return list;
				}
			}
			
			@Override
			void ReportResults(List<PerceptronTrainer> trainerList,
					List<ExperimentMetrics> metrics, String header) {
				
				//Examine metrics and decide if we have trained correctly
				Assert.assertTrue("Invalid accuracy for digit 1. - " + metrics.get(1).getAccuracy(),
						metrics.get(1).getAccuracy() >= 90.0);
				Assert.assertTrue("Invalid accuracy for digit 2. - " + metrics.get(2).getAccuracy(),
						metrics.get(1).getAccuracy() >= 90.0);
				return;
			}
		};
		
		//execute the experiment
		experimentOne.execute("", "", 0,false);
		
	}
	
	@Test
	public void testShouldKeepTraining_NotConverged(){
		ExperimentBase o = new ExperimentBase();
		Assert.assertTrue(o.shouldKeepTraining(1, 0, 1000, 63, 75));
	}
	
	@Test
	public void testShouldNotKeepTraining_Converged(){
		ExperimentBase o = new ExperimentBase();
		Assert.assertFalse(o.shouldKeepTraining(1, 0, 1000, 75, 75 + (ExperimentBase.TRAINING_CONSTANT/2)));
	}
	
	@Test
	public void testShouldNotKeepTraining_MaxEpochs(){
		ExperimentBase o = new ExperimentBase();
		Assert.assertFalse(o.shouldKeepTraining(10, 0, 10, 65, 75 + ExperimentBase.TRAINING_CONSTANT));
	}
	
	@Test
	public void testShouldKeepTraining_MinEpochs(){
		ExperimentBase o = new ExperimentBase();
		Assert.assertTrue(o.shouldKeepTraining(10, 15, 20, 75, 75));
	}
	
	/**
	 * Mock PerceptronTrainer class to make the testing easier 
	 */
	class PerceptronTrainerMock extends PerceptronTrainer {
		@Override
		public EvaluateResult evaluateOnDataRow(int[] data, int targetClass) {
			//Return true if the first data element matches the target class
			if(data[0] == targetClass){
				return new EvaluateResult(true,0);
			}
			else{
				return new EvaluateResult(false,0);
			}
		}
	}
}
