package oracle;

import java.util.LinkedList;
import java.util.List;

public class Oracle {

	private Tree tree ;
	
	public Oracle ( Tree tree ){
		this.tree = tree;
	}
	
	public Result evalue( String string ){ 
		List<Character> list = new LinkedList<>();
		for (int i=0;i<string.length(); i++)
			list.add( string.charAt(i) );
		return tree.evalue( list.iterator() ); 
	}
	
}
