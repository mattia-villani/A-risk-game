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
		//TODO: give meaning to the following
		static float[] point_for_continent = { .5f , .5f , .5f , .5f , .5f , .5f };
		static float DIVISOR_FOR_SOURANDING_ENEMY_COUNT = 30f; // TODO ???

		
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
	}
	//TODO: initialize the profiles with the right coefficients
	static final private Profile ATTACK_PROFILE = new Profile( new float[9]/*{ insert here values.... }*/ );
	static final private Profile MOST_ADAPT_TO_ATTACK_PROFILE = new Profile( new float[9] );
	static final private Profile REINFORCE_PROFILE = new Profile( new float[9] );

	
	//TODO fill the following list;
	static int[] BORDER_STATES = {};
	static int MAX_ADJACENT_STATES ;
	static {
		int max = 0;
		for ( int[] adj : GameData.ADJACENT )
			max = Math.max( max , adj.length );
		MAX_ADJACENT_STATES = max;
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
			int totalNumberOfAdjs = GameData.ADJACENT[ country ].length ;
			
			float[] pointSystemValues = new float[9];
			
			// point_system_adjacent_states_owned_by_the_other_player  
			pointSystemValues[0] = ( (float)foeAdjacentStates.size() ) / (float) MAX_ADJACENT_STATES; // to bring it to the interval 0..1
			
			//TODO: fix the following value 
			//point_system_continent_value 
			pointSystemValues[1] = Profile.point_for_continent[ 0 ];
			
			//how close are we to owning the continent			
			// pointSystemValues[2] = TODO
			
			//point_system_is_border_country 
			pointSystemValues[3] = Arrays.binarySearch(BORDER_STATES, country) >= 0.f ? 1.f : 0.f ;
			
			// point_system_enemy_armies_around 
			pointSystemValues[4] = 0 ;
			for( int id : foeAdjacentStates ) pointSystemValues[4] += board.getNumUnits( id );
			pointSystemValues[4] /= Profile.DIVISOR_FOR_SOURANDING_ENEMY_COUNT;

			//how likely we are to win		
			// pointSystemValues[5] = TODO
			
			//if it connects two blocks		
			// pointSystemValues[6] = TODO
			
			// point_system_would_we_be_more_protected  
			pointSystemValues[7] = 
					Math.min( 1.f , ( (float)( totalNumberOfAdjs - foeAdjacentStates.size()) ) / (float)foeAdjacentStates.size() );
			
			//are they part of a opponent's continent?			
			// pointSystemValues[8] = TODO
			
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
		command = GameData.COUNTRY_NAMES[(int)(Math.random() * GameData.NUM_COUNTRIES)];
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
		// put your code here
		command = "skip";
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
						ATTACK_PROFILE) 
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
							MOST_ADAPT_TO_ATTACK_PROFILE
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
		String command = "";
		// put your code here
		command = "1";
		return(command);
	}

	public String getMoveIn (int attackCountryId) {
		String command = "";
		// put your code here
		command = "0";
		return(command);
	}

	public String getFortify () {
		String command = "";
		// put code here
		command = "skip";
		return(command);
	}

}
