package model.segmentation;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.FeatureMatrix;
import model.SegmentationObserver;

public class AntipoleTreeSegmentation extends SegmentationAlgorithm {

	private FeatureMatrix image;
	private SegmentationObserver observer;
	private int radio = 200;
	
	public void process(FeatureMatrix image, SegmentationObserver observer, HashMap<String, String> params) {
		this.image = image;
		this.observer = observer;
		
		Integer radioParam = new Integer(params.get("radio"));
		if (radioParam != null){
			radio = radioParam;
		}
	}
	
	public void run() {

		//System.out.println("Comenzo");
		List<Point> objects = new ArrayList<Point>(image.getHeight()
				* image.getWidth());

		/* Crear la lista con todos los puntos */
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				objects.add(new Point(j, i));
			}
		}

		AntipoleTree.mustExit = false;
		AntipoleTree.build(image, objects, radio, null, observer);
		if (observer != null) {
			observer.onChange();
			observer.onComplete();
		}

	}
	
	static class AntipoleTree {
		protected static boolean mustExit;
		protected static byte segmentNum = 0;
		protected FeatureMatrix image;
		protected static SegmentationObserver observer;

		/**
		 * Build the Antipole tree
		 * 
		 * @param objects
		 *            The List of MetricSpaceObject's to insert in the tree
		 * @param maxRadius
		 *            The maximum cluster radius
		 * @param antipolePair
		 *            Antipole Pair
		 */

		public static AntipoleTree build(FeatureMatrix image, List<Point> objects,
				double maxRadius, Pair<Point> objectPair, SegmentationObserver observer) {

			if (Thread.interrupted())
				mustExit = true;
			if (mustExit)
				return new Cluster();
			
			AntipoleTree.observer = observer;
			/* Check whether should compute de antipole pair */
			if (objectPair == null) {
				/* Approximate the furthest pair in the List */
				objectPair = getPossibleAntipolePair(image, objects, maxRadius);

				/* All the List must become a cluster */
				if (objectPair == null) {
					approximate1Median(image, objects);
					return new Cluster(image, objects);
				}
			}
			/* Remove the Antipole pair from the List */
			List<Point> ListA = new ArrayList<Point>();
			List<Point> ListB = new ArrayList<Point>();
			double distA, distB;
			for (Point o : objects) {
				distA = distanceBetween(image.getData()[o.y][o.x],
						image.getData()[objectPair.A.y][objectPair.A.x]);
				distB = distanceBetween(image.getData()[o.y][o.x],
						image.getData()[objectPair.B.y][objectPair.B.x]);
				if (distA < distB) {
					ListA.add(o);
				} else {
					ListB.add(o);
				}
			}

			/* Check if we compute the next antipole pair for the 2 branches */
			Pair<Point> antipolePairA = approximateAntipolePair(image, ListA,
					maxRadius);
			Pair<Point> antipolePairB = approximateAntipolePair(image, ListB,
					maxRadius);

			return new InternalNode(image, objectPair.A, objectPair.B, build(image,
					ListA, maxRadius, antipolePairA, observer), build(image, ListB,
					maxRadius, antipolePairB, observer));
		}

		private static Pair<Point> approximateAntipolePair(FeatureMatrix image,
				List<Point> List, double maxRadius) {

			for (Point i : List)
				for (Point j : List)
					if (i != j
							&& distanceBetween(image.getData()[i.y][i.x], image
									.getData()[j.y][j.x]) > maxRadius)
						return new Pair<Point>(i, j);

			return null;
		}

		private static Pair<Point> getPossibleAntipolePair(FeatureMatrix image,
				List<Point> objects, double maxRadius) {

			for (Point i : objects)
				for (Point j : objects)
					if (i != j
							&& distanceBetween(image.getData()[i.y][i.x], image
									.getData()[j.y][j.x]) > maxRadius)
						return new Pair<Point>(i, j);

			return null;
		}

		private static int[] approximate1Median(FeatureMatrix image,
				List<Point> points) {
			int[] info = new int[image.getDepth()];
			int[] output = new int[info.length];

			for (Point p : points) {
				int[] data = image.getData()[p.y][p.x];
				for (int i = 0; i < data.length; i++) {
					info[i] += data[i];
				}
			}

			for (int i = 0; i < info.length; i++) {
				output[i] = (info[i] / points.size());
			}

			return output;
		}

		private static double distanceBetween(int[] vec1, int[] vec2) {

			double dist = 0;

			for (int i = 0; i < vec1.length; i++) {
				dist += Math.pow(vec1[i] - vec2[i], 2);
			}

			return Math.sqrt(dist);
		}

		/**
		 * This class implements a Cluster of an Antipole tree. The clusters are the
		 * leaves of the tree
		 */
		static class Cluster extends AntipoleTree {
			public Cluster(){
				
			}
			
			public Cluster(FeatureMatrix image, List<Point> objects) {
				this.image = image;
			
				/* Asignar el numero de segmento e invocar al observer */
				for (Point o : objects) {
					image.getSegment()[o.y][o.x] = segmentNum;
				}

				segmentNum++;
				observer.onChange();
			}
		}

		static class InternalNode extends AntipoleTree {
			
			public InternalNode(FeatureMatrix image) {
				this.image = image;
			}

			public InternalNode(FeatureMatrix image, Point A, Point B,
					AntipoleTree treeA, AntipoleTree treeB) {
				this.image = image;
			}
		}

		static class Pair<T> {
			public T A, B;

			public Pair(T a, T b) {
				A = a;
				B = b;
			}
		}
	}
}
