package com.jgrue.vgpc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.jgrue.vgpc.data.Price;
import com.jgrue.vgpc.scrapers.GameScraper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class PriceChartActivity extends Activity {
	private static final String TAG = "PriceChartActivity";
	private String gameName;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pricechart);
		
		gameName = getIntent().getStringExtra("GAME_NAME");
		new PriceChartTask().execute(
				getIntent().getStringExtra("CHART_TYPE"),
				getIntent().getStringExtra("GAME_ALIAS"), 
				getIntent().getStringExtra("CONSOLE_ALIAS"));
	}
	
	private class PriceChartTask extends AsyncTask<String, Void, List<Price>> {
		@Override
		protected List<Price> doInBackground(String... arg0) {
			return GameScraper.getPriceHistory(arg0[0], arg0[1], arg0[2]);
		}
	
		@Override
		protected void onPostExecute(List<Price> priceList) {
			if(priceList.size() > 0) {
				List<Date[]> dates = new ArrayList<Date[]>();
				dates.add(new Date[priceList.size()]);
			    List<double[]> values = new ArrayList<double[]>();
			    values.add(new double[priceList.size()]);
			    
				for(int i = 0; i < priceList.size(); i++) {
					dates.get(0)[i] = priceList.get(i).getPriceDate();
					values.get(0)[i] = Math.round((double)priceList.get(i).getPrice() * 100) / 100.0d;
				}
				
			    int[] colors = new int[] { Color.GREEN };
			    PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
			    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
			    
			    setChartSettings(renderer, gameName, "Date", "Price", dates.get(0)[0].getTime(),
			        dates.get(0)[dates.get(0).length - 1].getTime(), 0, getYMax(values.get(0)), 
			        Color.GRAY, Color.LTGRAY);
			    renderer.setXLabels(5);
			    renderer.setYLabels(10);
			    int length = renderer.getSeriesRendererCount();
			    for (int i = 0; i < length; i++) {
			    	SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
			    	seriesRenderer.setDisplayChartValues(true);
			    	seriesRenderer.setChartValuesTextSize(seriesRenderer.getChartValuesTextSize() * 1.5f);
			    }
			    renderer.setZoomButtonsVisible(true);
			    renderer.setPanEnabled(true, false); 
			    renderer.setZoomEnabled(true, false);
			    renderer.setPanLimits(new double[] { dates.get(0)[0].getTime(),
				        dates.get(0)[dates.get(0).length - 1].getTime(), 0, 0 });  
			    
			    Intent intent = ChartFactory.getTimeChartIntent(PriceChartActivity.this, 
			    		buildDateDataset(new String[] { getIntent().getStringExtra("CHART_NAME") }, dates, values),
			    		renderer, "MM/dd/yyyy");
			    
			    startActivityForResult(intent, 0);
			    finish();
			} else {
				((TextView)PriceChartActivity.this.findViewById(R.id.textView1)).setText("Error loading prices.");
			}
		}

		private double getYMax(double[] ds) {
			double maxPrice = 0.0d;
			
			for(int i = 0; i < ds.length; i++) {
				if (ds[i] > maxPrice)
					maxPrice = ds[i];
			}

			for(int i = 0; i < 10; i++) {
				if (i == 0 && maxPrice < 1.0d) {
					return 1.0d;
				} else if(i == 1 && maxPrice < 10.0d) {
					return Math.ceil(maxPrice);
				} else if(i > 1 && maxPrice < Math.pow(10.0d, i)) {
					return Math.ceil(maxPrice / (5 * Math.pow(10.0d, i - 2))) * (5 * Math.pow(10.0d, i - 2));
				}
			}
			
			return maxPrice;
		}
	}
	
	/**
	 * Builds an XY multiple series renderer.
	 * 
	 * @param colors the series rendering colors
	 * @param styles the series point styles
	 * @return the XY multiple series renderers
	 */
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}
	  
	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setPointSize(5f);
	    renderer.setMargins(new int[] { 20, 30, 15, 20 });
	    int length = colors.length;
	    for (int i = 0; i < length; i++) {
	    	XYSeriesRenderer r = new XYSeriesRenderer();
	    	r.setColor(colors[i]);
	    	r.setPointStyle(styles[i]);
	    	r.setLineWidth(r.getLineWidth() * 2);
	    	renderer.addSeriesRenderer(r);
	    }
	}
	  
	/**
	 * Sets a few of the series renderer settings.
	 * 
	 * @param renderer the renderer to set the properties to
	 * @param title the chart title
	 * @param xTitle the title for the X axis
	 * @param yTitle the title for the Y axis
	 * @param xMin the minimum value on the X axis
	 * @param xMax the maximum value on the X axis
	 * @param yMin the minimum value on the Y axis
	 * @param yMax the maximum value on the Y axis
	 * @param axesColor the axes color
	 * @param labelsColor the labels color
	 */
	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
			String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	  }
	  
	protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues, 
			List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues, 
			List<double[]> yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}
	
	/**
	 * Builds an XY multiple time dataset using the provided values.
	 * 
	 * @param titles the series titles
	 * @param xValues the values for the X axis
	 * @param yValues the values for the Y axis
	 * @return the XY multiple time dataset
	 */
	protected XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
			List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			TimeSeries series = new TimeSeries(titles[i]);
			Date[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}
}
