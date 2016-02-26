package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Animator {
		
	static public abstract class Handler implements Runnable{
		int delay ;
		int code ;
		int duration;
		Runnable postHandler;
		public Handler ( int delay, int duration, int code, Runnable postHandler ){ 
			this.delay = delay;
			this.code = code;
			this.duration = duration;
			this.postHandler = postHandler;
		}
		public Handler ( int delay, int duration, Runnable postHandler ){ this(delay, duration,0,postHandler); }
		public Handler ( int delay, int duration, int code ){ this(delay,duration,code,null); }
		public Handler ( int delay ){ this(delay,0,0,null); }
		public boolean checkIfExpired(int afterTime){
			duration -= afterTime;
			return delay <= 0 && duration<0;
		}
		public boolean delayExpired(int afterTime){
			delay -= afterTime;
			return delay <= 0;
		}
	};
	
	static public abstract class FromZeroToOneIntervalHandler extends Handler{
		private boolean prePerformed = false;
		private int originalDuration;
		abstract public void run(float point);
		abstract public void post();
		abstract public void pre();
		public FromZeroToOneIntervalHandler(int delay, int duration){
			super(delay, duration, null);
			assert ( duration != 0 ) : "For a timed animation you need a duration ";
			originalDuration = duration;
			this.postHandler = new Runnable(){
				@Override 
				public void run(){ post(); }
			};
		}
		@Override
		public void run(){
			if ( prePerformed == false ){
				prePerformed = true;
				pre();
			}
			float timing = ((float)(originalDuration-duration))/(float)originalDuration;
			// fix in range
			timing = (timing < 0) ? 0 : ( (timing>1? 1 : timing ));
			run(timing);
		}
	}
		
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
				// since a post handler may add an animation in a list that is sync it is better to post pone the adding to avoid waits
				List<Runnable> postHandlers = new ArrayList<>();
				while ( true ){
					toIgnore.clear();
					postHandlers.clear();
					synchronized ( list ){
						Iterator<Handler> iterator = list.iterator();
						while ( iterator.hasNext() ){
							Handler current = iterator.next();
							if ( toIgnore.contains(current.code) ) iterator.remove();
							else {
								if ( current.code!=0 ) toIgnore.add(current.code);
								if ( current.delayExpired( timeToWait ) )current.run();
								if ( current.checkIfExpired(timeToWait) ){
									if ( current.postHandler != null ) postHandlers.add(current.postHandler);
									 iterator.remove();
								}
							}
						}
						iterator = null;
						list.removeAll(toIgnore);
					}
					for ( Runnable runnable : postHandlers )
						runnable.run();
					try {
						Thread.sleep(timeToWait);
					} catch (InterruptedException e) {e.printStackTrace();}
				}	
			}
			
		});
		thread.start();
	}
	
}
