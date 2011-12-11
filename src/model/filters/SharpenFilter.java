package model.filters;

public class SharpenFilter extends ConvolutionFilter {

	@Override
	protected double[][] getWeights() {
		double strengh = 0.1;
		return new double[][] { { -strengh, -strengh, -strengh }, { -strengh, 8*strengh+1,-strengh }, { -strengh, -strengh, -strengh} };
	}
}
