package de.infoscout.betterhome.view.menu.graph;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.ValueDependentColor;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.components.AorS_Object;
import de.infoscout.betterhome.model.device.components.StatisticItem;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.utils.MathUtil;
import de.infoscout.betterhome.view.utils.Utilities;

public class ReadDrawStatistics extends AsyncTask<Void, Void, Boolean> {
	private Calendar from;
	private Calendar to;
	private AorS_Object actSens;
	
	private Toast toast = null;

	private int numVerLabel = 18;
	private int numHorLabel = 7;

	private FragmentActivity activity = null;
	private LinearLayout graphViewLayout = null;

	private boolean lineGraph = true;
	private GraphView graphView = null;
	private ProgressDialog dialog = null;

	private GraphViewData[] graphData = null;
	private ArrayList<StatisticItem> stats;
	private int maxPoints = 100;
	private float sizePoints = 7f;
	private int strokeWidth = 8;
	private boolean cutStats = true;
	private boolean shortLabels = false;
	private boolean scalable = true;
	private boolean drawBigValue = false;

	public ReadDrawStatistics(AorS_Object actSens, Calendar from, Calendar to,
			FragmentActivity act, LinearLayout graph) {
		this.from = from;
		this.to = to;
		this.actSens = actSens;
		this.activity = act;
		this.graphViewLayout = graph;
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		boolean res = false;
		if (actSens instanceof Actuator) {
			res = ((Actuator) actSens).updateWithStats(from, to, activity);
		} else if (actSens instanceof Sensor) {
			res = ((Sensor) actSens).updateWithStats(from, to, activity);
		}

		return res;
	}

	private ArrayList<StatisticItem> cutStatsDownToMax(
			ArrayList<StatisticItem> stats) {

		int multiplier = stats.size() / maxPoints;

		ArrayList<StatisticItem> newList = new ArrayList<StatisticItem>();
		if (multiplier > 1) {
			for (int i = 0; i < stats.size()-1; i++) {
				if (i % multiplier == 0) {
					// jedes n-te Element nur noch Teil der Liste
					newList.add(stats.get(i));
				}
			}
			// letzte Wert auf jeden fall Teil der Liste!
			newList.add(stats.get(stats.size()-1));
		} else {
			newList = stats;
		}

		return newList;
	}
	
	private ArrayList<StatisticItem> removeValue(
			ArrayList<StatisticItem> stats, double value) {
		
		ArrayList<StatisticItem> newList = new ArrayList<StatisticItem>();
		
		for (int i=0; i<stats.size(); i++) {
			if (value != stats.get(i).getValue()) {
				newList.add(stats.get(i));
			}
		}
		
		return newList;
	}
	
	private ArrayList<StatisticItem> removeOutliers(
			ArrayList<StatisticItem> stats) {

		List<Double> values = new ArrayList<Double>();
		for (int i=0; i<stats.size(); i++) {
			values.add(stats.get(i).getValue());
		}
		
		MathUtil mathUtil = new MathUtil();
		Double outlier = mathUtil.getOutlier(values);
		
		int countOutlier = 0;
		while (outlier != null) {
			System.out.println("outlier = "+outlier);
			countOutlier++;
			
			boolean removed = values.remove(outlier);
			
			if (outlier != 100.0 && outlier != 0.0) {
				stats = removeValue(stats, outlier);
			}
			
			if (removed) {
				outlier = mathUtil.getOutlier(values);
			} else {
				// letzte outlier wurde nicht aus values entfernt -> breakout
				System.out.println("outlier not removed... "+outlier);
				outlier = null;
			}
		}
		
		if (countOutlier > 0)
			toast("Ausreißer entfernt: "+countOutlier);
		
		return stats;
	}

