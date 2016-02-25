package oracle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



public class Tree {
	static final private boolean verboseAdd = false;
	static final private boolean verboseEvalue = false;
	
	@SuppressWarnings("serial")
	public class NotUniqueException extends RuntimeException{};
	@SuppressWarnings("serial")
	public class EmptyException extends RuntimeException{};
	
	public class EmptyTree extends Tree{
		@Override
		public String toString(){
			return "EmpetyTree";
		}
		@Override
		public String getUniquePath() throws NotUniqueException, EmptyException{
			return "";
		}
	}
	
	private Map<Character, Tree> childs = new HashMap<>();
	
	private Tree(){}
	
	public Tree ( String tail ){
		assert tail != null && !tail.equals("") ;
		add(tail);
	}
	
	public Tree( String[] input ){
		for (String string : input)
			if ( string != null )
				add(string);
	}
	
	public Tree( Collection<String> input ){
		boolean insertedAtLeastOne = false;
		for ( String string : input )
			if ( string != null ){
				add( string );
				insertedAtLeastOne = true;
			}
		assert insertedAtLeastOne : "The collection was invalid or empty";
	}
	
	public void add( String string ){
		
		if ( verboseAdd ) System.out.println("tree.add("+string+").pre "+this);

		assert (string!=null && !string.equals("")) : "Why is string null or empty??";
		char head = string.charAt(0);
		String tail = string.substring(1);
		Tree tree = null;
		if ( childs.containsKey(head) ){ 
			if ( ! tail.equals("") ) {
				if ( childs.get(head) == null ) childs.put(head, new Tree() );
				childs.get(head).add( tail );
			}
		} else {
			tree = tail.equals("") ? null : new Tree(tail);
			childs.put(head, tree);
		}
		
		if ( verboseAdd ) System.out.println("tree.add("+string+").post "+this);
	}
	
	public Tree evalue( String string ){
		if ( verboseEvalue ) System.out.println("tree.evalue("+string+").pre "+this);

		assert string != null : "string should be not null";

		Tree result = null;
		
		if ( string.equals("") ) result = this;
		else{
			char head = string.charAt(0);
			String tail = string.substring(1);
			if ( childs.containsKey(head) == false ) result = null;
			else {
				Tree tree = childs.get(head);
				result = (tree!=null) ? tree.evalue(tail) : new EmptyTree() ;
			}
		}

		if ( verboseEvalue ) System.out.println("tree.evalue("+string+").post:result="+result+"; "+this);
		return result;
	}
	
	public String getUniquePath() throws NotUniqueException, EmptyException{
		int childsNum = childs.size();
		
		if ( childsNum == 0 ) throw new EmptyException();
		if ( childsNum != 1 ) throw new NotUniqueException();
		
		char key = childs.keySet().iterator().next();
		Tree tree = childs.get(key);
		
		if ( tree == null ) return key+"";
		else return key + tree.getUniquePath();
	}
	
	
	@Override 
	public String toString(){
		String result = "tree{ ";
		for ( char key : childs.keySet() )
			result += key+":"+childs.get(key)+" ";
		result+="}";
		return result;
	}
	
}

