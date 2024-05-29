package view;

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import model.GraphModel;
import shared.Notify;

@SuppressWarnings("deprecation")
public class ConsolePanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	private JTextArea txtArea = new JTextArea(10, 10);
	private JScrollPane scrollPane = new JScrollPane();
	private GraphModel model;
	
	public ConsolePanel(GraphModel model) {
		super();
		this.model = model;
		model.addObserver(this);
		this.setLayout(new GridLayout(1, 0));
		scrollPane.setViewportView(txtArea);
		this.add(scrollPane);
		this.setBorder(new TitledBorder(null, "Console", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg != Notify.LOG) return;
		System.out.println(model.getLogger().getMessage());
		this.txtArea.setText(model.getLogger().getMessage());
	}
	
	
}
