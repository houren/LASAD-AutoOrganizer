package lasad.gwt.client.importer.LARGO;

import java.util.HashMap;

import lasad.gwt.client.logger.Logger;

public class AsahiPetTranscript implements LARGOTranscriptMappingInterface {

	private int line = 0;
	//private int charNumber;
	private int point;

	public void mapChars(int charNumber, boolean start) {

		HashMap<Integer, Integer> c2l = new HashMap<Integer, Integer>();
		//this.charNumber = charNumber;

		c2l.put(0, 3);
		c2l.put(1, 513);
		c2l.put(2, 582);
		c2l.put(3, 610);
		c2l.put(4, 733);
		c2l.put(5, 782);
		c2l.put(6, 853);
		c2l.put(7, 913);
		c2l.put(8, 1202);
		c2l.put(9, 1584);
		c2l.put(10, 1956);
		c2l.put(11, 2149);
		c2l.put(12, 2555);
		c2l.put(13, 2790);
		c2l.put(14, 3232);
		c2l.put(15, 3534);
		c2l.put(16, 3692);
		c2l.put(17, 3845);
		c2l.put(18, 3909);
		c2l.put(19, 3962);
		c2l.put(20, 3983);
		c2l.put(21, 4005);
		c2l.put(22, 4104);
		c2l.put(23, 4151);
		c2l.put(24, 4176);
		c2l.put(25, 4228);
		c2l.put(26, 4540);
		c2l.put(27, 4870);
		c2l.put(28, 5222);
		c2l.put(29, 5317);
		c2l.put(30, 5457);
		c2l.put(31, 5492);
		c2l.put(32, 5769);
		c2l.put(33, 5859);
		c2l.put(34, 6165);
		c2l.put(35, 6389);
		c2l.put(36, 6856);
		c2l.put(37, 7531);
		c2l.put(38, 7642);
		c2l.put(39, 7727);
		c2l.put(40, 7800);
		c2l.put(41, 7872);
		c2l.put(42, 8032);
		c2l.put(43, 8102);
		c2l.put(44, 8247);
		c2l.put(45, 8404);
		c2l.put(46, 8498);
		c2l.put(47, 8543);
		c2l.put(48, 8580);
		c2l.put(49, 8601);
		c2l.put(50, 8850);
		c2l.put(51, 8915);
		c2l.put(52, 9035);
		c2l.put(53, 9188);
		c2l.put(54, 9512);
		c2l.put(55, 9866);
		c2l.put(56, 10167);
		c2l.put(57, 10544);
		c2l.put(58, 10802);
		c2l.put(59, 11249);
		c2l.put(60, 11635);
		c2l.put(61, 12094);
		c2l.put(62, 12262);
		c2l.put(63, 12392);
		c2l.put(64, 12711);
		c2l.put(65, 12933);
		c2l.put(66, 13056);
		c2l.put(67, 13275);
		c2l.put(68, 13381);
		c2l.put(69, 13410);
		c2l.put(70, 13476);
		c2l.put(71, 13514);
		c2l.put(72, 13594);
		c2l.put(73, 13616);
		c2l.put(74, 13681);
		c2l.put(75, 13718);
		c2l.put(76, 13764);
		c2l.put(77, 13795);
		c2l.put(78, 13895);
		c2l.put(79, 14062);
		c2l.put(80, 14207);
		c2l.put(81, 14265);
		c2l.put(82, 14297);
		c2l.put(83, 14331);
		c2l.put(84, 14378);
		c2l.put(85, 14411);
		c2l.put(86, 14472);
		c2l.put(87, 14529);
		c2l.put(88, 14575);
		c2l.put(89, 14796);
		c2l.put(90, 15032);
		c2l.put(91, 15304);
		c2l.put(92, 15407);
		c2l.put(93, 15568);
		c2l.put(94, 15623);
		c2l.put(95, 15838);
		c2l.put(96, 15968);
		c2l.put(97, 16162);
		c2l.put(98, 16204);
		c2l.put(99, 16389);
		c2l.put(100, 16571);
		c2l.put(101, 16947);
		c2l.put(102, 17078);
		c2l.put(103, 17187);
		c2l.put(104, 17259);
		c2l.put(105, 17544);
		c2l.put(106, 17899);
		c2l.put(107, 18167);
		c2l.put(108, 18215);
		c2l.put(109, 18326);
		c2l.put(110, 18403);
		c2l.put(111, 18432);
		c2l.put(112, 18547);
		c2l.put(113, 18802);
		c2l.put(114, 19061);
		c2l.put(115, 19470);
		c2l.put(116, 19542);
		c2l.put(117, 19865);
		c2l.put(118, 19913);
		c2l.put(119, 19979);
		c2l.put(120, 20206);
		c2l.put(121, 20488);
		c2l.put(122, 20689);
		c2l.put(123, 20909);
		c2l.put(124, 21358);
		c2l.put(125, 21417);
		c2l.put(126, 21928);
		c2l.put(127, 21969);
		c2l.put(128, 22029);
		c2l.put(129, 22166);
		c2l.put(130, 22285);
		c2l.put(131, 22555);
		c2l.put(132, 22611);
		c2l.put(133, 22749);
		c2l.put(134, 22769);
		c2l.put(135, 22872);
		c2l.put(136, 22958);
		c2l.put(137, 23092);
		c2l.put(138, 23179);
		c2l.put(139, 23419);
		c2l.put(140, 23786);
		c2l.put(141, 24129);
		c2l.put(142, 24501);
		c2l.put(143, 24725);
		c2l.put(144, 24741);
		c2l.put(145, 30000);
		for (int i = 0; i < 145; i++) {
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
