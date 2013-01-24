package net.project.events.async;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import net.project.hibernate.service.ServiceFactory;
import net.project.events.ApplicationEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;

public class ActivitySubscriber extends EventConsumer implements MessageListener {
	
    private static final Log LOG = LogFactory.getLog(ActivitySubscriber.class);

	private JmsTemplate template;

	private static String topicId = "activityId".concat(""+System.currentTimeMillis());

	private Destination destination;

	private Connection connection;

	private Session session;

	private MessageConsumer consumer;

	/**
	 * Method to get connection from template connection factory 
	 * and creating the session, consumer objects
	 * @throws JMSException
	 */
	public void start() throws JMSException {
		String selector = "next = '" + topicId + "'";

		try {
			ConnectionFactory factory = template.getConnectionFactory();
			connection = factory.createConnection();

			// we might be a reusable connection in spring
			// so lets only set the client ID once if its not set
			synchronized (connection) {
				if (connection.getClientID() == null) {
					connection.setClientID(topicId);
				}
			}

			connection.start();

			session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
			consumer = session.createConsumer(destination, selector, false);
			consumer.setMessageListener(this);
		} catch (JMSException ex) {
			LOG.error("", ex);
			throw ex;
		}
	}

	/**
	 * Method to stop the connection, closing the consumer and session objects
	 * @throws JMSException
	 */
	public void stop() throws JMSException {
		if (consumer != null) {
			consumer.close();
		}
		if (session != null) {
			session.close();
		}
		if (connection != null) {
			connection.close();
		}
	}

	/* (non-Javadoc)
	 * @see net.project.events.async.EventConsumer#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message message) {
		super.onMessage(message);
		
		ApplicationEvent applicationEvent = null;
		try {
			// Save the message object to the Activity Log table
			applicationEvent = (ApplicationEvent) ((ObjectMessage) message).getObject();
			ServiceFactory.getInstance().getPnActivityLogService().saveApplicationEvent(applicationEvent);
		
			message.acknowledge();
		} catch (JMSException e) {
			LOG.error("Failed to acknowledge: " + e, e);
		}
	}

	// Properties
	// -------------------------------------------------------------------------
	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public JmsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * @return the topicId
	 */
	public static String getTopicId() {
		return topicId;
	}
}
