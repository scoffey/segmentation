package model.segmentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.FeatureMatrix;
import model.SegmentationObserver;

/**
 * Implementación del método de segmentación split and merge.
 */
public class SplitAndMerge extends SegmentationAlgorithm {

	private FeatureMatrix image;
	private SegmentationObserver observer;

	private double splitStandardDeviation = 5;
	private double mergeStandardDeviation = 10;
	private int minSize = 3;

	public void process(FeatureMatrix image, SegmentationObserver observer,
			HashMap<String, String> params) {
		this.image = image;
		this.observer = observer;

		/* Levantamos los parámetros de la segmentación. */

		Double auxDouble;
		Integer auxInteger;
		String s;
		s = params.get("splitStandardDeviation");
		System.out.println(s);
		if (s != null && (auxDouble = Double.parseDouble(s)) != null) {
			splitStandardDeviation = auxDouble;
		}
		s = params.get("mergeStandardDeviation");
		System.out.println(s);
		if (s != null && (auxDouble = Double.parseDouble(s)) != null) {
			mergeStandardDeviation = auxDouble;
		}
		s = params.get("minSize");
		System.out.println(s);
		if (s != null && (auxInteger = Integer.parseInt(s)) != null) {
			minSize = auxInteger;
		}

		System.out.println("Ejecutando split and merge "
				+ splitStandardDeviation + " " + mergeStandardDeviation + " "
				+ minSize);
	}

	public void run() {
		Set<ImageZone> zones = new HashSet<ImageZone>();
		int currentSegmentIndex = 1;
		boolean changed = true;

		zones.add(new ImageZone(0, image.getWidth(), 0, image.getHeight(),
				currentSegmentIndex));

		/* SPLIT */
		while (changed) {
			if (Thread.interrupted())
				return;
			Set<ImageZone> auxZones = new HashSet<ImageZone>();

			changed = false;

			for (ImageZone zone : zones) {

				/* Pintar en la imagen los segmentos */
				for (int i = zone.yFrom; i < zone.yTo; i++) {
					for (int j = zone.xFrom; j < zone.xTo; j++) {
						image.getSegment()[i][j] = (byte) zone.segment.segmentIndex;
					}
				}

				/* Determinar si el segmento se debe dividir o no */
				if (zone.isHomogeneus() || zone.size() <= minSize
						|| zone.xTo - zone.xFrom == 1
						|| zone.yTo - zone.yFrom == 1) {

					/* No se divide */
					auxZones.add(zone);
				} else {

					/* Se divide */
					changed = true;
					auxZones.addAll(Arrays.asList(zone
							.divide(currentSegmentIndex)));
					currentSegmentIndex += 4;
				}
			}

			observer.onChange();
			zones = auxZones;
		}

		/* COSMOVISION */
		ArrayList<ImageSegment> segments = new ArrayList<ImageSegment>();
		for (ImageZone zone : zones) {
			if (Thread.interrupted())
				return;
			ImageSegment currentSegment = zone.segment;

			Set<ImageZone> neighbourZones = new HashSet<ImageZone>();
			neighbourZones.addAll(zone.north);
			neighbourZones.addAll(zone.south);
			neighbourZones.addAll(zone.east);
			neighbourZones.addAll(zone.west);

			for (ImageZone z : neighbourZones) {
				if (z.segment != currentSegment) {
					currentSegment.neighbours.add(z.segment);
				}
			}
			segments.add(currentSegment);
		}

		/* MERGE */
		changed = true;
		while (changed) {
			if (Thread.interrupted())
				return;
			changed = false;

			/*
			 * Elimino los segmentos que no contengan zonas y ordeno la lista de
			 * menor a mayor por tamaño.
			 */
			sortAndRemoveEmpty(segments);

			/*
			 * Itero sobre los segmentos y busco si puedo juntar a alguien con
			 * algun vecino.
			 */
			for (ImageSegment segment : segments) {

				/* Si quedo alguno sin zonas lo salteo. */
				if (segment.zones.size() == 0)
					continue;

				/* Busco el mejor vecino homogéneo. */
				ImageSegment bestNeighbour = segment
						.getBestHomogeneousNeighbour();

				/* Si lo encontré, me uno a él. */
				if (bestNeighbour != null) {

					segment.mergeWithNeighbour(bestNeighbour);
					changed = true;

					/* Repinto el segmento en la imagen. */
					for (ImageZone zone : segment.zones) {
						for (int i = zone.yFrom; i < zone.yTo; i++) {
							for (int j = zone.xFrom; j < zone.xTo; j++) {
								image.getSegment()[i][j] = (byte) (segment.segmentIndex);
							}
						}
					}
				}
			}
			observer.onChange();
		}
		if (observer != null) {
			observer.onComplete();
		}
	}

