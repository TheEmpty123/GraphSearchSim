package model;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class AbstractAlgorithm implements Algorithm{
	protected boolean[] visited;
	protected LinkedList<Integer> list;
	protected int[][] matrix;
	protected int[] component;
	private int count;
	
	
	public AbstractAlgorithm(int[][] matrix) {
		this.matrix = matrix;
		int size = matrix.length;
		visited = new boolean[size];
		list = new LinkedList<>();
	}
	
	public AbstractAlgorithm() {
		super();
	}
	
	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
		int size = matrix.length;
		visited = new boolean[size];
		list = new LinkedList<>();
		this.component = new int[size];
	}
	
	private void doBFS(int v_start, int[][] arr, boolean[] visit) {
		int size = arr.length;
		list.addFirst(v_start);
		while (!list.isEmpty()) {
			int u = list.getFirst();
			component[u] = count;
			list.removeFirst();
			if (visit[u]) continue;
			visit[u] = true;
			//kiểm tra các cạnh kề của đỉnh u
			for (int v = 0; v < size; v++) {
				if (visit[v] || arr[u][v] == 0) continue;
				list.addFirst(v);
			}
		}
	}

	@Override
	public int countComponent() {
		this.count = 0;
		int size = matrix.length;
		// Tạo ma trận vô hướng dựa trên matrix hiện tại
		int[][] unGraph = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (matrix[i][j] != 0) {
					unGraph[i][j] = matrix[i][j];
					unGraph[j][i] = matrix[i][j];
				}
			}
		}
		boolean[] visit = new boolean[size];
		for (int i = 0; i < size; i++) {
			if (visit[i]) continue;
			//có đỉnh chưa đc thăm
			doBFS(i, unGraph, visit);
			count++;
		};
		System.out.println(Arrays.toString(component));
		return count;
	}
	
	public int getComponent(int v) {
		return component[v];
	}

}
