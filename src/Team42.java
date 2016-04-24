import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

// put your code here

public class Team42 implements Bot {
	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects
	// So you can use player.getNumUnits() but you can't use player.addUnits(10000), for example

	private BoardAPI board;
	private PlayerAPI player;

	private int numOfAttacksDoneInThisTurn = 0;

	/*
	 * READING BOARDS
	 */

	interface Property { boolean satisfies( int id ); }
	class Properties {
		final public Property validCountryId = new Property(){
			@Override
			public boolean satisfies(int id) {return id>=0 && id<GameData.NUM_COUNTRIES;}			
		};
		final public Property myCountry = new Property(){
			@Override
			public boolean satisfies(int id) {
				return validCountryId.satisfies(id) && player.getId() == board.getOccupier(id) ;}	
		};
		final public Property foeCountry = new Property(){
			@Override
			public boolean satisfies(int id) {
				return validCountryId.satisfies(id) && ! myCountry.satisfies(id); }	
		};
		final public Property countryAbleToAttack = new Property(){
			@Override
			public boolean satisfies(int id) {
				return validCountryId.satisfies(id) && board.getNumUnits(id)>1; }	
		};
		final public Property myCountryAbleToAttack = new Property(){
			@Override
			public boolean satisfies(int id) {
				return myCountry.satisfies(id) && countryAbleToAttack.satisfies(id); }	
		};
	}
	Properties properties = new Properties();


	/**
	 * MODIFIER!!!!! list is modified and returned ( the returned list is the same of the list param )
	 */
	<T extends Iterable<Integer>> T filter ( T list, Property property ){
		Iterator<Integer> it = list.iterator();
		while ( it.hasNext() )
			if ( ! property.satisfies( it.next() ) )
				it.remove();
		return list;
	}
	int count ( int[] values, Property property ){
		int c = 0;
		for ( int v : values ) if ( property.satisfies(v) ) ++c;
		return c;
	}
	interface Op<RET_T, PARAM_T>{ RET_T op ( RET_T a, PARAM_T b ); } // TxT -> T 
	class SumArmies implements Op<Integer,Integer>{
		@Override public Integer op ( Integer sum, Integer countryId ){ return sum + board.getNumUnits(countryId); }
	}
	/**
	 * @return op( ... op( op( op( val, values[0] ), values[1] ) ... , values[ values.length-1] ); 
	 */
	<RET_T, PARAM_T> RET_T foldl ( Iterable<PARAM_T> values, RET_T val, Op<RET_T,PARAM_T> operation ){
		for ( PARAM_T t : values ) val = operation.op( val , t );
		return val;
	}
	List<Integer> createListFromArray( int[] values ){
		LinkedList <Integer> list = new LinkedList<Integer>();
		for ( int v : values ) list.add(v);
		return list;
	}

	List<Integer> getAllCountries(){
		List<Integer> list = new LinkedList<>();
		for (int i=0; i<GameData.NUM_COUNTRIES; i++)
			list.add(i);
		return list;
	}
	List<Integer> getAdjacent( int countryId ){
		List<Integer> list = new LinkedList<>();
		for ( int id : GameData.ADJACENT[countryId] )
			list.add(id);
		return list;
	}

	/* QUERIES */
	List<Integer> getMyCountries()		
	{ return this.filter( this.getAllCountries(), properties.myCountry ); }
	List<Integer> getFoesAdjacentTo( int countryId )
	{ return this.filter( this.getAdjacent(countryId), properties.foeCountry ); }

	Set<Integer> getAllAdjacentFoes(){
		Set<Integer> set = new HashSet<>();
		List<Integer> list = getMyCountries();
		for ( int i : list )
			set.addAll( getFoesAdjacentTo(i) );
		return set;
	}
	Set<Integer> getOnlyAttackableAdjacentFoes(){
		Set<Integer> set = new HashSet<>();
		List<Integer> list = this.filter( this.getAllCountries(), properties.myCountryAbleToAttack );
		for ( int i : list )
			set.addAll( getFoesAdjacentTo(i) );
		return set;
	}


