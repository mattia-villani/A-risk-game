/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import oracle.Oracle;
import oracle.Result;
import oracle.Tree;

public class TestSudgestior {
	
	public static void yes(int i){
		System.out.println("test "+(i++)+" ok");
	}
	
	public static void no(int i){
		System.out.println("test "+(i++)+" fail");
	}

	public static void main(String[] args) {
		
		String[] dictionary = new String[]{
			"abc", "abd", "hello world", "abef", "abeg", "daf", "asd", "dsas", "hello"	
		};
		
		Set<String> words = new HashSet<>();
		for ( String s : dictionary )
			words.add(s);
		
		Tree root = new Tree( words );

		System.out.println( "debug: "+root );
		
		Tree tree;
		
		Tree oracle = root;
				
		try{
			tree = oracle.evalue("abc");
			if ( tree!=null && "".equals(tree.getUniquePath()) ) yes(1);
			else no(1);
		}catch(Exception e){ no(1); e.printStackTrace(); };

		try{
			tree = oracle.evalue("ab");
			tree.getUniquePath();
			no(2);
		}catch(Tree.NotUniqueException e){ yes(2); }
		catch(Exception e){ no(2); e.printStackTrace();};

		try{
			tree = oracle.evalue("errore");
			if ( tree == null ) yes(3);
			else no(3);
		}catch(Exception e){ no(3); e.printStackTrace();};

		try{
			tree = oracle.evalue("hello ");
			String result = tree.getUniquePath();
			if ( result.equals("world") ) yes(4);
			else no(4);
		}catch(Exception e){ no(4); e.printStackTrace();};

		try{
			tree = oracle.evalue("hello");
			if( ! tree.isFinal() ) no(5);
			else tree.getUniquePath();
			no(5);
		}catch(Tree.NotUniqueException e){ yes(5); }
		catch(Exception e){ no(5); e.printStackTrace();};


	}

}
