package view;

import java.awt.*;
import java.awt.image.*;

import javax.swing.*;

public class ImageView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JScrollPane pane1;

	private JScrollPane pane2;

	public ImageView() {
		super(new GridLayout(1, 2));
		pane1 = addScrollPane("Imagen original");
		pane2 = addScrollPane("Imagen segmentada");
	}

	private JScrollPane addScrollPane(String s) {
		JScrollPane pane = new JScrollPane(getScrollPaneLabel(s));
		pane.setVisible(true);
		add(pane);
		return pane;
	}

	private JLabel getScrollPaneLabel(String s) {
		JLabel label = new JLabel(s);
		label.setForeground(new Color(128, 128, 128));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	public void setHorizontalView() {
		pane1.setVisible(true);
		setLayout(new GridLayout(1, 2));
		validate();
	}

	public void setVerticalView() {
		pane1.setVisible(true);
		setLayout(new GridLayout(2, 1));
		validate();
	}

	public void setSingleView() {
		pane1.setVisible(false);
		validate();
	}

	public void redrawImages(BufferedImage original, BufferedImage segmented,
			double zoomFactor) {
		redrawPane(pane1, scale(original, zoomFactor));
		redrawPane(pane2, scale(segmented, zoomFactor));
	}

	public void redrawImage(ImageProducer p) {
		redrawPane(pane2, getToolkit().createImage(p));
	}

	public void redrawImage(BufferedImage bimage, double zoomFactor) {
		redrawPane(pane2, scale(bimage, zoomFactor));
	}

	public Image scale(BufferedImage bimage, double factor) {
		if (factor == 1.0) {
			return bimage;
		}
		double w = bimage.getWidth() * factor;
		double h = bimage.getHeight() * factor;
		return bimage.getScaledInstance((int) w, (int) h, Image.SCALE_SMOOTH);
	}

	private void redrawPane(JScrollPane pane, Image image) {
		JLabel label = null;
		if (image == null) {
			label = getScrollPaneLabel(pane == pane1 ? "Imagen original"
					: "Imagen segmentada");
		} else {
			label = new JLabel(new ImageIcon(image));
		}
		pane.setViewportView(label);
		label.validate();
	}

}