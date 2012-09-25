package routines.system;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

public class OracleTIMESTAMPTZUtils {

    private static byte REGIONIDBIT = -128;

    private static int getHighOrderbits(int paramInt) {
        return (paramInt & 0x7F) << 6;
    }

    private static int getLowOrderbits(int paramInt) {
        return (paramInt & 0xFC) >> 2;
    }

    private static int OFFSET_HOUR = 20;

    private static int OFFSET_MINUTE = 60;

    public static TalendTimestampWithTZ toTalendTSTZ(Connection paramConnection, byte[] paramArrayOfByte) throws SQLException {
        Timestamp localTimestamp = toTimestamp2(paramConnection, paramArrayOfByte);

        int i3;
        String str;
        if ((paramArrayOfByte[11] & REGIONIDBIT) != 0) {
            i3 = getHighOrderbits(paramArrayOfByte[11]);
            i3 += getLowOrderbits(paramArrayOfByte[12]);
            str = new String(ZONEIDMAP.getRegion(i3));
        } else {
            i3 = paramArrayOfByte[11] - OFFSET_HOUR;
            String sign = i3 >= 0 ? "+" : "";
            int i4 = Math.abs(paramArrayOfByte[12] - OFFSET_MINUTE);
            String i4s = i4 < 10 ? "0" + i4 : i4 + "";
            str = new String("GMT" + sign + i3 + ":" + i4s);
        }

        return new TalendTimestampWithTZ(localTimestamp, TimeZone.getTimeZone(str));
    }

    private static int SIZE_TIMESTAMPTZ = 13;

    public static Timestamp toTimestamp2(Connection connection, byte abyte0[]) throws SQLException {
        int ai[] = new int[SIZE_TIMESTAMPTZ];
        for (int i = 0; i < SIZE_TIMESTAMPTZ; i++) {
            ai[i] = abyte0[i] & 255;
        }

        int j = getJavaYear(ai[0], ai[1]);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.clear();
        calendar.set(1, j);
        calendar.set(2, ai[2] - 1);
        calendar.set(5, ai[3]);
        calendar.set(11, ai[4] - 1);
        calendar.set(12, ai[5] - 1);
        calendar.set(13, ai[6] - 1);
        calendar.set(14, 0);
        long l = calendar.getTime().getTime();
        Timestamp timestamp = new Timestamp(l);
        int k = ai[7] << 24;
        k |= ai[8] << 16;
        k |= ai[9] << 8;
        k |= ai[10] & 255;
        timestamp.setNanos(k);
        return timestamp;
    }

    private static int getJavaYear(int i, int j) {
        return (i - 100) * 100 + (j - 100);
    }

    static class ZONEIDMAP {

        static class TableClass extends Hashtable {

            TableClass() {
                v = new Vector();
            }

            public void dispTable() {
                for (int i = 0; i < v.size(); i++) {
                    System.out.println(i + "   " + v.elementAt(i));
                }

            }

            public Object getKey(int i) {
                return v.elementAt(i);
            }

            public Object put(Object obj, Integer integer) {
                if (v.size() < integer.intValue() + 1) {
                    v.setSize(integer.intValue() + 1);
                }
                if (v.elementAt(integer.intValue()) != null) {
                    return super.get(obj);
                } else {
                    super.put(obj, integer);
                    v.setElementAt(obj, integer.intValue());
                    return null;
                }
            }

            private Vector v;
        }

        public ZONEIDMAP() {
        }

        public static int getID(String s) {
            Integer integer = (Integer) zoneid.get(s);
            if (integer == null) {
                return INV_ZONEID;
            } else {
                return integer.intValue();
            }
        }

        public static String getRegion(int i) {
            return (String) zoneid.getKey(i);
        }

        static TableClass zoneid;

        protected static int INV_ZONEID = -1;

