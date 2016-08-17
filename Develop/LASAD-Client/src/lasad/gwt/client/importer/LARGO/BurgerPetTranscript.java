package lasad.gwt.client.importer.LARGO;

import java.util.HashMap;

import lasad.gwt.client.logger.Logger;

public class BurgerPetTranscript implements LARGOTranscriptMappingInterface {

	private int line = 0;
	//private int charNumber;
	private int point;

	public void mapChars(int charNumber, boolean start) {

		HashMap<Integer, Integer> c2l = new HashMap<Integer, Integer>();
		//this.charNumber = charNumber;

		c2l.put(0, 3);
		c2l.put(1, 458);
		c2l.put(2, 555);
		c2l.put(3, 618);
		c2l.put(4, 687);
		c2l.put(5, 872);
		c2l.put(6, 1310);
		c2l.put(7, 1612);
		c2l.put(8, 1848);
		c2l.put(9, 2292);
		c2l.put(10, 2643);
		c2l.put(11, 2813);
		c2l.put(12, 2972);
		c2l.put(13, 3509);
		c2l.put(14, 3797);
		c2l.put(15, 4082);
		c2l.put(16, 4498);
		c2l.put(17, 4715);
		c2l.put(18, 5069);
		c2l.put(19, 5445);
		c2l.put(20, 5729);
		c2l.put(21, 5920);
		c2l.put(22, 6236);
		c2l.put(23, 6551);
		c2l.put(24, 6739);
		c2l.put(25, 6909);
		c2l.put(26, 6939);
		c2l.put(27, 7051);
		c2l.put(28, 7138);
		c2l.put(29, 7403);
		c2l.put(30, 7543);
		c2l.put(31, 7612);
		c2l.put(32, 7951);
		c2l.put(33, 8008);
		c2l.put(34, 8558);
		c2l.put(35, 8746);
		c2l.put(36, 8857);
		c2l.put(37, 8909);
		c2l.put(38, 9143);
		c2l.put(39, 9232);
		c2l.put(40, 9334);
		c2l.put(41, 9359);
		c2l.put(42, 9702);
		c2l.put(43, 10020);
		c2l.put(44, 10128);
		c2l.put(45, 10231);
		c2l.put(46, 10258);
		c2l.put(47, 10291);
		c2l.put(48, 10593);
		c2l.put(49, 11094);
		c2l.put(50, 11193);
		c2l.put(51, 11306);
		c2l.put(52, 11452);
		c2l.put(53, 11485);
		c2l.put(54, 11552);
		c2l.put(55, 11602);
		c2l.put(56, 11634);
		c2l.put(57, 12049);
		c2l.put(58, 12186);
		c2l.put(59, 12429);
		c2l.put(60, 12786);
		c2l.put(61, 12961);
		c2l.put(62, 13232);
		c2l.put(63, 13358);
		c2l.put(64, 13421);
		c2l.put(65, 13884);
		c2l.put(66, 14126);
		c2l.put(67, 14233);
		c2l.put(68, 14651);
		c2l.put(69, 15052);
		c2l.put(70, 15718);
		c2l.put(71, 15941);
		c2l.put(72, 16271);
		c2l.put(73, 16723);
		c2l.put(74, 17114);
		c2l.put(75, 17501);
		c2l.put(76, 17861);
		c2l.put(77, 18121);
		c2l.put(78, 18372);
		c2l.put(79, 18609);
		c2l.put(80, 18847);
		c2l.put(81, 19159);
		c2l.put(82, 19321);
		c2l.put(83, 19639);
		c2l.put(84, 20394);
		c2l.put(85, 20739);
		c2l.put(86, 20927);
		c2l.put(87, 21183);
		c2l.put(88, 21183);
		c2l.put(89, 21317);
		c2l.put(90, 21350);
		c2l.put(91, 21429);
		c2l.put(92, 21531);
		c2l.put(93, 21575);
		c2l.put(94, 21625);
		c2l.put(95, 21694);
		c2l.put(96, 22037);
		c2l.put(97, 22083);
		c2l.put(98, 22137);
		c2l.put(99, 22251);
		c2l.put(100, 22285);
		c2l.put(101, 22315);
		c2l.put(102, 22349);
		c2l.put(103, 22482);
		c2l.put(104, 22516);
		c2l.put(105, 22542);
		c2l.put(106, 23284);
		c2l.put(107, 23384);
		c2l.put(108, 23856);
		c2l.put(109, 24044);
		c2l.put(110, 24335);
		c2l.put(111, 24619);
		c2l.put(112, 24946);
		c2l.put(113, 25526);
		c2l.put(114, 25956);
		c2l.put(115, 26044);
		c2l.put(116, 26082);

		for (int i = 0; i < 116; i++) {
			int a = c2l.get(i);
			int b = c2l.get(i + 1);
			int c = charNumber;
			String tmpChar = "" + i;
			if ((c >= a) && (c < b)) {
				if (start) {
					Logger.log("\n" + "Im start", Logger.DEBUG_DETAILS);
					if (i == 0) {
						line = 0;
						point = c - a;
						Logger.log("Im line: " + i + " and Point: " + point + " B was: " + b + " c was: " + c, Logger.DEBUG_DETAILS);

					} else {
						Logger.log("c-a", Logger.DEBUG_DETAILS);
						line = i;
						point = c - a - tmpChar.length() - 2;//
						Logger.log("Im line: " + i + " and Point: " + point + " B was: " + b + " c was: " + c, Logger.DEBUG_DETAILS);
					}
				} else {
					Logger.log("c-a", Logger.DEBUG_DETAILS);
					line = i;
					if (i == 0) {
						point = c - a;
					} else {
						point = c - a - tmpChar.length() - 2;
					}
					Logger.log("Im line: " + i + " and Point: " + point + " B was: " + b + " c was: " + c, Logger.DEBUG_DETAILS);
				}
				if (point < 0) {
					point = 0;
				}
				break;
			}
		}
	}

	public int getLine() {
		return line;
	}

	public int getPoint() {
		return point;
	}

}
