package view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class MenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Controller ctrl;

	public MenuBar(Controller controller) {
		super();
		ctrl = controller;
		add(getFileMenu());
		add(getEditMenu());
		add(getViewMenu());
		add(getHelpMenu());
	}

	public JMenu getFileMenu() {
		JMenu menu = new JMenu("Archivo");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.add(getMenuItem("Abrir...", "open", KeyEvent.VK_A, 'O'));
		menu.add(getMenuItem("Guardar como...", "saveAs", KeyEvent.VK_G, 'S'));
		menu.add(getMenuItem("Salir", "quit", KeyEvent.VK_S, 'Q'));
		return menu;
	}

	public JMenu getEditMenu() {
		JMenu menu = new JMenu("Edición");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.add(getMenuItem("Deshacer todo", "undoAll", KeyEvent.VK_D, 'Z'));
		JMenu filterSubmenu = new JMenu("Filtrar");
		filterSubmenu.setMnemonic(KeyEvent.VK_F);
		filterSubmenu.add(getMenuItem("Blur", "blurFilter", KeyEvent.VK_B,
				KeyEvent.VK_F1));
		filterSubmenu.add(getMenuItem("Sharpen", "sharpenFilter",
				KeyEvent.VK_H, KeyEvent.VK_F2));
		filterSubmenu.add(getMenuItem("Reducir resolución",
				"reduceResolutionFilter", KeyEvent.VK_R, KeyEvent.VK_F3));
		filterSubmenu.add(getMenuItem("Max", "maxFilter", KeyEvent.VK_X,
				KeyEvent.VK_F4));
		filterSubmenu.add(getMenuItem("Average", "minFilter", KeyEvent.VK_N,
				KeyEvent.VK_F5));
		filterSubmenu.add(getMenuItem("Max-Min", "maxMinFilter", KeyEvent.VK_M,
				KeyEvent.VK_F6));
		filterSubmenu.add(getMenuItem("Midpoint", "midPointFilter",
				KeyEvent.VK_P, KeyEvent.VK_F7));
		filterSubmenu.add(getMenuItem("Ecualizar", "equalizeFilter",
				KeyEvent.VK_Q, KeyEvent.VK_F8));
		menu.add(filterSubmenu);
		menu.add(getMenuItem("Segmentar...", "applySegmentation",
				KeyEvent.VK_S, 'G'));
		return menu;
	}

	public JMenu getViewMenu() {
		JMenu menu = new JMenu("Ver");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.add(getMenuItem("Acercar", "zoomIn", KeyEvent.VK_C, 'K'));
		menu.add(getMenuItem("Alejar", "zoomOut", KeyEvent.VK_L, 'L'));
		menu.add(getMenuItem("Escala original", "zoomOriginal", KeyEvent.VK_O,
				'0'));
		menu.add(getMenuItem("En paneles a izquierda y derecha",
				"viewHorizontal", KeyEvent.VK_I, ','));
		menu.add(getMenuItem("En paneles arriba y abajo", "viewVertical",
				KeyEvent.VK_A, '.'));
		return menu;
	}

	public JMenu getHelpMenu() {
		JMenu menu = new JMenu("Ayuda");
		menu.setMnemonic(KeyEvent.VK_Y);
		menu.add(getMenuItem("Acerca de...", "about", KeyEvent.VK_A, 'A'));
		return menu;
	}

	public JMenuItem getMenuItem(String label, String callback, int mnemonic,
			char accel) {
		JMenuItem item = new JMenuItem(label, mnemonic);
		item.setAccelerator(KeyStroke
				.getKeyStroke(accel, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener(ctrl, callback));
		return item;
	}

	public JMenuItem getMenuItem(String label, String callback, int mnemonic,
			int accel) {
		JMenuItem item = new JMenuItem(label, mnemonic);
		item.setAccelerator(KeyStroke.getKeyStroke(accel, 0));
		item.addActionListener(new ActionListener(ctrl, callback));
		return item;
	}
}