	@Override
	protected void onPostExecute(Boolean read) {
		super.onPostExecute(read);

		if (read && graphViewLayout != null) {
			stats = actSens.getStatistics();

			if (stats.size() > 0) {
			
				if (cutStats) {
					stats = cutStatsDownToMax(stats);
				}
				
				if (RuntimeStorage.isGraphOutlierDetection()) {
					stats = removeOutliers(stats);
				}
	
				graphData = new GraphViewData[stats.size()];
				for (int i = 0; i < stats.size(); i++) {
					GraphViewData d = new GraphViewData(i + 1, stats.get(i)
							.getValue());
					graphData[i] = d;
				}
	
				if (graphViewLayout.getChildCount() == 1
						&& graphViewLayout.getChildAt(0) instanceof GraphView) {
					graphView = (GraphView) graphViewLayout.getChildAt(0);
				} else if (graphViewLayout.getChildCount() == 2) {
					// Graphs mit delete button
					graphView = (GraphView) graphViewLayout.getChildAt(1);
				}
	
				if (graphView == null) {
					String title = "";
					if (actSens instanceof Actuator) {
						title = ((Actuator)actSens).getAppname();
					} else if (actSens instanceof Sensor) {
						title = ((Sensor)actSens).getAppname();
					}
					
					if (lineGraph) {
						graphView = new LineGraphView(activity // context
								, title // heading
						);
					} else {
						graphView = new BarGraphView(activity // context
								, title // heading
						);
					}
					
					graphView.setTitleSize(4);
					
					if (scalable) {
						graphView.setScrollable(true);
						graphView.setScalable(true);
					}
	
					if (graphView instanceof LineGraphView) {
						((LineGraphView) graphView).setDrawBackground(true);
						((LineGraphView) graphView).setDrawDataPoints(true);
						((LineGraphView) graphView).setDataPointsRadius(sizePoints);
						((LineGraphView) graphView).setStrokeWidth(strokeWidth);
					}
	
					graphView.setBackgroundColor(Color.argb(65, 186, 213, 240));
					graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
	
					graphView.getGraphViewStyle().setNumHorizontalLabels(
							numHorLabel);
					graphView.getGraphViewStyle().setNumVerticalLabels(numVerLabel);
	
					graphView.getGraphViewStyle().setTextSize(
							activity.getResources().getDimension(
									R.dimen.text_size_graph));
	
					graphView.setOnTouchListener(new MyTouchListener());
	
					graphViewLayout.addView(graphView);
				}
	
				graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
					@Override
					public String formatLabel(double value, boolean isValueX) {
						if (isValueX && value > 0 && value <= stats.size()) {
							Date date = ((StatisticItem) stats
									.get(((int) (value - 1)))).getDate();
							SimpleDateFormat formatter = null;
							if (!shortLabels) {
								formatter = new SimpleDateFormat("dd.MM.HH:mm",
										Locale.getDefault());
							} else {
								formatter = new SimpleDateFormat("HH:mm", Locale
										.getDefault());
							}
	
							return formatter.format(date);
						} else {
							// y-value
							if (actSens.getUnit().equals("boolean")) {
	
								if (value == 100.0) {
									return "100";
								} else if (value == 0.0) {
									return "0";
								} else {
									return "0";
								}
							} else {
								return (String.valueOf(((double) Math
										.round(value * 10)) / 10) + " " + actSens
										.getUnit());
							}
						}
					}
				});
	
				double lastValue = stats.get(stats.size() - 1).getValue();
				if (drawBigValue) drawBigValue(lastValue);
	
				graphView.removeAllSeries();
				
				GraphViewSeriesStyle seriesStyleTemperature = new GraphViewSeriesStyle();
				seriesStyleTemperature.setValueDependentColor(new ValueDependentColor() {
			        @Override
			        public int get(GraphViewDataInterface data) {
			        	int color = 0;
			        	
			        	if (data.getY() < 19) {
			        		color = activity.getResources().getColor(R.color.wetness_blue);
			        	} else if (data.getY() >= 19 && data.getY() < 21) {
			        		color = activity.getResources().getColor(R.color.wetness_green);
			        	} else if (data.getY() >= 21 && data.getY() <= 24) {
			        		color = activity.getResources().getColor(R.color.temp_orange);
			        	} else if (data.getY() > 24) {
			        		color = Color.RED;
			        	}
			        	return color;
			        }
			    });
				
				GraphViewSeriesStyle seriesStyleWetness = new GraphViewSeriesStyle();
				seriesStyleWetness.setValueDependentColor(new ValueDependentColor() {
			        @Override
			        public int get(GraphViewDataInterface data) {
			        	int color = 0;
			        	
			        	if (data.getY() < 40) {
			        		color = activity.getResources().getColor(R.color.wetness_red);
			        	} else if (data.getY() >= 40 && data.getY() <= 60) {
			        		color = activity.getResources().getColor(R.color.wetness_green);
			        	} else if (data.getY() > 60) {
			        		color = activity.getResources().getColor(R.color.wetness_blue);
			        	}
			        	return color;
			        }
			    });
				
				GraphViewSeries series1 = null;
				
				if (actSens.getType().equals("temperature")) {
					series1 = new GraphViewSeries("", seriesStyleTemperature, graphData);
				} else if (actSens.getType().equals("hygrometer")) {
					series1 = new GraphViewSeries("", seriesStyleWetness, graphData);
				} else {
					series1 = new GraphViewSeries(graphData);
				}
				
				graphView.addSeries(series1); // data
	
				graphView.refreshDrawableState();
				graphView.postInvalidate();
	
			} else {
				// Stats sind nach Abholen leer
				toast(activity.getResources().getString(R.string.no_stats) + "(name:"+actSens.getName()+";from:"+from.getTimeInMillis()+":to:"+to.getTimeInMillis()+")");
			}
			
