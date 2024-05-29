package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import model.GraphModel;

public class MainView extends JFrame{
	private Canvas canvas;
	private SideBar sideBar;
	private JPanel contentPane;
	private Header header;
	private Footer footer;
	private Menu menu;
	
	public MainView(String name) {
		super(name);
		init();
		this.setContentPane(contentPane);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	private void init() {
		GraphModel model = new GraphModel();
		canvas = new Canvas(model);
		sideBar = new SideBar(model);
		header = new Header(model);
		footer = new Footer(model);
		menu = new Menu(model);
		contentPane = new JPanel(new BorderLayout());
		contentPane.add(canvas, BorderLayout.CENTER);
		contentPane.add(sideBar, BorderLayout.EAST);
		contentPane.add(header, BorderLayout.NORTH);
		contentPane.add(footer, BorderLayout.SOUTH);
		this.setJMenuBar(menu);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					MainView window = new MainView("Chương trình vẽ đồ thị");
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
