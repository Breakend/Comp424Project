package s260457751.mytools;

public class CustomTimer {
	
	long start;
	long stop;
	long expireTime;
	private static CustomTimer instance;
	
	/**
	 * Singleton
	 * @return
	 */
	public static CustomTimer getInstance(){
		if(instance == null) instance = new CustomTimer();
		
		return instance;	
	}
	
	/**
	 * To make it a singleton
	 */
	private CustomTimer(){
		
	}
	
	/**
	 * Reset the time
	 * @param expireTime
	 */
	public void set(long expireTime){
		this.expireTime = expireTime;
	}
	
	public void start(){
		this.start = System.currentTimeMillis();
		this.stop = this.start + expireTime;
	}
	
	public boolean expired(){
		return System.currentTimeMillis() > this.stop;
	}
	
}
