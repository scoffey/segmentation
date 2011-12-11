package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

public class ProgressDialog extends JOptionPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel label;

	private JDialog dialog;

	private ActionListener listener;

	public ProgressDialog(JDialog d, ActionListener cancelListener) {
		super(new JPanel(), JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, null, new String[] { "Cancelar" });
		JPanel panel = (JPanel) getMessage();
		dialog = d;
		listener = cancelListener;
		panel.setPreferredSize(new Dimension(300, 60));
		panel.setLayout(new BorderLayout());
		label = new JLabel();
		panel.add(label, BorderLayout.NORTH);
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(true);
		panel.add(progressBar, BorderLayout.CENTER);
		packIntoDialog();
	}

	private void packIntoDialog() {
		final JOptionPane optionPane = this;
		dialog.setContentPane(this);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (listener != null) {
					listener.run();
				}
			}
		});
		addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();

				if (dialog.isVisible() && (e.getSource() == optionPane)
						&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					if (listener != null) {
						listener.run();
					}
					close();
					setValue("Continuar");
				}
			}
		});
		dialog.pack();
	}

	public void setLabel(String message) {
		label.setText(message);
	}

	public void cancel() {
		close();
		if (listener != null) {
			listener.run(); // no new threads, just run
		}
	}

	public void open(String message) {
		setLabel(message);
		dialog.setVisible(true);
	}

	public void close() {
		dialog.setVisible(false);
	}

}
