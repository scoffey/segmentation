package model.segmentation;

import java.util.HashMap;

import model.FeatureMatrix;
import model.SegmentationObserver;

/**
 * Interfaz para los métodos de segmentación. Extiende de
 * <code>Runnable</code>, ya que los métodos deben ser threads.
 * En el método <code>process</code> se le setea la imagen y el observer,
 * pero recién empieza el procesamiento cuando se hace el 
 * <code>start</code> del thread. 
 */

public abstract class SegmentationAlgorithm extends Thread {

	/**
	 * Establece los parámetros para ser usados en la segmentación.
	 * 
	 * @param image Imagen a segmentar.
	 * @param observer Observador a invocar luego de cada iteración.
	 * @param params Parámetros para la segmentación.
	 */
	public abstract void process(FeatureMatrix image, SegmentationObserver observer, HashMap<String, String> params);
}
