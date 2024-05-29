package model;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import shared.Notify;
import shared.State;

@SuppressWarnings("deprecation")
class Pair<K, V>{

    K key;
    V value;

    public Pair(K key, V  value) {
        this.key = key;
        this.value = value;
    }

    public void setValue(V value) {
        this.value = value;
    }
    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }
    public K getKey() {
        return key;
    }

}
@SuppressWarnings("deprecation")
public class GraphModel extends Observable{
	private List<Node> nodes = new ArrayList<Node>();
	private int[][] matrix = new int[0][0];
	private Map<Node, Integer> nodeMap = new HashMap<Node, Integer>();
	private Map<Line, Pair<Integer, Integer>> lineMap = new HashMap<>();
	private iShape target;
	private State state = State.DEFAULT;
	private Algorithm algorithm;
	private int[] searchResult = null;
	private Log logger = new Log();
	private Color[] colors = new Color[] {
			Color.green,
			Color.orange,
			Color.cyan,
			Color.pink,
			Color.yellow,
			Color.red,
			Color.MAGENTA
	};
	
	public void add_node(Node node) {
		this.nodes.add(node);
		String message = String.format("Thêm node [%s]", node.getName());
		logMessage(message);
		this.searchResult = null;
		this.setChanged();
		this.notifyObservers(Notify.LOG);
		reBuild_Model();
	}
	
	public void update_node(Node node, String name) {
		node.setName(name);
		this.setChanged();
		this.notifyObservers(Notify.CHANGED_VALUE);
	}
	
	public void remove_node(Node node) {
		if (node == null) return;
		int i = nodeMap.get(node);
		//xóa các cạnh k�? liên quan đến đỉnh này
		var keySet = lineMap.keySet();
		for (Iterator<Line> iterator = keySet.iterator(); iterator.hasNext();) {
			var key = iterator.next();
			if (key.getStart() == node || key.getEnd() == node) iterator.remove();
		}
		//xóa cạnh trong ma trận k�?
		for (int j = 0; j < nodes.size(); j++) {
//			System.out.println(String.format("I'm trying to remove element [%s][%s] and [%s][%s]", i, j, j, i));
			matrix[i][j] = 0;
			matrix[j][i] = 0;
		}
		this.searchResult = null;
		String message = String.format("Xóa node [%s]", node.getName());
		logMessage(message);
		this.setChanged();
		this.notifyObservers(Notify.LOG);
		this.nodes.remove(i);
		this.nodeMap.remove(node);
		reBuild_Model();
	}
	
	public Node get_node(Point p) {
		return nodes.stream()
			.filter(node -> node.contains(p))
			.findFirst()
			.orElse(null);
	}
	
	public int get_node_index(Node node) {
		return this.nodeMap.get(node);
	}
	
	public void move_node(int index, Point new_Location) {
		var node = nodes.get(index);
		node.moveTo(new_Location);
	}
	
	public void add_line(Line line) {
		int i = nodeMap.get(line.getStart());
		int j = nodeMap.get(line.getEnd());
		lineMap.put(line, new Pair<Integer, Integer>(i, j));
		this.matrix[i][j] = line.getValue();
		this.searchResult = null;
		this.setChanged();
		this.notifyObservers(Notify.CHANGED_STRUCTURE);
		String message = String.format("Thêm cạnh [%s -> %s]", line.getStart().getName(), line.getEnd().getName());
		logMessage(message);
		this.setChanged();
		this.notifyObservers(Notify.LOG);
	}
	
	public void update_line(Line line, int value) {
		int i = nodeMap.get(line.getStart());
		int j = nodeMap.get(line.getEnd());
		matrix[i][j] = value;
		line.setValue(value);
		this.setChanged();
		this.notifyObservers(Notify.CHANGED_STRUCTURE);
	}
	
	public void remove_line(Line line) {
		var pair = lineMap.get(line);
		matrix[pair.key][pair.value] = 0;
		lineMap.remove(line);
		this.searchResult = null;
		this.setChanged();
		this.notifyObservers(Notify.CHANGED_STRUCTURE);
		String message = String.format("Xóa cạnh [%s -> %s]", line.getStart().getName(), line.getEnd().getName());
		logMessage(message);
		this.setChanged();
		this.notifyObservers(Notify.LOG);
	}
	
	public Line get_line(Point p) {
		return lineMap.keySet().stream()
				.filter(line -> line.isClicked(p))
				.findFirst()
				.orElse(null);
	}
	
	public int[] get_line_index(Line line) {
		var pair = lineMap.get(line);
		return new int[] {pair.key, pair.value};
	}
	
	public boolean check_line_existed(Node start, Node end) {
		int i = nodeMap.get(start);
		int j = nodeMap.get(end);
		return matrix[i][j] != 0 || matrix[j][i] != 0;
	}

