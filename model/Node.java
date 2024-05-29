package model;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D.Float;
import java.util.Arrays;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class Node extends Observable implements iShape{
	Point coords;
	private String name;
	public static final int SIZE = 50;
	final Point center;
	private final Font font = new Font("Roboto", Font.BOLD, 30);
	private Shape[] characters;
	private Rectangle rect;
	
	public Node(Point coords, String name) {
		super();
		this.coords = coords;
		this.name = name;
		this.rect = new Rectangle(coords.x, coords.y, SIZE, SIZE);
		this.center = new Point(coords.x + SIZE / 2, coords.y + SIZE / 2);
	}
	
	public boolean contains(Point point) {
		var e = new Ellipse2D.Float(coords.x, coords.y, SIZE, SIZE);
		return e.contains(point);
	}

	@Override
	public void draw(Graphics g, Color color) {
		this.setChanged();
		this.notifyObservers();
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.black);
		g2.drawOval(coords.x, coords.y, SIZE, SIZE);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
		g2.setColor(color);
		g2.fillOval(coords.x, coords.y, SIZE, SIZE);
		drawString(g2);
		g2.dispose();
	}
	
	@Override
	public boolean isClicked(Point p) {
		var e = new Ellipse2D.Float(coords.x, coords.y, SIZE, SIZE);
		return e.contains(p);
	}
	
	public void moveTo(Point p) {
		this.coords.x = p.x;
		this.coords.y = p.y;
		this.rect.x = p.x;
		this.rect.y = p.y;
		center.x = coords.x + SIZE / 2;
		center.y = coords.y + SIZE / 2;
	}
	
	public void drawString(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
	    FontMetrics metrics = g.getFontMetrics(font);
	    GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), name);
	    int x = rect.x + (rect.width - metrics.stringWidth(name)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    int num = gv.getNumGlyphs();
	    this.characters = new Shape[num];
	    for (int i = 0; i < num; i++) this.characters[i] = gv.getGlyphOutline(i);
	    g2.translate(x, y);
	    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	    g2.setColor(Color.yellow);
	    Arrays.stream(this.characters).forEach(shape -> g2.fill(shape));
	    g2.setStroke(new BasicStroke(1));
	    g2.setColor(Color.red);
	    Arrays.stream(this.characters).forEach(shape -> g2.draw(shape));
	    g2.dispose();
	}
	
	public void outline(Graphics g, Color color) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
        g2.setColor(color);
        g2.setStroke(new BasicStroke(10));
        g2.drawOval(coords.x, coords.y, SIZE, SIZE);
        g2.dispose();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
