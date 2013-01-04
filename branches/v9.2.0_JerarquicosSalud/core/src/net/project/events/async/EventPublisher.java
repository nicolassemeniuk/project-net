package net.project.events.async;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import net.project.events.AbstractEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class EventPublisher {
	
    private static final Log LOG = LogFactory.getLog(EventPublisher.class);

	private JmsTemplate template;

	private Destination destination;

	private int messageCount = 10;

	private String texttMessage;

	private AbstractEvent applicationEvent;

	public void publish(AbstractEvent applicationEvent) throws JMSException {
		this.applicationEvent = applicationEvent;
		start();
	}

	public void start() throws JMSException {
		template.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage(applicationEvent);
				message.setStringProperty("next", ActivitySubscriber.getTopicId());
				return message;
			}
		});
	}

	public void stop() throws JMSException {
	}

	// Properties
	//-------------------------------------------------------------------------

	public JmsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * @return the texttMessage
	 */
	public String getTexttMessage() {
		return texttMessage;
	}

	/**
	 * @param texttMessage the texttMessage to set
	 */
	public void setTexttMessage(String texttMessage) {
		this.texttMessage = texttMessage;
	}

}
