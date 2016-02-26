package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Animator {
		
	static public abstract class Handler implements Runnable{
		int delay ;
		int code;
		public Handler ( int delay, int code ){ this.delay = delay; }
		public Handler ( int delay ){ this(delay,0); }
		public boolean checkIfExpired(int afterTime){
			delay -= afterTime;
			return delay <= 0;
		}
	};
	
	static final private boolean verbose = true;
	static private int timeToWait = 30;
	static ArrayList<Handler> list = new ArrayList<Handler>();
	static private boolean init = false;
	static private Thread thread ;
	
	static void add( Handler handler ){
		synchronized ( list ){
			list.add(handler);
		}
	}
	
	public Animator() {
		if ( init ) return;
		init = true;
		thread = new Thread(new Runnable(){

			@Override
			public void run() {
				if ( verbose ) System.out.println("Animator started");
				List<Integer> toIgnore = new ArrayList<Integer>();
				while ( true ){
					toIgnore.clear();
					synchronized ( list ){
						Iterator<Handler> iterator = list.iterator();
						while ( iterator.hasNext() ){
							Handler current = iterator.next();
							if ( toIgnore.contains(current.code) ) iterator.remove();
							else if ( current.checkIfExpired(timeToWait) ){
								if ( current.code!=0 ) toIgnore.add(current.code);
								current.run();
								iterator.remove();
							}
						}
					}
					
					try {
						Thread.sleep(timeToWait);
					} catch (InterruptedException e) {e.printStackTrace();}
				}	
			}
			
		});
		thread.start();
	}
	
}
