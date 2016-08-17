package lasad.gwt.client.importer.LARGO;

import java.util.HashMap;

import lasad.gwt.client.logger.Logger;

public class BurnhamPetTranscript implements LARGOTranscriptMappingInterface {

	private int line = 0;
	//private int charNumber;
	private int point;
	//private boolean start;

	public void mapChars(int charNumber, boolean start) {

		HashMap<Integer, Integer> c2l = new HashMap<Integer, Integer>();
		//this.charNumber = charNumber;
		//this.start = start;

		c2l.put(0, 3);
		c2l.put(1, 559);
		c2l.put(2, 678);
		c2l.put(3, 694);
		c2l.put(4, 758);
		c2l.put(5, 822);
		c2l.put(6, 1133);
		c2l.put(7, 1411);
		c2l.put(8, 1820);
		c2l.put(9, 2097);
		c2l.put(10, 2532);
		c2l.put(11, 2970);
		c2l.put(12, 3184);
		c2l.put(13, 3534);
		c2l.put(14, 3850);
		c2l.put(15, 4112);
		c2l.put(16, 4308);
		c2l.put(17, 4333);
		c2l.put(18, 4376);
		c2l.put(19, 4455);
		c2l.put(20, 4642);
		c2l.put(21, 4675);
		c2l.put(22, 4702);
		c2l.put(23, 4871);
		c2l.put(24, 4893);
		c2l.put(25, 4940);
		c2l.put(26, 5042);
		c2l.put(27, 5066);
		c2l.put(28, 5153);
		c2l.put(29, 5191);
		c2l.put(30, 5218);
		c2l.put(31, 5587);
		c2l.put(32, 5914);
		c2l.put(33, 5943);
		c2l.put(34, 5993);
		c2l.put(35, 6280);
		c2l.put(36, 6659);
		c2l.put(37, 6896);
		c2l.put(38, 6931);
		c2l.put(39, 7263);
		c2l.put(40, 7581);
		c2l.put(41, 7994);
		c2l.put(42, 8212);
		c2l.put(43, 8345);
		c2l.put(44, 8419);
		c2l.put(45, 8524);
		c2l.put(46, 8872);
		c2l.put(47, 9373);
		c2l.put(48, 9780);
		c2l.put(49, 10017);
		c2l.put(50, 10068);
		c2l.put(51, 10187);
		c2l.put(52, 10295);
		c2l.put(53, 10743);
		c2l.put(54, 10906);
		c2l.put(55, 10944);
		c2l.put(56, 11182);
		c2l.put(57, 11249);
		c2l.put(58, 11571);
		c2l.put(59, 11666);
		c2l.put(60, 11691);
		c2l.put(61, 12119);
		c2l.put(62, 12206);
		c2l.put(63, 12393);
		c2l.put(64, 12485);
		c2l.put(65, 12602);
		c2l.put(66, 12637);
		c2l.put(67, 12690);
		c2l.put(68, 12815);
		c2l.put(69, 12852);
		c2l.put(70, 12914);
		c2l.put(71, 12942);
		c2l.put(72, 12997);
		c2l.put(73, 13122);
		c2l.put(74, 13162);
		c2l.put(75, 13198);
		c2l.put(76, 13334);
		c2l.put(77, 13597);
		c2l.put(78, 13644);
		c2l.put(79, 13699);
		c2l.put(80, 13746);
		c2l.put(81, 13778);
		c2l.put(82, 13995);
		c2l.put(83, 14115);
		c2l.put(84, 14170);
		c2l.put(85, 14650);
		c2l.put(86, 14819);
		c2l.put(87, 14842);
		c2l.put(88, 15160);
		c2l.put(89, 15185);
		c2l.put(90, 15227);
		c2l.put(91, 15772);
		c2l.put(92, 15882);
		c2l.put(93, 16100);
		c2l.put(94, 16168);
		c2l.put(95, 16210);
		c2l.put(96, 16250);
		c2l.put(97, 16293);
		c2l.put(98, 16475);
		c2l.put(99, 16799);
		c2l.put(100, 17210);
		c2l.put(101, 17478);
		c2l.put(102, 17841);
		c2l.put(103, 18053);
		c2l.put(104, 18098);
		c2l.put(105, 18236);
		c2l.put(106, 18364);
		c2l.put(107, 18407);
		c2l.put(108, 18443);
		c2l.put(109, 18478);
		c2l.put(110, 18510);
		c2l.put(111, 18637);
		c2l.put(112, 18791);
		c2l.put(113, 18841);
		c2l.put(114, 18883);
		c2l.put(115, 18967);
		c2l.put(116, 19008);
		c2l.put(117, 19069);
		c2l.put(118, 19509);
		c2l.put(119, 19865);
		c2l.put(120, 20034);
		c2l.put(121, 20349);
		c2l.put(122, 20381);
		c2l.put(123, 20503);
		c2l.put(124, 20633);
		c2l.put(125, 20653);
		c2l.put(126, 20848);
		c2l.put(127, 21126);
		c2l.put(128, 21168);
		c2l.put(129, 21256);
		c2l.put(130, 21351);
		c2l.put(131, 21399);
		c2l.put(132, 21425);
		c2l.put(133, 21475);
		c2l.put(134, 21576);
		c2l.put(135, 21576);
		c2l.put(136, 21616);
		c2l.put(137, 21719);
		c2l.put(138, 21752);
		c2l.put(139, 21776);
		c2l.put(140, 22141);
		c2l.put(141, 22286);
		c2l.put(142, 22342);
		c2l.put(143, 22477);
		c2l.put(144, 22495);
		c2l.put(145, 22592);
		c2l.put(146, 22610);
		c2l.put(147, 22687);
		c2l.put(148, 23037);
		c2l.put(149, 23271);
		c2l.put(150, 23668);
		c2l.put(151, 24109);
		c2l.put(152, 24365);
		c2l.put(153, 24569);
		c2l.put(154, 25063);
		c2l.put(155, 25174);
		c2l.put(156, 25242);
		c2l.put(157, 25469);
		c2l.put(158, 25497);
		c2l.put(159, 25623);
		c2l.put(160, 25696);
		c2l.put(161, 26069);
		c2l.put(162, 26221);
		c2l.put(163, 26247);
		c2l.put(164, 26269);
		c2l.put(165, 26429);
		c2l.put(166, 26464);
		c2l.put(167, 26519);
		c2l.put(168, 26833);
		c2l.put(169, 27012);
		c2l.put(170, 27066);
		c2l.put(171, 27331);
		c2l.put(172, 27430);
		c2l.put(173, 27490);
		c2l.put(174, 27815);
		c2l.put(175, 28068);
		c2l.put(176, 28094);
		c2l.put(177, 28379);
		c2l.put(178, 28422);
		c2l.put(179, 28445);
		c2l.put(180, 28479);
		c2l.put(181, 28825);
		c2l.put(182, 29194);
		c2l.put(183, 29354);
		c2l.put(184, 29422);
		c2l.put(185, 29487);
		c2l.put(186, 29522);
		c2l.put(187, 29595);
		c2l.put(188, 29727);
		c2l.put(189, 30018);
		c2l.put(190, 30251);
		c2l.put(191, 30674);
		c2l.put(192, 30973);
		c2l.put(193, 31233);
		c2l.put(194, 31584);
		c2l.put(195, 31935);
		c2l.put(196, 32323);
		c2l.put(197, 32650);
		c2l.put(198, 32719);
		c2l.put(199, 32758);

		for (int i = 0; i < 199; i++) {
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
