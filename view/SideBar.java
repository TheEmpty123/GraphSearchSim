package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import controller.SideBarController;
import model.GraphModel;
import model.Line;
import model.Node;
import model.iShape;
import shared.Notify;
import shared.State;

@SuppressWarnings("deprecation")
public class SideBar extends JPanel  implements Observer{
	private static final long serialVersionUID = 1L;

	private SideBarController controller;

	private JLabel[] labels = {
			new JLabel("Index"), 
			new JLabel("Loại"), 
			new JLabel("Giá trị")
	};
	
	private JTextField[] txtFields = {
			new JTextField(),
			new JTextField(),
			new JTextField()
	};
	
	{
		txtFields[0].setEditable(false);
		txtFields[1].setEditable(false);
	}
	
	private JButton[] btns = {
			new JButton("Lưu"),
			new JButton("Xóa"),
			new JButton("Thêm đỉnh"),
			new JButton("Thêm cạnh")
	};
	
	{
		btns[0].setActionCommand("save");
		btns[1].setActionCommand("remove");
		btns[2].setActionCommand("add_node");
		btns[3].setActionCommand("add_line");
	}

	public SideBar(GraphModel model) {
		this.controller = new SideBarController(this, model);
		this.setLayout(new BorderLayout());
		JPanel tools = toolPanel();
		this.add(tools, BorderLayout.NORTH);
		for (var btn : btns) btn.addActionListener(controller);
		model.addObserver(this);
	}
	
	public JPanel toolPanel() {
		JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
		for (int i = 0; i < 3; i++) {
			panel.add(labels[i]);
			panel.add(txtFields[i]);
		}
		for (int i = 0; i < 4; i++) {
			panel.add(btns[i]);
		}
		panel.setBorder(new TitledBorder(null, "Đang lựa chọn", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		return panel;
	}
	
	public void loadTargetInfo(Object arg) {
		iShape target = null;
		if (arg == null || !(arg instanceof iShape)) {
			//clear
			txtFields[0].setText("");
			txtFields[1].setText("");
			txtFields[2].setText("");
			return;
		}
		target = (iShape) arg;
		
		String index = "";
		String type = "";
		String value = "";
		
		if (target instanceof Line) {
			var line = (Line) target;
			var pair = controller.getModel().get_line_index(line);
			index = String.format("[%s, %s]", pair[0], pair[1]);
			value = line.getValue()+"";
			type = "Line";
		}
		else if (target instanceof Node) {
			var node = (Node) target;
			index = String.format("[%s]", controller.getModel().get_node_index(node));
			value = node.getName();
			type = "Node";
		}
		txtFields[0].setText(index);
		txtFields[1].setText(type);
		txtFields[2].setText(value);
	}
	
	public void remove() {
		var target = controller.getModel().getTarget();
		if (target == null) return;
		if (target instanceof Node) controller.getModel().remove_node((Node) target);
		else if (target instanceof Line) controller.getModel().remove_line((Line) target);
		controller.getModel().setTarget(null);
	}
	
	public void update() {
		var target = controller.getModel().getTarget();
		String value = txtFields[2].getText();
		if (target == null || !(target instanceof iShape) || value == null) return;
		if (target instanceof Node) controller.getModel().update_node((Node) target, value);
		else if (target instanceof Line) {
			if (!value.matches("^-?\\d*(\\.\\d+)?$")) {
				JOptionPane.showMessageDialog(this, "Giá trị không có nghĩa!", "Không thể lưu", JOptionPane.ERROR_MESSAGE);
				return;
			}
			controller.getModel().update_line((Line) target, Integer.parseInt(value));
		}
	}
	
	public void switchToDrawLine() {
		this.controller.getModel().setState(State.CREATING_LINE);
	}
	
	public void switchToDrawNode() {
		this.controller.getModel().setState(State.CREATING_NODE);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Notify) return;
		loadTargetInfo(arg);
	}
}
