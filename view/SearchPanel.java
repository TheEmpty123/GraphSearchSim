package view;

import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import controller.SearchPanelController;
import model.BFS;
import model.DFS;
import model.GraphModel;
import model.Node;
import model.iShape;
import shared.Notify;

@SuppressWarnings("deprecation")
public class SearchPanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	private JLabel label = new JLabel("Đỉnh bắt đầu");
	private JComboBox<String> comboBox = new JComboBox<String>();
	private JButton[] btns = {
			new JButton("Theo chiều rộng"),
			new JButton("Theo chiều sâu")
	};
	{
		btns[0].setActionCommand("bfs");
		btns[1].setActionCommand("dfs");
	}
	private SearchPanelController controller;
	public SearchPanel(GraphModel model) {
		this.controller = new SearchPanelController(this, model);
		comboBox.addItemListener(controller);
		this.setLayout(new GridLayout(2, 2, 3, 3));
		this.add(label);
		this.add(comboBox);
		for (var btn : btns) {
			btn.addActionListener(controller);
			this.add(btn);
		}
		this.setBorder(new TitledBorder(null, "Duyệt đồ thị", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		model.addObserver(this);
	}
	@Override
	public void update(Observable o, Object arg) {
		if (arg == Notify.CHANGED_STRUCTURE || arg == Notify.CHANGED_VALUE) {
			updateComboBox();
			updateButtons();
		}
		else if (arg instanceof Node) {
			Node node = (Node) arg;
			int index = controller.getModel().get_node_index(node);
			this.comboBox.setSelectedIndex(index);
		}
	}
	public void updateComboBox() {
		var nodes = controller.getModel().getNodes();
		int size = nodes.size();
		String[] nodes_name = new String[size];
		nodes.stream()
				.map(node -> node.getName())
				.collect(Collectors.toList())
				.toArray(nodes_name);
		this.comboBox.setModel(new DefaultComboBoxModel<>(nodes_name));
	}
	public void updateButtons() {
		int count = controller.getModel().countComponent();
		if (count > 1) {
			this.btns[0].setEnabled(false);
			this.btns[1].setEnabled(false);
		}
		else {
			this.btns[0].setEnabled(true);
			this.btns[1].setEnabled(true);
		}
	}
	
	public void searchBFS() {
		int i = comboBox.getSelectedIndex();
		controller.getModel().setAlgorithm(new BFS());
		controller.getModel().search(i);
	}
	
	public void searchDFS() {
		int i = comboBox.getSelectedIndex();
		controller.getModel().setAlgorithm(new DFS());
		controller.getModel().search(i);
	}
}
