package model.filters;

import java.awt.Color;

import model.ImageMatrix;

public abstract class ConvolutionFilter implements FilterAlgorithm {

	public void filter(ImageMatrix input, ImageMatrix output) {
		double[][] weights = getWeights();
		double acum[] = new double[3];
		Color c;
		for (int i = 1; i < input.getHeight() - 1; i++) {
			for (int j = 1; j < input.getWidth() - 1; j++) {
				acum[0] = acum[1] = acum[2] = 0;
				for (int m = 0; m < 3; m++) {
					for (int n = 0; n < 3; n++) {
						c = new Color(input.getPixels()[i + m - 1][j + n - 1]);
						acum[0] += weights[m][n] * c.getRed();
						acum[1] += weights[m][n] * c.getGreen();
						acum[2] += weights[m][n] * c.getBlue();

					}
				}
				checkColor(acum);
				output.getPixels()[i][j] = new Color((int) acum[0],
						(int) acum[1], (int) acum[2]).getRGB();
			}
		}
	}

	protected abstract double[][] getWeights();

	private void checkColor(double color[]) {
		if (color[0] < 0)
			color[0] = 0;
		if (color[1] < 0)
			color[1] = 0;
		if (color[2] < 0)
			color[2] = 0;

		if (color[0] > 255)
			color[0] = 255;
		if (color[1] > 255)
			color[1] = 255;
		if (color[2] > 255)
			color[2] = 255;
	}
}
