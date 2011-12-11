package model.segmentation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import model.FeatureMatrix;
import model.SegmentationObserver;

/**
 * Implementación del método K-means de segmentación.
 */
public class KMeans extends SegmentationAlgorithm {

	private Cluster[] clusters;
	private FeatureMatrix image;
	private SegmentationObserver observer;
	private boolean useAllClusters = false;
	private int clustersCount = 10;

	// @Override
	public void process(FeatureMatrix image, SegmentationObserver observer,
			HashMap<String, String> params) {
		this.image = image;
		this.observer = observer;

		String s = params.get("useAllClusters");
		if (s != null) {
			useAllClusters = (new Integer(s) == 0) ? false : true;
		}

		s = params.get("clustersCount");
		if (s != null) {
			clustersCount = new Integer(s);
		}

		this.clusters = new Cluster[clustersCount];

	}

	private void generateClusters() {
		boolean hasChanged = true;
		boolean[] clustersEmptyStatus = new boolean[clusters.length];		

		/* Mientras no hayan cambios en los clusters */
		while (hasChanged && !isInterrupted()) {
			for (int i = 0; i < clustersEmptyStatus.length; i++)
				clustersEmptyStatus[i] = true;
			//System.out.println("iterando");
			hasChanged = false;
			/* Poner cada objeto en el cluster que le quede mas cerca */
			for (int k = 0; k < clusters.length; k++) {
				Cluster c = clusters[k];
				for (int i = 0; i < c.getObjects().size(); i++) {
					Point object = c.getObjects().get(i);
					int bestCluster = -1;

					double minDistance = Double.MAX_VALUE;

					/*
					 * Buscar el cluster que tiene su centroide más cerca de
					 * este objeto
					 */
					for (int m = 0; m < clusters.length; m++) {

						/* Si este cluster no esta ocupado, lo meto ahi y ya */
						if (useAllClusters
								&& (clusters[m].getCentroid() == null && clustersEmptyStatus[m])) {
							bestCluster = m;
							minDistance = 0;
							clustersEmptyStatus[m] = false;
							//System.out.println("clustrer vacio");
						} else {
							if (clusters[m].getCentroid() != null
									&& distanceBetween(KMeans.this.image
											.getData()[object.y][object.x],
											clusters[m].getCentroid()) < minDistance) {
								bestCluster = m;
								minDistance = distanceBetween(KMeans.this.image
										.getData()[object.y][object.x],
										clusters[m].getCentroid());
							}
						}
					}

					/* De ser necesario poner el objeto en el nuevo cluster */
					if (bestCluster != k) {
						hasChanged = true;
						KMeans.this.image.getSegment()[object.y][object.x] = (byte) bestCluster;
					}
				}

			}

			rebuildClusters();

			if (observer != null) {
				observer.onChange();
			}
		}
		if (observer != null) {
			observer.onComplete();
		}
	}

	private void rebuildClusters() {

		this.clusters = new Cluster[this.clusters.length];

		for (int i = 0; i < clusters.length; i++) {
			clusters[i] = new Cluster();
		}

		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				clusters[image.getSegment()[i][j]].add(new Point(j, i));
			}
		}

		for (int i = 0; i < clusters.length; i++) {
			clusters[i].updateCentroid();
		}

	}

	private void randomInit() {

		Random r = new Random();
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				int clusterIndex = r.nextInt(clusters.length);
				image.getSegment()[i][j] = (byte) clusterIndex;
				// System.out.println(clusterIndex);
			}
		}

	}

	private double distanceBetween(int[] vec1, int[] vec2) {

		double dist = 0;

		for (int i = 0; i < vec1.length; i++) {
			dist += Math.pow(vec1[i] - vec2[i], 2);
		}

		return Math.sqrt(dist);
	}

	class Cluster {
		private List<Point> objects; /* objetos que alberga */
		private int[] centroid; /* centroide */

		public Cluster() {
			objects = new ArrayList<Point>();
			centroid = null;
		}

		public Cluster(List<Point> objects) {
			this.objects = objects;
			this.centroid = euclideanMeanDistance(objects);
		}

		public void add(Point o) {
			objects.add(o);
			/* No se recomputa el centroide por un tema de performance */
		}

		public void updateCentroid() {
			if (objects != null && objects.size() > 0) {
				centroid = euclideanMeanDistance(objects);
			} else {
				centroid = null;
			}
		}

		public int[] getCentroid() {
			return centroid;
		}

		public List<Point> getObjects() {
			return objects;
		}

		public int[] euclideanMeanDistance(List<Point> points) {

			int[] info = new int[KMeans.this.image.getDepth()];
			int[] output = new int[info.length];

			for (Point p : points) {
				int[] data = KMeans.this.image.getData()[p.y][p.x];
				for (int i = 0; i < data.length; i++) {
					info[i] += data[i];
				}
			}

			for (int i = 0; i < info.length; i++) {
				output[i] = (info[i] / points.size());
			}

			return output;
		}
	}

	public void run() {
		randomInit();
		rebuildClusters();
		generateClusters();
	}
}
