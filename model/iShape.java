package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public interface iShape {
	public void draw(Graphics g, Color color);
	public void outline(Graphics g, Color color);
	public boolean isClicked(Point p);
}