	/*
	 * GIVING VALUE TO A COUNTRY
	 */
	static class Profile{
		static float[][] WINNING_PROPABILITY = new float[][]{ // rows: attackers-1; colums: defender-1
			// values taken from http://boardgames.stackexchange.com/questions/3514/how-can-i-estimate-my-chances-to-win-a-risk-battle
			//DEFENDERS: 0		1      2      3      4      5      6      7      8      9     10
			new float[]{ 0f, .000f, .000f, .000f, .000f, .002f, .000f, .000f, .000f, .000f, .000f }, // 0 attacker 
			new float[]{ 1f, .417f, .106f, .027f, .007f, .002f, .000f, .000f, .000f, .000f, .000f }, // 1 attacker 
			new float[]{ 1f, .754f, .363f, .206f, .091f, .049f, .021f, .011f, .005f, .003f, .001f }, // 2 attacker 
			new float[]{ 1f, .916f, .656f, .470f, .315f, .206f, .134f, .084f, .054f, .033f, .021f }, // 3 attacker 
			new float[]{ 1f, .972f, .785f, .642f, .477f, .359f, .253f, .181f, .123f, .086f, .057f }, // 4 attacker 
			new float[]{ 1f, .990f, .890f, .769f, .638f, .506f, .397f, .297f, .224f, .162f, .118f }, // 5 attacker 
			new float[]{ 1f, .997f, .934f, .857f, .754f, .638f, .521f, .423f, .329f, .258f, .193f }, // 6 attacker 
			new float[]{ 1f, .999f, .967f, .910f, .834f, .736f, .640f, .536f, .446f, .357f, .287f }, // 7 attacker 
			new float[]{ 1f,  1.0f, .980f, .947f, .888f, .818f, .730f, .643f, .547f, .464f, .380f }, // 8 attacker 
			new float[]{ 1f,  1.0f, .990f, .967f, .930f, .873f, .808f, .726f, .646f, .558f, .480f }, // 9 attacker 
			new float[]{ 1f,  1.0f, .994f, .981f, .954f, .916f, .861f, .800f, .724f, .650f, .568f } // 10 attacker 
		};

		//TODO: give meaning to the following
		static float[] point_for_continent = { .5f , .5f , .5f , .5f , .5f , .5f };
		static float DIVISOR_FOR_SOURANDING_ENEMY_COUNT = 15f; // TODO 
		static float PENALIZATION_FOR_EXTRA_COUNTRY = 0.7f; // TODO 


		float[] coefficients;
		public Profile ( float[]coeffs ){this.coefficients = coeffs;}
		float eval( float[] points ){
			if ( coefficients.length != points.length ) throw new RuntimeException("Check code, the coeffs don't corrisponds to the values");
			float result = 0;
			for ( int i=0; i<points.length; i++ )
				result+= points[i] * this.coefficients[i];
			return result;
		}
		@Override public String toString(){
			StringBuilder st = new StringBuilder();
			st.append("{ ");
			for ( int i=0; i<this.coefficients.length; i++ ){
				if ( i != 0 ) st.append(", "); 
				st.append(this.coefficients[i]);
				st.append(" ");
			}
			st.append("}");
			return st.toString();
		}
		static public float coefficientForJointOwnedArmies( int numOfCountries ){
			return 1.f + PENALIZATION_FOR_EXTRA_COUNTRY*(float)(numOfCountries-1);
		}
		static public float howLikelyWeAreToWin( int attacking, int defending ){
			if ( attacking < 0 || defending < 0 ) throw new RuntimeException("Something wired happend");
			if ( attacking <= 10 && defending <= 10 )
				return Profile.WINNING_PROPABILITY[attacking][defending];
			// consideration in documentation
			if ( attacking == defending ) return attacking < 20 ? 0.5062f : 0.63343f;
			if ( attacking == defending -1 ) return 0.50650f;
			if ( attacking == defending -2 ) return 0.50393f;
			// unknown cases... try to reduce to a known situation
			while ( attacking > 10 || defending > 10 ){
				attacking *= 0.8f;
				defending *= 0.8f;
			}
			return Profile.WINNING_PROPABILITY[attacking][defending];			
		}
	}
	//TODO: initialize the profiles with the right coefficients
	private Profile attackProfile = new Profile( new float[9]/*{ insert here values.... }*/ );
	private Profile mostAdaptToAttackProfile = new Profile( new float[9] );
	private Profile reinforceProfile = new Profile( new float[9] );
	protected Team42(BoardAPI inBoard, PlayerAPI inPlayer, Profile attack_profile, Profile most_adapt_to_attack_profile, Profile reinforce_profile ){
		this(inBoard, inPlayer);
		this.attackProfile = attack_profile;
		this.mostAdaptToAttackProfile = most_adapt_to_attack_profile;
		this.reinforceProfile = reinforce_profile;
	}


