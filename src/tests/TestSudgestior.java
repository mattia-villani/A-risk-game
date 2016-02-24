package tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import oracle.Oracle;
import oracle.Result;
import oracle.Tree;

public class TestSudgestior {

	public static void main(String[] args) {
		
		String[] dictionary = new String[]{
			"abc", "abd", "abef", "abeg", "daf", "asd", "dsas"	
		};
		
		Set<String> words = new HashSet<>();
		for ( String s : dictionary )
			words.add(s);
		
		Tree tree = new Tree( words );
		
		Oracle oracle = new Oracle( tree );
		
		Result [][] results = new Result[][]{
			new Result[]{ oracle.evalue("abc"), new Result(true,true,true,"")  },
			new Result[]{ oracle.evalue("ab"),  new Result(true,false,false,"")  },
			new Result[]{ oracle.evalue("ERR"), new Result(false,false,false,null)  },
			new Result[]{ oracle.evalue("da"), new Result(true,false,true,"f")  },
			new Result[]{ oracle.evalue("d"), new Result(true,false,true,"sas")  }			
		};
		
		int i=0;
		for ( Result[] pair : results )
			if ( pair[1].equals(pair[0]) ) System.out.println("test "+(i++)+" ok");
			else System.out.println("test "+(i++)+" fail");

	}

}
