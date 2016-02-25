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
			"abc", "abd", "abef", "abeg", "daf", "asd", "dsas"	
		};
		
		Set<String> words = new HashSet<>();
		for ( String s : dictionary )
			words.add(s);
		
		Tree tree;
		
		Oracle oracle = new Oracle( new Tree( words ) );
				
		try{
			tree = oracle.evalue("abc");
			if ( tree!=null && tree.isUniquePath() && "".equals(tree.getUniquePath()) ) yes(1);
			else no(1);
		}catch(Exception e){ no(1); };

		try{
			tree = oracle.evalue("ab");
			if ( tree!=null && !tree.isUniquePath() ) yes(2);
			else no(2);
		}catch(Exception e){ no(2); };

		try{
			tree = oracle.evalue("errore");
			if ( tree==null ) yes(3);
			else no(3);
		}catch(Exception e){ no(3); };


	}

}