	//TODO fill the following list;
	static int[] BORDER_STATES = {};
	static int MAX_ADJACENT_STATES ;
	static int[] COUNTRY_CONTINENT;
	static {
		Arrays.sort( BORDER_STATES );
		int max = 0;
		for ( int[] adj : GameData.ADJACENT )
			max = Math.max( max , adj.length );
		MAX_ADJACENT_STATES = max;

		COUNTRY_CONTINENT = new int[GameData.NUM_COUNTRIES];
		for ( int continentId = 0 ; continentId<GameData.NUM_CONTINENTS; continentId++ )
			for ( int countryId : GameData.CONTINENT_COUNTRIES[continentId] )
				COUNTRY_CONTINENT[ countryId ] = continentId;
	}

	/*
	 *  END OF PROFILING , COUNTRY VALUE PAIR TIME 
	 */
	class CountryValuePair implements Comparable<CountryValuePair>{
		int country;
		Profile profile;
		float value;
		public CountryValuePair( int country, Profile profile ){
			//TODO: use the profile coefficients to evaluate the value of the country with id countryID

			List<Integer> foeAdjacentStates = Team42.this.getFoesAdjacentTo( country );
			List<Integer> myJointCountries = Team42.this.filter( Team42.this.getAdjacent( country ), properties.myCountry );
			int totalNumberOfAdjs = GameData.ADJACENT[ country ].length;
			SumArmies sumArmies = new SumArmies();
			int armiesInThisCountry = foldl( foeAdjacentStates, 0, sumArmies );
			int myArmiesInTheJointStates = foldl( myJointCountries, 0, sumArmies );
			int continentId = COUNTRY_CONTINENT[country];
			int occupierId = board.getOccupier(country);


			float[] pointSystemValues = new float[9];

			// point_system_adjacent_states_owned_by_the_other_player  
			pointSystemValues[0] = ( (float)foeAdjacentStates.size() ) / (float) MAX_ADJACENT_STATES; // to bring it to the interval 0..1

			//TODO: fix the following value 
			//point_system_continent_value 
			pointSystemValues[1] = Profile.point_for_continent[ 0 ];

			//how close are we to owning the continent			
			pointSystemValues[2] = (float) count( GameData.CONTINENT_COUNTRIES[continentId] , 
					new Property(){ 
				@Override public boolean satisfies(int id) {
					return player.getId() == board.getOccupier(id);
				}
			}) / (float)GameData.CONTINENT_COUNTRIES[continentId].length;

			//point_system_is_border_country 
			pointSystemValues[3] = Arrays.binarySearch(BORDER_STATES, country) >= 0.f ? 1.f : 0.f ;

			// point_system_enemy_armies_around 
			pointSystemValues[4] = armiesInThisCountry / Profile.DIVISOR_FOR_SOURANDING_ENEMY_COUNT ;

			//how likely we are to win		
			pointSystemValues[5] = Profile.howLikelyWeAreToWin( 
					(int)( ( myArmiesInTheJointStates - myJointCountries.size() ) 
							/ Profile.coefficientForJointOwnedArmies( myJointCountries.size() ) ), // attacking
					armiesInThisCountry ); // defending

			//if it connects two blocks		
			// pointSystemValues[6] = TODO

			// point_system_would_we_be_more_protected  
			pointSystemValues[7] = 
					Math.min( 1.f , ( (float)( totalNumberOfAdjs - foeAdjacentStates.size()) ) / (float)foeAdjacentStates.size() );

			//are they part of a opponent's continent?			
			pointSystemValues[8] = 
					(float) count( GameData.CONTINENT_COUNTRIES[continentId] , 
							new Property(){ 
						@Override public boolean satisfies(int id) {
							return occupierId == board.getOccupier(id);
						}
					}) / (float)GameData.CONTINENT_COUNTRIES[continentId].length;

			this.country = country;
			this.profile = profile;
			this.value = this.profile.eval( pointSystemValues );
		}
		public int getCountry(){ return country; }
		public float getValue(){ return value; }
		public boolean isUpperTheThreshold( float treshold ){return value >= treshold;}
		@Override public int compareTo(CountryValuePair arg0) {return Float.compare( value , arg0.getValue() );}
		@Override public boolean equals(Object obj){
			CountryValuePair cvp = (CountryValuePair)obj;
			return this.country==cvp.country && this.profile==cvp.profile;
		}
	}

