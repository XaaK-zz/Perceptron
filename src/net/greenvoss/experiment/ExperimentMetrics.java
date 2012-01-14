package net.greenvoss.experiment;

/**
 * Class for collecting the metrics of a training run
 *
 */
public class ExperimentMetrics {
	public int TruePositives = 0;
	public int TrueNegatives = 0;
	public int FalsePositives = 0;
	public int FalseNegatives = 0;
	
	public float getAccuracy() {
		return (float)(TruePositives + TrueNegatives) / (float)(FalsePositives + FalseNegatives + TruePositives + TrueNegatives);
	}
}