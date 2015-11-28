package org.puc.rio.war;

import java.awt.Dimension;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.puc.rio.war.model.Continent;
import org.puc.rio.war.model.Deck;
import org.puc.rio.war.model.Map;
import org.puc.rio.war.model.TerritoryCard;

import com.google.gson.Gson;

public class Util {
	/* Dummy classes for json parsing */
	class Territories {
		List<Territory> territories;

		public void setTerritories(List<Territory> ts) {
			this.territories = ts;
		}

		public List<Territory> getTerritories() {
			return this.territories;
		}
	}

	class Territory {
		String name;
		Integer continent;
		Integer type;
		List<List<java.lang.Double>> boundsPoints;

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getContinent() {
			return this.continent;
		}

		public void setContinent(int c) {
			this.continent = c;
		}

		public int getType() {
			return this.type;
		}

		public void setType(int c) {
			this.type = c;
		}

		public List<List<java.lang.Double>> getBoundsPoints() {
			return this.boundsPoints;
		}

		public void setBoundsPoints(List<List<java.lang.Double>> cos) {
			this.boundsPoints = cos;
		}
	}

	public static void loadTerritories(Map map, Deck deck) {
		String jsonContent;
		try {
			jsonContent = readFile("resources/territories.json",
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Territories ts = new Gson().fromJson(jsonContent, Territories.class);
		for (Territory t : ts.getTerritories()) {
			List<Point2D.Double> points = new LinkedList<Point2D.Double>();
			for (List<java.lang.Double> bp : t.getBoundsPoints()) {
				Point2D.Double point = new Point2D.Double(bp.get(0), bp.get(1));
				points.add(point);
			}
			org.puc.rio.war.model.Territory newTerritory = new org.puc.rio.war.model.Territory(
					t.getName(), points, Continent.getById(t.getContinent()));
			map.addTerritory(newTerritory);
			TerritoryCard c = new TerritoryCard(newTerritory, t.getType());
			deck.addCard(c);
		}
	}

	public static ArrayList<Line2D.Double> getLineSegments(GeneralPath p) {

		ArrayList<double[]> linePoints = new ArrayList<>();
		ArrayList<Line2D.Double> lineSegments = new ArrayList<>();

		double[] coords = new double[6];

		for (PathIterator pi = p.getPathIterator(null); !pi.isDone(); pi.next()) {
			// The type will be SEG_LINETO, SEG_MOVETO, or SEG_CLOSE
			// since p is composed of straight lines
			int type = pi.currentSegment(coords);

			// We record a double array of {segment type, x coord, y coord}
			double[] pathIteratorCoords = { type, coords[0], coords[1] };
			linePoints.add(pathIteratorCoords);
		}

		double[] start = new double[3]; // To record where each polygon starts

		for (int i = 0; i < linePoints.size(); i++) {
			// If we're not on the last point, return a line from this point to
			// the next
			double[] currentElement = linePoints.get(i);

			// We need a default value in case we've reached the end of the
			// ArrayList
			double[] nextElement = { -1, -1, -1 };
			if (i < linePoints.size() - 1) {
				nextElement = linePoints.get(i + 1);
			}

			// Make the lines
			if (currentElement[0] == PathIterator.SEG_MOVETO) {
				start = currentElement; // Record where the polygon started to
										// close it later
			}

			if (nextElement[0] == PathIterator.SEG_LINETO) {
				lineSegments.add(new Line2D.Double(currentElement[1],
						currentElement[2], nextElement[1], nextElement[2]));
			} else if (nextElement[0] == PathIterator.SEG_CLOSE) {
				lineSegments.add(new Line2D.Double(currentElement[1],
						currentElement[2], start[1], start[2]));
			}
		}
		return lineSegments;
	}

	public static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static Dimension getGameSize() {
		Dimension x = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		if(System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0){
			x.setSize(x.width, (int)(x.height*0.9));
		}
		return x;
	}
}