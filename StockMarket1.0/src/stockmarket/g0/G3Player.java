package stockmarket.g0;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import stockmarket.sim.EconomicIndicator;
import stockmarket.sim.Player;
import stockmarket.sim.Portfolio;
import stockmarket.sim.Stock;
import stockmarket.sim.Trade;

public class G3Player extends Player {

	public String getName() {
		return "Sophisticated Regression Player";
	}
	
	@Override
	public void learn(ArrayList<EconomicIndicator> indicators,
			ArrayList<Stock> stocks) {
		
		HashMap <String, OLSMultipleLinearRegression> regressions = new HashMap <String, OLSMultipleLinearRegression> ();
		
		for(int k = 0; k < stocks.size(); k ++) {
			OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression ();
			OLSMultipleLinearRegression bestRegression = new OLSMultipleLinearRegression ();
			double maxR = Double.MIN_VALUE;
			
			for(int i = 1; i < 32; i ++) {
				String currBinary = Integer.toBinaryString(i);
				
				//Count number of regressors in current attempt
				int regressorCount = 0;
				for(int j = 0; j < 5; j ++ ) {
					if(currBinary.charAt(j) == '1') { regressorCount ++; }
				}
				
				double[] y = new double[25];
				double[][] x = new double[25] [5];
				
				//Zero initialize
				for(int a = 0; a < 25; a ++) {
					for(int b = 0; b < 5; b++) {
						x[a][b] = 0;
					}
				}
				
				//Populate data
				for(int j = 0; j < 25; j ++) {
					y[j] = stocks.get(k).getPriceAtRound(j);
					for(int a = 0; a < 5; a ++) {
						if(currBinary.charAt(a) == '1') {
							x[j][a] = indicators.get(a).getValueAtRound(j);
						}
					}
				}
				
				//Update regression
				regression.newSampleData(y, x);

				//Since we're using 0s to designate inactive regressors, we have to manually compute adjustment
				double adjR = regression.calculateRSquared() - ((1-regression.calculateRSquared()) * (regressorCount / (5 - 1 - regressorCount)));
				if(adjR > maxR) {
					maxR = adjR;
					bestRegression.newSampleData(y, x);
				}
			}			
			regressions.put(stocks.get(k).getName(), bestRegression);
		}

	}
	
	/* TODO
	 * 1) Kelly ratio?
	 * 2) Diversify portfolio by measuring historical correlation/forecasted correlation
	 * 3) Try ARIMA + GARCH fit
	 * 4) Only sell when made a gain
	 */

	@Override
	public ArrayList<Trade> placeTrade(int currentRound,
			ArrayList<EconomicIndicator> indicators, ArrayList<Stock> stocks,
			Portfolio porfolioCopy) {
		
		//If we forecast price going up by more than transaction cost, buy
		
		//Potentially implement kelly ratio
				
		//If last round, liquidate that shit
		
		return null;
	}

	
}
