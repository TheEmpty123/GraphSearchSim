package view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.CanvasController;
import model.GraphModel;
import model.Line;
import model.Node;
import model.iShape;
import shared.State;

@SuppressWarnings("deprecation")
public class Canvas extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	private CanvasController controller;
	Line2D tempLine = new Line2D.Float();
	Ellipse2D tempNode = new Ellipse2D.Float();
	
	public Canvas(GraphModel model) {
		super();
		this.controller = new CanvasController(this, model);
		model.addObserver(this);
	}

	public void paintBackground(Graphics2D g2) {
		g2.setColor(Color.LIGHT_GRAY);
		//Vẽ các đư�?ng gạch ngang cách nhau 10px
		for (int i = 0; i < this.getSize().height; i += 10) {
			Shape line = new Line2D.Float(0, i, this.getSize().width, i);
			g2.draw(line);
		}
		//Vẽ các đư�?ng gạch d�?c cách nhau 10px
		for (int i = 0; i < this.getSize().width; i += 10) {
			Shape line = new Line2D.Float(i, 0, i, this.getSize().height);
			g2.draw(line);
		}
	}
	
	public void drawTempLine(Graphics g) {
		int x1 = (int) tempLine.getX1();
		int x2 = (int) tempLine.getX2();
		int y1 = (int) tempLine.getY1();
		int y2 = (int) tempLine.getY2();
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(Color.DARK_GRAY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke( new BasicStroke(2.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, new float[]{10.0f}, 0.0f));
		g2.drawLine(x1, y1, x2, y2);
		repaint();
	}
	
	public void drawTempNode(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		int x = (int) tempNode.getX();
		int y = (int) tempNode.getY();
		int size = (int) tempNode.getWidth();
		//Khử răng cưa
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//Thêm Stroked
		g2.setStroke( new BasicStroke(2.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, new float[]{10.0f}, 0.0f));
//        this.lineList.forEach(line -> line.draw(g2));
		g2.setColor(Color.black);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		g2.drawOval(x, y, size, size);
		//Thêm độ trong suốt
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
        //Fill Circle
		g2.setColor(Color.green);
		g2.fillOval(x, y, size, size);
        g2.dispose();
        repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
    	Graphics2D g2 = (Graphics2D) g;
    	paintBackground(g2);
    	controller.getModel().draw_model(g2);
    	if (controller.getModel().getState() == State.CREATING_LINE) drawTempLine(g2);
    	if (controller.getModel().getState() == State.CREATING_NODE) drawTempNode(g2);
	}
	
	public void setTempLine(int x1, int y1, int x2, int y2) {
		this.tempLine.setLine(x1, y1, x2, y2);
	}
	
	public void setTempNode(int x, int y, int size) {
		this.tempNode.setFrame(x, y, size, size);
	}
	
	public void moveNode(Point mousePoint) {
		var target = controller.getModel().getTarget();
		if (target == null || !(target instanceof Node)) return;
		Node ntarget = (Node) target;
		int nIndex = controller.getModel().get_node_index(ntarget);
		Point location = new Point(mousePoint.x - Node.SIZE / 2, mousePoint.y - Node.SIZE / 2);
		controller.getModel().move_node(nIndex, location);
		repaint();
	}
	
	public void addLine(Node startNode, Node endNode) {
		if (startNode == null || endNode == null) return;
		if (controller.getModel().check_line_existed(startNode, endNode)) {
			JOptionPane.showMessageDialog(this, "Có cạnh tồn tại giữa 2 đỉnh này!", "Không thể tạo cạnh", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String optionMsg = String.format("Tạo cạnh từ %s -> %s", startNode.getName(), endNode.getName());
		String str_value = JOptionPane.showInputDialog(this, optionMsg, "Tạo cạnh", JOptionPane.PLAIN_MESSAGE);
		if (str_value == null || !str_value.matches("^-?\\d*(\\.\\d+)?$") || str_value.isEmpty()) {
  		  JOptionPane.showMessageDialog(this, 
				  "Giá trị nhập vào không xác định!", 
				  "Không thể tạo cạnh", 
				  JOptionPane.ERROR_MESSAGE);
		  return;
		}
		controller.getModel().add_line(new Line(startNode, endNode, Integer.parseInt(str_value)));
		controller.getModel().setState(State.DEFAULT);
	}
	
	public void addNode(Point clickedPoint) {
		String optionMsg = String.format("Nhập tên đỉnh cần tạo");
		String str_value = JOptionPane.showInputDialog(this, optionMsg, "Tạo đỉnh", JOptionPane.PLAIN_MESSAGE);
		if (str_value == null) return;
		controller.getModel().add_node(new Node(clickedPoint, str_value));
		controller.getModel().setState(State.DEFAULT);
	}
	
	public void setTarget(Point p) {
		//tìm target tương ứng dựa trên point p
		iShape target = controller.getModel().get_node(p);
		target = (target == null) ? controller.getModel().get_line(p) : target;
		controller.getModel().setTarget(target);
	}

	@Override
	public void update(Observable o, Object arg) {
		repaint();
		System.out.println("updated");
	}
}