	/**
	 * Dada una lista de segmentos, los ordena de menor a mayor tamaño, elimina
	 * los que no contengan zonas (segmentos vacíos) y redistribuye los núemeros
	 * de segmentos desde 1.
	 * 
	 * @param segments
	 *            Lista de segmentos a procesar.
	 */
	private void sortAndRemoveEmpty(ArrayList<ImageSegment> segments) {

		List<ImageSegment> auxList = new ArrayList<ImageSegment>();
		for (ImageSegment s : segments) {
			if (s.size() != 0) {
				auxList.add(s);
			}
		}

		ImageSegment[] segmentsArray = new ImageSegment[auxList.size()];
		Arrays.sort(auxList.toArray(segmentsArray),
				new Comparator<ImageSegment>() {
					public int compare(ImageSegment arg0, ImageSegment arg1) {
						return arg0.size() - arg1.size();
					}
				});

		segments.clear();
		int index = 1;
		for (ImageSegment s : segmentsArray) {
			segments.add(s);
			s.segmentIndex = index++;
		}
		return;
	}

	/**
	 * Un segmento de una imagen. Esta formado por varios ImageZone. Contiene un
	 * conjunto de vecinos en las cuatro direcciones.
	 */
	class ImageSegment {

		private Set<ImageSegment> neighbours = new HashSet<ImageSegment>();
		private Set<ImageZone> zones = new HashSet<ImageZone>();
		private int segmentIndex;

		/**
		 * Crea un nuevo segmento con una Ãºnica zona y el numero de segmento
		 * especificado.
		 */
		public ImageSegment(ImageZone zone, int segment) {
			this.zones.add(zone);
			zone.segment = this;
			this.segmentIndex = segment;
		}

		/**
		 * Busca entre todos los vecinos aquellos con los cuales es homogéneo, y
		 * de todos estos retorna el que tendría menor desvío en el feature
		 * vector si se juntara con él.
		 * 
		 * @return El mejor vecino homogéneo.
		 */
		public ImageSegment getBestHomogeneousNeighbour() {

			double bestNeighbourDistance = Double.MAX_VALUE;
			ImageSegment bestNeighbour = null;

			/* Busco el mejor vecino */
			for (ImageSegment neighbour : this.neighbours) {
				if (neighbour.size() == 0) {
					continue;
				}
				if (this.isHomogeneousWithRespectTo(neighbour)) {
					if (this.distanceWithNeighbour(neighbour) < bestNeighbourDistance) {
						bestNeighbourDistance = this
								.distanceWithNeighbour(neighbour);
						bestNeighbour = neighbour;
					}
				}
			}
			return bestNeighbour;
		}

		/**
		 * Verifica si el segmento actual es homogÃ©neo con respecto a otro
		 * segmento.
		 */
		public boolean isHomogeneousWithRespectTo(ImageSegment segment) {
			for (int i = 0; i < image.getDepth(); i++) {
				if (this.distanceWith(segment, i) > mergeStandardDeviation)
					return false;
			}
			return true;
		}

		/**
		 * Junta el segmento actual con otro. Todas las zonas del otro son
		 * transferidas al segmento actual, asi como los vecinos, y todas las
		 * referencias son actualizadas. Luego de esta invocación, el segmento
		 * que se pasó por parámetro podría ser eliminado de la lista de
		 * segmentos.
		 * 
		 * @param segment
		 *            Segmento con el cual unirme.
		 */
		public void mergeWithNeighbour(ImageSegment segment) {

			segment.segmentIndex = 0;

			/* Mover todas las zonas de segment hacia mi */
			for (ImageZone zone : segment.zones) {
				zone.segment = this;
			}
			this.zones.addAll(segment.zones);
			segment.zones.clear();

			/*
			 * Cambiar las referencias desde los vecinos de segment. Ojo porque
			 * entre estos vecinos estoy yo (yo soy vecino de mi vecino)
			 */
			for (ImageSegment neighbour : segment.neighbours) {
				neighbour.neighbours.remove(segment); /* siempre */

				/* Evito tenerme a mi como vecino. */
				if (neighbour != this) {
					neighbour.neighbours.add(this);
				}
			}

			/* Agregarle a mis vecinos los vecinos de segment. */
			this.neighbours.addAll(segment.neighbours);
			this.neighbours.remove(this);

			/* Borramos los vecinos y las zonas del vecino */
			segment.neighbours.clear();
			segment.zones.clear();
		}

