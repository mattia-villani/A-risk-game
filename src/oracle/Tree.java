/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package oracle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Tree {
	static final private boolean verboseAdd = false;
	static final private boolean verboseEvalue = false;
	
	private boolean finalNode = false;
	
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
	
	public String getInstruction(){ return ""; }
	
	private Tree(){}
	
	public Tree ( String tail ){
		assert tail != null ;
		if ( tail.equals("") ) finalNode=true;
		else add(tail);
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
		if ( childs.containsKey(head) ){ 
			if ( childs.get(head) == null ) childs.put(head, new Tree() );
			if ( ! tail.equals("") ) {
				childs.get(head).add( tail );
			}else childs.get(head).setFinal();
		} else 
			childs.put(head, new Tree(tail));
		
		if ( verboseAdd ) System.out.println("tree.add("+string+").post "+this);
	}
	
	public void setFinal(){ finalNode = true; }
	public boolean isFinal(){ return finalNode; }
	
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
		
		if ( childsNum == 0 ) return "";
		if ( childsNum != 1 || finalNode ) throw new NotUniqueException();
		
		char key = childs.keySet().iterator().next();
		Tree tree = childs.get(key);
		
		if ( tree == null ) return key+"";
		else return key + tree.getUniquePath();
	}
	
	
	@Override 
	public String toString(){
		String result = finalNode ? "finalTree{ " : "tree{ ";
		for ( char key : childs.keySet() )
			result += key+":"+childs.get(key)+" ";
		result+="}";
		return result;
	}
	
}

