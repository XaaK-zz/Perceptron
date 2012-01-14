package net.greenvoss.experiment;

import java.util.ArrayList;
import java.util.List;

import net.greenvoss.PerceptronTrainer;

public class ExperimentOne extends ExperimentBase {

	List<PerceptronTrainer> trainerList;
	
	public void execute(String trainingDataPath, String testingDataPath, int digit) {
		//create trainers 
		trainerList = this.getPerceptronTrainers(10, 64, 0.2f);
		
		//Load training data
		List<String> fileData = this.getFileContents(trainingDataPath);
		
		//train on the data
		int epoch = 1;
		double accuracy = 0.0;
		
		while(accuracy < 90.0) {
			for(String fileRow : fileData) {
				int[] rowData = this.getRowContents(fileRow);
				//check the last element in the row
				if(rowData[rowData.length-1] == digit) {
					//Positive example (i.e. same digit)
					for(int x=0;x<trainerList.size();x++) {
						trainerList.get(x).trainOnDataRow(rowData, 1);
					}
				}
				else {
					//Negative example (i.e. some other digit)
					trainerList.get(rowData[rowData.length-1]).trainOnDataRow(rowData, -1);
				}
			}
			
		}
	}

}
