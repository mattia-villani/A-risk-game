/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core;

public class Constants {
	public static final int NUM_PLAYERS = 2;
	public static final int NUM_NEUTRALS = 4;
	public static final int NUM_PLAYERS_PLUS_NEUTRALS = NUM_PLAYERS + NUM_NEUTRALS;
	public static final int NUM_COUNTRIES = 42;
	public static final int INIT_COUNTRIES_PLAYER = 9;
	public static final int INIT_COUNTRIES_NEUTRAL = 6;
	public static final int INIT_UNITS_PLAYER = 36;
	public static final int INIT_UNITS_NEUTRAL = 24;
	public static final int TURNS_OF_REINFORCEMENTS = 18;
	public static final int NUM_TOTAL_PLAYERS = NUM_PLAYERS + NUM_NEUTRALS;
	
	public static final String[] COUNTRY_NAMES = {
		"Ontario","Quebec","NW Territory","Alberta","Greenland","E United States","W United States","Central America","Alaska",
		"Great Britain","W Europe","S Europe","Ukraine","N Europe","Iceland","Scandinavia",
		"Afghanistan","India","Middle East","Japan","Ural","Yakutsk","Kamchatka","Siam","Irkutsk","Siberia","Mongolia","China",
		"E Australia","New Guinea","W Australia","Indonesia",
		"Venezuela","Peru","Brazil","Argentina",
		"Congo","N Africa","S Africa","Egypt","E Africa","Madagascar"};  // for reference
	
	public static final String[] CARD_TYPES = {
		"CAVALRY",		// 00	Ontario
		"ARTILLERY",	// 01	Quebec
		"ARTILLERY",	// 02	NW Territory
		"INFANTRY",		// 03	Alberta
		"CAVALRY",		// 04	Greenland
		"ARTILLERY",	// 05	E United States
		"INFANTRY",		// 06	W United States
		"CAVALRY",		// 07	Central America
		"INFANTRY",		// 08	Alaska
		"CAVALRY",		// 09	Great Britain
		"INFANTRY",		// 10	W Europe
		"CAVALRY",		// 11	S Europe
		"ARTILLERY",	// 12	Ukraine
		"CAVALRY",		// 13	N Europe
		"INFANTRY",		// 14	Iceland
		"ARTILLERY",	// 15	Scandinavia
		"INFANTRY",		// 16	Afghanistan
		"INFANTRY",		// 17	India
		"ARTILLERY",	// 18	Middle East
		"INFANTRY",		// 19	Japan
		"CAVALRY",		// 20	Ural
		"CAVALRY",		// 21	Yakutsk
		"CAVALRY",		// 22	Kamchatka
		"ARTILLERY",	// 23	Siam
		"INFANTRY",		// 24	Irkutsk
		"ARTILLERY",	// 25	Siberia
		"ARTILLERY",	// 26	Mongolia
		"CAVALRY",		// 27	China
		"INFANTRY",		// 28	E Australia
		"CAVALRY",		// 29	New Guinea
		"ARTILLERY",	// 30	W Australia
		"CAVALRY",		// 31	Indonesia
		"ARTILLERY",	// 32	Venezuela
		"CAVALRY",		// 33	Peru
		"ARTILLERY",	// 34	Brazil
		"INFANTRY",		// 35	Argentina
		"CAVALRY",		// 36	Congo
		"INFANTRY",		// 37	N Africa
		"ARTILLERY",	// 38	S Africa
		"INFANTRY",		// 39	Egypt
		"ARTILLERY",	// 40	E Africa
		"INFANTRY"		// 41	Madagascar		
	};
	
