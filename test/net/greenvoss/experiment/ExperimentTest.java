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
		
		Assert.assertEquals("Invalid Accuracy", .5f,metrics.getAccuracy());
	}
	
	@Test
	public void shouldCalculateAccuracyCorrectly(){
		List<PerceptronTrainer> list = new ArrayList<PerceptronTrainer>();
		//first create list of trainers that will return the data we want
		list.add(new PerceptronTrainerMock());
		list.add(new PerceptronTrainerMock());
		list.add(new PerceptronTrainerMock());
		
		//now construct list of data to get accuracy measurements on
		//	This list combined with the mock Perceptron trainer makes this test work
		String[] fileData = {
			"1,0,0",	//correctly identified as zero (because the first element is the correct class (1 vs -1))
			"0,0,0",	//incorrectly identified
			"1,1,0",
			"1,1,0",
			"-1,2,1",	//The first four lines are evaluated for each of the trainers (since digit 0 is the selected digit)
			"0,0,1",
			"0,2,1",
			"0,2,1",
			"-1,2,2",
			"-1,0,2",
			"-1,2,2",
			"0,2,2",
		};
		
		ExperimentOne experimentOne = new ExperimentOne();
		ExperimentMetrics metrics = experimentOne.calculateMetrics(list, fileData, 0);
		
		Assert.assertNotNull("Metrics should not be null",metrics);
		Assert.assertEquals("Invalid TruePositives",6,metrics.TruePositives);
		Assert.assertEquals("Invalid TrueNegatives",4,metrics.TrueNegatives);
		Assert.assertEquals("Invalid FalsePositives",4,metrics.FalsePositives);
		Assert.assertEquals("Invalid FalseNegatives",2,metrics.FalseNegatives);
		Assert.assertEquals("Invalid Accuracy",(float)10/(float)16,metrics.getAccuracy());
		
	}
	
	//@Test
	public void simpleExperimentOneTest() {
		ExperimentOne experimentOne = new ExperimentOne() {
			@Override
			List<PerceptronTrainer> getPerceptronTrainers(int numberOfTrainers,
					int inputSize, float learningRate) {
				//Just return two trainers
				PerceptronTrainer trainer = new PerceptronTrainer();
				//Just set two input nodes
				trainer.init(2, .2f);
				List<PerceptronTrainer> list = new ArrayList<PerceptronTrainer>();
				list.add(trainer);
				//second one
				trainer = new PerceptronTrainer();
				trainer.init(2, .2f);
				list.add(trainer);
				return list;
			}
			
			@Override
			List<String> getFileContents(String file) {
				//return a simple file for testing (only 2 nodes and a class)
				List<String> list = new ArrayList<String>();
				list.add("0,0,1");
				list.add("1,0,1");
				list.add("0,1,1");
				list.add("1,1,1");
				list.add("2,2,0");
				list.add("2,0,0");
				list.add("0,2,0");
				list.add("2,2,0");
				
				return list;
			}
		};
		
		//execute the experiment
		//experimentOne.execute("", "", 1);
		
		
	}
	
	/**
	 * Mock PerceptronTrainer class to make the testing easier 
	 */
	class PerceptronTrainerMock extends PerceptronTrainer {
		@Override
		public boolean evaluateOnDataRow(int[] data, int targetClass) {
			//Return true if the first data element matches the target class
			if(data[0] == targetClass){
				return true;
			}
			else{
				return false;
			}
		}
	}
}
