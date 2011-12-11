package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.FeatureMatrix;
import model.ImageMatrix;
import model.SegmentationObserver;
import model.converters.ImageConverter;
import model.filters.AverageFilter;
import model.filters.BlurFilter;
import model.filters.EqualizeFilter;
import model.filters.FilterAlgorithm;
import model.filters.MaxFilter;
import model.filters.MaxMinFilter;
import model.filters.MidPointFilter;
import model.filters.ReduceResolutionFilter;
import model.filters.SharpenFilter;
import model.segmentation.SegmentationAlgorithm;

public class Controller {

	private CgTpe1 view;

	private BufferedImage original;

	private ImageMatrix matrix;

	private double zoom;

	private SegmentationAlgorithm algorithm;

	private ProgressDialog dialog;

	public Controller(CgTpe1 frame) {
		view = frame;
		original = null;
		matrix = null;
		zoom = 1.0;
		algorithm = null;
		dialog = null;
	}

	public void open() {
		File f = view.getFileFromFileChooser();
		if (f != null) {
			try {
				original = ImageIO.read(f);
			} catch (IOException e) {
				raiseException(String.format("No se puede abrir el archivo:"
						+ "\n%s\n\nDetalle: %s", f.toString(), e
						.getLocalizedMessage()));
			}
			undoAll();
		}
	}

	public void saveAs() {
		if (original == null || matrix == null) {
			raiseException("No hay ninguna imagen abierta.");
			return;
		}
		saveAs(view.saveFileWithFileChooser());
	}

	public void saveAs(File f) {
		if (f == null) {
			return;
		}
		try {
			String name = f.getName();
			int i = name.lastIndexOf('.');
			String extension = (i < 0 ? "png" : name.substring(i + 1));
			BufferedImage segmented = matrix.getBufferedImage();
			ImageIO.write(segmented, extension, f);
		} catch (IOException e) {
			view.showErrorDialog("Error de escritura", String.format(
					"No se puede guardar el archivo:\n%s\n\nDetalle: %s", f
							.toString(), e.getLocalizedMessage()));
		}
	}

	public void undoAll() {
		if (original == null) {
			raiseException("No hay ninguna imagen abierta.");
			return;
		}

		zoom = 1.0;
		ImageView iv = view.getImageView();
		iv.redrawImages(original, original, zoom);
		// asumiendo que original == matrix.getBufferedImage()

		try {
			matrix = new ImageMatrix(original);
		} catch (IOException e) {
			raiseException(e.getMessage());
			return;
		}
	}

	public void quit() {
		view.quit();
	}

	public void blurFilter() {
		filter(new BlurFilter());
	}

	public void reduceResolutionFilter() {
		filter(new ReduceResolutionFilter());
	}

	public void sharpenFilter() {
		filter(new SharpenFilter());
	}

	public void maxFilter() {
		filter(new MaxFilter());
	}

	public void minFilter() {
		filter(new AverageFilter());
	}

	public void maxMinFilter() {
		filter(new MaxMinFilter());
	}

	public void midPointFilter() {
		filter(new MidPointFilter());
	}

	public void equalizeFilter() {
		filter(new EqualizeFilter());
	}

	public void filter(FilterAlgorithm fa) {
		if (original == null || matrix == null) {
			raiseException("No hay ninguna imagen abierta.");
			return;
		}
		ImageMatrix output = new ImageMatrix(matrix.getWidth(), matrix
				.getHeight());
		fa.filter(matrix, output);
		matrix = output;
		view.getImageView().redrawImage(matrix.getBufferedImage(), zoom);
	}

	public void applySegmentation() {
		if (original == null || matrix == null) {
			raiseException("No hay ninguna imagen abierta.");
			return;
		}
		OptionsPanel p = new OptionsPanel();
		p.setVisible(true);
		if (view.showConfirmDialog(p, "Segmentar")) {
			ImageConverter ic = p.getSelectedFeature(matrix);
			final FeatureMatrix fm = ic.createFeatureMatrix();
			getProgressDialog().open("Procesando...");
			stopSegmentation();
			algorithm = p.getSelectedSegmentationMethod();
			algorithm.process(fm, new SegmentationObserver() {

				private int i = 0;

				public void onChange() {
					String s = String.format("Procesando... (Iteración Nº%d)",
							Integer.valueOf(++i));
					getProgressDialog().setLabel(s);
					matrix = fm.getImageMatrix();
					view.getImageView().redrawImage(matrix.getBufferedImage(),
							zoom);
				}

				public void onComplete() {
					getProgressDialog().close();
				}

			}, p.getSegmentationParameters());
			algorithm.start();
		}
	}

	public ProgressDialog getProgressDialog() {
		if (dialog == null) {
			ActionListener al = new ActionListener(this, "stopSegmentation");
			dialog = new ProgressDialog(view.createDialog(
					"Progreso de la segmentación", false), al);
		}
		return dialog;
	}

	public void stopSegmentation() {
		if (algorithm == null) {
			return;
		}
		algorithm.interrupt();
		algorithm = null;
	}

	public void viewHorizontal() {
		view.getImageView().setHorizontalView();
	}

	public void viewVertical() {
		view.getImageView().setVerticalView();
	}

	public void zoomIn() {
		scale(zoom * 3.0 / 2.0);
	}

	public void zoomOut() {
		scale(zoom * 2.0 / 3.0);
	}

	public void zoomOriginal() {
		scale(1.0);
	}

	public void scale(double factor) {
		if (original == null || matrix == null) {
			raiseException("No hay ninguna imagen abierta.");
			return;
		}
		if (factor > 4.0) {
			raiseException("La imagen no se puede acercar más.");
			return;
		}
		if (factor < 1.0 / 8.0) {
			raiseException("La imagen no se puede alejar más.");
			return;
		}
		zoom = factor;
		BufferedImage segmented = matrix.getBufferedImage();
		view.getImageView().redrawImages(original, segmented, zoom);
	}

	public void about() {
		final String s = "Trabajo Práctico Especial Nº1\n"
				+ "Computación Gráfica\n"
				+ "Este programa muestra distintos métodos de segmentación\n"
				+ "de imágenes que producen diversos resultados dependiendo\n"
				+ "de los parámetros y los filtros aplicados.\n\n"
				+ "Autores:\n"
				+ "\tRafael Martín Bigio <rbigio@alu.itba.edu.ar>\n"
				+ "\tSantiago Andrés Coffey <scoffey@alu.itba.edu.ar>\n"
				+ "\tAndrés Santiago Gregoire <agregoir@alu.itba.edu.ar>\n";
		view.showInformationDialog("Acerca del programa", s);
	}

	public void raiseException(String message) {
		view.showErrorDialog("Error", message);
	}

}
