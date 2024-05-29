package view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import model.GraphModel;

public class Footer extends JPanel{
	private JPanel p_console;
	private JPanel p_matrix;
	
	public Footer(GraphModel model) {
		p_matrix = new MatrixPanel(model);
		p_console = new ConsolePanel(model);
		this.setLayout(new BorderLayout());
		this.add(p_console, BorderLayout.CENTER);
		this.add(p_matrix, BorderLayout.EAST);
		this.setBorder(new EmptyBorder(5, 0, 0, 0));
	}
	
	
}
