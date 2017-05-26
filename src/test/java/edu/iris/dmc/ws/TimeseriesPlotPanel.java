package edu.iris.dmc.ws;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class TimeseriesPlotPanel extends JPanel {

	private BufferedImage image;

	private double zoom;

	public TimeseriesPlotPanel(BufferedImage image) {
		this.image = image;

		this.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				double temp = zoom - (notches * 0.2);
				// minimum zoom factor is 1.0
				temp = Math.max(temp, 1.0);
				if (temp != zoom) {
					zoom = temp;
					try {
						zoom(temp, temp);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	public void zoom(double xscale, double yscale) throws Exception {
		System.out.println("zooming");
		int width = (int) (this.image.getWidth() * xscale);
		int height = (int) (this.image.getHeight() * yscale);
		BufferedImage biScale = null;

		try {
			biScale = new BufferedImage(width, height, this.image.getType()); // TYPE_4BYTE_ABGR

			AffineTransform tx = new AffineTransform();
			tx.scale(xscale, yscale);

			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);

			biScale = op.filter(image, null);
			this.image = biScale;

			this.repaint();
		} catch (java.lang.OutOfMemoryError outOfMemoryError) {
			throw new Exception("Zoom not possible, OutOfMemoryError" + outOfMemoryError.getMessage());
		} catch (java.lang.IllegalArgumentException illegalArgumentException) {
			throw new Exception("Zoom not possible, IllegalArgumentException" + illegalArgumentException.getMessage());
		}

	}

	public static BufferedImage imageToBufferedImage(Image img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB); // TYPE_4BYTE_ABGR
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(img, null, null);

		return bi;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this); // see javadoc for more info on the
										// parameters
	}

}
