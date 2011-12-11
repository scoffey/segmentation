package model.filters;

import java.awt.Color;

import model.ImageMatrix;

public class ReduceResolutionFilter implements FilterAlgorithm {

	//@Override
	public void filter(ImageMatrix input, ImageMatrix output) {
		float[] hsbvals = new float[3];
		float newval[] = new float[3];
		for (int i = 0; i < input.getHeight(); i++) {
			for (int j = 0; j < input.getWidth(); j++) {
				Color c = new Color(input.getPixels()[i][j]);
				hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(),
						hsbvals);
				/* Como representamos el pixel */
				if (hsbvals[1] < (1 - 0.8 * hsbvals[2])) {
					newval[0] = 0;
					newval[1] = 0;
					newval[2] = hsbvals[2];
				} else {
					newval[0] = hsbvals[0];
					newval[1] = 1;
					newval[2] = 1;
				}
				output.getPixels()[i][j] = Color.HSBtoRGB(newval[0], newval[1],
						newval[2]);

			}
		}

	}

}
