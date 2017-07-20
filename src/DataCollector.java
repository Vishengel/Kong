import java.io.IOException;

public class DataCollector {
	
	public DataCollector() throws IOException {

	}
	
	public void chooseParameters() throws IOException, InterruptedException {
		//Parameter values to be tested for each parameter
		double[] learningRatesSigmoid = {0.0005, 0.001, 0.005, 0.01, 0.05};
		double[] learningRatesRelu = {0.00005, 0.0001, 0.0005, 0.001, 0.005};
		double[] temperatures = {0.5, 1.0, 2.0, 4.0, 8.0};
		double[] hiddenNodes = {25, 50, 100, 150, 200};
		double[] hiddenLayers = {1, 2, 3};
		double[] hiddenLayersReLU = {1, 2, 3};
		
		for (double par : learningRatesSigmoid) {;
			runTrials("learningRateSigmoid", par);
		}
		
		for (double par : temperatures) {
			runTrials("temperature", par);
		}
		
		for (double par : hiddenNodes) {
			runTrials("hiddenNodes", par);
		}
		
		for (double par : hiddenLayers) {
			runTrials("hiddenLayers", par);
		}
		
		for (double par : learningRatesRelu) {
			runTrials("learningRateRelu", par);
		}
		
		for (double par : hiddenLayersReLU) {
			runTrials("hiddenLayersRelu", par);
		}
		
		//Run trials with the optimal parameter values as defined in the constants file
		runTrials("bestParameters", 0);
		
		System.out.println("Done");
		System.exit(0);
	}
	
	public void runTrials(String parName, double parValue) throws IOException, InterruptedException {
		for (int run=0; run<constants.RUNS_PER_PARAMETER; run++) {
			GameController gc = new GameController();
			gc.getGameModel().setRun(run);
			gc.getGameModel().setParName(parName);
			gc.getGameModel().setParValue(parValue);
		    gc.start();
		    gc = null;
		}
	}
	
}
