/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 package net.project.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.project.base.compatibility.Compatibility;
import net.project.notification.email.IEmailAttachment;
import net.project.security.SessionManager;

import org.apache.log4j.Logger;


/**
 * The Email class provides methods to send emails to user(s) of the Project.net
 * application. It uses the <code>emailCharacterEncoding</code> application
 * setting to determine charset to specify in the email. <p> This class makes
 * use of two of the core components of the J2EE platform : <li> JavaMail
 * package. <li> Javabeans Activation Framwork. ( JAF ) <br> Here is an example
 * to use this class : <PRE><code> Email email = new Email(); ArrayList array =
 * new ArrayList(); array.add( "user1@company.com" ); email.setTo( array );
 *
 * array = new ArrayList(); array.add( "user2@company.com" ); email.setCC( array
 * );
 *
 * email.setFrom( "me@mycompany.com" ); email.setSubject( "Testing Email.java"
 * ); email.setMessageBody( "This is a test." );
 *
 * if( email.send() ) { // send success. } else { // send failed } </code></PRE>
 * </p>
 *
 * @author Raouf Rizk
 * @author Tim Morrow
 * @since 1.0
 */
public class Email {
    private static final Logger LOGGER = Logger.getLogger(Email.class);

    /** Indicates email send was successful. */
    private static final int EMAIL_OK = 1;

    private final List toList = new ArrayList();
    private final List ccList = new ArrayList();
    private String from = new String();
    private String subject = new String();
    private String message = new String();
    private String contentType = Notification.TEXT_PLAIN;
    /**
     * Attachments.
     */
    private final List attachments = new ArrayList();

    /**
     * Creates a new Email message which will be sent using the default SMTP
     * host. <p/> This constructor looks up all InetAddresses for the default
     * host and uses each one in turn for sending emails if sending fails for
     * some reason.
     */
    public Email() {
    }

    /**
     * Sets the email address of the sender of this email.
     *
     * @param from the email address of the sender
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Sets multiple recipients in the To line of this email.
     *
     * @param toList the collection of recipient email addresses to send to
     */
    public void setTo(List toList) {
        this.toList.addAll(toList);
    }

    /**
     * Sets a single recipient in the To line of this email.
     *
     * @param address the recipient email address
     */
    public void setTo(String address) {
        this.toList.add(address);
    }

    /**
     * Sets multiple recipients in the CC line of this email.
     *
     * @param ccList the collection of email addresses to cc
     */
    public void setCC(List ccList) {
        this.ccList.addAll(ccList);
    }

    /**
     * Sets the subject of this email.
     *
     * @param subject the subject of this email
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Sets the content of this email.
     *
     * @param message the email content
     */
    public void setMessageBody(String message) {
        this.message = message;
    }

    /**
     * Add an attachment to this email.
     *
     * @param attachment the attachment to add
     */
    public void attach(IEmailAttachment attachment) {
        this.attachments.add(attachment);
    }