			if (dialog != null) {
				dialog.cancel();
			}

		} else {
			XsError.printError(activity);
		}

		return;
	}

	private void drawBigValue(double lastValue) {
		String type = actSens.getType();
		String unit = actSens.getUnit();
		
		String out = "";
		if (actSens instanceof Actuator) {
			out = Utilities.getWertForActuator(unit, lastValue, type, activity);
		} else if (actSens instanceof Sensor) {
			out = Utilities.getWertForSensor(unit, lastValue, type, activity);
		}
		
		graphView.setShowBigValue(true);
		graphView.setBigValue(out);
		graphView.setBigValueShadowColor(activity.getResources()
				.getColor(R.color.infoscout_gray));
		graphView.setBigValueTextColor(activity.getResources().getColor(
				R.color.infoscout_blue));
	}

	private final class MyTouchListener implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
						view);
				view.startDrag(data, shadowBuilder, view, 0);
				view.setVisibility(View.INVISIBLE);
				return true;
			} else {
				return false;
			}
		}
	}

	public Calendar getFrom() {
		return from;
	}

	public void setFrom(Calendar from) {
		this.from = from;
	}

	public Calendar getTo() {
		return to;
	}

	public void setTo(Calendar to) {
		this.to = to;
	}

	public AorS_Object getActSens() {
		return actSens;
	}

	public void setActSens(AorS_Object actSens) {
		this.actSens = actSens;
	}

	public int getNumVerLabel() {
		return numVerLabel;
	}

	public void setNumVerLabel(int numVerLabel) {
		this.numVerLabel = numVerLabel;
	}

	public int getNumHorLabel() {
		return numHorLabel;
	}

	public void setNumHorLabel(int numHorLabel) {
		this.numHorLabel = numHorLabel;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	public float getSizePoints() {
		return sizePoints;
	}

	public void setSizePoints(float sizePoints) {
		this.sizePoints = sizePoints;
	}

	public boolean isCutStats() {
		return cutStats;
	}

	public void setCutStats(boolean cutStats) {
		this.cutStats = cutStats;
	}

	public FragmentActivity getActivity() {
		return activity;
	}

	public void setActivity(FragmentActivity activity) {
		this.activity = activity;
	}

	public ProgressDialog getDialog() {
		return dialog;
	}

	public void setDialog(ProgressDialog dialog) {
		this.dialog = dialog;
	}

	public boolean isShortLabels() {
		return shortLabels;
	}

	public void setShortLabels(boolean shortLabels) {
		this.shortLabels = shortLabels;
	}

	public boolean isScalable() {
		return scalable;
	}

	public void setScalable(boolean scalable) {
		this.scalable = scalable;
	}

	public boolean isDrawBigValue() {
		return drawBigValue;
	}

	public void setDrawBigValue(boolean drawBigValue) {
		this.drawBigValue = drawBigValue;
	}
	
	private void toast(final String msg) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(activity.getApplicationContext(), "",
							Toast.LENGTH_LONG);
				}
				toast.setText(msg);
				toast.show();
			}
		});
	}
	
}
