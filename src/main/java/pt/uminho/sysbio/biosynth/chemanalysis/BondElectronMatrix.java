package pt.uminho.sysbio.biosynth.chemanalysis;

public class BondElectronMatrix {
	private String[] atomIndex;
	private int[][] matrix;
	
	public String[] getAtomIndex() {
		return atomIndex;
	}
	public void setAtomIndex(String[] atomIndex) {
		this.atomIndex = atomIndex;
	}
	public int[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
	}
	
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {
			sb.append(atomIndex[i]);
			for (int j = 0; j < matrix.length; j++) {
				sb.append('\t');
				sb.append(matrix[i][j]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
