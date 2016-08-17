package lasad.gwt.client.importer.LARGO;

import java.util.HashMap;

import lasad.gwt.client.logger.Logger;

public class CarneyTranscript implements LARGOTranscriptMappingInterface {

	private int line = 0;
	//private int charNumber;
	private int point;
	//private boolean start;

	public void mapChars(int charNumber, boolean start) {

		HashMap<Integer, Integer> c2l = new HashMap<Integer, Integer>();
		//this.charNumber = charNumber;
		//this.start = start;

		c2l.put(3, 3);
		c2l.put(4, 63);
		c2l.put(5, 77);
		c2l.put(6, 115);
		c2l.put(7, 144);
		c2l.put(8, 191);
		c2l.put(9, 319);
		c2l.put(10, 335);
		c2l.put(11, 450);
		c2l.put(12, 526);
		c2l.put(13, 551);
		c2l.put(14, 617);
		c2l.put(15, 686);
		c2l.put(16, 762);
		c2l.put(17, 1370);
		c2l.put(18, 1747);
		c2l.put(19, 2029);
		c2l.put(20, 2452);
		c2l.put(21, 2512);
		c2l.put(22, 2901);
		c2l.put(23, 3050);
		c2l.put(24, 3279);
		c2l.put(25, 3326);
		c2l.put(26, 3593);
		c2l.put(27, 3661);
		c2l.put(28, 3701);
		c2l.put(29, 3768);
		c2l.put(30, 3879);
		c2l.put(31, 3970);
		c2l.put(32, 4031);
		c2l.put(33, 4412);
		c2l.put(34, 4795);
		c2l.put(35, 5283);
		c2l.put(36, 5562);
		c2l.put(37, 5750);
		c2l.put(38, 5819);
		c2l.put(39, 6063);
		c2l.put(40, 6216);
		c2l.put(41, 6766);
		c2l.put(42, 7273);
		c2l.put(43, 7451);
		c2l.put(44, 7504);
		c2l.put(45, 7550);
		c2l.put(46, 7619);
		c2l.put(47, 7745);
		c2l.put(48, 7803);
		c2l.put(49, 7996);
		c2l.put(50, 8039);
		c2l.put(51, 8263);
		c2l.put(52, 8364);
		c2l.put(53, 8745);
		c2l.put(54, 8943);
		c2l.put(55, 9007);
		c2l.put(56, 9040);
		c2l.put(57, 9079);
		c2l.put(58, 9272);
		c2l.put(59, 9580);
		c2l.put(60, 9613);
		c2l.put(61, 9860);
		c2l.put(62, 10005);
		c2l.put(63, 10153);
		c2l.put(64, 10274);
		c2l.put(65, 10574);
		c2l.put(66, 10866);
		// b c2l.put(66, 10984);
		c2l.put(67, 11268);
		c2l.put(68, 11406);
		c2l.put(69, 11489);
		c2l.put(70, 11704);
		c2l.put(71, 11819);
		c2l.put(72, 11974);
		c2l.put(73, 12052);
		c2l.put(74, 12143);
		c2l.put(75, 12534);
		c2l.put(76, 12696);
		c2l.put(77, 13049);
		c2l.put(78, 13552);
		c2l.put(79, 14027);
		c2l.put(80, 14313);
		c2l.put(81, 14454);
		c2l.put(82, 14613);
		c2l.put(83, 14722);
		c2l.put(84, 14797);
		c2l.put(85, 14939);
		c2l.put(86, 14994);
		c2l.put(87, 15027);
		c2l.put(88, 15070);
		c2l.put(89, 15118);
		c2l.put(90, 15185);
		c2l.put(91, 15287);
		c2l.put(92, 15400);
		c2l.put(93, 15490);
		c2l.put(94, 15595);
		c2l.put(95, 15654);
		c2l.put(96, 15675);
		c2l.put(97, 15752);
		c2l.put(98, 15772);
		c2l.put(99, 15881);
		c2l.put(100, 16043);
		c2l.put(101, 16077);
		c2l.put(102, 16103);
		c2l.put(103, 16236);
		c2l.put(104, 16318);
		c2l.put(105, 16351);
		c2l.put(106, 16495);
		c2l.put(107, 16666);
		c2l.put(108, 16861);
		c2l.put(109, 16907);
		c2l.put(110, 16951);
		c2l.put(111, 16968);
		c2l.put(112, 16994);
		c2l.put(113, 17026);
		c2l.put(114, 17061);
		c2l.put(115, 17103);
		c2l.put(116, 17120);
		c2l.put(117, 17177);
		c2l.put(118, 17212);
		c2l.put(119, 17246);
		c2l.put(120, 17347);
		c2l.put(121, 17837);
		c2l.put(122, 18060);
		c2l.put(123, 18307);
		c2l.put(124, 18533);
		c2l.put(125, 18576);
		c2l.put(126, 18588);
		c2l.put(127, 18650);
		c2l.put(128, 19197);
		c2l.put(129, 19733);
		c2l.put(130, 19998);
		c2l.put(131, 20566);
		c2l.put(132, 20772);
		c2l.put(133, 20980);
		c2l.put(134, 21016);
		c2l.put(135, 21098);
		c2l.put(136, 21611);
		c2l.put(137, 21886);
		c2l.put(138, 22266);
		c2l.put(139, 22407);
		c2l.put(140, 22458);
		c2l.put(141, 22544);
		c2l.put(142, 22574);
		c2l.put(143, 22814);
		c2l.put(144, 23099);
		c2l.put(145, 23276);
		c2l.put(146, 23330);
		c2l.put(147, 23364);
		c2l.put(148, 23410);
		c2l.put(149, 23513);
		c2l.put(150, 23581);
		c2l.put(151, 23598);
		c2l.put(152, 23675);
		c2l.put(153, 23724);
		c2l.put(154, 23975);
		c2l.put(155, 24070);
		c2l.put(156, 24087);
		c2l.put(157, 24269);
		c2l.put(158, 24325);
		c2l.put(159, 25092);
		c2l.put(160, 25342);
		c2l.put(161, 25387);
		c2l.put(162, 25821);
		c2l.put(163, 25934);
		c2l.put(164, 25982);
		c2l.put(165, 26098);
		c2l.put(166, 26172);
		c2l.put(167, 26215);
		c2l.put(168, 26258);
		c2l.put(169, 26640);
		c2l.put(170, 26943);
		c2l.put(171, 27044);
		c2l.put(172, 27080);

		for (int i = 3; i < 172; i++) {
			int a = c2l.get(i);
			int b = c2l.get(i + 1);
			int c = charNumber;
			String tmpChar = "" + i;
			if ((c >= a) && (c < b)) {
				if (start) {
					Logger.log("\n" + "Im start", Logger.DEBUG_DETAILS);
					if (i == 3) {
						line = 3;
						point = c - a;
						Logger.log("Im line: " + i + " and Point: " + point + " B was: " + b + " c was: " + c, Logger.DEBUG_DETAILS);

					} else {
						if (i == 3) {
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