        static {
            zoneid = new TableClass();
            zoneid.put("Africa/Asmera", new Integer(46));
            zoneid.put("Africa/Bamako", new Integer(58));
            zoneid.put("Africa/Bangui", new Integer(37));
            zoneid.put("Africa/Banjul", new Integer(49));
            zoneid.put("Africa/Bissau", new Integer(52));
            zoneid.put("Africa/Blantyre", new Integer(57));
            zoneid.put("Africa/Bujumbura", new Integer(35));
            zoneid.put("Africa/Cairo", new Integer(44));
            zoneid.put("Africa/Casablanca", new Integer(61));
            zoneid.put("Africa/Conakry", new Integer(51));
            zoneid.put("Africa/Dakar", new Integer(69));
            zoneid.put("Africa/Dar_es_Salaam", new Integer(75));
            zoneid.put("Africa/Djibouti", new Integer(43));
            zoneid.put("Africa/Douala", new Integer(36));
            zoneid.put("Africa/Freetown", new Integer(70));
            zoneid.put("Africa/Gaborone", new Integer(33));
            zoneid.put("Africa/Harare", new Integer(80));
            zoneid.put("Africa/Johannesburg", new Integer(72));
            zoneid.put("Africa/Kampala", new Integer(78));
            zoneid.put("Africa/Khartoum", new Integer(73));
            zoneid.put("Africa/Kigali", new Integer(67));
            zoneid.put("Africa/Kinshasa", new Integer(39));
            zoneid.put("Africa/Lagos", new Integer(66));
            zoneid.put("Africa/Libreville", new Integer(48));
            zoneid.put("Africa/Lome", new Integer(76));
            zoneid.put("Africa/Luanda", new Integer(31));
            zoneid.put("Africa/Lubumbashi", new Integer(40));
            zoneid.put("Africa/Lusaka", new Integer(79));
            zoneid.put("Africa/Malabo", new Integer(45));
            zoneid.put("Africa/Maputo", new Integer(63));
            zoneid.put("Africa/Maseru", new Integer(54));
            zoneid.put("Africa/Mbabane", new Integer(74));
            zoneid.put("Africa/Mogadishu", new Integer(71));
            zoneid.put("Africa/Monrovia", new Integer(55));
            zoneid.put("Africa/Nairobi", new Integer(53));
            zoneid.put("Africa/Ndjamena", new Integer(38));
            zoneid.put("Africa/Niamey", new Integer(65));
            zoneid.put("Africa/Nouakchott", new Integer(60));
            zoneid.put("Africa/Ouagadougou", new Integer(34));
            zoneid.put("Africa/Porto-Novo", new Integer(32));
            zoneid.put("Africa/Sao_Tome", new Integer(68));
            zoneid.put("Africa/Timbuktu", new Integer(59));
            zoneid.put("Africa/Tripoli", new Integer(56));
            zoneid.put("Africa/Tunis", new Integer(77));
            zoneid.put("Africa/Windhoek", new Integer(64));
            zoneid.put("America/Adak", new Integer(108));
            zoneid.put("America/Anchorage", new Integer(106));
            zoneid.put("America/Anguilla", new Integer(146));
            zoneid.put("America/Antigua", new Integer(147));
            zoneid.put("America/Aruba", new Integer(181));
            zoneid.put("America/Asuncion", new Integer(200));
            zoneid.put("America/Barbados", new Integer(149));
            zoneid.put("America/Belize", new Integer(150));
            zoneid.put("America/Bogota", new Integer(195));
            zoneid.put("America/Buenos_Aires", new Integer(175));
            zoneid.put("America/Argentina/Buenos_Aires", new Integer(687));
            zoneid.put("America/Caracas", new Integer(205));
            zoneid.put("America/Cayenne", new Integer(198));
            zoneid.put("America/Cayman", new Integer(151));
            zoneid.put("America/Chicago", new Integer(101));
            zoneid.put("America/Costa_Rica", new Integer(152));
            zoneid.put("America/Cuiaba", new Integer(189));
            zoneid.put("America/Curacao", new Integer(196));
            zoneid.put("America/Dawson_Creek", new Integer(131));
            zoneid.put("America/Denver", new Integer(102));
            zoneid.put("America/Dominica", new Integer(154));
            zoneid.put("America/Edmonton", new Integer(129));
            zoneid.put("America/El_Salvador", new Integer(156));
            zoneid.put("America/Fortaleza", new Integer(185));
            zoneid.put("America/Godthab", new Integer(207));
            zoneid.put("America/Grand_Turk", new Integer(172));
            zoneid.put("America/Grenada", new Integer(157));
            zoneid.put("America/Guadeloupe", new Integer(158));
            zoneid.put("America/Guatemala", new Integer(159));
            zoneid.put("America/Guayaquil", new Integer(197));
            zoneid.put("America/Guyana", new Integer(199));
            zoneid.put("America/Halifax", new Integer(120));
            zoneid.put("America/Havana", new Integer(153));
            zoneid.put("America/Indianapolis", new Integer(111));
            zoneid.put("America/Jamaica", new Integer(162));
            zoneid.put("America/La_Paz", new Integer(182));
            zoneid.put("America/Lima", new Integer(201));
            zoneid.put("America/Los_Angeles", new Integer(103));
            zoneid.put("America/Managua", new Integer(165));
            zoneid.put("America/Manaus", new Integer(192));
            zoneid.put("America/Martinique", new Integer(163));
            zoneid.put("America/Mazatlan", new Integer(144));
            zoneid.put("America/Mexico_City", new Integer(141));
            zoneid.put("America/Miquelon", new Integer(170));
            zoneid.put("America/Montevideo", new Integer(204));
            zoneid.put("America/Montreal", new Integer(122));
            zoneid.put("America/Montserrat", new Integer(164));
            zoneid.put("America/Nassau", new Integer(148));
            zoneid.put("America/New_York", new Integer(100));
            zoneid.put("America/Noronha", new Integer(183));
            zoneid.put("America/Panama", new Integer(166));
            zoneid.put("America/Paramaribo", new Integer(202));
            zoneid.put("America/Phoenix", new Integer(109));
            zoneid.put("America/Port-au-Prince", new Integer(160));
            zoneid.put("America/Port_of_Spain", new Integer(203));
            zoneid.put("America/Porto_Acre", new Integer(193));
            zoneid.put("America/Puerto_Rico", new Integer(167));
            zoneid.put("America/Regina", new Integer(127));
            zoneid.put("America/Santiago", new Integer(194));
            zoneid.put("America/Santo_Domingo", new Integer(155));
            zoneid.put("America/Sao_Paulo", new Integer(188));
            zoneid.put("America/Scoresbysund", new Integer(206));
            zoneid.put("America/St_Johns", new Integer(118));
            zoneid.put("America/St_Kitts", new Integer(168));
            zoneid.put("America/St_Lucia", new Integer(169));
            zoneid.put("America/St_Thomas", new Integer(174));
            zoneid.put("America/St_Vincent", new Integer(171));
            zoneid.put("America/Tegucigalpa", new Integer(161));
            zoneid.put("America/Thule", new Integer(208));
            zoneid.put("America/Tijuana", new Integer(145));
            zoneid.put("America/Tortola", new Integer(173));
            zoneid.put("America/Vancouver", new Integer(130));
            zoneid.put("America/Winnipeg", new Integer(126));
            zoneid.put("PST", new Integer(2151));
            zoneid.put("EST", new Integer(211));
            zoneid.put("CST", new Integer(1637));
            zoneid.put("Antarctica/Casey", new Integer(230));
            zoneid.put("Antarctica/DumontDUrville", new Integer(233));
            zoneid.put("Antarctica/Mawson", new Integer(232));
            zoneid.put("Antarctica/McMurdo", new Integer(236));
            zoneid.put("Antarctica/Palmer", new Integer(235));
            zoneid.put("Asia/Aden", new Integer(302));
            zoneid.put("Asia/Amman", new Integer(268));
            zoneid.put("Asia/Anadyr", new Integer(312));
            zoneid.put("Asia/Aqtau", new Integer(271));
            zoneid.put("Asia/Aqtobe", new Integer(270));
            zoneid.put("Asia/Ashkhabad", new Integer(297));
            zoneid.put("Asia/Baghdad", new Integer(265));
            zoneid.put("Asia/Bahrain", new Integer(243));
            zoneid.put("Asia/Baku", new Integer(242));
            zoneid.put("Asia/Bangkok", new Integer(296));
            zoneid.put("Asia/Beirut", new Integer(277));
            zoneid.put("Asia/Bishkek", new Integer(272));
            zoneid.put("Asia/Brunei", new Integer(246));
            zoneid.put("Asia/Calcutta", new Integer(260));
            zoneid.put("Asia/Colombo", new Integer(293));
            zoneid.put("Asia/Dacca", new Integer(244));
            zoneid.put("Asia/Damascus", new Integer(294));
            zoneid.put("Asia/Dubai", new Integer(298));
            zoneid.put("Asia/Dushanbe", new Integer(295));
            zoneid.put("Asia/Hong_Kong", new Integer(254));
            zoneid.put("Asia/Irkutsk", new Integer(307));
            zoneid.put("Asia/Jakarta", new Integer(261));
            zoneid.put("Asia/Jayapura", new Integer(263));
            zoneid.put("Asia/Jerusalem", new Integer(266));
            zoneid.put("Asia/Kabul", new Integer(240));
            zoneid.put("Asia/Kamchatka", new Integer(311));
            zoneid.put("Asia/Karachi", new Integer(284));
            zoneid.put("Asia/Katmandu", new Integer(282));
            zoneid.put("Asia/Krasnoyarsk", new Integer(306));
            zoneid.put("Asia/Kuala_Lumpur", new Integer(278));
            zoneid.put("Asia/Kuwait", new Integer(275));
            zoneid.put("Asia/Macao", new Integer(256));
            zoneid.put("Asia/Magadan", new Integer(310));
            zoneid.put("Asia/Manila", new Integer(286));
            zoneid.put("Asia/Muscat", new Integer(283));
            zoneid.put("Asia/Nicosia", new Integer(257));
            zoneid.put("Asia/Novosibirsk", new Integer(305));
            zoneid.put("Asia/Phnom_Penh", new Integer(248));
            zoneid.put("Asia/Pyongyang", new Integer(274));
            zoneid.put("Asia/Qatar", new Integer(287));
            zoneid.put("Asia/Rangoon", new Integer(247));
            zoneid.put("Asia/Riyadh", new Integer(288));
            zoneid.put("Asia/Saigon", new Integer(301));
            zoneid.put("Asia/Seoul", new Integer(273));
            zoneid.put("Asia/Shanghai", new Integer(250));
            zoneid.put("Asia/Singapore", new Integer(292));
            zoneid.put("Asia/Taipei", new Integer(255));
            zoneid.put("Asia/Tashkent", new Integer(300));
            zoneid.put("Asia/Tbilisi", new Integer(258));
            zoneid.put("Asia/Tehran", new Integer(264));
            zoneid.put("Asia/Thimbu", new Integer(245));
            zoneid.put("Asia/Tokyo", new Integer(267));
            zoneid.put("Asia/Ujung_Pandang", new Integer(262));
            zoneid.put("Asia/Ulan_Bator", new Integer(793));
            zoneid.put("Asia/Vientiane", new Integer(276));
            zoneid.put("Asia/Vladivostok", new Integer(309));
            zoneid.put("Asia/Yakutsk", new Integer(308));
            zoneid.put("Asia/Yekaterinburg", new Integer(303));
            zoneid.put("Asia/Yerevan", new Integer(241));
            zoneid.put("Atlantic/Azores", new Integer(336));
            zoneid.put("Atlantic/Bermuda", new Integer(330));
            zoneid.put("Atlantic/Canary", new Integer(338));
            zoneid.put("Atlantic/Cape_Verde", new Integer(339));
            zoneid.put("Atlantic/Faeroe", new Integer(333));
            zoneid.put("Atlantic/Jan_Mayen", new Integer(335));
            zoneid.put("Atlantic/Reykjavik", new Integer(334));
            zoneid.put("Atlantic/South_Georgia", new Integer(332));
            zoneid.put("Atlantic/St_Helena", new Integer(340));
            zoneid.put("Atlantic/Stanley", new Integer(331));
            zoneid.put("Australia/Adelaide", new Integer(349));
            zoneid.put("Australia/Brisbane", new Integer(347));
            zoneid.put("Australia/Darwin", new Integer(345));
            zoneid.put("Australia/Perth", new Integer(346));
            zoneid.put("Australia/Sydney", new Integer(352));
            zoneid.put("Australia/ACT", new Integer(864));
            zoneid.put("EET", new Integer(368));
            zoneid.put("GMT", new Integer(513));
            zoneid.put("UTC", new Integer(5121));
            zoneid.put("MET", new Integer(367));
            zoneid.put("MST", new Integer(212));
            zoneid.put("HST", new Integer(213));
            zoneid.put("Europe/Amsterdam", new Integer(396));
            zoneid.put("Europe/Andorra", new Integer(373));
            zoneid.put("Europe/Athens", new Integer(385));
            zoneid.put("Europe/Belgrade", new Integer(412));
            zoneid.put("Europe/Berlin", new Integer(383));
            zoneid.put("Europe/Brussels", new Integer(376));
            zoneid.put("Europe/Bucharest", new Integer(400));
            zoneid.put("Europe/Budapest", new Integer(386));
            zoneid.put("Europe/Chisinau", new Integer(393));
            zoneid.put("Europe/Copenhagen", new Integer(379));
            zoneid.put("Europe/Dublin", new Integer(371));
            zoneid.put("Europe/Gibraltar", new Integer(384));
            zoneid.put("Europe/Guernsey", new Integer(2417));
            zoneid.put("Europe/Helsinki", new Integer(381));
            zoneid.put("Isle_of_Man", new Integer(2929));
            zoneid.put("Europe/Istanbul", new Integer(407));
            zoneid.put("Europe/Jersey", new Integer(1905));
            zoneid.put("Europe/Kaliningrad", new Integer(401));
            zoneid.put("Europe/Kiev", new Integer(408));
            zoneid.put("Europe/Lisbon", new Integer(399));
            zoneid.put("Europe/London", new Integer(369));
            zoneid.put("Europe/Luxembourg", new Integer(391));
            zoneid.put("Europe/Madrid", new Integer(404));
            zoneid.put("Europe/Malta", new Integer(392));
            zoneid.put("Europe/Mariehamn", new Integer(893));
            zoneid.put("Europe/Minsk", new Integer(375));
            zoneid.put("Europe/Monaco", new Integer(395));
            zoneid.put("Europe/Moscow", new Integer(402));
            zoneid.put("Europe/Oslo", new Integer(397));
            zoneid.put("Europe/Paris", new Integer(382));
            zoneid.put("Europe/Podgorica", new Integer(2972));
            zoneid.put("Europe/Prague", new Integer(378));
            zoneid.put("Europe/Riga", new Integer(388));
            zoneid.put("Europe/Rome", new Integer(387));
            zoneid.put("Europe/Samara", new Integer(403));
            zoneid.put("Europe/Simferopol", new Integer(411));
            zoneid.put("Europe/Sofia", new Integer(377));
            zoneid.put("Europe/Stockholm", new Integer(405));
            zoneid.put("Europe/Tallinn", new Integer(380));
            zoneid.put("Europe/Tirane", new Integer(372));
            zoneid.put("Europe/Vaduz", new Integer(389));
            zoneid.put("Europe/Vienna", new Integer(374));
            zoneid.put("Europe/Vilnius", new Integer(390));
            zoneid.put("Europe/Volgograd", new Integer(413));
            zoneid.put("Europe/Warsaw", new Integer(398));
            zoneid.put("Europe/Zurich", new Integer(406));
            zoneid.put("Indian/Antananarivo", new Integer(438));
            zoneid.put("Indian/Chagos", new Integer(436));
            zoneid.put("Indian/Christmas", new Integer(439));
            zoneid.put("Indian/Cocos", new Integer(440));
            zoneid.put("Indian/Comoro", new Integer(441));
            zoneid.put("Indian/Kerguelen", new Integer(435));
            zoneid.put("Indian/Mahe", new Integer(442));
            zoneid.put("Indian/Maldives", new Integer(437));
            zoneid.put("Indian/Mauritius", new Integer(443));
            zoneid.put("Indian/Mayotte", new Integer(444));
            zoneid.put("Indian/Reunion", new Integer(445));
            zoneid.put("Pacific/Apia", new Integer(479));
            zoneid.put("Pacific/Auckland", new Integer(471));
            zoneid.put("Pacific/Chatham", new Integer(472));
            zoneid.put("Pacific/Easter", new Integer(451));
            zoneid.put("Pacific/Efate", new Integer(488));
            zoneid.put("Pacific/Enderbury", new Integer(460));
            zoneid.put("Pacific/Fakaofo", new Integer(482));
            zoneid.put("Pacific/Fiji", new Integer(454));
            zoneid.put("Pacific/Funafuti", new Integer(484));
            zoneid.put("Pacific/Galapagos", new Integer(452));
            zoneid.put("Pacific/Gambier", new Integer(455));
            zoneid.put("Pacific/Guadalcanal", new Integer(481));
            zoneid.put("Pacific/Guam", new Integer(458));
            zoneid.put("Pacific/Honolulu", new Integer(450));
            zoneid.put("Pacific/Kiritimati", new Integer(461));
            zoneid.put("Pacific/Kosrae", new Integer(468));
            zoneid.put("Pacific/Majuro", new Integer(463));
            zoneid.put("Pacific/Marquesas", new Integer(456));
            zoneid.put("Pacific/Nauru", new Integer(469));
            zoneid.put("Pacific/Niue", new Integer(473));
            zoneid.put("Pacific/Norfolk", new Integer(474));
            zoneid.put("Pacific/Noumea", new Integer(470));
            zoneid.put("Pacific/Pago_Pago", new Integer(478));
            zoneid.put("Pacific/Palau", new Integer(475));
            zoneid.put("Pacific/Pitcairn", new Integer(477));
            zoneid.put("Pacific/Ponape", new Integer(467));
            zoneid.put("Pacific/Port_Moresby", new Integer(476));
            zoneid.put("Pacific/Rarotonga", new Integer(453));
            zoneid.put("Pacific/Saipan", new Integer(462));
            zoneid.put("Pacific/Tahiti", new Integer(457));
            zoneid.put("Pacific/Tarawa", new Integer(459));
            zoneid.put("Pacific/Tongatapu", new Integer(483));
            zoneid.put("Pacific/Truk", new Integer(466));
            zoneid.put("Pacific/Wake", new Integer(487));
            zoneid.put("Pacific/Wallis", new Integer(489));
            zoneid.put("Africa/Brazzaville", new Integer(41));
            zoneid.put("Egypt", new Integer(556));
            zoneid.put("Africa/Ceuta", new Integer(81));
            zoneid.put("Africa/El_Aaiun", new Integer(62));
            zoneid.put("Libya", new Integer(568));
            zoneid.put("America/Atka", new Integer(620));
            zoneid.put("US/Aleutian", new Integer(1132));
            zoneid.put("US/Alaska", new Integer(618));
            zoneid.put("America/Araguaina", new Integer(186));
            zoneid.put("America/Belem", new Integer(184));
            zoneid.put("America/Boa_Vista", new Integer(191));
            zoneid.put("America/Boise", new Integer(110));
            zoneid.put("America/Cambridge_Bay", new Integer(135));
            zoneid.put("America/Cancun", new Integer(140));
            zoneid.put("America/Catamarca", new Integer(179));
            zoneid.put("CST6CDT", new Integer(215));
            zoneid.put("US/Central", new Integer(613));
            zoneid.put("America/Chihuahua", new Integer(142));
            zoneid.put("America/Cordoba", new Integer(177));
            zoneid.put("America/Dawson", new Integer(139));
            zoneid.put("America/Shiprock", new Integer(1638));
            zoneid.put("MST7MDT", new Integer(216));
            zoneid.put("Navajo", new Integer(614));
            zoneid.put("US/Mountain", new Integer(1126));
            zoneid.put("America/Detroit", new Integer(116));
            zoneid.put("US/Michigan", new Integer(628));
            zoneid.put("Canada/Mountain", new Integer(641));
            zoneid.put("America/Glace_Bay", new Integer(121));
            zoneid.put("America/Goose_Bay", new Integer(119));
            zoneid.put("Canada/Atlantic", new Integer(632));
            zoneid.put("Cuba", new Integer(665));
            zoneid.put("America/Hermosillo", new Integer(143));
            zoneid.put("America/Indiana/Knox", new Integer(113));
            zoneid.put("America/Knox_IN", new Integer(625));
            zoneid.put("US/Indiana-Starke", new Integer(1137));
            zoneid.put("America/Indiana/Marengo", new Integer(112));
            zoneid.put("America/Indiana/Vevay", new Integer(114));
            zoneid.put("America/Fort_Wayne", new Integer(623));
            zoneid.put("America/Indiana/Indianapolis", new Integer(1647));
            zoneid.put("America/Indiana/Vincennes", new Integer(209));
            zoneid.put("America/Indiana/Petersburg", new Integer(210));
            zoneid.put("US/East-Indiana", new Integer(1135));
            zoneid.put("America/Inuvik", new Integer(137));
            zoneid.put("America/Iqaluit", new Integer(133));
            zoneid.put("Jamaica", new Integer(674));
            zoneid.put("America/Jujuy", new Integer(178));
            zoneid.put("America/Juneau", new Integer(104));
            zoneid.put("PST8PDT", new Integer(217));
            zoneid.put("US/Pacific", new Integer(615));
            zoneid.put("US/Pacific-New", new Integer(1639));
            zoneid.put("America/Louisville", new Integer(115));
            zoneid.put("America/Maceio", new Integer(187));
            zoneid.put("Brazil/West", new Integer(704));
            zoneid.put("Mexico/BajaSur", new Integer(656));
            zoneid.put("America/Mendoza", new Integer(180));
            zoneid.put("America/Menominee", new Integer(117));
            zoneid.put("Mexico/General", new Integer(653));
            zoneid.put("Canada/Eastern", new Integer(634));
            zoneid.put("EST5EDT", new Integer(214));
            zoneid.put("US/Eastern", new Integer(612));
            zoneid.put("America/Nipigon", new Integer(124));
            zoneid.put("America/Nome", new Integer(107));
            zoneid.put("Brazil/DeNoronha", new Integer(695));
            zoneid.put("America/Pangnirtung", new Integer(132));
            zoneid.put("US/Arizona", new Integer(621));
            zoneid.put("Brazil/Acre", new Integer(705));
            zoneid.put("America/Porto_Velho", new Integer(190));
            zoneid.put("America/Rainy_River", new Integer(125));
            zoneid.put("America/Rankin_Inlet", new Integer(134));
            zoneid.put("Canada/East-Saskatchewan", new Integer(639));
            zoneid.put("Canada/Saskatchewan", new Integer(1151));
            zoneid.put("America/Rosario", new Integer(176));
            zoneid.put("Chile/Continental", new Integer(706));
            zoneid.put("Brazil/East", new Integer(700));
            zoneid.put("Canada/Newfoundland", new Integer(630));
            zoneid.put("America/Virgin", new Integer(686));
            zoneid.put("America/Swift_Current", new Integer(128));
            zoneid.put("America/Thunder_Bay", new Integer(123));
            zoneid.put("America/Ensenada", new Integer(657));
            zoneid.put("Mexico/BajaNorte", new Integer(1169));
            zoneid.put("Canada/Pacific", new Integer(642));
            zoneid.put("America/Whitehorse", new Integer(138));
            zoneid.put("Canada/Yukon", new Integer(650));
            zoneid.put("Canada/Central", new Integer(638));
            zoneid.put("America/Yakutat", new Integer(105));
            zoneid.put("America/Yellowknife", new Integer(136));
            zoneid.put("Antarctica/Davis", new Integer(231));
            zoneid.put("Antarctica/South_Pole", new Integer(748));
            zoneid.put("Antarctica/Syowa", new Integer(234));
            zoneid.put("Asia/Almaty", new Integer(269));
            zoneid.put("Asia/Chungking", new Integer(251));
            zoneid.put("Asia/Dili", new Integer(259));
            zoneid.put("Asia/Gaza", new Integer(285));
            zoneid.put("Asia/Harbin", new Integer(249));
            zoneid.put("Hongkong", new Integer(766));
            zoneid.put("Asia/Hovd", new Integer(280));
            zoneid.put("Asia/Istanbul", new Integer(1431));
            zoneid.put("Asia/Tel_Aviv", new Integer(778));
            zoneid.put("Israel", new Integer(1290));
            zoneid.put("Asia/Kashgar", new Integer(253));
            zoneid.put("Asia/Kuching", new Integer(279));
            zoneid.put("Asia/Omsk", new Integer(304));
            zoneid.put("Asia/Riyadh87", new Integer(289));
            zoneid.put("Mideast/Riyadh87", new Integer(801));
            zoneid.put("Asia/Riyadh88", new Integer(290));
            zoneid.put("Mideast/Riyadh88", new Integer(802));
            zoneid.put("Asia/Riyadh89", new Integer(291));
            zoneid.put("Mideast/Riyadh89", new Integer(803));
            zoneid.put("Asia/Samarkand", new Integer(299));
            zoneid.put("ROK", new Integer(785));
            zoneid.put("PRC", new Integer(762));
            zoneid.put("Singapore", new Integer(804));
            zoneid.put("ROC", new Integer(767));
            zoneid.put("Iran", new Integer(776));
            zoneid.put("Japan", new Integer(779));
            zoneid.put("Asia/Ulaanbaatar", new Integer(281));
            zoneid.put("Asia/Urumqi", new Integer(252));
            zoneid.put("Atlantic/Madeira", new Integer(337));
            zoneid.put("Iceland", new Integer(846));
            zoneid.put("Australia/South", new Integer(861));
            zoneid.put("Australia/Queensland", new Integer(859));
            zoneid.put("Australia/Broken_Hill", new Integer(353));
            zoneid.put("Australia/Yancowinna", new Integer(865));
            zoneid.put("Australia/North", new Integer(857));
            zoneid.put("Australia/Hobart", new Integer(350));
            zoneid.put("Australia/Tasmania", new Integer(862));
            zoneid.put("Australia/Lindeman", new Integer(348));
            zoneid.put("Australia/Lord_Howe", new Integer(354));
            zoneid.put("Australia/LHI", new Integer(866));
            zoneid.put("Australia/Melbourne", new Integer(351));
            zoneid.put("Australia/Victoria", new Integer(863));
            zoneid.put("Australia/West", new Integer(858));
            zoneid.put("Australia/Canberra", new Integer(1376));
            zoneid.put("Australia/NSW", new Integer(1888));
            zoneid.put("CET", new Integer(366));
            zoneid.put("Etc/GMT", new Integer(1));
            zoneid.put("Etc/GMT+0", new Integer(1025));
            zoneid.put("Etc/GMT-0", new Integer(2049));
            zoneid.put("Etc/GMT0", new Integer(3073));
            zoneid.put("Etc/Greenwich", new Integer(4097));
            zoneid.put("GMT+0", new Integer(1537));
            zoneid.put("GMT-0", new Integer(2561));
            zoneid.put("GMT0", new Integer(3585));
            zoneid.put("Greenwich", new Integer(4609));
            zoneid.put("Etc/GMT+1", new Integer(16));
            zoneid.put("Etc/GMT+10", new Integer(25));
            zoneid.put("Etc/GMT+11", new Integer(26));
            zoneid.put("Etc/GMT+12", new Integer(27));
            zoneid.put("Etc/GMT+2", new Integer(17));
            zoneid.put("Etc/GMT+3", new Integer(18));
            zoneid.put("Etc/GMT+4", new Integer(19));
            zoneid.put("Etc/GMT+5", new Integer(20));
            zoneid.put("Etc/GMT+6", new Integer(21));
            zoneid.put("Etc/GMT+7", new Integer(22));
            zoneid.put("Etc/GMT+8", new Integer(23));
            zoneid.put("Etc/GMT+9", new Integer(24));
            zoneid.put("Etc/GMT-1", new Integer(15));
            zoneid.put("Etc/GMT-10", new Integer(6));
            zoneid.put("Etc/GMT-11", new Integer(5));
            zoneid.put("Etc/GMT-12", new Integer(4));
            zoneid.put("Etc/GMT-13", new Integer(3));
            zoneid.put("Etc/GMT-14", new Integer(2));
            zoneid.put("Etc/GMT-2", new Integer(14));
            zoneid.put("Etc/GMT-3", new Integer(13));
            zoneid.put("Etc/GMT-4", new Integer(12));
            zoneid.put("Etc/GMT-5", new Integer(11));
            zoneid.put("Etc/GMT-6", new Integer(10));
            zoneid.put("Etc/GMT-7", new Integer(9));
            zoneid.put("Etc/GMT-8", new Integer(8));
            zoneid.put("Etc/GMT-9", new Integer(7));
            zoneid.put("Etc/UCT", new Integer(29));
            zoneid.put("UCT", new Integer(541));
            zoneid.put("Etc/UTC", new Integer(28));
            zoneid.put("Etc/Universal", new Integer(1052));
            zoneid.put("Etc/Zulu", new Integer(2076));
            zoneid.put("Universal", new Integer(1564));
            zoneid.put("Zulu", new Integer(2588));
            zoneid.put("Europe/Belfast", new Integer(370));
            zoneid.put("Europe/Ljubljana", new Integer(924));
            zoneid.put("Europe/Sarajevo", new Integer(1436));
            zoneid.put("Europe/Skopje", new Integer(1948));
            zoneid.put("Europe/Zagreb", new Integer(2460));
            zoneid.put("Eire", new Integer(883));
            zoneid.put("Turkey", new Integer(919));
            zoneid.put("Portugal", new Integer(911));
            zoneid.put("GB", new Integer(881));
            zoneid.put("GB-Eire", new Integer(1393));
            zoneid.put("W-SU", new Integer(914));
            zoneid.put("Europe/Bratislava", new Integer(890));
            zoneid.put("Europe/San_Marino", new Integer(1411));
            zoneid.put("Europe/Vatican", new Integer(899));
            zoneid.put("Europe/Tiraspol", new Integer(394));
            zoneid.put("Europe/Uzhgorod", new Integer(409));
            zoneid.put("Poland", new Integer(910));
            zoneid.put("Europe/Zaporozhye", new Integer(410));
            zoneid.put("NZ", new Integer(983));
            zoneid.put("NZ-CHAT", new Integer(984));
            zoneid.put("Chile/EasterIsland", new Integer(963));
            zoneid.put("US/Hawaii", new Integer(962));
            zoneid.put("Pacific/Johnston", new Integer(485));
            zoneid.put("Pacific/Kwajalein", new Integer(464));
            zoneid.put("Kwajalein", new Integer(976));
            zoneid.put("Pacific/Midway", new Integer(486));
            zoneid.put("Pacific/Samoa", new Integer(1502));
            zoneid.put("US/Samoa", new Integer(990));
            zoneid.put("Pacific/Yap", new Integer(465));
            zoneid.put("WET", new Integer(365));
        }
    }
}
