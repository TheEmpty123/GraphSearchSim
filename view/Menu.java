package view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import controller.MenuController;
import model.GraphModel;

public class Menu extends JMenuBar{
	private static final long serialVersionUID = 1L;
	private JMenu menu1 = new JMenu("File");
	private JMenuItem item1 = new JMenuItem("Open File");
	private JMenuItem item2 = new JMenuItem("Save File");
	private JMenuItem item3 = new JMenuItem("Exit");
	private GraphModel model;
	private MenuController controller;
	
	public Menu(GraphModel model) {
		super();
		this.model = model;
		this.add(menu1);
		this.controller = new MenuController(model, this);
		
		item1.setActionCommand("open_file");
		item2.setActionCommand("save_file");
		item3.setActionCommand("exit");
		
		item1.addActionListener(controller);
		item2.addActionListener(controller);
		item3.addActionListener(controller);
		
		menu1.add(item1);
		menu1.add(item2);
		menu1.add(item3);
	}
	
	
}
