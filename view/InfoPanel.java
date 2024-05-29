package view;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class InfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel lb_Project = new JLabel("Phần mềm vẽ đồ thị - nhóm 6", SwingConstants.CENTER);
	private JSeparator separator = new JSeparator();
	private JLabel logo = new JLabel("FIT - NLU 2023", SwingConstants.CENTER);
	public InfoPanel() {
		this.setLayout(new GridLayout(3, 0, 5, 5));
		this.add(lb_Project);
		this.add(separator);
		this.add(logo);
		lb_Project.setFont(new Font("Tahoma", Font.BOLD, 12));
	}
}
