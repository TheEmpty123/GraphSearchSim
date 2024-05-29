package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BFS extends AbstractAlgorithm{

	public BFS(int[][] matrix) {
		super(matrix);
	}
	
	public BFS() {
		super();
	}

	@Override
	public int[] search(int start) {
		int size = matrix.length;
		List<Integer> temp = new ArrayList<>();
		list.addLast(start);
		while (!list.isEmpty()) {
			int u = list.getFirst();
			list.removeFirst();
			if (visited[u]) continue;
			visited[u] = true;
			temp.add(u);
			//kiểm tra các cạnh kề của đỉnh u
			for (int v = 0; v < size; v++) {
				if (visited[v] || matrix[u][v] == 0) continue;
				list.addLast(v);
			}
		}
		int[] array = new int[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			array[i] = temp.get(i);
		}
		return array;
	}

	@Override
	public String getName() {
		return "BFS";
	}
}
