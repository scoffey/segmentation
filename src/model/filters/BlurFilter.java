package model.filters;

public class BlurFilter extends ConvolutionFilter {

	@Override
	protected double[][] getWeights() {
		return new double[][] { { 1 / 16.0, 2 / 16.0, 1 / 16.0 },
				{ 2 / 16.0, 4 / 16.0, 2 / 16.0 },
				{ 1 / 16.0, 2 / 16.0, 1 / 16.0 } };
	}
}
