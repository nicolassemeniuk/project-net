package net.project.base;


/**
 * Indicates some Event failure.
 */
public class EventException extends PnetException {
	
	/**
     * Constructs an EventException
     */
	public EventException() {
		super();
	}

    /**
     * Constructs an empty EventException with the specified detail message.
     * @param message the message
     */
    public EventException(String message) {
        super(message);
    }
    
    /**
     * Constructs an empty EventException with the specified detail message and specified
     * causing Exception.
     * @param message detailed message
     * @param cause the cause of the exception
     */
    public EventException(String message, Throwable cause) {
        super(message, cause);
    }

}
