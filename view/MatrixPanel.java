package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import controller.MatrixPanelController;
import model.GraphModel;
import shared.Notify;

class MyTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 2040263844646738181L;
	private int[][] matrix;
	
	public MyTableModel(int[][] matrix) {
		super();
		this.matrix = matrix;
	}

	@Override
	public int getRowCount() {
		return this.matrix.length;
	}

	@Override
	public int getColumnCount() {
		return this.matrix.length;
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Integer.class;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return matrix[rowIndex][columnIndex];
	}
}

class MyTableRender extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 4538824832054916698L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		table.getColumnModel().getColumn(column).setMaxWidth(30);
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		return this;
	}
	@Override
	public void setHorizontalAlignment(int alignment) {
		super.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
}

@SuppressWarnings("deprecation")
public class MatrixPanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	private MatrixPanelController controller;
	private JTable jTable = new JTable() {
		private static final long serialVersionUID = 1L;
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	{

	}
	private JScrollPane scrollPane = new JScrollPane();
	
	public MatrixPanel(GraphModel model) {
		super();
		model.addObserver(this);
		this.controller = new MatrixPanelController(this, model);
		this.setLayout(new GridLayout(1, 0));
		Dimension d = new Dimension( 150, 150 );
		scrollPane.setViewportView(jTable);
		jTable.setPreferredScrollableViewportSize(d);
		this.add(scrollPane);
		this.setBorder(new TitledBorder(null, "Ma trận", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		initTable();
	}
	
	public void initTable() {
		jTable.setCellSelectionEnabled(true);
		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jTable.setModel(new MyTableModel(controller.getModel().getMatrix()));
		jTable.setDefaultRenderer(Integer.class, new MyTableRender());
		jTable.setTableHeader(null);
	}
	
	public void updateTable() {
		jTable.setModel(new MyTableModel(controller.getModel().getMatrix()));
		jTable.repaint();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg == Notify.CHANGED_STRUCTURE || arg == Notify.CHANGED_VALUE) updateTable();
	}
	
}
