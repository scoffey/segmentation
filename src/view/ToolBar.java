package view;

import javax.swing.*;

import view.ActionListener;
import view.Controller;

public class ToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Controller ctrl;

	public ToolBar(Controller controller) {
		super();
		ctrl = controller;
		setFloatable(false);
		add(getButton("Abrir...", "document-open", "open"));
		add(getButton("Guardar como...", "media-floppy", "saveAs"));
		add(getButton("Deshacer todo", "edit-undo", "undoAll"));
		add(getButton("Filtro Blur", "stock_standard-filter", "blurFilter"));
		add(getButton("Filtro Sharpen", "stock_standard-filter", "sharpenFilter"));
		add(getButton("Reducir resolución", "stock_standard-filter", "reduceResolutionFilter"));
		add(getButton("Filtro Max", "stock_standard-filter", "maxFilter"));
		add(getButton("Filtro Average", "stock_standard-filter", "minFilter"));
		add(getButton("Filtro Max-Min", "stock_standard-filter", "maxMinFilter"));
		add(getButton("Filtro Midpoint", "stock_standard-filter", "midPointFilter"));
		add(getButton("Ecualizar", "stock_standard-filter", "equalizeFilter"));
		add(getButton("Segmentar...", "stock_filters-pop-art", "applySegmentation"));
		add(getButton("Acercar", "zoom-in", "zoomIn"));
		add(getButton("Alejar", "zoom-out", "zoomOut"));
		add(getButton("Escala original", "zoom-original", "zoomOriginal"));
		add(getButton("Ver a izquierda y derecha", "stock_view-left-right", "viewHorizontal"));
		add(getButton("Ver arriba y abajo", "stock_view-top-bottom", "viewVertical"));
	}

	private JButton getButton(String label, String icon, String action) {
		JButton button = new JButton();
		button.setToolTipText(label);
		button.setIcon(new ImageIcon("resources/icons/" + icon + ".png"));
		button.addActionListener(new ActionListener(ctrl, action));
		return button;
	}

	public JMenuItem getMenuItem(String label, String callback) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(new ActionListener(ctrl, callback));
		return item;
	}
	
}