    /**
     * Sends this email.
     *
     * @return {@link #EMAIL_OK} always
     * @throws EmailSendFailException if there is a critical error sending the
     * email; for example, the SMTP server is not available or one or more
     * addresses are local to the SMTP server but not valid
     * @throws InvalidEmailAddressException if errors caused there to be zero
     * "to" or "cc" recipients
     * @throws ValidEmailSendFailException if there is a problem sending the
     * email to one or more recipients; the email may have been sent to one or
     * more valid recipients
     * @throws EmailSendFailException if there is a problem sending the email
     * but there do not seem to be any invalid recipients
     * @throws EmailException if there is some other problem sending the email
     */
    public int send() throws ValidEmailSendFailException, EmailSendFailException, EmailException {
        EmailProgress progress = new EmailProgress();

        try {
            //Get an email session
            Session session = Compatibility.getMailSessionProvider().getSession();

            // create a message
            MimeMessage msg = new MimeMessage(session);

            // Add the addressing information
            addAddressing(msg, this.from, this.toList, this.ccList, progress);

            // setting the subject of the message with the correct character
            // encoding
            msg.setSubject(subject, SessionManager.getEmailCharacterEncoding());

            // create and fill the first message part
            // using the correct character encoding
            MimeBodyPart mbp1 = new MimeBodyPart();
            if(message != null ) { 
	            if ( contentType.equals(Notification.TEXT_PLAIN )) {
	                mbp1.setText(message, SessionManager.getEmailCharacterEncoding());
	            } else if ( contentType.equals(Notification.TEXT_HTML) ){
	            	mbp1.setContent(message, Notification.TEXT_HTML);
	            }
            }

            // create the Multipart and its parts to it
            Multipart mp = new MimeMultipart();

            // Add body
            mp.addBodyPart(mbp1);

            // Add all attachments
            addAttachments(mp);

            // add the Multipart to the message
            msg.setContent(mp);

            // set the Date: header
            msg.setSentDate(new Date());


            try {
                // send the message
                Transport.send(msg);


            } catch (SendFailedException sfe) {
                // Occurs when the email cannot be sent to one or more recipients
                // Since the only recipients in the message are those which
                // represented valid internet addresses, this is a serious error
                //
                // This error occurs if a the SMTP server rejects an email
                // address because it is in a local domain and the SMTP server
                // does not know about the user (550 User unknown)
                Logger.getLogger(Email.class).debug("Email.send threw a SendFailedException: " + sfe, sfe);

                // Check to see if there were any email addresses that the
                // SMTP server does not know about
                if (sfe.getInvalidAddresses() != null && sfe.getInvalidAddresses().length > 0) {
                    LOGGER.info("Could not send the email to one or more recipients." + sfe.getInvalidAddresses(), sfe);
                    throw new ValidEmailSendFailException("The email could not be sent.  Some valid email addresses were rejected by the mail server: " + sfe.getMessage(), sfe);

                } else {
                    LOGGER.info("Could not send the email.", sfe);
                    throw new EmailSendFailException("The email could not be sent: " + sfe.getMessage(), sfe);
                }

            } catch (MessagingException mex) {
                // Some other unhandled messaging error
                // Serious problem; we will retry to send
                LOGGER.warn("Unable to send email.", mex);
                throw new EmailException("The email could not be sent: " + mex, mex);
            }


            // At this point the email has been sent
            // Now we must determine if there were any recipient errors
            // that did not actually prevent the sending of the email

            if (progress.isError()) {

                StringBuffer errorMessage = new StringBuffer();

                // Start with basic message
                errorMessage.append("An email was sent, however not all recipients may receive the message.");

                // Add all error messages accumulated
                if (progress.getErrorMessages().size() > 0) {
                    errorMessage.append("  The following problems occurred:\n");

                    Iterator it = progress.getErrorMessages().iterator();
                    while (it.hasNext()) {
                        errorMessage.append((String) it.next());
                        errorMessage.append('\n');
                    }

                }

                throw new EmailException(errorMessage.toString());
            }

        } catch (MessagingException me) {
            // A serious problem occurred while constructing the message,
            // for example a character encoding conversion failed
            // No point in re-trying
            LOGGER.info("Unable to construct email message.", me);
            throw new EmailException("Send mail operation failed: " + me, me);

        }

        return EMAIL_OK;
    }


    //
    // Utility Methods
    //

    /**
     * Adds the specified addressing information to the message. If any of the
     * addresses cannot be successfully added (for example, their format is
     * invalid), the EmailProgress is updated and all error messages are added
     * to that.  No exceptions are thrown in this case.
     *
     * @param message the message to update
     * @param from the from address
     * @param to the collection of string addresses for the "To" line
     * @param cc the collection of string addresses for the "CC" line
     * @param progress the EmailProgress object to which to add any errors
     * @throws javax.mail.MessagingException if there is a problem setting any
     * of the addresses in the message; note that this is not the same as an
     * invalid InternetAddress
     * @throws InvalidEmailAddressException if after adding the addressing there
     * is not a single recipient in the "to" or "cc"
     */
    private static void addAddressing(javax.mail.Message message, String from, Collection to, Collection cc, EmailProgress progress)
        throws javax.mail.MessagingException, InvalidEmailAddressException {

        InternetAddress fromAddress = null;
        List toAddresses = new ArrayList();
        List ccAddresses = new ArrayList();

        // From address
        try {
            fromAddress = new InternetAddress(from);
        } catch (javax.mail.internet.AddressException ae) {
            // Error in the from address; mail cannot be sent
            progress.addErrorMessage("The from address '" + from + "' is invalid:  " + ae.getMessage());
        }

        // To Addresses
        try {
            makeAddressList(to, toAddresses);
        } catch (InvalidInternetAddressException iiae) {
            progress.addErrorMessages(iiae.getFormattedMessages());
        }

        // CC Addresses
        try {
            makeAddressList(cc, ccAddresses);
        } catch (InvalidInternetAddressException iiae) {
            progress.addErrorMessages(iiae.getFormattedMessages());
        }

        if (toAddresses.size() == 0 && ccAddresses.size() == 0) {
            throw new InvalidEmailAddressException("Unable to address email; Zero \"to\" or \"cc\" recipients");
        }

        message.setFrom(fromAddress);
        message.setRecipients(Message.RecipientType.TO, (InternetAddress[]) toAddresses.toArray(new InternetAddress[]{}));
        message.setRecipients(Message.RecipientType.CC, (InternetAddress[]) ccAddresses.toArray(new InternetAddress[]{}));

    }


