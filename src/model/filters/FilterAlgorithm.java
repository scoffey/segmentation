package model.filters;

import model.ImageMatrix;

/**
 * 
 * Interfaz que deben implementar todas las clases que filtran una imagen antes
 * de segmentarla.
 * 
 */
public interface FilterAlgorithm {

	public void filter(ImageMatrix input, ImageMatrix output);
	
}
