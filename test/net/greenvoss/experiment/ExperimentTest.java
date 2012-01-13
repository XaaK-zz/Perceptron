package net.greenvoss.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import junit.framework.Assert;

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
		String[] rowData = experimentBase.getRowContents("test1,test2,test3,test4");
		
		Assert.assertEquals("Did not parse row correctly.", 4, rowData.length);
		Assert.assertEquals("Incorrect data 0.", "test1", rowData[0]);
		Assert.assertEquals("Incorrect data 1.", "test2", rowData[1]);
		Assert.assertEquals("Incorrect data 2.", "test3", rowData[2]);
		Assert.assertEquals("Incorrect data 3.", "test4", rowData[3]);
		
	}
	
}