	public static final int[][] ADJACENT = { 
		{4,1,5,6,3,2},			// 00	Ontario
		{4,5,0},				// 01	Quebec
		{4,0,3,8},				// 02	NW Territory
		{2,0,6,8},				// 03	Alberta
		{14,1,0,2},				// 04	Greenland
		{0,1,7,6}, 				// 05	E United States
		{3,0,5,7},				// 06	W United States
		{6,5,32},				// 07	Central America
		{2,3,22},				// 08	Alaska
		{14,15,13,10},			// 09	Great Britain
		{9,13,11,37},			// 10	W Europe
		{13,12,18,39,10},		// 11	S Europe
		{20,16,18,11,13,15},	// 12	Ukraine
		{15,12,11,10,9},		// 13	N Europe
		{15,9,4},				// 14	Iceland
		{12,13,14,9},			// 15	Scandinavia
		{20,27,17,18,12},		// 16	Afghanistan
		{16,27,23,18},			// 17	India
		{12,16,17,40,39,11},	// 18	Middle East
		{26,22},				// 19	Japan
		{25,27,16,12},			// 20	Ural
		{22,24,25},				// 21	Yakutsk
		{8,19,26,24,21},		// 22	Kamchatka
		{27,31,17},				// 23	Siam
		{21,22,26,25},			// 24	Irkutsk
		{21,24,26,27,20},		// 25	Siberia
		{24,22,19,27,25},		// 26	Mongolia
		{26,23,17,16,20,25},	// 27	China
		{29,30},				// 28	E Australia
		{28,30,31},				// 29	New Guinea
		{29,28,31},				// 30	W Australia
		{23,29,30},				// 31	Indonesia
		{7,34,33},				// 32	Venezuela
		{32,34,35},				// 33	Peru
		{32,37,35,33},			// 34	Brazil
		{33,34},				// 35	Argentina
		{37,40,38},				// 36	Congo
		{10,11,39,40,36,34},	// 37	N Africa
		{36,40,41},				// 38	S Africa
		{11,18,40,37},			// 39	Egypt
		{39,18,41,38,36,37},	// 40	E Africa
		{38,40}					// 41	Madagascar
	};
	public static final int NUM_CONTINENTS = 6;
	public static final String[] CONTINENT_NAMES = {"N America","Europe","Asia","Australia","S America","Africa"};  // for reference 
	public static final int[] CONTINENT_IDS = {0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,3,3,3,3,4,4,4,4,5,5,5,5,5,5};
	public static final int[] CONTINENT_VALUES = {5,5,7,2,2,3};
	public static final int FRAME_WIDTH = 1000;    // must be even
	public static final int FRAME_HEIGHT = 600;
	public static final int[][] COUNTRY_COORD = {
		{191,150},	// 00	Ontario
		{255,150},	// 01	Quebec
		{146,85},	// 02	NW Territory
		{123,144},	// 03	Alberta
		{314,56},	// 04	Greenland
		{210,230},	// 05	E United States
		{135,210},	// 06	W United States
		{140,299},	// 07	Central America
		{45,85},	// 08	Alaska
		{370,199},	// 09	Great Britain
		{398,280},	// 10	W Europe
		{465,260},	// 11	S Europe
		{547,165},	// 12	Ukraine
		{460,200},	// 13	N Europe
		{393,127},	// 14	Iceland
		{463,122},	// 15	Scandinavia
		{628,218},	// 16	Afghanistan
		{672,312},	// 17	India
		{572,338},	// 18	Middle East
		{861,213},	// 19	Japan
		{645,147},	// 20	Ural
		{763,65},	// 21	Yakutsk
		{840,75},	// 22	Kamchatka
		{751,336},	// 23	Siam
		{750,140},	// 24	Irkutsk
		{695,100},	// 25	Siberia
		{760,206},	// 26	Mongolia
		{735,267},	// 27	China
		{866,502},	// 28	E Australia
		{850,429},	// 29	New Guinea
		{813,526},	// 30	W Australia
		{771,454},	// 31	Indonesia
		{213,352},	// 32	Venezuela
		{221,426},	// 33	Peru
		{289,415},	// 34	Brazil
		{233,510},	// 35	Argentina
		{496,453},	// 36	Congo
		{435,388},	// 37	N Africa
		{510,530},	// 38	S Africa
		{499,350},	// 39	Egypt
		{547,417},	// 40	E Africa
		{586,545}	// 41	Madagascar
	};	
}
