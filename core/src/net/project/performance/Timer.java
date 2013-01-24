package net.project.performance;

public class Timer {
	
	private long t;
        
    public Timer() {
    	reset();
    }
    
    /**
     * Reset counter
     *
     */
    public void reset() {
    	t = System.currentTimeMillis();
    }
    
    /**
     * calculate time
     * @return
     */
    private long elapsed() {
    	return System.currentTimeMillis() - t;
    }
    
    /**
     *  Return time 
     * @return
     */
    public long getTime() {
    	return elapsed();
    }
}
