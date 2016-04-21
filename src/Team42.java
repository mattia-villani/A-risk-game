import java.util.Arrays;
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
			
	
	/*
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
		int id = 0;
		while ( properties.validCountryId.satisfies( id ) )
			list.add(id);
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
		List<Integer> list = getMyCountries();
		for ( int i : list )
			if ( board.getNumUnits(i) > 1 )
				set.addAll( getFoesAdjacentTo(i) );
		return set;
	}

	
	/*
	 * GIVING VALUE TO A COUNTRY
	 */
	static class Profile{
		//TODO: put here the coefficients for evaluating a country
		float coefficient1;
		float coefficient2;
		// ecc... 
	}
	//TODO: initialize the profiles with the right coefficients
	static final private Profile ATTACK_PROFILE = new Profile();
	static final private Profile REINFORCE_PROFILE = new Profile();

	/*
	 * 
	 */
	static class CountryValuePair implements Comparable<CountryValuePair>{
		int country;
		Profile profile;
		public CountryValuePair( int country, Profile profile ){
			this.country = country;
			this.profile = profile;
		}
		public int getCountry(){ return country; }
		public float getValue(){ return 1; }
		@Override
		public int compareTo(CountryValuePair arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public boolean equals(Object obj){
			CountryValuePair cvp = (CountryValuePair)obj;
			return this.country==cvp.country && this.profile==cvp.profile;
		}
		public boolean isUpperTheThreshold( float treshold ){
			return true;
		}
	}
	
	List<CountryValuePair> getSortedStrategyValueOfAttackableCountries(){
		return sortCountryValuePair( getStrategyValueOfAttackableCountries() );
	}	
	List<CountryValuePair> getStrategyValueOfAttackableCountries(){
		return getStrategyValueOf( getOnlyAttackableAdjacentFoes(), ATTACK_PROFILE );
	}
	List<CountryValuePair> getStrategyValueOf( Set<Integer> countryIds, Profile profile ){
		List<CountryValuePair> list = new LinkedList<>();
		for ( int id : countryIds )
			list.add( getStrategyValueOf( id, profile ) );
		return list;
	}
	CountryValuePair getStrategyValueOf( int countryId, Profile profile ){
		//TODO: use the profile coefficients to evaluate the value of the country with id countryID
		return new CountryValuePair( countryId, profile );
	}
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
		final float treshold = 0.7f;
		
		List<CountryValuePair> list = this.getSortedStrategyValueOfAttackableCountries();
		if ( ! list.isEmpty() && list.get(0).isUpperTheThreshold(treshold) ){
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
