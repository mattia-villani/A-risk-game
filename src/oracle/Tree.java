package oracle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.events.Characters;


public class Tree {
	
	int count = 0;
	boolean finalNode = false;
	Character nodeLabel ;
	private Map<Character, Tree> childs = new HashMap<>();
	
	public Tree ( char head, String tail ){
		assert tail != null;
		nodeLabel = head;
		if ( ! tail.equals("") )
			add(tail);
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
	
	public void setAsFinal(){
		finalNode = true;
	}
	
	public void add( String string ){
		assert (string!=null && !string.equals("")) : "Why is string null or empty??";
		char head = string.charAt(0);
		String tail = string.substring(1);
		Tree tree = null;
		count ++;
		if ( childs.containsKey(head) ) tree = childs.get(head);
		else {
			tree = new Tree(head, tail);
			childs.put(head, tree);
		}
		if ( tail.equals("") ) tree.setAsFinal();
	}
	
	public Tree evalue( String string ){
		assert string != null : "string should be not null";
		assert !string.equals("") || finalNode : "string shouldn't be empty";
		Tree result = null;
		if ( string.equals("") && this.finalNode ) result = this;
		else{
			char head = string.charAt(0);
			String tail = string.substring(1);
			if ( childs.containsKey(head) == false ) { /* not into */ }
			else {
				Tree tree = childs.get(head);
				result = tree.evalue(tail);
			}
		}
		return result;
	}
	public boolean isFinal(){
		return finalNode;
	}
	public boolean isUniquePath(){
		return count == 0;
	}
	public Character getChar(){
		return nodeLabel;
	}
	
	public String getUniquePath(){
		if ( ! isUniquePath() ) throw new RuntimeException();
		char key = childs.keySet().iterator().next();
		Tree tree = childs.get(key);
		if ( tree.isFinal() ) return tree.getChar().toString();
		else return tree.getChar()+tree.getUniquePath();
	}
	
}

