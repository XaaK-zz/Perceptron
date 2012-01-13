package net.greenvoss.experiment;

import java.util.List;

import net.greenvoss.PerceptronTrainer;

public class ExperimentOne extends ExperimentBase {

	public void execute(String trainingDataPath, String testingDataPath, int digit) {
		//create trainer 
		PerceptronTrainer trainer = new PerceptronTrainer();
		trainer.init(64, 0.2f);
		
		//Load training data
		List<String> fileData = this.getFileContents(trainingDataPath);
		
		for(String fileRow : fileData) {
			String[] rowData = this.getRowContents(fileRow);
			if(rowData.length == 64) {
				
			}
		}
		
	}

}
