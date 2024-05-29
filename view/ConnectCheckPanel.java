package view;

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import controller.ConnectCheckPanelController;
import model.BFS;
import model.GraphModel;
import shared.Notify;
@SuppressWarnings("deprecation")
public class ConnectCheckPanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	private JLabel[] labels = {
			new JLabel("Đồ thị liên thông?"),
			new JLabel("Thành phần liên thông")
	};
	private JTextField[] txtFields = {
			new JTextField(),
			new JTextField()
	};
	{
		txtFields[0].setEditable(false);
		txtFields[1].setEditable(false);
	}
	private ConnectCheckPanelController controller;
	public ConnectCheckPanel(GraphModel model) {
		model.addObserver(this);
		this.controller = new ConnectCheckPanelController(this, model);
		this.setLayout(new GridLayout(2,2,3,3));
		for (int i = 0; i < 2; i++) {
			this.add(labels[i]);
			this.add(txtFields[i]);
		}
		this.setBorder(new TitledBorder(null, "Kiểm tra tính liên thông", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	}
	@Override
	public void update(Observable o, Object arg) {
		if (arg == Notify.CHANGED_STRUCTURE) check();
	}
	private void check() {
		controller.getModel().setAlgorithm(new BFS());
		int count = controller.getModel().countComponent();
//		System.out.println("Số thành phần liên thông là: "+count);
		String isConnect = count == 1 ? "True" : "False";
		txtFields[0].setText(isConnect);
		txtFields[1].setText(count+"");
	}
}
