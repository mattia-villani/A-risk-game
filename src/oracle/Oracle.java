package oracle;

import java.util.LinkedList;
import java.util.List;

public class Oracle {
	/** DEPRECADED */
	
	private Tree tree ;
	
	public Oracle ( Tree tree ){
		this.tree = tree;
	}
	
	public Tree evalue( String string ){ 
		return tree.evalue( string ); 
	}
	
}
