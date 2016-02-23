package oracle;

public class Result{
	boolean contained; // e.g. "wor" is contained in {"hello", "world", "word"}
	boolean exactlyContained; // e.g. in the previus example "wor" is not exactlyContained but "world" is
	boolean unique; // e.g. in the previus example "wor" is unique, but "w" is not
	/** WORNING: tail has a value not null only if contained & unique
	 * if exactlyContained then tail is "" */
	String tail; // e.g. "ld" is the tail of "wor" in the previus example
		
	static public class EmptyResult extends Result{
		public EmptyResult(){
			super(false,false,false,null);
		}
	};
	
	
	public Result(boolean contained, boolean exactlyContained, boolean unique, String tail) {
		this.contained = contained;
		this.exactlyContained = exactlyContained;
		this.unique = unique;
		this.tail = tail;
	}
	@Override
	public boolean equals(Object o){
		if ( o==null || ! (o instanceof Result) ) return false;
		Result r = (Result) o ;
		return 
				r.contained==this.contained && 
				r.exactlyContained==this.exactlyContained &&
				r.unique==this.unique &&
				( (r.tail==null && this.tail==null) || 
						( r.tail==null && this.tail==null && r.tail.equals(this.tail) ));
	}
}