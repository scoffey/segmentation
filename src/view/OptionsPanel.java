package view;

import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.*;

import model.ImageMatrix;
import model.converters.ImageConverter;
import model.segmentation.SegmentationAlgorithm;

public class OptionsPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FeatureOptionsPanel panel1;

	private SegmentationOptionsPanel panel2;

	public OptionsPanel() {
		GridLayout layout = new GridLayout(1, 2);
		layout.setHgap(8);
		setLayout(layout);
		panel1 = new FeatureOptionsPanel();
		add(panel1);
		panel2 = new SegmentationOptionsPanel();
		add(panel2);
	}
	
	public HashMap<String, String> getSegmentationParameters() {
		return panel2.getParameters();
	}

	public ImageConverter getSelectedFeature(ImageMatrix matrix) {
		return panel1.getSelectedFeature(matrix);
	}

	public SegmentationAlgorithm getSelectedSegmentationMethod() {
		return panel2.getSelectedSegmentationMethod();
	}

}
