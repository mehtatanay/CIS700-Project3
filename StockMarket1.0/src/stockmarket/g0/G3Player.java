package stockmarket.g0;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import stockmarket.sim.EconomicIndicator;
import stockmarket.sim.Market;
import stockmarket.sim.Player;
import stockmarket.sim.Portfolio;
import stockmarket.sim.Stock;
import stockmarket.sim.Trade;

public class G3Player extends Player {
	
	HashMap <String, Object[]> regressions = new HashMap <String, Object[]> ();
	
	public String getName() {
		return "Regression Player";
	}
	
	@Override
	public void learn(ArrayList<EconomicIndicator> indicators,
			ArrayList<Stock> stocks) {
		
		
		for(int k = 0; k < stocks.size(); k ++) {
			OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression ();
			OLSMultipleLinearRegression bestRegression = new OLSMultipleLinearRegression ();
			double maxR = Double.MIN_VALUE;
			String best = "";
			
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
					best = currBinary;
				}
			}	
			
			//use an ArrayList to ensure passing by value rather than reference
			ArrayList<Double> betas = new ArrayList<Double> ();
			double[] oldBetas = bestRegression.estimateRegressionParameters();

			for(int i = 0; i < oldBetas.length; i++) {
				betas.add(i,oldBetas[i]);
			}

			for(int i = 1; i < best.length()+1; i++) {
				if(best.charAt(i-1) == '0') { betas.add(i,0.0); }
			}
			
			regressions.put(stocks.get(k).getName(), betas.toArray());
		}
	}
	
	/* TODO
	 * 1) Kelly ratio?
	 * 2) Try ARIMA + GARCH fit
	 * 3) Only sell when made a gain
	 * 4) Implement some sort of asset allocation model (diversify by historical/forecasted correlation?)
	 */

	@Override
	public ArrayList<Trade> placeTrade(int currentRound,
			ArrayList<EconomicIndicator> indicators, ArrayList<Stock> stocks,
			Portfolio portfolioCopy) {
		
		ArrayList<Trade> toBePlaced = new ArrayList<Trade> ();		
		ArrayList<Stock> toBeBought = new ArrayList<Stock> ();
		double pendingTransactionFees = 0;
		
		//If last round, liquidate that shit
		if(currentRound == Market.getMaxRounds()) {
			toBePlaced.clear();
			for(Stock s: portfolioCopy.getAllStocks()) {
				toBePlaced.add(new Trade(Trade.SELL, s, portfolioCopy.getSharesOwned(s)));
			}
			return toBePlaced;
		}
		
		//If we forecast price going up by more than 2*transaction cost, buy
		for(Stock s: stocks) {
			Object[] current = regressions.get(s.getName());
			double forecastedPrice = (Double) current[0]; //intercept
			for(int i = 1; i < current.length; i++) {
				forecastedPrice += ((Double) current[i]) * indicators.get(i-1).currentValue();
			}
			
			if((forecastedPrice - s.currentPrice()) > (Market.getTransactionFee()*2)) {
				toBeBought.add(s);
			}
		}
		
		for(Stock s: portfolioCopy.getAllStocks()) {
			if(!toBeBought.contains(s)) {
				toBePlaced.add(new Trade(Trade.SELL, s, portfolioCopy.getSharesOwned(s)));
				pendingTransactionFees -= Market.getTransactionFee();
			}
		}
		
		double availableCapital = portfolioCopy.getMonetaryValue() - pendingTransactionFees;
		availableCapital -= Market.getTransactionFee() * toBeBought.size();
		double capitalPerStock = availableCapital/toBeBought.size();
		
		for(Stock s: toBeBought) {
			if(!portfolioCopy.getAllStocks().contains(s)) {
				availableCapital -= Market.getTransactionFee();
				toBePlaced.add(new Trade(Trade.BUY, s, (int) Math.floor(capitalPerStock/s.currentPrice())));
			}
		}
		
		return toBePlaced;
	}

	
}
