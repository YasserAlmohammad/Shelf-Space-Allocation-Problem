/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssap;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Chart extends ApplicationFrame
{
   public DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
   public Chart( String applicationTitle , String chartTitle )
   {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
              chartTitle,
            "solution number","objective function value",
            dataset,
            PlotOrientation.VERTICAL,
            true,true,false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      CategoryPlot plot=(CategoryPlot)lineChart.getPlot();
     
      setContentPane( chartPanel );
      this.pack( );
      RefineryUtilities.centerFrameOnScreen( this );
   }
   public static void main( String[ ] args ) 
   {
      Chart chart = new Chart(
      "Shelf Space Allocation Problem" ,
      "Objective Function (Maximize profit)");

      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
   }
}