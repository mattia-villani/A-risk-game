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
	
	private boolean finalNode;
	private Map<Character, Tree> childs;
	
	private Tree(){
		finalNode = true;
		childs = new HashMap<>();
	}
	public Tree( Iterator<Character> iterator ){
		this();
		add(iterator);
	}
	public Tree( String string ){
		this();
		add(string);
	}
	public Tree( Collection<String> input ){
		this();
		for ( String string : input )
			add( string );
	}
	
	public void add( String string ){
		List<Character> list = new LinkedList<>();
		for ( int i = 0 ; i<string.length(); i++ )
			list.add( string.charAt(i) );
		add( list.iterator() ) ;
	}
	
	public void add ( Iterator<Character> iterator ){
		if ( iterator.hasNext() == false ) finalNode = (childs.size()==0);
		else{
			finalNode = false;
			Character character = iterator.next();
			if ( childs.containsKey(character) ) childs.get(character).add(iterator);
			else childs.put(character, new Tree(iterator) );
		}
	}
	
	private String tailIfUnique(){
		String string = null ;
		if ( finalNode )
			string = "";
		else
			if ( childs.size()==1 ) {
				Character character = childs.keySet().iterator().next();
				String tail = childs.get(character).tailIfUnique();
				if ( tail != null )
					string = character + tail;
			}
		return string;
	}
	
	public Result evalue( Iterator<Character> iterator ){
		Result result = null;
		if ( iterator.hasNext() == false ){
			String tail = tailIfUnique();
			result = new Result(
					true, // if i am arrived so far, then the head is present
					this.finalNode, // if it is true, then, both the tree and the iterator finished together
					tail != null,
					tail
				);
		}else{
			Character character = iterator.next();
			if ( childs.containsKey(character) == false )
				result = new Result.EmptyResult();
			else 
				result = childs.get(character).evalue(iterator);
		}
		return result;
	}
}

