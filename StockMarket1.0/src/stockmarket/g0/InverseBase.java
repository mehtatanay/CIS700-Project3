package stockmarket.g0;
import java.util.ArrayList;

import stockmarket.sim.EconomicIndicator;
import stockmarket.sim.Stock;
import Jama.Matrix;


public class InverseBase {

	public ArrayList<Stock> stocks;
	public ArrayList<EconomicIndicator> indicators;
	
	
	
	
	public double solveForRounds(int stockid, int rounds)
	{
		
		
	double [][] values = new double[5][5];
		
		
		for (int j = 0; j<indicators.size(); j++){
			double [] valins = new double[5];
			for(int i = 0 ; i<5; i++)
			{
				
				valins[i] = ((EconomicIndicator)indicators.get(j)).getValueAtRound(i+rounds);
				
				
			}
			values[j]= valins;
		}
		double stvals [][] = new double[1][5];
		for (int q=0; q<1; q++){
			double[] valins = new double[5]; 
			for(int i=0; i<5; i++)
			{
			
			valins[i]=stocks.get(stockid).getPriceAtRound(rounds+i);
			}
			stvals[q]=valins;
		}
		
        
		
        Matrix a = new Matrix(values);
        Matrix v = new Matrix(stvals);
        Matrix ai = a.inverse();
       Matrix coef = v.times(ai);
       double res[][] = coef.getArray();
       double newp = 0;
     for(int i = 0; i<1; i++)
     {
    	 
    	 for(int j=0; j<5; j++)
    	 {
    		 
    		newp=newp+((EconomicIndicator)indicators.get(j)).getValueAtRound(5+rounds)*res[i][j];
    		 
    	 }
    	 
    	 
    	
    	 
     }
     return newp;
		
	}
}
