package view;

import java.util.HashMap;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;

import model.segmentation.*;

public class SegmentationOptionsPanel extends JPanel {

	private static enum SegmentationMethod {
		K_MEANS("K-means"), ANTI_POLE("Anti-pole"), SPLIT_AND_MERGE(
				"Split & Merge");

		private String name;

		SegmentationMethod(String s) {
			name = s;
		}

		public String toString() {
			return name;
		}
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox combo;

	private JPanel panel;

	private SegmentationMethod selection;

	private HashMap<String, String> parameters;

	public SegmentationOptionsPanel() {
		parameters = new HashMap<String, String>();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel("Método:"), c);
		c.gridy = 1;
		add(getComboBox(), c);
		c.gridy = 2;
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(250, 150));
		panel.setLayout(new GridBagLayout());
		panel.setVisible(true);
		add(panel, c);
		showOptions();
	}

	private JComboBox getComboBox() {
		if (combo == null) {
			SegmentationMethod[] ms  = SegmentationMethod.values();
			String[] names = new String[ms.length];
			for (int i = 0; i < ms.length; i++) {
				names[i] = ms[i].toString();
			}
			combo = new JComboBox(names);
			selection = ms[0];
			combo.addActionListener(new ActionListener() {

				// @Override
				public void actionPerformed(ActionEvent e) {
					JComboBox combo = (JComboBox) e.getSource();
					int i = combo.getSelectedIndex();
					SegmentationMethod newSelection = SegmentationMethod.values()[i];
					if (selection != newSelection) {
						selection = newSelection;
						showOptions();
					}
				}

			});
		}
		return combo;
	}

	public SegmentationAlgorithm getSelectedSegmentationMethod() {
		SegmentationAlgorithm sa = null;
		switch (selection) {
		case K_MEANS:
			sa = new KMeans();
			break;
		case ANTI_POLE:
			sa = new AntipoleTreeSegmentation();
			break;
		case SPLIT_AND_MERGE:
			sa = new SplitAndMerge();
			break;
		}
		return sa;
	}
	
	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void showOptions() {
		
		if (selection == null) {
			return;
		}
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(16, 4, 2, 4);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		
		panel.setVisible(false);		
		panel.removeAll();
		parameters.clear();
		
		switch (selection) {
		
		case K_MEANS:
			c.gridy = 0;
			addSpinner("Cant. máx. de clusters:", "clustersCount", new SpinnerNumberModel(10, 2, 50, 1), c);
			c.insets = new Insets(2, 4, 2, 4);
			c.gridy = 1;
			addCheckBox("Usar todos los clusters", "useAllClusters", false, c);
			break;
			
		case ANTI_POLE:
			c.gridy = 0;
			addSpinner("Radio máx. de clusters:", "radio", new SpinnerNumberModel(200, 20, 400, 1), c);
			break;
			
		case SPLIT_AND_MERGE:
			c.gridy = 0;
			addSpinner("Límite Split:", "splitStandardDeviation", new SpinnerNumberModel(5, 1, 50, 1), c);
			c.insets = new Insets(2, 4, 2, 4);
			c.gridy = 1;
			addSpinner("Límite Merge:", "mergeStandardDeviation", new SpinnerNumberModel(15, 1, 50, 1), c);
			c.gridy = 2;
			addSpinner("Área mínima:", "minSize", new SpinnerNumberModel(3, 1, 100, 1), c);
			break;
			
		}
		
		panel.setVisible(true);
		
	}
	
	private void addSpinner(String name, String key, SpinnerModel sm, GridBagConstraints c) {
		JLabel label = new JLabel(name);
		c.gridx = 0;
		c.gridwidth = 2;
		panel.add(label, c);
		JSpinner spinner = new JSpinner(sm);
		final String k = key;
		final JSpinner s = spinner;
		spinner.addChangeListener(new ChangeListener() {

			// @Override
			public void stateChanged(ChangeEvent e) {
				parameters.put(k, s.getValue().toString());
			}
			
		});
		c.gridx = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(spinner, c);
		parameters.put(key, sm.getValue().toString());
	}

	private void addCheckBox(String name, String key, boolean checked, GridBagConstraints c) {
		JCheckBox checkbox = new JCheckBox(name, checked);
		final String k = key;
		final JCheckBox cb = checkbox;
		checkbox.addChangeListener(new ChangeListener() {

			// @Override
			public void stateChanged(ChangeEvent e) {
				parameters.put(k, cb.isSelected() ? "1" : "0");
			}
			
		});
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(checkbox, c);
		parameters.put(key, checked ? "1" : "0");
	}

}