	private void reBuild_Model() {
		int size = nodes.size();
		for (int i = 0; i < size; i++) {
			Node node = nodes.get(i);
			if (!nodeMap.containsKey(node)) nodeMap.put(node, i);
			nodeMap.replace(node, i);
		}
		int[][] temp = new int[size][size];
		lineMap.keySet().stream()
		.forEach(line -> {
			Node start = line.getStart();
			Node end = line.getEnd();
			int value = line.getValue();
			int i = nodeMap.get(start);
			int j = nodeMap.get(end);
			var pair = lineMap.get(line);
			pair.key = i;
			pair.value = j;
			temp[i][j] = value;
		});
		this.searchResult = null;
		this.matrix = temp;
		this.setChanged();
		this.notifyObservers(Notify.CHANGED_STRUCTURE);
	}
	
	public void draw_model(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		if (target != null) target.outline(g2, Color.red);
		nodes.forEach(node -> {
			int v = nodeMap.get(node);
			int componentIndex = ((AbstractAlgorithm) algorithm).getComponent(v);
			Color color = colors[componentIndex % colors.length];
			node.draw(g2, color);
		});
		lineMap.keySet().forEach(line -> line.draw(g2, Color.black));
		if (searchResult != null) showSearchResult(g2);
		g2.dispose();
	}
	
	public iShape getTarget() {
		return target;
	}
	
	public void setTarget(iShape target) {
		this.target = target;
		this.setChanged();
		this.notifyObservers(target);
	}
	
	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
		this.setChanged();
		this.notifyObservers(Notify.UNCHANGED);
	}
	
	public int[][] getMatrix() {
		return matrix;
	}
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	public Node getNode(int index) {
		return nodes.get(index);
	}
	
	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
		((AbstractAlgorithm) algorithm).setMatrix(matrix);
	}
	
	public void showSearchResult(Graphics g) {
		if (searchResult == null) return;
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		int current, next = 0;
		//lap qua ket qua duyet do thi
		for (int i = 0; i < searchResult.length; i++) {
			current = i;
			next = i + 1;
			if (next == searchResult.length) break;
			Node start = nodes.get(searchResult[current]);
//			System.out.println("start = "+start);
			Node end = nodes.get(searchResult[next]);
//			System.out.println("end = "+end);
			(new Line(start, end, 0)).drawSubLine(g2, Color.blue);
		}
		g2.dispose();
	}
	
	public int[] search(int start) {
		String message;
		if (algorithm == null) throw new IllegalStateException("Không có thuật toán tìm kiếm");
		var rs = algorithm.search(start);
		this.searchResult = rs;
		this.setChanged();
		this.notifyObservers(Notify.UNCHANGED);
		String[] names = new String[rs.length];
		for (int n = 0; n < rs.length; n++) {
			names[n] = "["+getNode(rs[n]).getName()+"]";	
		}
		message = String.format("Kết quả duyệt %s: %s", algorithm.getName(), String.join("->", names));
		logMessage(message);
		this.setChanged();
		this.notifyObservers(Notify.LOG);
		return rs;
	}
	
	public void readFile(File file) {
		String content = "";
		try {
			Scanner reader = new Scanner(file);
			int num = Integer.parseInt(reader.nextLine());
			int[][] temp = new int[num][num];
			int row = 0;
			//reset model
			while (reader.hasNextLine()) {
				int column = 0;
				String line = reader.nextLine();
				content += line + "\n";
				Scanner lineReader = new Scanner(line);
				while (lineReader.hasNext()) {
					int x = lineReader.nextInt();
					temp[row][column++] = x;
				}
				lineReader.close();
				row++;
			}
			reader.close();
			logMessage(String.format("Đã đọc file tên %s: \nMa trận thu được:\n%s Tiến hành tạo Model tương ứng:", file.getName(), content));
			this.setChanged();
			this.notifyObservers(Notify.LOG);
			this.matrix = temp;
			createModel(temp);
			this.setChanged();
			this.notifyObservers(Notify.CHANGED_STRUCTURE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void saveFile(File file) {
		String content = "";
		content += matrix.length+"\n";
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				content += matrix[i][j] + " ";
			}
			content = content.trim() + "\n";
		}
		System.out.println(content);
		Writer wr;
		try {
			wr = new FileWriter(file);
			wr.write(content);
			wr.flush();
			wr.close();
			logMessage(String.format("Đã ghi file %s: \nNội dung:\n%s", file.toString(), content));
			this.setChanged();
			this.notifyObservers(Notify.LOG);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createModel(int[][] arr) {
		this.nodes.clear();
		this.nodeMap.clear();
		this.lineMap.clear();
		int size = arr.length;
		for (int i = 0; i < size; i++) {
			Node node = new Node(new Point(0, 0), i+"");
			add_node(node);
		}
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				if (arr[i][j] == 0) continue;
				Node start = nodes.get(i);
				Node end = nodes.get(j);
				add_line(new Line(start, end, arr[i][j]));
			}
		}
	}
	
	public int countComponent() {
		return algorithm.countComponent();
	}
	
	public void logMessage(String message) {
		logger.addLog(message);
	}
	
	public Log getLogger() {
		return logger;
	}
}
