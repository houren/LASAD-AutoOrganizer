package lasad.gwt.client.importer.LARGO;

import java.util.HashMap;

import lasad.gwt.client.logger.Logger;

public class AsahiResTranscript implements LARGOTranscriptMappingInterface {

	private int line = 0;
	//private int charNumber;
	private int point;

	public void mapChars(int charNumber, boolean start) {

		HashMap<Integer, Integer> c2l = new HashMap<Integer, Integer>();
		//this.charNumber = charNumber;
		c2l.put(145, 3);
		c2l.put(146, 44);
		c2l.put(147, 119);
		c2l.put(148, 180);
		c2l.put(149, 496);
		c2l.put(150, 713);
		c2l.put(151, 1070);
		c2l.put(152, 1323);
		c2l.put(153, 1794);
		c2l.put(154, 1839);
		c2l.put(155, 1999);
		c2l.put(156, 2065);
		c2l.put(157, 2157);
		c2l.put(158, 2372);
		c2l.put(159, 2466);
		c2l.put(160, 2524);
		c2l.put(161, 2631);
		c2l.put(162, 2676);
		c2l.put(163, 2711);
		c2l.put(164, 2772);
		c2l.put(165, 2915);
		c2l.put(166, 2941);
		c2l.put(167, 3011);
		c2l.put(168, 3183);
		c2l.put(169, 3405);
		c2l.put(170, 3429);
		c2l.put(171, 3618);
		c2l.put(172, 3657);
		c2l.put(173, 3696);
		c2l.put(174, 3843);
		c2l.put(175, 3994);
		c2l.put(176, 4050);
		c2l.put(177, 4338);
		c2l.put(178, 4636);
		c2l.put(179, 4715);
		c2l.put(180, 4972);
		c2l.put(181, 5038);
		c2l.put(182, 5158);
		c2l.put(183, 5223);
		c2l.put(184, 5471);
		c2l.put(185, 5761);
		c2l.put(186, 6004);
		c2l.put(187, 6319);
		c2l.put(188, 6756);
		c2l.put(189, 7097);
		c2l.put(190, 7296);
		c2l.put(191, 7421);
		c2l.put(192, 7659);
		c2l.put(193, 7859);
		c2l.put(194, 7946);
		c2l.put(195, 8055);
		c2l.put(196, 8329);
		c2l.put(197, 8662);
		c2l.put(198, 8788);
		c2l.put(199, 8818);
		c2l.put(200, 8857);
		c2l.put(201, 8990);
		c2l.put(202, 9129);
		c2l.put(203, 9308);
		c2l.put(204, 9427);
		c2l.put(205, 9810);
		c2l.put(206, 9970);
		c2l.put(207, 10025);
		c2l.put(208, 10060);
		c2l.put(209, 10121);
		c2l.put(210, 10446);
		c2l.put(211, 11009);
		c2l.put(212, 11179);
		c2l.put(213, 11228);
		c2l.put(214, 11529);
		c2l.put(215, 11603);
		c2l.put(216, 11674);
		c2l.put(217, 11852);
		c2l.put(218, 12061);
		c2l.put(219, 12466);
		c2l.put(220, 12601);
		c2l.put(221, 12725);
		c2l.put(222, 12746);
		c2l.put(223, 12931);
		c2l.put(224, 13309);
		c2l.put(225, 13528);
		c2l.put(226, 13735);
		c2l.put(227, 14038);
		c2l.put(228, 14334);
		c2l.put(229, 14831);
		c2l.put(230, 15018);
		c2l.put(231, 15120);
		c2l.put(232, 15193);
		c2l.put(233, 15234);
		c2l.put(234, 15269);
		c2l.put(235, 15310);
		c2l.put(236, 15584);
		c2l.put(237, 15733);
		c2l.put(238, 15817);
		c2l.put(239, 16090);
		c2l.put(240, 16151);
		c2l.put(241, 16195);
		c2l.put(242, 16226);
		c2l.put(243, 16305);
		c2l.put(244, 16591);
		c2l.put(245, 16797);
		c2l.put(246, 16921);
		c2l.put(247, 16976);
		c2l.put(248, 17036);
		c2l.put(249, 17322);
		c2l.put(250, 17610);
		c2l.put(251, 17969);
		c2l.put(252, 18019);
		c2l.put(253, 18375);
		c2l.put(254, 18652);
		c2l.put(255, 18723);
		c2l.put(256, 18835);
		c2l.put(257, 18899);
		c2l.put(258, 18978);
		c2l.put(259, 19008);
		c2l.put(260, 19087);
		c2l.put(261, 19363);
		c2l.put(262, 19424);
		c2l.put(263, 19445);
		c2l.put(264, 19478);
		c2l.put(265, 19698);
		c2l.put(266, 19858);
		c2l.put(267, 19927);
		c2l.put(268, 19981);
		c2l.put(269, 20142);
		c2l.put(270, 20174);
		c2l.put(271, 20219);
		c2l.put(272, 20579);
		c2l.put(273, 20693);
		c2l.put(274, 20961);
		c2l.put(275, 21242);
		c2l.put(276, 21465);
		c2l.put(277, 22014);
		c2l.put(278, 22281);
		c2l.put(279, 22516);
		c2l.put(280, 22600);
		c2l.put(281, 22769);
		c2l.put(282, 22909);
		c2l.put(283, 22953);
		c2l.put(284, 22998);
		c2l.put(285, 23195);
		c2l.put(286, 23229);
		c2l.put(287, 23317);
		c2l.put(288, 23345);
		c2l.put(289, 23434);
		c2l.put(290, 23633);
		c2l.put(291, 23691);
		c2l.put(292, 23835);
		c2l.put(293, 24001);
		c2l.put(294, 24113);
		c2l.put(295, 24162);
		c2l.put(296, 24414);
		c2l.put(297, 24466);
		c2l.put(298, 24760);
		c2l.put(299, 24915);
		c2l.put(300, 25139);
		c2l.put(301, 25188);
		c2l.put(302, 25230);
		c2l.put(303, 25326);
		c2l.put(304, 25409);
		c2l.put(305, 25518);
		c2l.put(306, 25577);
		c2l.put(307, 25708);
		c2l.put(308, 25896);
		c2l.put(309, 25967);
		c2l.put(310, 26161);
		c2l.put(311, 26252);
		c2l.put(312, 26353);
		c2l.put(313, 26488);
		c2l.put(314, 26721);
		c2l.put(315, 26895);
		c2l.put(316, 26972);
		c2l.put(317, 26992);
		c2l.put(318, 27250);
		c2l.put(319, 27534);
		c2l.put(320, 27886);
		c2l.put(321, 27914);
		c2l.put(322, 28331);
		c2l.put(323, 28547);
		c2l.put(324, 28784);
		c2l.put(325, 28890);
		c2l.put(326, 28917);
		c2l.put(327, 29095);
		c2l.put(328, 29127);
		c2l.put(329, 29176);
		c2l.put(330, 29214);
		c2l.put(331, 29264);
		c2l.put(332, 29714);
		c2l.put(333, 29766);

		for (int i = 145; i < 333; i++) {
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