    /**
     * Adds the string internet addresses to as real <code>InternetAddress</code>s.
     *
     * @param stringAddresses the string email addresses to read
     * @param internetAddresses the internet address list to update, where each
     * element will be of type <code>InternetAddress</code>
     * @throws InvalidInternetAddressException if one or more email addresses
     * were invalid; however, the internetAddresses collection will contain all
     * the valid ones.
     */
    private static void makeAddressList(Collection stringAddresses, Collection internetAddresses)
        throws InvalidInternetAddressException {

        boolean isInvalidAddress = false;
        HashMap invalidAddresses = new HashMap();
        String nextAddress;
        InternetAddress nextInternetAddress;

        if (stringAddresses != null) {

            Iterator it = stringAddresses.iterator();
            while (it.hasNext()) {
                nextAddress = (String) it.next();

                try {
                    // Convert from a string to an InternetAddress
                    // and add to internet address collection
                    nextInternetAddress = new InternetAddress(nextAddress);
                    internetAddresses.add(nextInternetAddress);

                } catch (javax.mail.internet.AddressException ae) {
                    // Email address was invalid
                    isInvalidAddress = true;
                    invalidAddresses.put(nextAddress, ae.getMessage());
                }

            }

        }

        if (isInvalidAddress) {
            throw new InvalidInternetAddressException("One or more internet addresses were invalid", invalidAddresses);
        }

    }


    /**
     * Adds all the attachments to the multipart.
     *
     * @param mp the MIME multipart to add attachments to
     * @throws MessagingException if there is a problem attaching the
     * attachment
     */
    private void addAttachments(Multipart mp) throws MessagingException {
        IEmailAttachment attachment;
        MimeBodyPart attachmentPart;

        Iterator it = this.attachments.iterator();
        while (it.hasNext()) {
            attachment = (IEmailAttachment) it.next();

            attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(attachment.getDataSource()));
            attachmentPart.setFileName(attachment.getName());
            mp.addBodyPart(attachmentPart);
        }
    }


    //
    // Utility Classes
    //


    /**
     * An InvalidInternetAddressException is thrown when one or more
     * InternetAddresses are invalid.
     */
    private static class InvalidInternetAddressException extends net.project.base.PnetException {

        /**
         * The invalid addresses and messages. Each key is a string (the invalid
         * string addresses).  Each value is a string (the error message found
         * when converting the string address to an internet address).
         */
        private HashMap invalidAddresses = null;

        /**
         * Creates a new InvalidInternetAddressException with the specified
         * message and map of invalid addresses.
         */
        private InvalidInternetAddressException(String message, HashMap invalidAddresses) {
            super(message);
            this.invalidAddresses = invalidAddresses;
        }

        /**
         * Returns a collection of formatted error messages, one for each of the
         * invalid addresses in this exception.
         *
         * @return the collection of formatted error messages; each element is a
         *         <code>String</code>.
         */
        private Collection getFormattedMessages() {
            List messages = new ArrayList();
            String nextAddress;

            Iterator it = this.invalidAddresses.keySet().iterator();
            while (it.hasNext()) {
                nextAddress = (String) it.next();

                // Add a message about this invalid address
                messages.add("The email address '" + nextAddress + "' is invalid: " + this.invalidAddresses.get(nextAddress));

            }

            return messages;
        }

    }

    /**
     * Indicates whether the specified internet address is valid. A valid
     * address complies with RFC822
     *
     * @param internetAddressText the internet address
     * @return true if the address is valid; false otherwise
     * @see javax.mail.internet.InternetAddress
     */
    public static boolean isValidInternetAddress(String internetAddressText) {
        boolean isValid = true;

        try {
            new InternetAddress(internetAddressText);

        } catch (javax.mail.internet.AddressException ae) {
            // Email address was invalid & eat up the exception set flag to false
            isValid = false;

        } finally {
            return isValid;
        }
    }

    /**
     * Formats an email address to include a name. For example: <code>Joe
     * Somebody &lt;joe@somebody.com&gt;</code>
     *
     * @param name the name to add; if null or empty, then no name is added; the
     * address is simply returned
     * @param address the email address
     * @return the formatted email address
     */
    public static String formatEmailAddress(String name, String address) {

        StringBuffer result = new StringBuffer();

        if (name != null && name.trim().length() > 0) {
            result.append(name).append(" <").append(address).append(">");

        } else {
            result.append(address);

        }

        return result.toString();
    }

    /**
     * Tracks Email Send progress.
     */
    private static class EmailProgress {

        private final List errorMessages = new ArrayList();

        private Collection getErrorMessages() {
            return this.errorMessages;
        }

        private void addErrorMessage(String message) {
            this.errorMessages.add(message);
        }

        private void addErrorMessages(Collection messages) {
            this.errorMessages.addAll(messages);
        }

        /**
         * Indicates whether there is at least one error.
         *
         * @return true if there is at least one error; false otherwise
         */
        private boolean isError() {
            return (errorMessages.size() != 0);
        }

    }

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
    
    
}
