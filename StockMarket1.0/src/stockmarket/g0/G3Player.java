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
				
				/*int regressorCount = 0;
				for(int j = 0; j < 5; j ++ ) {
					if(currBinary.charAt(j) == '1') { regressorCount ++; }
				}*/
				
				double[] y = new double[25];
				double[][] x = new double[25] [5];
				
				for(int a = 0; a < 25; a ++) {
					for(int b = 0; b < 5; b++) {
						x[a][b] = 0;
					}
				}
				for(int j = 0; j < 25; j ++) {
					y[j] = stocks.get(k).getPriceAtRound(j);
					for(int a = 0; a < 5; a ++) {
						if(currBinary.charAt(a) == '1') {
							x[j][a] = indicators.get(a).getValueAtRound(j);
						}
					}
				}
				
				regression.newSampleData(y, x);
				if(regression.calculateAdjustedRSquared() > maxR) {
					maxR = regression.calculateAdjustedRSquared();
					bestRegression.newSampleData(y, x);
				}
			}			
			regressions.put(stocks.get(k).getName(), bestRegression);
		}

	}

	@Override
	public ArrayList<Trade> placeTrade(int currentRound,
			ArrayList<EconomicIndicator> indicators, ArrayList<Stock> stocks,
			Portfolio porfolioCopy) {
		// TODO Auto-generated method stub
		return null;
	}

}
