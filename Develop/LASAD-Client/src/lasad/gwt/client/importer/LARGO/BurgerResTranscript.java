package lasad.gwt.client.importer.LARGO;

import java.util.HashMap;

import lasad.gwt.client.logger.Logger;

public class BurgerResTranscript implements LARGOTranscriptMappingInterface {

	private int line = 0;
	//private int charNumber;
	private int point;

	public void mapChars(int charNumber, boolean start) {

		HashMap<Integer, Integer> c2l = new HashMap<Integer, Integer>();
		//this.charNumber = charNumber;
		c2l.put(143, 3);
		c2l.put(144, 19);
		c2l.put(145, 92);
		c2l.put(146, 383);
		c2l.put(147, 439);
		c2l.put(148, 524);
		c2l.put(149, 965);
		c2l.put(150, 1292);
		c2l.put(151, 1375);
		c2l.put(152, 1575);
		c2l.put(153, 1836);
		c2l.put(154, 2194);
		c2l.put(155, 2494);
		c2l.put(156, 2819);
		c2l.put(157, 2995);
		c2l.put(158, 3039);
		c2l.put(159, 3329);
		c2l.put(160, 3599);
		c2l.put(161, 3844);
		c2l.put(162, 4089);
		c2l.put(163, 4282);
		c2l.put(164, 4712);
		c2l.put(165, 5014);
		c2l.put(166, 5292);
		c2l.put(167, 5695);
		c2l.put(168, 6101);
		c2l.put(169, 6197);
		c2l.put(170, 6506);
		c2l.put(171, 6737);
		c2l.put(172, 6863);
		c2l.put(173, 6963);
		c2l.put(174, 7258);
		c2l.put(175, 7578);
		c2l.put(176, 7741);
		c2l.put(177, 7881);
		c2l.put(178, 7989);
		c2l.put(179, 8054);
		c2l.put(180, 8154);
		c2l.put(181, 8195);
		c2l.put(182, 8531);
		c2l.put(183, 8667);
		c2l.put(184, 8909);
		c2l.put(185, 9184);
		c2l.put(186, 9545);
		c2l.put(187, 9675);
		c2l.put(188, 9855);
		c2l.put(189, 9881);
		c2l.put(190, 9968);
		c2l.put(191, 10236);
		c2l.put(192, 10461);
		c2l.put(193, 10726);
		c2l.put(194, 11189);
		c2l.put(195, 11439);
		c2l.put(196, 11520);
		c2l.put(197, 11619);
		c2l.put(198, 11716);
		c2l.put(199, 11898);
		c2l.put(200, 12313);
		c2l.put(201, 12418);
		c2l.put(202, 12524);
		c2l.put(203, 12566);
		c2l.put(204, 13008);
		c2l.put(205, 13154);
		c2l.put(206, 13611);
		c2l.put(207, 13793);
		c2l.put(208, 14034);
		c2l.put(209, 14196);
		c2l.put(210, 14554);
		c2l.put(211, 14639);
		c2l.put(212, 15146);
		c2l.put(213, 15420);
		c2l.put(214, 15762);
		c2l.put(215, 16118);
		c2l.put(216, 16265);
		c2l.put(217, 16296);
		c2l.put(218, 16405);
		c2l.put(219, 16638);
		c2l.put(220, 16847);
		c2l.put(221, 17170);
		c2l.put(222, 17430);
		c2l.put(223, 17498);
		c2l.put(224, 17834);
		c2l.put(225, 17856);
		c2l.put(226, 17911);
		c2l.put(227, 18145);
		c2l.put(228, 18368);
		c2l.put(229, 18590);
		c2l.put(230, 18978);
		c2l.put(231, 19072);
		c2l.put(232, 19383);
		c2l.put(233, 19589);
		c2l.put(234, 19589);
		c2l.put(235, 19633);
		c2l.put(236, 19745);
		c2l.put(237, 19779);
		c2l.put(238, 19993);
		c2l.put(239, 20090);
		c2l.put(240, 20119);
		c2l.put(241, 20198);
		c2l.put(242, 20280);
		c2l.put(243, 20384);
		c2l.put(244, 20758);
		c2l.put(245, 21084);
		c2l.put(246, 21612);
		c2l.put(247, 21954);
		c2l.put(248, 22240);
		c2l.put(249, 22622);
		c2l.put(250, 23011);
		c2l.put(251, 23402);
		c2l.put(252, 23837);
		c2l.put(253, 24197);
		c2l.put(254, 24503);
		c2l.put(255, 24576);
		c2l.put(256, 24717);
		c2l.put(257, 24792);
		c2l.put(258, 24857);
		c2l.put(259, 25086);
		c2l.put(260, 25231);
		c2l.put(261, 25253);
		c2l.put(262, 25369);
		c2l.put(263, 25635);
		c2l.put(264, 26064);
		c2l.put(265, 26211);
		c2l.put(266, 26576);
		c2l.put(267, 26636);
		c2l.put(268, 26666);
		c2l.put(269, 26726);
		c2l.put(270, 26894);
		c2l.put(271, 27255);
		c2l.put(272, 27585);
		c2l.put(273, 27802);
		c2l.put(274, 28116);
		c2l.put(275, 28171);
		c2l.put(276, 28209);
		c2l.put(277, 28295);

		for (int i = 143; i < 277; i++) {
			int a = c2l.get(i);
			int b = c2l.get(i + 1);
			int c = charNumber;
			String tmpChar = "" + i;
			if ((c >= a) && (c < b)) {
				if (start) {
					Logger.log("\n" + "Im start", Logger.DEBUG_DETAILS);
					if (i == 143) {
						line = 143;
						point = c - a;
						Logger.log("Im line: " + i + " and Point: " + point + " B was: " + b + " c was: " + c, Logger.DEBUG_DETAILS);

					} else {
						if (i == 143) {
							line = i;
						}
						Logger.log("c-a", Logger.DEBUG_DETAILS);
						line = i;
						point = c - a - tmpChar.length() - 2;//
						Logger.log("Im line: " + i + " and Point: " + point + " B was: " + b + " c was: " + c, Logger.DEBUG_DETAILS);
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
