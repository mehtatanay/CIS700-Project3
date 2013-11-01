/**
 * 
 */
package stockmarket.g0;

import java.util.ArrayList;
import java.util.Random;

import stockmarket.sim.EconomicIndicator;
import stockmarket.sim.Portfolio;
import stockmarket.sim.Stock;
import stockmarket.sim.Trade;
import Jama.Matrix;

/**
 * @author Anne
 *
 */
public class ConstantPlayer extends stockmarket.sim.Player {
	private InverseBase inv;

	public ConstantPlayer(){
		name = "Constant Player";
			}
	
	@Override
	public void learn(ArrayList<EconomicIndicator> indicators,
			ArrayList<Stock> stocks) {
		System.out.println("Indicators");
		for (EconomicIndicator indicator : indicators){
			System.out.println(indicator);
		}
		System.out.println("Stocks");
		for (Stock stock : stocks){
			System.out.println(stock);
		}
inv = new InverseBase();
inv.stocks = stocks;
inv.indicators = indicators;
		
	}

	/*
	@Override
	public ArrayList<Trade> placeTrade(int currentRound,
			ArrayList<EconomicIndicator> indicators, ArrayList<Stock> stocks, Portfolio portfolioCopy) {
		System.out.println("\nRound " + currentRound + "\n" + portfolioCopy);
		Stock stockToTrade = stocks.get(Math.abs(random.nextInt()%10));
		int tradeAmount = Math.abs(random.nextInt()%100);
		int type = Trade.BUY;
		if(Math.abs(random.nextInt() %2) > 0){
			type = Trade.SELL;
		}
		ArrayList<Trade> trades = new ArrayList<Trade>();
		trades.add(new Trade(type, stockToTrade, tradeAmount));
		System.out.println(trades.get(0));
		return trades;
	}
	*/
	@Override
	

	
	
	public ArrayList<Trade> placeTrade(int currentRound,
			ArrayList<EconomicIndicator> indicators, ArrayList<Stock> stocks, Portfolio portfolioCopy) {
		System.out.println("\nRound " + currentRound + "\n" + portfolioCopy);
		/*
		System.out.println("Indicators");
		for (EconomicIndicator indicator : indicators){
			System.out.println(indicator);
		}
		System.out.println("Stocks");
		for (Stock stock : stocks){
			System.out.println(stock);
		}
		*/
		
		
		ArrayList<Trade> trades = new ArrayList<Trade>();
		int type;
		int tradeAmount;
		Stock stockToTrade;
		Object[] myStocks = portfolioCopy.getAllStocks().toArray();
	
		
		inv.stocks = stocks;
		inv.indicators = indicators;
	
		for(int j =0; j<10; j++)
		{
			
			double pr = inv.solveForRounds(j,currentRound-5);
			double curr = stocks.get(j).getPriceAtRound(currentRound-1);
			if(pr>curr+2)
			{
				type = Trade.BUY;
				int amnt = (int)(pr-(curr+2));
				trades.add(new Trade(Trade.BUY, stocks.get(j), 50*amnt));

			
			}
			else if(pr<curr)
			{
				int pickedStock = j;
				if(myStocks.length>j)
				{
					
			
				Stock stockToTradeC = (Stock) myStocks[j];
	
					//System.out.println("hey");
					stockToTrade = stockToTradeC;
					int sharesOwned = portfolioCopy.getSharesOwned(stockToTrade);
					trades.add(new Trade(Trade.SELL, stockToTrade, sharesOwned));
			
			
				
				
				}
			}
			
		}
		
	
		return trades;
	}

	
}
