package model.converters;

import java.awt.Color;
import java.util.HashMap;

import model.ImageMatrix;

/**
 * Implementación concreta de un converter que toma como feature el
 * histograma de una ventana alrededor de cada pixel.
 */
public class HistogramImageConverter extends ImageConverter {
	private int intervalClassesCountPerChannel = 3;
	private int xinfluence = 0;
	private int yinfluence = 0;
	private int spaceChannelsCount = 0;

	public HistogramImageConverter(ImageMatrix image,
			HashMap<String, String> params) {
		super(image, params);

		String param;
		if ((param = params.get("intervalClassesPerChannel")) != null) {
			this.intervalClassesCountPerChannel = new Integer(param);
		}

		if ((param = params.get("xinfluence")) != null && !param.equals("0")) {
			this.xinfluence = new Integer(param);
			spaceChannelsCount++;
		}

		if ((param = params.get("yinfluence")) != null && !param.equals("0")) {
			this.yinfluence = new Integer(param);
			spaceChannelsCount++;
		}
	}

	@Override
	protected void createFeature(int i, int j, int[] feature) {
		int xmoves[] = { 0, 1, -1, 0, 1, -1, 0, 1, -1 };
		int ymoves[] = { 0, 0, 0, 1, 1, 1, -1, -1, -1 };
		int histogramSamples = 0;
		float[] components = new float[3];
		String s = params.get("colorSpace");

		for (int k = 0; k < xmoves.length; k++) {
			if (i + ymoves[k] > 0 && i + ymoves[k] < this.image.getHeight()
					&& j + xmoves[k] > 0
					&& j + xmoves[k] < this.image.getWidth()) {

				components = getColorComponents(this.image.getPixels()[i
						+ ymoves[k]][j + xmoves[k]]);

				feature[(int) Math.floor((components[0] / 256.0)
						* intervalClassesCountPerChannel)]++;
				// si no es grayscale considerar los otros dos canales
				if (!(s != null && s.equalsIgnoreCase("grayscale"))) {
					feature[intervalClassesCountPerChannel
							+ (int) Math.floor((components[1] / 256.0)
									* intervalClassesCountPerChannel)]++;
					feature[2
							* intervalClassesCountPerChannel
							+ (int) Math.floor((components[2] / 256.0)
									* intervalClassesCountPerChannel)]++;
				}
				histogramSamples++;

			}
		}

		for (int k = 0; k < feature.length - spaceChannelsCount; k++) {
			feature[k] = (feature[k] * 1000) / histogramSamples;
		}

		if (params.get("xinfluence") != null
				&& !params.get("xinfluence").equals("0")) {
			feature[feature.length - 2] = (int) ((i / new Double(image
					.getHeight())) * xinfluence);
		}
		if (params.get("yinfluence") != null
				&& !params.get("xinfluence").equals("0")) {
			feature[feature.length - 1] = (int) ((j / new Double(image
					.getWidth())) * yinfluence);
		}
	}

	@Override
	protected int getDepth() {
		int n = 3;
		String s = params.get("colorSpace");
		if (s != null && s.equalsIgnoreCase("grayscale")) {
			n = 1;
		}
		return n * intervalClassesCountPerChannel + spaceChannelsCount;
	}

	
	private float[] getColorComponents(int pixel) {
		float[] ret = new float[3];
		String s = params.get("colorSpace");
		if (s == null || s.equalsIgnoreCase("RGB")) {
			Color c = new Color(pixel);
			ret[0] = c.getRed();
			ret[1] = c.getGreen();
			ret[2] = c.getBlue();
		} else if (s.equalsIgnoreCase("HSB")) {
			Color c = new Color(pixel);
			ret = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), ret);
			ret[0] *= 255;
			ret[1] *= 255;
			ret[2] *= 255;
		} else {
			Color c = new Color(pixel);
			int gray = (int) (0.3 * c.getRed() + 0.59 * c.getGreen() + 0.11 * c
					.getBlue());
			ret[0] = gray;
			ret[1] = gray;
			ret[2] = gray;
		}
		return ret;
	}
}