	List<CountryValuePair> getStrategyValueOf( Collection<Integer> countryIds, Profile profile ){
		List<CountryValuePair> list = new LinkedList<>();
		for ( int id : countryIds )
			list.add( new CountryValuePair( id, profile ) );
		return list;
	}

	/**
	 * MODIFIER!!!!
	 */
	List<CountryValuePair> sortCountryValuePair( List<CountryValuePair> list ){
		Collections.sort( list );
		return list;
	}

	/*
	 * PUBLIC API 
	 */

	Team42 (BoardAPI inBoard, PlayerAPI inPlayer) {
		board = inBoard;	
		player = inPlayer;
		// put your code here
		return;
	}

	public String getName () {
		String command = "";
		// put your code here
		command = "BotTeam42";
		return(command);
	}

	public String getReinforcement () {
		numOfAttacksDoneInThisTurn = 0; // this is needed by the getBattle in order to make at least one attack
		String command = "";
		// put your code here
		List<CountryValuePair> list = 
				this.sortCountryValuePair(
						this.getStrategyValueOf(getOnlyAttackableAdjacentFoes(), 
								reinforceProfile) 
						);

		int countryToAttackId  = list.get(0).country;


		command = GameData.COUNTRY_NAMES[(int)countryToAttackId];
		command = command.replaceAll("\\s", "");
		command += " 1";
		return(command);
	}

	public String getPlacement (int forPlayer) {
		String command = "";
		// put your code here
		command = GameData.COUNTRY_NAMES[(int)(Math.random() * GameData.NUM_COUNTRIES)];
		command = command.replaceAll("\\s", "");
		return(command);
	}

	public String getCardExchange () {
		String command = "";
		int [] cardTypes = new int [4]; //too small, creates positions [0], [1] and [2]. should be new int [4];

		if (player.getCards().size() < 3) command = "skip";

		else{
			for (int i = 0; i < player.getCards().size(); ++i){

				cardTypes[player.getCards().get(i).getInsigniaId()]++; 
			}
			if (cardTypes[0] == 3) command = "iii";
			else if (cardTypes[1] == 3) command = "ccc";
			else if (cardTypes[2] == 3) command = "aaa";
			else if (cardTypes[0] >= 1 && cardTypes [1] >= 1 && cardTypes [2] >= 1 ) command = "ica";

			else if (cardTypes[0] >= 2 && cardTypes[3] >= 1) command = "iiw";
			else if (cardTypes[1] >= 2 && cardTypes[3] >= 1) command = "ccw";
			else if (cardTypes[2] >= 2 && cardTypes[3] >= 1) command = "aaw";

			else if(cardTypes[3] >= 1){
				if (cardTypes[0] >= 1 && cardTypes[1] >= 1 || (cardTypes[0] >= 1 && cardTypes[2] >= 1) || (cardTypes[2] >= 1 && cardTypes[1] >= 1)){

					command = "w";
					if (cardTypes[0] >= 1 && cardTypes[1] >= 1){
						command += "ic";
					}
					else if (cardTypes[0] >= 1 && cardTypes[2] >= 1){
						command += "ia";
					}
					else if (cardTypes[1] >=1 && cardTypes[2] >=1)
						command += "ca";
				}
			}
			else if (cardTypes[3] >= 2){
				command = "ww";
				if (cardTypes[0] >= 1) command += "i";
				else if (cardTypes[1] >= 1) command += "c";
				else if (cardTypes[2] >= 1) command += "a";	

			}
			else{
				command = "skip";
			}

		}
		return(command);
	}

