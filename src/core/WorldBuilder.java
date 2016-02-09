package core;

import java.util.ArrayList;
import java.util.Collections;

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
				
				for ( int i=0; i<Constants.NUM_COUNTRIES; i++ )
					states.add( new State( i ){
						@Override
						protected void init(int i) {
							this.index = i;
							this.name = Constants.COUNTRY_NAMES[i];
							
						}
					});
				
				this.states = Collections.unmodifiableList( states );
			}
		};
	}
}