		/**
		 * Calcula el desvío estándar de cada componente del feature vector con
		 * respecto al del vecino que recibe por argumento. Luego toma la norma
		 * de dicho vector y la retorna.
		 * 
		 * @param segment
		 *            Vecino con el cual medir la distancia.
		 * @return Norma de los desvíos.
		 */
		public double distanceWithNeighbour(ImageSegment segment) {
			double acum = 0;
			for (int i = 0; i < image.getDepth(); i++) {
				acum += Math.pow(this.distanceWith(segment, i), 2);
			}
			return Math.sqrt(acum);
		}

		/**
		 * Calcula el desvío de todos los píxeles del segmento actual con los
		 * del segmento, para una sola componente del feature vector.
		 * 
		 * @param segment
		 *            Segmento con el cual compararme.
		 * @param feature
		 *            Dimension a evaluar del feature vector.
		 * @return Desvio estándar de dicha dimensión.
		 */
		private double distanceWith(ImageSegment segment, int feature) {

			double mean = 0;
			double standardDeviation = 0;
			int count = 0;

			for (ImageZone zone : zones) {
				for (int i = zone.yFrom; i < zone.yTo; i++) {
					for (int j = zone.xFrom; j < zone.xTo; j++) {
						mean += image.getData()[i][j][feature];
						count++;
					}
				}
			}

			for (ImageZone zone : segment.zones) {
				for (int i = zone.yFrom; i < zone.yTo; i++) {
					for (int j = zone.xFrom; j < zone.xTo; j++) {
						mean += image.getData()[i][j][feature];
						count++;
					}
				}
			}

			mean = mean / count;

			for (ImageZone zone : zones) {
				for (int i = zone.yFrom; i < zone.yTo; i++) {
					for (int j = zone.xFrom; j < zone.xTo; j++) {
						standardDeviation += Math.pow(
								image.getData()[i][j][feature] - mean, 2);
					}
				}
			}
			for (ImageZone zone : segment.zones) {
				for (int i = zone.yFrom; i < zone.yTo; i++) {
					for (int j = zone.xFrom; j < zone.xTo; j++) {
						standardDeviation += Math.pow(
								image.getData()[i][j][feature] - mean, 2);
					}
				}
			}
			standardDeviation = Math.sqrt(standardDeviation / count);
			return standardDeviation;
		}

		/**
		 * Calcula el tamaño de un segmento como la sumatoria de los tamaños de
		 * las zonas que contiene.
		 * 
		 * @return Cantidad de píxeles que abarca el segmento.
		 */
		public int size() {
			int count = 0;
			for (ImageZone zone : zones) {
				count += (zone.yTo - zone.yFrom) * (zone.xTo - zone.xFrom);
			}
			return count;
		}
	}

	/**
	 * Un cuadrado de la imagen. Un segmento va a estar formado por varios de
	 * estos.
	 */
	class ImageZone {
		private int xFrom;
		private int yFrom;
		private int xTo;
		private int yTo;

		private ImageSegment segment;

		private Set<ImageZone> east = new HashSet<ImageZone>();
		private Set<ImageZone> west = new HashSet<ImageZone>();
		private Set<ImageZone> north = new HashSet<ImageZone>();
		private Set<ImageZone> south = new HashSet<ImageZone>();

		public ImageZone(int x_from, int x_to, int y_from, int y_to,
				int segmentIndex) {

			if (x_to <= x_from || y_to <= y_from) {
				throw new RuntimeException(
						"No puede existir una zona de menos de 1 pixel.");
			}

			this.xFrom = x_from;
			this.yFrom = y_from;
			this.xTo = x_to;
			this.yTo = y_to;

			this.segment = new ImageSegment(this, segmentIndex);
		}