	public String getBattle () {
		String command = "skip";

		// TODO: fix this value as good as possible

		int minNumOfAttacks = 1, maxNumOfAttacks = 5;
		final float tresholdToAttack = 0.7f;
		final float tresholdToBeSelectedToAttack = 0.7f;

		List<CountryValuePair> list = 
				this.sortCountryValuePair(
						this.getStrategyValueOf(getOnlyAttackableAdjacentFoes(), 
								attackProfile) 
						);
		if ( ! list.isEmpty() && this.numOfAttacksDoneInThisTurn < maxNumOfAttacks
				&& ( list.get(0).isUpperTheThreshold(tresholdToAttack) || this.numOfAttacksDoneInThisTurn < minNumOfAttacks ) ){

			int countryToAttackId  = list.get(0).country;

			List<CountryValuePair> valuesOfAttackers = 
					this.sortCountryValuePair(
							this.getStrategyValueOf( 
									this.filter( 	
											this.getAdjacent( countryToAttackId ), 
											properties.myCountryAbleToAttack 
											),
									mostAdaptToAttackProfile
									)
							);
			// the list can't be empty since list was filtered on the attackable country, then there is a country able to attacke this.
			if ( valuesOfAttackers.get(0).isUpperTheThreshold(tresholdToBeSelectedToAttack) || this.numOfAttacksDoneInThisTurn < minNumOfAttacks  ){
				command = 
						GameData.CONTINENT_NAMES[countryToAttackId] +
						" " +
						GameData.CONTINENT_NAMES[valuesOfAttackers.get(0).country] +
						" " +
						( Math.min(3, board.getNumUnits(valuesOfAttackers.get(0).country)-1) );
				numOfAttacksDoneInThisTurn++;
			}
		}		
		return(command);
	}

	public String getDefence (int countryId) {
		String command = "2";
		// we have to return 1 when we don't have enough armies
		if ( board.getNumUnits(countryId) == 1 ) command = "1";
		return(command);
	}

	public String getMoveIn (int attackCountryId) {
		String command = "";
		/*
		 * look at if both states had one, what their values would be, then give each state ratio of points
		 */
		command = "0";
		return(command);
	}

	public String getFortify () {
		String command = "";
		// put code here
		command = "skip";
		return(command);
	}

	public float howClosetoOwningContinent(int countryId){
		int contId = GameData.CONTINENT_IDS[countryId];
		int owned = 0;
		int unowned = 0;
		for (int i = 0; i < GameData.CONTINENT_IDS.length; ++i){
			if (contId == GameData.CONTINENT_IDS[i]){
				owned++;
			}
			else unowned++;
		}
		return owned/(unowned + owned);
	}
	public boolean willMakeCluster(int countryId){
		ArrayList<Integer> ownedAdj = new ArrayList<Integer>();
		for (int i = 0; i < GameData.NUM_COUNTRIES; ++i){
			if (countryId != i){
				if (board.isAdjacent(countryId, i)){
					if (board.getOccupier(i) == player.getId()) ownedAdj.add(i);
				}

			}
		}
		if (ownedAdj.size() < 2) return false;
		else{
			for (int i = 0; i < ownedAdj.size(); ++i){
				for (int j = 0; j < ownedAdj.size(); ++j){
					if (i != j){
						if (board.isAdjacent(ownedAdj.get(i), ownedAdj.get(j))){} //micro optimization
						else if (!board.isConnected(ownedAdj.get(i), ownedAdj.get(j))){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}

