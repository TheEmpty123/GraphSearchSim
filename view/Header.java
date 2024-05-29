package view;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import model.GraphModel;

public class Header extends JPanel{
	private JPanel infoPanel = new InfoPanel();
	private JPanel checkConnectPanel;
	private JPanel searchPanel;
	
	public Header(GraphModel model) {
		this.searchPanel = new SearchPanel(model);
		this.checkConnectPanel = new ConnectCheckPanel(model);
		this.setLayout(new GridLayout(1, 3));
		this.add(infoPanel);
		this.add(checkConnectPanel);
		this.add(searchPanel);
		this.setBorder(new EmptyBorder(0, 5, 5, 5));
	}
	
	
}
