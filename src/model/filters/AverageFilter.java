package model.filters;

import java.awt.Color;
import java.util.List;

public class AverageFilter extends NeighboursFilter {

	@Override
	protected int getFinalColor(List<int[]> rgbs) {
		int[] sum = new int[3];
		Color c;

		for (int[] rgb : rgbs) {
			c = new Color(rgb[0], rgb[1], rgb[2]);
			sum[0] += c.getRed();

			sum[1] += c.getGreen();

			sum[2] += c.getBlue();
		}

		return new Color(sum[0] / rgbs.size(), sum[1] / rgbs.size(), sum[2]
				/ rgbs.size()).getRGB();

	}

}