		/**
		 * Divide un segmento que contiene solamente una zona, y retorna 4
		 * segmentos, cada uno con 1 zona.
		 */
		public ImageZone[] divide(int segment) {

			int x_mid = xFrom + ((xTo - xFrom) / 2);
			int y_mid = yFrom + ((yTo - yFrom) / 2);

			ImageZone nw = new ImageZone(xFrom, x_mid, yFrom, y_mid, segment);
			ImageZone ne = new ImageZone(x_mid, xTo, yFrom, y_mid, segment + 1);
			ImageZone sw = new ImageZone(xFrom, x_mid, y_mid, yTo, segment + 2);
			ImageZone se = new ImageZone(x_mid, xTo, y_mid, yTo, segment + 3);

			/* Vamos a fijar a los vecinos externos del norte */

			nw.north = new HashSet<ImageZone>();
			ne.north = new HashSet<ImageZone>();
			for (ImageZone neighbour : north) {
				neighbour.south.remove(this);

				if (neighbour.xFrom < nw.xTo) {
					nw.north.add(neighbour);
					neighbour.south.add(nw);
				}

				if (neighbour.xTo > ne.xFrom) {
					ne.north.add(neighbour);
					neighbour.south.add(ne);
				}
			}

			/* Vamos a fijar a los vecinos externos del sur */

			sw.south = new HashSet<ImageZone>();
			se.south = new HashSet<ImageZone>();
			for (ImageZone neighbour : south) {
				neighbour.north.remove(this);

				if (neighbour.xFrom < sw.xTo) {
					sw.south.add(neighbour);
					neighbour.north.add(sw);
				}

				if (neighbour.xTo > se.xFrom) {
					se.south.add(neighbour);
					neighbour.north.add(se);
				}
			}

			/* Vamos a fijar los vecinos externos del west */

			nw.west = new HashSet<ImageZone>();
			sw.west = new HashSet<ImageZone>();
			for (ImageZone neighbour : west) {
				neighbour.east.remove(this);

				if (neighbour.yFrom < nw.yTo) {
					nw.west.add(neighbour);
					neighbour.east.add(nw);
				}

				if (neighbour.yFrom < sw.yTo) {
					sw.west.add(neighbour);
					neighbour.east.add(sw);
				}
			}

			/* Vamos a los vecinos externos del east */

			ne.east = new HashSet<ImageZone>();
			se.east = new HashSet<ImageZone>();
			for (ImageZone neighbour : east) {
				neighbour.west.remove(this);

				if (neighbour.yFrom < ne.yTo) {
					ne.east.add(neighbour);
					neighbour.west.add(ne);
				}

				if (neighbour.yFrom < se.yTo) {
					se.east.add(neighbour);
					neighbour.west.add(se);
				}
			}

			/* Vecinos internos */
			nw.south = new HashSet<ImageZone>(Arrays.asList(sw));
			nw.east = new HashSet<ImageZone>(Arrays.asList(ne));
			ne.west = new HashSet<ImageZone>(Arrays.asList(nw));
			ne.south = new HashSet<ImageZone>(Arrays.asList(se));
			sw.north = new HashSet<ImageZone>(Arrays.asList(nw));
			sw.east = new HashSet<ImageZone>(Arrays.asList(se));
			se.north = new HashSet<ImageZone>(Arrays.asList(ne));
			se.west = new HashSet<ImageZone>(Arrays.asList(sw));

			return new ImageZone[] { nw, ne, sw, se };
		}

		/**
		 * Verifica si el segmento actual cumple el criterio de homogeneidad o
		 * no.
		 */
		public boolean isHomogeneus() {
			for (int i = 0; i < image.getDepth(); i++) {
				if (this.standardDeviation(i) > splitStandardDeviation)
					return false;
			}
			return true;
		}

		public double standardDeviation(int feature) {
			double mean = mean(feature);
			double acum = 0;
			int count = 0;

			for (int i = yFrom; i < yTo; i++) {
				for (int j = xFrom; j < xTo; j++) {
					acum += Math.pow(image.getData()[i][j][feature] - mean, 2);
					count++;
				}
			}

			acum = acum / count;
			return Math.sqrt(acum);
		}

		public double mean(int feature) {
			double acum = 0;
			int count = 0;

			for (int i = yFrom; i < yTo; i++) {
				for (int j = xFrom; j < xTo; j++) {
					acum += image.getData()[i][j][feature];
					count++;
				}
			}

			return acum / count;
		}

		public int size() {
			return (xTo - xFrom) * (yTo - yFrom);
		}
	}

}
