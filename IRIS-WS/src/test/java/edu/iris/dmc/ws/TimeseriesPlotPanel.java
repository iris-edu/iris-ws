package edu.iris.dmc.ws;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class TimeseriesPlotPanel extends JPanel {

	private BufferedImage image;

	public TimeseriesPlotPanel(BufferedImage image) {
		this.image = image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this); // see javadoc for more info on the
										// parameters
	}
}
