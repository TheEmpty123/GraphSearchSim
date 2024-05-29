package model;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class Line implements iShape, Observer{
	private Node start;
	private Node end;
	private Point pointFrom;
	private Point pointTo;
	private Point center_onLine;
	private int value;
	private final Font font = new Font("Roboto", Font.BOLD, 20);
	private Shape[] characters;
	private Rectangle rect;
	//mũi tên
	private Polygon shape;
	
	public Line(Node startNode, Node endNode, int value) {
		super();
		this.start = startNode;
		this.end = endNode;
		this.value = value;
		this.center_onLine = new Point((start.center.x + end.center.x) / 2, (start.center.y + end.center.y) / 2);
		this.rect = new Rectangle(center_onLine.x, center_onLine.y, 0, 0);
		this.shape = new Polygon(new int[]{-8, -8, 0}, new int[]{-8, 8, 0}, 3);
		this.pointFrom = new Point(0, 0);
		this.pointTo = new Point(0, 0);
		start.addObserver(this);
		end.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		_updateAll();
	}
	
	private void _updateAll() {
		Point p1 = start.center;
		Point p2 = end.center;
		int size = Node.SIZE;
		double aFrom = _getAngleBetween(p1, p2);
		double aTo = _getAngleBetween(p2, p1);
		
		//update pointFrom, pointTo
		this.pointFrom.x = _getPointOnCircle(p1, aFrom, size / 2).x;
		this.pointFrom.y = _getPointOnCircle(p1, aFrom, size / 2).y;
		
		this.pointTo.x = _getPointOnCircle(p2, aTo, size / 2).x;
		this.pointTo.y = _getPointOnCircle(p2, aTo, size / 2).y;
		
		//tính toán lại điểm giữa của line
		this.center_onLine.x = (p1.x + p2.x) / 2;
		this.center_onLine.y = (p1.y + p2.y) / 2;
		//update lại rect
		this.rect.x = center_onLine.x;
		this.rect.y = center_onLine.y;
	}
	
	//Tính toán lại điểm vẽ line trên Node hình tròn
	private Point _getPointOnCircle(Point center_point, double radians, int radius) {
	    double x = center_point.x;
	    double y = center_point.y;

	    radians = radians - Math.toRadians(90.0); // 0 becomes the top
	    // Calculate the outter point of the line
	    int xPosy = Math.round((float) (x + Math.cos(radians) * radius));
	    int yPosy = Math.round((float) (y + Math.sin(radians) * radius));

	    return new Point(xPosy, yPosy);
	}
	
	private void drawArrow(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		final AffineTransform tx = AffineTransform.getTranslateInstance(pointTo.x, pointTo.y);
		tx.rotate(Math.atan2(pointTo.y - pointFrom.y, pointTo.x - pointFrom.x));
		g2.fill(tx.createTransformedShape(shape));
		g2.dispose();
	}

	private Point[] getSubPoints() {
//      Get center point
		var p1 = start.coords;
		var p2 = end.coords;
		Point n1 = new Point(p1.x + Node.SIZE / 2, p1.y + Node.SIZE / 2);
		Point n2 = new Point(p2.x + Node.SIZE / 2, p2.y + Node.SIZE / 2);

//      Calculate angle between two Nodes
		float angle = (float) Math.toRadians(getAngle(n1, n2) + 20);

//    Get distance between 2 nodes
		int radius;

		radius = Node.SIZE - Node.SIZE / 6;

//      Determine coordinate point arround Node
		int incX = (int) (radius * Math.sin(angle - Math.toRadians(90)));
		int incY = (int) (radius * Math.cos(angle - Math.toRadians(90)));

		int r1 = (int) (radius * 2 * Math.cos(Math.toRadians(getAngle(n1, n2))));
		int r2 = (int) (radius * 2 * Math.sin(Math.toRadians(getAngle(n1, n2))));
		int x1 = n1.x + incX;
		int y1 = n1.y - incY;

		int x2 = n2.x + incX + r1;
		int y2 = n2.y - incY + r2;

		Point[] pts = new Point[2];
		pts[0] = new Point(x1, y1);
		pts[1] = new Point(x2, y2);
		return pts;
	}
	
	public int getDistance(Point n1, Point n2) {
        int d = (int) Math.sqrt(Math.pow(n2.getX() - n1.getX(), 2) + Math.pow(n2.getY() - n1.getY(), 2));
        return d;
    }
	
	//Tính toán góc giữa 2 điểm
	private double _getAngleBetween(Point from, Point to) {
	    // This is the difference between the anchor point
	    // and the mouse.  Its important that this is done
	    // within the local coordinate space of the component,
	    // this means either the MouseMotionListener needs to
	    // be registered to the component itself (preferably)
	    // or the mouse coordinates need to be converted into
	    // local coordinate space
		double deltaX = to.getX() - from.getX();
		double deltaY = to.getY() - from.getY();
		
	    // Calculate the angle...
	    // This is our "0" or start angle..
		double rotation = -Math.atan2(deltaX, deltaY);
		rotation = Math.toRadians(Math.toDegrees(rotation) + 180);
		return rotation;
	}
	
	public float getAngle(Point n1, Point n2) {
      float angle = (float) Math.toDegrees(Math.atan2(n1.y - n2.y, n1.x - n2.x));
      if (angle < 0) {
          angle += 360;
      }
      return angle;
  }

	@Override
	public void draw(Graphics g, Color color) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setStroke(new BasicStroke(3));
		g2.setColor(color);
		g2.drawLine(pointFrom.x, pointFrom.y, pointTo.x, pointTo.y);
		drawArrow(g2);
		drawString(g2);
		g2.dispose();
	}
	
	private void drawString(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
	    FontMetrics metrics = g.getFontMetrics(font);
	    GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), value+"");
	    int x = rect.x + (rect.width - metrics.stringWidth(value+"")) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    int num = gv.getNumGlyphs();
	    this.characters = new Shape[num];
	    for (int i = 0; i < num; i++) this.characters[i] = gv.getGlyphOutline(i);
	    g2.translate(x, y);
	    g2.setColor(Color.yellow);
	    Arrays.stream(this.characters).forEach(shape -> g2.fill(shape));
	    g2.setStroke(new BasicStroke(1));
	    g2.setColor(Color.red);
	    Arrays.stream(this.characters).forEach(shape -> g2.draw(shape));
	    g2.dispose();
	}
	
	@Override
	public void outline(Graphics g, Color color) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
        g2.setColor(color);
        g2.setStroke(new BasicStroke(5));
        g2.drawLine(pointFrom.x, pointFrom.y, pointTo.x, pointTo.y);
        AffineTransform tx = AffineTransform.getTranslateInstance (pointTo.x, pointTo.y);
        tx.scale(1.5, 1.5);
        tx.rotate (Math.atan2 (pointTo.y - pointFrom.y, pointTo.x - pointFrom.x));
        g2.fill (tx.createTransformedShape (shape));
        g2.dispose();
	}
	
	public void drawSubLine(Graphics g, Color color) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.90f));
        g2.setColor(color);
        var points = getSubPoints();
        g2.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
		final AffineTransform tx = AffineTransform.getTranslateInstance(points[1].x, points[1].y);
		tx.rotate(Math.atan2(points[1].y - points[0].y, points[1].x - points[0].x));
		g2.fill(tx.createTransformedShape(shape));
        g2.dispose();
	}

	@Override
	public boolean isClicked(Point p) {
		Line2D shape = new Line2D.Float(pointFrom, pointTo);
		return shape.ptLineDist(p) <= 1.7;
	}
	
	public int getValue() {
		return value;
	}
	
	public Node getStart() {
		return start;
	}
	
	public Node getEnd() {
		return end;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}
