package edu.iris.dmc.service.response;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import edu.iris.dmc.criteria.Strings;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.Type;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.iris.dmc.timeseries.DecompressedDataRecord;
import edu.iris.dmc.timeseries.Segment;
import edu.iris.dmc.timeseries.Timeseries;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class TimeseriesBufferedImage extends BufferedImage {

	private Logger logger = Logger.getLogger(TimeseriesBufferedImage.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = -516439987098618997L;

	private int padding = 25;
	private int labelPadding = 25;
	private int pointWidth = 4;
	private Color lineColor = new Color(44, 102, 230, 180);
	private Color pointColor = new Color(100, 100, 100, 180);
	private Color gridColor = new Color(200, 200, 200, 200);
	private int numberYDivisions = 10;
	private boolean showRecordLine;

	private Color RECORD_ENDING = new Color(189, 252, 252);
	private Color SEGMENT_ENDING = new Color(250, 108, 0);

	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

	private Map<String, Timeseries> map = new HashMap<>();
	private Timeseries timeseries;

	private TimeseriesBufferedImage(Timeseries timeseries, int width, int height) {
		super(width, height, BufferedImage.TYPE_BYTE_INDEXED);
		this.timeseries = timeseries;
	}

	public static TimeseriesBufferedImage from(String networkCode, String stationCode, String location,
			String channelCode, int width, int height) {
		return from(Timeseries.from(networkCode, stationCode, location, channelCode), width, height);
	}

	public static TimeseriesBufferedImage from(Timeseries timeseries, int width, int height) {
		return new TimeseriesBufferedImage(timeseries, width, height);
	}

	public void add(String location, Timestamp startTimestamp, DataRecord record)
			throws UnsupportedCompressionType, CodecException, SeedFormatException {
		this.add(location, startTimestamp, record, false);
	}

	public void add(String location, Timestamp startTimestamp, DataRecord record, boolean reduce)
			throws UnsupportedCompressionType, CodecException, SeedFormatException {

		Timeseries ts = map.get(location);
		if(ts==null){
			
		}
		this.timeseries.add(record, reduce);
	}

	public void showRecordLine(boolean showRecordLine) {
		this.showRecordLine = showRecordLine;
	}

	public void dispose() {
		this.getGraphics().dispose();
	}

	public void paintComponent() {

		this.currentTimeAxis = 0;
		Graphics2D g2 = (Graphics2D) this.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		this.title(g2);
		this.paintYAxis(g2);
		paintData(g2);

		this.footNote(g2);
		this.timeseries.getSegments().clear();
	}

	private void paintScale(Graphics2D g2, double scale) {
		// g2.drawLine(x1, y1, x2, y2);
	}

	private void paintYAxis(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		// create hatch marks and grid lines for y axis.
		for (int i = 0; i < numberYDivisions + 1; i++) {
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = getHeight()
					- ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if (this.timeseries.getTotalNumberOfSamples() > 0) {
				g2.setColor(gridColor);
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
				g2.setColor(Color.BLACK);
				String yLabel = ((int) ((this.timeseries.getMin()
						+ (this.timeseries.getMax() - this.timeseries.getMin()) * ((i * 1.0) / numberYDivisions))
						* 100)) / 100.0 + "";
				FontMetrics metrics = g2.getFontMetrics();
				int labelWidth = metrics.stringWidth(yLabel);
				g2.setColor(Color.lightGray);
				if (i > 0 && i < numberYDivisions) {
					g2.drawString(yLabel, x0, y0 + (metrics.getHeight() / 2) - 10);
				}
			}
			g2.drawLine(x0, y0, x1, y1);
		}

		// paintXaxix(g2);
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding,
				getHeight() - padding - labelPadding);
	}

	private void paintData(Graphics2D g2) {
		Stroke oldStroke = g2.getStroke();
		g2.setColor(lineColor);
		// g2.setStroke(GRAPH_STROKE);

		int offset = 0;

		float yScale = ((float) getHeight() - 2 * padding - labelPadding)
				/ (this.timeseries.getMax() - this.timeseries.getMin());
		double x1 = 0;
		double y1 = 0;
		float xScale = ((float) this.getWidth()) / this.timeseries.getActualNumberOfSamples();
		paintScale(g2, xScale);
		for (Segment s : this.timeseries.getSegments()) {
			this.timeAxix(g2, s.getStartTimeAsLong(), x1);
			for (DecompressedDataRecord d : s.getData()) {
				float[] array = d.getRecord().getData();
				if (this.showRecordLine) {
					float x = ((offset + array.length) * xScale) + padding + labelPadding;
					printRecordLine(g2, x, getHeight() - padding - labelPadding);
					hoursAxix(g2, d.getEndTime(), x1);
				}

				for (int i = 0; i < array.length - 1; i++) {
					if (offset == 0) {
						x1 = ((offset * xScale) + padding + labelPadding);
						y1 = ((this.timeseries.getMax() - array[i]) * yScale + padding);
					}

					offset++;

					double x2 = ((offset * xScale) + padding + labelPadding);
					double y2 = ((this.timeseries.getMax() - array[i + 1]) * yScale + padding);

					g2.draw(new Line2D.Double(x1, y1, x2, y2));

					x1 = x2;
					y1 = y2;

				}
			}
			printSegmentLine(g2, x1, getHeight() - padding - labelPadding);
			g2.setColor(lineColor);
		}
	}

	private void printRecordLine(Graphics2D g2, double x, double height) {
		Color originalColor = g2.getColor();
		g2.setColor(RECORD_ENDING);
		g2.draw(new Line2D.Double(x, labelPadding, x, height));
		g2.setColor(originalColor);
	}

	private void printSegmentLine(Graphics2D g2, double x, int height) {
		g2.setColor(SEGMENT_ENDING);
		g2.draw(new Line2D.Double(x, labelPadding, x, height));
	}

	private void title(Graphics2D g2) {
		StringBuilder b = new StringBuilder(this.timeseries.getNetworkCode()).append("/")
				.append(this.timeseries.getStationCode()).append("/").append(this.timeseries.getLocation()).append(":")
				.append(this.timeseries.getChannelCode()).append(" [").append(this.timeseries.getDataQuality())
				.append("] SR:").append(this.timeseries.getSampleRate()).append(" #")
				.append(this.timeseries.getTotalNumberOfSamples()).append("/")
				.append(this.timeseries.getActualNumberOfSamples());
		g2.setColor(Color.BLACK);
		int start = padding + labelPadding;
		g2.drawString(b.toString(), start, 16);

	}

	private double currentTimeAxis = padding + labelPadding;

	private void hoursAxix(Graphics2D g2, long end, double x1) {

		if (x1 - currentTimeAxis < 30) {
			return;
		}
		Color originalColor = g2.getColor();
		Font originalFont = g2.getFont();
		Font newFont = originalFont.deriveFont(originalFont.getSize() * 0.8F);
		g2.setFont(newFont);
		g2.setColor(Color.BLACK);

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(end);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		String time = String.format("%02d", hour) + ":" + String.format("%02d", minute);

		this.currentTimeAxis = x1 + g2.getFontMetrics().stringWidth(time);
		g2.drawString(time, (int) x1, getHeight() - ((padding + labelPadding) - 10));
		g2.setFont(originalFont);
		g2.setColor(originalColor);
	}

	private void timeAxix(Graphics2D g2, long start, double x1) {
		Color originalColor = g2.getColor();
		Font originalFont = g2.getFont();
		Font newFont = originalFont.deriveFont(originalFont.getSize() * 0.8F);
		g2.setFont(newFont);
		g2.setColor(Color.BLACK);
		String time = Strings.format(start);

		g2.drawString(time, (int) x1 + padding + labelPadding, getHeight() - ((padding + labelPadding) - 10));
		this.currentTimeAxis += g2.getFontMetrics().stringWidth(time) + padding + labelPadding;
		g2.setFont(originalFont);
		g2.setColor(originalColor);
	}

	private void footNote(Graphics2D g2) {
		int height = getHeight() - 20;
		int start = padding + labelPadding;
		g2.setColor(RECORD_ENDING);
		g2.fillRect(start, height - 10, 10, 10);
		start = start + 10;
		g2.drawString("Record Ending", start + 5, height);
		start = start + g2.getFontMetrics().stringWidth("Record Ending");
		start = start + 5;
		g2.setColor(SEGMENT_ENDING);
		g2.fillRect(start + 10, height - 10, 10, 10);
		start = start + 20;
		g2.drawString("Segment Ending", start + 5, height);
	}

	public long getStartTime() {
		if (this.timeseries.getSegments() == null || this.timeseries.getSegments().isEmpty()) {
			return 0;
		}
		Segment s = this.timeseries.getSegments().get(0);
		return s.getStartTimeAsLong();
	}

	public long getEndTime() {
		if (this.timeseries.getSegments() == null || this.timeseries.getSegments().isEmpty()) {
			return 0;
		}
		Segment s = this.timeseries.getSegments().get(this.timeseries.getSegments().size() - 1);
		return s.getEndTimeAsLong();
	}

	public float getSampleRate() {
		if (this.timeseries.getSegments() == null || this.timeseries.getSegments().isEmpty()) {
			return -1;
		}
		Segment s = this.timeseries.getSegments().get(0);
		return s.getSamplerate();
	}

	public long getTotalNumberOfSamples() {
		return this.timeseries.getTotalNumberOfSamples();
	}

	public Type getSeriesType() {
		return this.timeseries.getType();
	}

	public float getMin() {
		return this.timeseries.getMin();
	}

	public float getMax() {
		return this.timeseries.getMax();
	}

	public String getNetworkCode() {
		return this.timeseries.getNetworkCode();
	}

	public String getStationCode() {
		return this.timeseries.getStationCode();
	}

	public String getLocationCode() {
		return this.timeseries.getLocation();
	}

	public String getChannelCode() {
		return this.timeseries.getChannelCode();
	}

	public char getDataQuality() {
		return this.timeseries.getDataQuality();
	}

	public List<Segment> getSegments() {
		return this.timeseries.getSegments();
	}

	@Override
	public String toString() {
		String text = "/" + this.getNetworkCode() + "/" + this.getStationCode() + "/" + this.getLocationCode() + ":"
				+ this.getChannelCode();
		return "TimeseriesPanel [" + text + " width=" + getWidth() + ", height=" + getHeight() + "]";
	}

}
