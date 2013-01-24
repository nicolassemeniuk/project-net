package net.project.events.async;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EventConsumer implements MessageListener {
	
    private static final Log LOG = LogFactory.getLog(EventConsumer.class);

	private List<Message> messages = new ArrayList<Message>();

	private Object semaphore;

	private boolean verbose;

	/**
	 * Constructor.
	 */
	public EventConsumer() {
		this(new Object());
	}

	/**
	 * Constructor, initialized semaphore object.
	 * 
	 * @param semaphore
	 */
	public EventConsumer(Object semaphore) {
		this.semaphore = semaphore;
	}

	/**
	 * @return all the messages on the list so far, clearing the buffer
	 */
	public synchronized List<Message> flushMessages() {
		List<Message> answer = new ArrayList<Message>(messages);
		messages.clear();
		return answer;
	}

	/**
	 * Method implemented from MessageListener interface.
	 * 
	 * @param message
	 */
	public synchronized void onMessage(Message message) {
		messages.add(message);

		LOG.info("Received event object : " + message.toString());
		
		synchronized (semaphore) {
			semaphore.notifyAll();
		}
	}

	/**
	 * Use to wait for a single message to arrive.
	 */
	public void waitForMessageToArrive() {
		try {
			if (hasReceivedMessage()) {
				synchronized (semaphore) {
					semaphore.wait(4000);
				}
			}
		} catch (InterruptedException e) {
			LOG.info("Caught: " + e);
		}	
	}

	/**
	 * Used to wait for a message to arrive given a particular message count.
	 * 
	 * @param messageCount
	 */
	public void waitForMessagesToArrive(int messageCount) {		
		for (int i = 0; i < 10; i++) {
			try {
				if (hasReceivedMessages(messageCount)) {
					break;
				}
				synchronized (semaphore) {
					semaphore.wait(1000);
				}
			} catch (InterruptedException e) {
				LOG.info("Caught: " + e);
			}
		}		
	}

	public void assertMessagesArrived(int total) {
		waitForMessagesToArrive(total);
		synchronized (this) {
			int count = messages.size();

			LOG.info("Messages received -" + total + " : " + count);
		}
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Identifies if the message is empty.
	 * 
	 * @return
	 */
	protected boolean hasReceivedMessage() {
		return messages.isEmpty();
	}

	/**
	 * Identifies if the message count has reached the total size of message.
	 * 
	 * @param messageCount
	 * @return
	 */
	protected synchronized boolean hasReceivedMessages(int messageCount) {
		return messages.size() >= messageCount;
	}
}
