package model.filters;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import model.ImageMatrix;

public abstract class NeighboursFilter implements FilterAlgorithm {

	//@Override
	public void filter(ImageMatrix input, ImageMatrix output) {
		int xmoves[] = { 1, -1, 0, 1, -1, 0, 1, -1 };
		int ymoves[] = { 0, 0, 1, 1, 1, -1, -1, -1 };
		
		Color c;

		for (int i = 0; i < input.getHeight(); i++) {
			for (int j = 0; j < input.getWidth(); j++) {

				List<int[]> rgbs = new ArrayList<int[]>();
				for (int k = 0; k < xmoves.length; k++) {
					if (i + ymoves[k] > 0 && i + ymoves[k] < input.getHeight()
							&& j + xmoves[k] > 0
							&& j + xmoves[k] < input.getWidth()) {
						c = new Color(input.getPixels()[i + ymoves[k]][j
								+ xmoves[k]]);
						rgbs.add(new int[] {c.getRed(), c.getGreen(), c.getBlue()});
					}
				}
				output.getPixels()[i][j] = getFinalColor(rgbs);
			}
		}

	}

	protected abstract int getFinalColor(List<int[]> rgbs);

}
