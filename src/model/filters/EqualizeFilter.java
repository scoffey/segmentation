package model.filters;

import java.awt.Color;

import model.ImageMatrix;

public class EqualizeFilter implements FilterAlgorithm {

	private static final int CHANNELS_RESOLUTION = 256;

	/* Histogramas */
	private double[] redHistogram = new double[CHANNELS_RESOLUTION];
	private double[] greenHistogram = new double[CHANNELS_RESOLUTION];
	private double[] blueHistogram = new double[CHANNELS_RESOLUTION];

	/* Histogramas acumulados */
	private double[] redCumulativeHistogram = new double[CHANNELS_RESOLUTION];
	private double[] greenCumulativeHistogram = new double[CHANNELS_RESOLUTION];
	private double[] blueCumulativeHistogram = new double[CHANNELS_RESOLUTION];

	/* Nuevos colores */
	private int[] newRed = new int[CHANNELS_RESOLUTION];
	private int[] newGreen = new int[CHANNELS_RESOLUTION];
	private int[] newBlue = new int[CHANNELS_RESOLUTION];

	//@Override
	public void filter(ImageMatrix input, ImageMatrix output) {
		computeHistograms(input);
		computeNewColors();

		Color c;
		int r, g, b;
		for (int i = 0; i < input.getHeight(); i++) {
			for (int j = 0; j < input.getWidth(); j++) {
				c = new Color(input.getPixels()[i][j]);

				r = newRed[c.getRed()];
				g = newGreen[c.getGreen()];
				b = newBlue[c.getBlue()];
				output.getPixels()[i][j] = new Color(r, g, b).getRGB();
			}
		}

	}

	private void computeHistograms(ImageMatrix image) {
		Color c;

		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				c = new Color(image.getPixels()[i][j]);
				redHistogram[c.getRed()]++;
				greenHistogram[c.getGreen()]++;
				blueHistogram[c.getBlue()]++;
			}
		}

		int samplesCount = image.getHeight() * image.getWidth();
		for (int k = 0; k < CHANNELS_RESOLUTION; k++) {
			redHistogram[k] /= samplesCount;
			greenHistogram[k] /= samplesCount;
			blueHistogram[k] /= samplesCount;

			if (k > 0) {
				redCumulativeHistogram[k] = redCumulativeHistogram[k - 1]
						+ redHistogram[k];
				greenCumulativeHistogram[k] = greenCumulativeHistogram[k - 1]
						+ greenHistogram[k];
				blueCumulativeHistogram[k] = blueCumulativeHistogram[k - 1]
						+ blueHistogram[k];
			}else{
				redCumulativeHistogram[k] = redHistogram[k];
				greenCumulativeHistogram[k] = greenHistogram[k];
				blueCumulativeHistogram[k] = blueHistogram[k];
			}
				
		}
	}

	private void computeNewColors() {
		double minRed = -1;
		double minGreen = -1;
		double minBlue = -1;
		/* Buscar los primeros valores con frecuencia no nula */
		for (int i = 0; i < CHANNELS_RESOLUTION; i++) {
			if (minRed == -1 && redCumulativeHistogram[i] != 0) {
				minRed = i;
			}

			if (minGreen == -1 && greenCumulativeHistogram[i] != 0) {
				minGreen = i;
			}

			if (minBlue == -1 && blueCumulativeHistogram[i] != 0) {
				minBlue = i;
			}
		}
		//System.out.println(minRed + " " + minGreen + " " + minBlue);

		minRed /= CHANNELS_RESOLUTION;
		minGreen /= CHANNELS_RESOLUTION;
		minBlue /= CHANNELS_RESOLUTION;

		double l = CHANNELS_RESOLUTION;
		double max = new Double(CHANNELS_RESOLUTION);
		for (int i = 0; i < CHANNELS_RESOLUTION; i++) {
			double s = i / max;
			newRed[i] = (int) Math.floor((s - minRed) * (l - 1) / (1 - minRed));
			newGreen[i] = (int) Math.floor((s - minGreen) * (l - 1)
					/ (1 - minGreen));
			newBlue[i] = (int) Math.floor((s - minBlue) * (l - 1)
					/ (1 - minBlue));
		}
	}

}
