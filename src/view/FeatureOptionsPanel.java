package view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.ImageMatrix;
import model.converters.*;

public class FeatureOptionsPanel extends JPanel {

	public static enum Feature {
		COLOR, HISTOGRAM
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox combo;

	private JPanel panel;

	private JPanel optionalPanel;

	private Feature selection;

	private HashMap<String, String> parameters;

	public FeatureOptionsPanel() {
		parameters = new HashMap<String, String>();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel("Feature:"), c);
		c.gridy = 1;
		add(getComboBox(), c);
		c.gridy = 2;
		panel = createOptionsPanel();
		add(panel, c);
		selection = Feature.COLOR;
		getHistogramOptionsPanel().setVisible(false);
	}

	private JComboBox getComboBox() {
		if (combo == null) {
			String[] options = new String[] { "Por color", "Por histograma" };
			combo = new JComboBox(options);
			selection = Feature.COLOR;
			combo.addActionListener(new ActionListener() {

				// @Override
				public void actionPerformed(ActionEvent e) {
					JComboBox combo = (JComboBox) e.getSource();
					int i = combo.getSelectedIndex();
					selection = Feature.values()[i];
					getHistogramOptionsPanel().setVisible(i > 0);
				}

			});
		}
		return combo;
	}

	private JPanel createOptionsPanel() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(250, 150));
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 0, 2, 0);
		c.anchor = GridBagConstraints.NORTH;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("Perfil:"), c);
		c.insets = new Insets(2, 0, 2, 0);
		c.weighty = 1;
		c.gridy = 1;
		panel.add(createRadioButtonsPanel(), c);
		c.weighty = 0;
		c.insets = new Insets(8, 0, 2, 0);
		c.gridy = 2;
		panel.add(getHistogramOptionsPanel(), c);
		return panel;
	}

	private JPanel createRadioButtonsPanel() {
		ActionListener al = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				parameters.put("colorSpace", e.getActionCommand());
			}

		};

		JRadioButton rgb = new JRadioButton("RGB", true);
		rgb.setMnemonic(KeyEvent.VK_R);
		rgb.setActionCommand("rgb");
		rgb.addActionListener(al);
		parameters.put("colorSpace", "rgb");

		JRadioButton hsb = new JRadioButton("HSB");
		hsb.setMnemonic(KeyEvent.VK_H);
		hsb.setActionCommand("hsb");
		hsb.addActionListener(al);

		JRadioButton gs = new JRadioButton("Escala de grises");
		gs.setMnemonic(KeyEvent.VK_G);
		gs.setActionCommand("grayscale");
		gs.addActionListener(al);

		ButtonGroup group = new ButtonGroup();
		group.add(rgb);
		group.add(hsb);
		group.add(gs);

		JPanel p = new JPanel();
		p.add(rgb);
		p.add(hsb);
		p.add(gs);
		return p;
	}

	public JPanel getHistogramOptionsPanel() {
		if (optionalPanel == null) {
			optionalPanel = new JPanel();
			optionalPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(2, 4, 2, 4);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 0;
			c.gridy = 0;
			addSpinner("Clases por canal:", "intervalClassesCountPerChannel",
					new SpinnerNumberModel(5, 2, 10, 1), c);
			c.gridy = 1;
			addSpinner("Influencia X:", "xinfluence", new SpinnerNumberModel(0,
					0, 10000, 10), c);
			c.gridy = 2;
			addSpinner("Influencia Y:", "yinfluence", new SpinnerNumberModel(0,
					0, 10000, 10), c);
		}
		return optionalPanel;
	}

	public ImageConverter getSelectedFeature(ImageMatrix matrix) {
		ImageConverter ic = null;
		switch (selection) {
		case COLOR:
			ic = new ColorImageConverter(matrix, parameters);
			break;
		case HISTOGRAM:
			ic = new HistogramImageConverter(matrix, parameters);
			break;
		}
		return ic;
	}

	private void addSpinner(String name, String key, SpinnerModel sm,
			GridBagConstraints c) {
		JLabel label = new JLabel(name);
		c.gridx = 0;
		c.gridwidth = 2;
		optionalPanel.add(label, c);
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
		optionalPanel.add(spinner, c);
		parameters.put(key, sm.getValue().toString());
	}

}
