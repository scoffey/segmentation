package model.converters;

import java.util.HashMap;

import model.FeatureMatrix;
import model.ImageMatrix;

/**
 * Clase abstracta de la cual deben extender todos los converters. Los
 * converters se encargan de convertir una <code>ImageMatrix</code> en
 * una <code>FeatureMatriz</code>, extrayendo alguna feature de la 
 * imagen.
 */
public abstract class ImageConverter {
	protected ImageMatrix image;
	protected HashMap<String, String> params;
	
	public ImageConverter(ImageMatrix image, HashMap<String, String> params) {
		this.image = image;
		this.params = params;
	}
	
	/**
	 * Convierte la <code>ImageMatrix</code> que se recibió en el constructor
	 * en una <code>FeatureMatriz</code>. Lo único que hace es iterar por toda
	 * la imagen, e invocar al método abstracto <code>createFeature</code>
	 * para computar la feature en cada punto. Este método será implementado por
	 * los converters específicos.
	 * 
	 * @return La <code>FeatureMatrix</code> resultante.
	 */
	public FeatureMatrix createFeatureMatrix() {
		FeatureMatrix imageMatrix = new FeatureMatrix(image.getWidth(), 
					image.getHeight(), this.getDepth());
		
		for (int i=0; i<image.getHeight(); i++) {
			for (int j=0; j<image.getWidth(); j++) {
				createFeature(i, j, imageMatrix.getData()[i][j]);
			}
		}
		
		return imageMatrix;
	}
	
	/**
	 * @return La dimensión del feature space.
	 */
	protected abstract int getDepth();
	
	/**
	 * Computa la feature para un punto determinado de la imagen. El
	 * vector que se recibe contiene la dimensión que se obtuvo
	 * mediante <code>getDepth</code>.
	 * 
	 * @param i Fila del pixel a evaluar.
	 * @param j Columna del pixel a evaluar.
	 * @param feature Vector en el cual dejar la feature.
	 */
	protected abstract void createFeature(int i, int j, int[] feature);
}
