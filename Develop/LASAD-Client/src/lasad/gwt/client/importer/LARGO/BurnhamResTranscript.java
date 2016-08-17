package lasad.gwt.client.importer.LARGO;

import java.util.HashMap;

import lasad.gwt.client.logger.Logger;

public class BurnhamResTranscript implements LARGOTranscriptMappingInterface {

	private int line = 0;
	//private int charNumber;
	private int point;

	public void mapChars(int charNumber, boolean start) {

		HashMap<Integer, Integer> c2l = new HashMap<Integer, Integer>();
		//this.charNumber = charNumber;

		c2l.put(199, 3);
		c2l.put(200, 23);
		c2l.put(201, 93);
		c2l.put(202, 168);
		c2l.put(203, 568);
		c2l.put(204, 1291);
		c2l.put(205, 1397);
		c2l.put(206, 1848);
		c2l.put(207, 1928);
		c2l.put(208, 1959);
		c2l.put(209, 2000);
		c2l.put(210, 2131);
		c2l.put(211, 2189);
		c2l.put(212, 2226);
		c2l.put(213, 2280);
		c2l.put(214, 2650);
		c2l.put(215, 2747);
		c2l.put(216, 3070);
		c2l.put(217, 3145);
		c2l.put(218, 3394);
		c2l.put(219, 3434);
		c2l.put(220, 3503);
		c2l.put(221, 3673);
		c2l.put(222, 3954);
		c2l.put(223, 4093);
		c2l.put(224, 4179);
		c2l.put(225, 4212);
		c2l.put(226, 4435);
		c2l.put(227, 4531);
		c2l.put(228, 4604);
		c2l.put(229, 4654);
		c2l.put(230, 4692);
		c2l.put(231, 4711);
		c2l.put(232, 4950);
		c2l.put(233, 5024);
		c2l.put(234, 5345);
		c2l.put(235, 5612);
		c2l.put(236, 5658);
		c2l.put(237, 5697);
		c2l.put(238, 5724);
		c2l.put(239, 5754);
		c2l.put(240, 5775);
		c2l.put(241, 6079);
		c2l.put(242, 6367);
		c2l.put(243, 6647);
		c2l.put(244, 6670);
		c2l.put(245, 6883);
		c2l.put(246, 6941);
		c2l.put(247, 7131);
		c2l.put(248, 7368);
		c2l.put(249, 7553);
		c2l.put(250, 7619);
		c2l.put(251, 7638);
		c2l.put(252, 7680);
		c2l.put(253, 7706);
		c2l.put(254, 7810);
		c2l.put(255, 7875);
		c2l.put(256, 7913);
		c2l.put(257, 7959);
		c2l.put(258, 8202);
		c2l.put(259, 8537);
		c2l.put(260, 8693);
		c2l.put(261, 8898);
		c2l.put(262, 9220);
		c2l.put(263, 9515);
		c2l.put(264, 9825);
		c2l.put(265, 10040);
		c2l.put(266, 10486);
		c2l.put(267, 10907);
		c2l.put(268, 11174);
		c2l.put(269, 11569);
		c2l.put(270, 11926);
		c2l.put(271, 12014);
		c2l.put(272, 12314);
		c2l.put(273, 12698);
		c2l.put(274, 13198);
		c2l.put(275, 13490);
		c2l.put(276, 13964);
		c2l.put(277, 14148);
		c2l.put(278, 14426);
		c2l.put(279, 14804);
		c2l.put(280, 14987);
		c2l.put(281, 15148);
		c2l.put(282, 15174);
		c2l.put(283, 15215);
		c2l.put(284, 15300);
		for (int i = 199; i < 284; i++) {
			int a = c2l.get(i);
			int b = c2l.get(i + 1);
			int c = charNumber;
			String tmpChar = "" + i;
			if ((c >= a) && (c < b)) {
				if (start) {
					Logger.log("\n" + "Im start", Logger.DEBUG_DETAILS);
					if (i == 199) {
						line = 199;
						point = c - a;
						Logger.log("Im line: " + i + " and Point: " + point + " B was: " + b + " c was: " + c, Logger.DEBUG_DETAILS);

					} else {
						if (c - b + tmpChar.length() + 2 < 0) //
						{
							Logger.log("c-a", Logger.DEBUG_DETAILS);
							line = i;
							point = c - a - tmpChar.length() - 2;//
							Logger.log("Im line: " + i + " and Point: " + point + " B was: " + b + " c was: " + c, Logger.DEBUG_DETAILS);
						} else {
							Logger.log("c-b", Logger.DEBUG_DETAILS);
							line = i + 1;
							point = c - b;
							Logger.log("Im line: " + i + " and Point: " + point + " a was: " + a + " c was: " + c, Logger.DEBUG_DETAILS);
						}
					}
				} else {
					Logger.log("c-a", Logger.DEBUG_DETAILS);
					line = i;
					point = c - a - tmpChar.length() - 2; //
					Logger.log("Im line: " + i + " and Point: " + point + " B was: " + b + " c was: " + c, Logger.DEBUG_DETAILS);
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
