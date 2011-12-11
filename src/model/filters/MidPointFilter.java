package model.filters;

import java.awt.Color;
import java.util.List;

public class MidPointFilter extends NeighboursFilter {

	@Override
	protected int getFinalColor(List<int[]> rgbs) {
		int[] max = new int[3];
		int[] min = new int[3];
		Color c;

		for (int[] rgb : rgbs) {
			c = new Color(rgb[0], rgb[1], rgb[2]);
			if (c.getRed() > max[0]) {
				max[0] = c.getRed();
			}

			if (c.getGreen() > max[1]) {
				max[1] = c.getGreen();
			}

			if (c.getBlue() > max[2]) {
				max[2] = c.getBlue();
			}

			if (c.getRed() < min[0]) {
				min[0] = c.getRed();
			}

			if (c.getGreen() < min[1]) {
				min[1] = c.getGreen();
			}

			if (c.getBlue() < min[2]) {
				min[2] = c.getBlue();
			}
		}

		return new Color((max[0] + min[0])/2, (max[1] + min[1])/2, (max[2] + min[2])/2)
				.getRGB();

	}

}
