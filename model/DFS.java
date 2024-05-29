package model;

import java.util.ArrayList;
import java.util.List;

public class DFS extends AbstractAlgorithm{

	public DFS(int[][] matrix) {
		super(matrix);
	}
	
	public DFS() {
		super();
	}

	@Override
	public int[] search(int start) {
		int size = matrix.length;
		List<Integer> temp = new ArrayList<>();
		list.addLast(start);
		while (!list.isEmpty()) {
			int u = list.getLast();
			list.removeLast();
			if (visited[u]) continue;
			visited[u] = true;
			temp.add(u);
			//kiểm tra các cạnh kề của đỉnh u
			for (int v = size - 1; v >= 0; v--) {
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
		return "DFS";
	}
}
