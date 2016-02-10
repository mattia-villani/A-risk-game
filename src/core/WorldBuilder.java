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
	 * Creates a world from the constatns file.
	 * @return
	 */
	static public World Build(){
		return new World(){
			@Override
			protected void init(){
				ArrayList <State> states = new ArrayList<>();
				ArrayList <Continent> continents = new ArrayList<>();
				ArrayList <List<State>> continent_ownership = new ArrayList<>();
				
				final Color[] colors = { Color.YELLOW, Color.BLUE, Color.GREEN, Color.PINK, Color.ORANGE, Color.CYAN };
				
				for ( int i=0; i<Constants.NUM_CONTINENTS; i++ )
					continents.add( new Continent(i){
						@Override
						protected void init(int i){
							this.name = Constants.CONTINENT_NAMES[i];
							this.index = i;
							this.color = colors[i];
							this.value = Constants.CONTINENT_VALUES[i];
							this.states = new ArrayList<State>();
							continent_ownership.add( this.states );
						}
					} );
				
				NeutralPlayer default_player = new NeutralPlayer("Default");
				for ( int i=0; i<Constants.NUM_COUNTRIES; i++ )
					states.add( new State( i ){
						@Override
						protected void init(int i) {
							this.index = i;
							this.name = Constants.COUNTRY_NAMES[i];
							this.x = Constants.COUNTRY_COORD[i][0];
							this.y = Constants.COUNTRY_COORD[i][1];
							this.adjacent = Constants.ADJACENT[i];
							this.continent = continents.get( Constants.CONTINENT_IDS[i] );
							this.setOwner(default_player);
							continent_ownership.get( Constants.CONTINENT_IDS[i] ).add( this );
						}
					});

				this.states = Collections.unmodifiableList( states );
				this.continents = Collections.unmodifiableList( continents );
			}
		};
	}
}
