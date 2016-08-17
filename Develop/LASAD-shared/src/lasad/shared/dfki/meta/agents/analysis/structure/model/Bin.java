package lasad.shared.dfki.meta.agents.analysis.structure.model;


/**
 * {@link Comparison}s will be sorted into one of 20 different bins. For each
 * bin a different kind of Jess clause will be generated. Details, see external
 * documentation.
 * 
 * @author oliverscheuer
 * 
 */
public enum Bin {

	ALL_BINS (0), BIN_1(1), BIN_2(2), BIN_3(3), BIN_4(4), BIN_5(5), BIN_6(6), BIN_7(7), BIN_8(
			8), BIN_9(9), BIN_10(10), BIN_11(11), BIN_12(12), BIN_13(13), BIN_14(
			14), BIN_15(15), BIN_16(16), BIN_17(17), BIN_18(18), BIN_19(19), BIN_20(
			20);

	private final int binNum;

	Bin(int binNum) {
		this.binNum = binNum;
	}
	
	public int getNum(){
		return binNum;
	}
	
	
}
