/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.entities.Continent;
import core.entities.NeutralPlayer;
import core.entities.State;
import core.entities.World;

public class WorldBuilder {
	/**
	 * Just a builder for the world
	 * 
	 * ABSTRACT: the most of the classes in the core.entities package are abstract and they contains no initialization methods.
	 * This is due to the fact that the entities are part of the core mechanisms of the game, while the initialization 
	 * that is performed is just one of the possibles and it doesn't affect the game after that the initialization is done.
	 * Moreover this type of init looks like too specific and not portable.
	 * For this reason the initialization is performed by inheriting the classes to initialize.
	 * If this initialization is well performed, then all the refs to the constans file should be here ( but width and height in word )
	 * and no more possibility to modifiy the internal state of the world should be possible a run time ( but the specied attributes which will be modified )
	 */
	
	/**
	 * Creates a world from the constatns file.
	 * @return a new instance of the World with the initialization performed and based on the constants.java file
	 */
	static public World Build(){
		// creates a world 
		return new World(){
			@Override
			// this overrides the init that is called by the constructor
			protected void init(){
				// list of the states to store
				ArrayList <State> states = new ArrayList<>();
				// list of the continents to store
				ArrayList <Continent> continents = new ArrayList<>();
				// list of list of states. each element of the list (one for each continent) is the list of states that are in that continent
				ArrayList <List<State>> continent_ownership = new ArrayList<>();
				
				// color to associate to the continents. 6 as speciefied in constatns file
				final Color[] colors = { Color.YELLOW, Color.BLUE, Color.GREEN, Color.PINK, Color.ORANGE, Color.CYAN };
				
				for ( int i=0; i<Constants.NUM_CONTINENTS; i++ )
					// for each continent a new continent is added the list after been instantiad and initialized
					continents.add( new Continent(i){
						@Override
						// init called by the constructor
						protected void init(int i){
							this.name = Constants.CONTINENT_NAMES[i];
							this.index = i;
							this.color = colors[i];
							this.value = Constants.CONTINENT_VALUES[i];
							// WORINNG: if states list is return by some getter, it will expose the internal rep.
							this.states = new ArrayList<State>();
							// must be temporanly memorized to add elements later
							continent_ownership.add( this.states );
						}
					} );
				
				// default neutral player 
				NeutralPlayer default_player = new NeutralPlayer(0,"Default");
				for ( int i=0; i<Constants.NUM_COUNTRIES; i++ )
					// for each country a new country is added the list after been instantiad and initialized
					states.add( new State( i ){
						@Override
						// call by the constructor
						protected void init(int i) {
							this.index = i;
							this.name = Constants.COUNTRY_NAMES[i];
							this.x = Constants.COUNTRY_COORD[i][0];
							this.y = Constants.COUNTRY_COORD[i][1];
							this.adjacent = Constants.ADJACENT[i];
							this.continent = continents.get( Constants.CONTINENT_IDS[i] );
							this.setOwner(default_player);
							// it adds itself to the continent list of states
							continent_ownership.get( Constants.CONTINENT_IDS[i] ).add( this );
						}
					});

				// since the lists are returned by getters, it is safer not to allow to modify them
				this.states = Collections.unmodifiableList( states );
				this.continents = Collections.unmodifiableList( continents );
			}
		};
	}
}
