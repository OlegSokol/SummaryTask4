package ua.nure.soklakov.SummaryTask4.web.utils;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import ua.nure.soklakov.SummaryTask4.core.user.User;

/**
 * Mail util class designed to send email for user with information.
 * 
 * @author Oleg Soklakov
 *
 */
public class MailUtils {
	private static final Logger LOG = Logger.getLogger(MailUtils.class);
	private static final Session SESSION = init();
	private static final String theme = "Registration";
	
	private static Session init() {
		Session session = null;
		try {
			Context initialContext = new InitialContext();
			session =  (Session) initialContext.lookup("java:comp/env/mail/Session");
		} catch (Exception ex) {
			LOG.error("mail session lookup error", ex);
		}
		return session;
	}

	/**
	 * Send email with information to user.
	 * 
	 * @param user
	 *            recipient.
	 * @param email
	 *            email address.
	 */
	public static void sendConfirmationEmail(User user, String email) {
		try {
			Message msg = new MimeMessage(SESSION);
			msg.setFrom(new InternetAddress("hospitalepam@gmail.com"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

			setContentToConfirmationEmailEng(msg, user);

			msg.setSentDate(new Date());

			Transport.send(msg);
		} catch (AddressException e) {
			LOG.error(e);
		} catch (MessagingException e) {
			LOG.error(e);
		} catch (UnsupportedEncodingException e) {
			LOG.error(e);
		}
	}

	/**
	 * Set content to confirmation email.
	 * 
	 * @param msg
	 *            massage.
	 * @param user
	 *            recipient.
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	private static void setContentToConfirmationEmailEng(Message msg, User user)
			throws MessagingException, UnsupportedEncodingException {
		msg.setSubject(theme);

		Multipart multipart = new MimeMultipart();

		InternetHeaders emailAndPass = new InternetHeaders();
		emailAndPass.addHeader("Content-type", "text/plain; charset=UTF-8");
		String hello = "Hello, " + user.getFirstName() + " " + user.getLastName() + " !\n"
				+ " You successfuly registered on our web-site!\n\n\n";

		String data = "\nLogin: " + user.getLogin() + "\nPassword: " + user.getPassword() + "\n\n";

		MimeBodyPart greetingAndData = new MimeBodyPart(emailAndPass, (hello + data).getBytes("UTF-8"));

		multipart.addBodyPart(greetingAndData);

		msg.setContent(multipart);
	}

}
