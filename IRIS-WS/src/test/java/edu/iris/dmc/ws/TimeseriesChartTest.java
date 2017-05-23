package edu.iris.dmc.ws;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.service.IrisService;
import edu.iris.dmc.service.NoDataFoundException;
import edu.iris.dmc.service.ServiceFactory;
import edu.iris.dmc.service.response.PngEncoder;
import edu.iris.dmc.service.response.TimeseriesBufferedImage;
import edu.iris.dmc.service.response.TimeseriesChartHandler;

public class TimeseriesChartTest {

	public static void main1(String[] args) {
		IrisService<TimeseriesBufferedImage> service = ServiceFactory.createService(new TimeseriesChartHandler());

		try {
			List<TimeseriesBufferedImage> list = service.fetch(
					"http://service.iris.edu/fdsnws/dataselect/1/query?net=IU&sta=ANMO&loc=00&cha=BHZ&start=2009-01-05T19:50:20.000&end=2009-01-05T20:08:55.000",
					null, null, new TimeseriesChartHandler());

			TimeseriesBufferedImage image = list.get(0);
			TimeseriesPlotPanel panel = new TimeseriesPlotPanel(image);

			JFrame frame = new JFrame();
			frame.setLayout(new BorderLayout());
			frame.setSize(400, 400);
			JScrollPane scrollPanel = new JScrollPane(panel);
			frame.getContentPane().add(scrollPanel, BorderLayout.CENTER);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);

		} catch (IOException | CriteriaException | NoDataFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		IrisService<TimeseriesBufferedImage> service = ServiceFactory.createService(new TimeseriesChartHandler());

		try {
			TimeseriesChartHandler handler = new TimeseriesChartHandler();
			handler.showRecordLine(true);
			handler.layered(true);
			handler.reduce(true);
			List<TimeseriesBufferedImage> list = service.fetch(
					"http://service.iris.edu/fdsnws/dataselect/1/query?network=IU&station=ANMO&channel=*&location=00&starttime=2010-02-27T07:00:00&endtime=2010-02-27T07:30:00",
					null, null, handler);

			System.out.println(list.size());

			TimeseriesBufferedImage image = list.get(0);
			image.paintComponent();
			// write(image,System.out);
			TimeseriesPlotPanel panel = new TimeseriesPlotPanel(image);

			JFrame frame = new JFrame();
			frame.setLayout(new BorderLayout());
			frame.setSize(1200, 450);
			JScrollPane scrollPanel = new JScrollPane(panel);
			frame.getContentPane().add(scrollPanel, BorderLayout.CENTER);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);

			BufferedImage bi = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bi.createGraphics();
			panel.paintAll(g2d);
			g2d.dispose();

			long start = System.currentTimeMillis();
			// ImageIO.write(bi, "png", new
			// File("/Users/Suleiman/images/111.png"));
			// ImageIO.write(bi, "png", new
			// File("/Users/Suleiman/images/111.png"));

			FileOutputStream fos = new FileOutputStream(new File("/Users/Suleiman/images/111.png"));
			PngEncoder encoder = new PngEncoder(bi);
			byte[] bytes = encoder.pngEncode();
			fos.write(bytes);
			fos.close();
			System.out.println(System.currentTimeMillis() - start);

		} catch (IOException | CriteriaException | NoDataFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
