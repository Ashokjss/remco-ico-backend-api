package com.remco.ico.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.remco.ico.DTO.StatusResponseDTO;
import com.remco.ico.model.Config;
import com.remco.ico.repository.ConfigInfoRepository;
import com.remco.ico.utils.EncryptDecrypt;

@SuppressWarnings("serial")
@Service
public class EmailNotificationService extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationService.class);
	
	@SuppressWarnings("unused")
	@Autowired
	private JavaMailSender javaMailService;
	@Autowired
	private Environment envi;
	
	@Value("${email.host}")
	private String host;

	@Value("${email.port}")
	private Integer port;

	@Value("${email.username}")
	private String username;

	@Value("${email.password}")
	private String password;

	@Value("${spring.mail.transport.protocol}")
	private String transportProtocol;

	@Value("${env}")
	private String env;
	@Value("${email.path}")
	private String emailPath;
	// Supply your SMTP credentials below. Note that your SMTP credentials are
	// different from your AWS credentials.
	@Value("${smtp.username}")
	private String SMTP_USERNAME;

	@Value("${smtp.password}")
	private String SMTP_PASSWORD;

	@Value("${spring.test.mail.host}")
	private String althost;

	@Value("${spring.test.mail.port}")
	private Integer altport;

	@Value("${spring.test.mail.username}")
	private String ALT_SMTP_USERNAME;

	@Value("${spring.test.mail.password}")
	private String ALT_SMTP_PASSWORD;

	@Autowired
	private ConfigInfoRepository configInfoRepository;

	public boolean sendEmail(String toEamilId, String subject, String content, String userName, String password) {

		LOG.info("Before sending email  ENV : " + env);
		LOG.info("host  : " + host);
		LOG.info("port  : " + port);
		LOG.info("username  : " + username);
		LOG.info("transportProtocol  : " + transportProtocol);
		// DEV Env
		if (env.equalsIgnoreCase("dev")) {
			return sendEmailDEV(toEamilId, subject, content, userName, password);
		} else {
			return sendEmailDEV(toEamilId, subject, content, userName, password);
		}

	}

	private boolean sendEmailDEV(String toEamilId, String subject, String content, String userName,
			String userPassword) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);
			StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
			statusResponseDTO.setMessage(envi.getProperty("verify.mailId"));

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);
			// Session session = Session.getDefaultInstance(props);
			MimeMessage msg = new MimeMessage(session);

			// creates a new e-mail message
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEamilId));
			msg.setSubject(subject);
			MimeMultipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();

			String encryptEmail = EncryptDecrypt.encrypt(toEamilId);

			System.out.println("Encrtypyed MAil------>" + encryptEmail);

			Map<String, String> input = new HashMap<String, String>();
			input.put("tushar.goyal@mansudsales.com", toEamilId);
			input.put("newuser", userName);
			input.put("123456", userPassword);
			input.put("5KW2X105F1NE1BA1Y5X24HS4L3C27DEC", content);
			input.put("activateLink", statusResponseDTO.getMessage() + toEamilId);
			input.put("actMessage", ("Click here to activate your account"));

			String htmlText = readEmailFromHtml(emailPath, input);
			messageBodyPart.setContent(htmlText, "text/html");
			multipart.addBodyPart(messageBodyPart);
			msg.setContent(multipart);
			Transport transport = session.getTransport();
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();

			System.out.println("Mail sent successfully...");

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unused")
	private boolean sendEmailPROD(String toEamilId, String subject, String content) {
		Transport transport = null;
		try {
			LOG.info("In  sendEmail PROD  START: " + env);

			// Create a Properties object to contain connection configuration
			// information.
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", transportProtocol);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");

			// Create a Session object to represent a mail session with the
			// specified properties.
			Session session = Session.getDefaultInstance(props);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			// Use to send an single 'TO' Recipient
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEamilId));
			msg.setSubject(subject);
			msg.setContent(content, "text/html");

			/*
			 * // Add multiple 'TO' Recipient in the Email msg = addMultipleMailId(msg,
			 * cc_SendAddress, Message.RecipientType.TO);
			 * 
			 * // Add multiple 'CC' Recipient in the Email msg = addMultipleMailId(msg,
			 * cc_SendAddress, Message.RecipientType.CC);
			 */

			// Create a transport
			transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			LOG.info("In  sendEmail PROD  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmail PROD  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmail PROD  END: " + env);
			e.printStackTrace();
			return false;
		}

	}

	// Method to replace the values for keys
	protected String readEmailFromHtml(String filePath, Map<String, String> input) {
		String msg = readContentFromFile(filePath);
		try {
			Set<Entry<String, String>> entries = input.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				msg = msg.replace(entry.getKey().trim(), entry.getValue().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	private String readContentFromFile(String fileName) {
		StringBuffer contents = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
					// System.out.println("hello");
				}
			} finally {
				reader.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return contents.toString();
	}

	public boolean sendEmailforgot(String toEamilId, String subject, String content, String userName) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String encryptUser = EncryptDecrypt.encrypt(toEamilId);

			String message = "<b>Dear " + userName + ",</b><br><br>";
			message += "<b>Please, Click Below Link To Reset Your Password.</b><br>";
			message += "<a href=https://remittancetoken.io/ResetPassword.aspx?Reference=" + encryptUser
					+ "> Click here to reset your password. </a><br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail
			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			LOG.info("Attempting to send an email");

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailScocial(String toEmailId, String subject, String content) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);
			System.out.println("toEmailId" + toEmailId);
			System.out.println("subject " + subject);
			System.out.println("content " + content);
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEmailId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEmailId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setText(content);
			// set plain text message
			msg.setContent(content, "text/html");
			// sends the e-mail
			LOG.info("Attempting to send an email");

			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();
			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailTokenTransfer(String toEmailId, String subject, String content) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);
			System.out.println("toEmailId" + toEmailId);
			System.out.println("subject " + subject);
			System.out.println("content " + content);
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEmailId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEmailId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setText(content);
			// set plain text message
			msg.setContent(content, "text/html");
			// sends the e-mail
			LOG.info("Attempting to send an email");

			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			LOG.info("Email Recipients !" + msg.getAllRecipients());
			transport.close();

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailforKYC(String toEamilId, String subject, String kycStatus, String name, String reason) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String message = "<b>Dear " + name + ",</b><br><br>";
			if (kycStatus.equals("1")) {
				message += "<b>Your KYC Details has been verified and Approved Successfully!</b><br>";
				message += "<br>";
				message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
				message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
				message += "<br><br>";
				message += "<b>Thank you,</b><br>";
				message += "<b>Remco team.</b>";
			} else if (kycStatus.equals("-1")) {
				message += "<b>Your KYC Details has been Rejected!</b><br>";
				message += "<b>Reason for Rejection: " + reason + "</b><br>";
				message += "<br>";
				message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
				message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
				message += "<br><br>";
				message += "<b>Thank you,</b><br>";
				message += "<b>Remco team.</b>";

			}
			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unused")
	public boolean sendEmailforgotReset(String toEamilId, String subject, String content, String firstName) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String encryptUser = EncryptDecrypt.encrypt(toEamilId);

			String message = "<b>Dear " + firstName + ",</b><br><br>";
			message += "<b>Your Remco Ether Wallet Password is given below,</b><br>";
			message += "Wallet Password : <b>" + content + "</b><br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			LOG.info("Attempting to send an email");

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unused")
	public boolean sendEmailforOTP(String toEamilId, String subject, String content, String userName,
			String ipAddress) {
		try {

			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);
			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String encryptUser = EncryptDecrypt.encrypt(toEamilId);
			Config config = configInfoRepository.findConfigByConfigKey("keyvalidity");

			String message = "<b>Dear " + userName + ",</b><br><br>";
			message += "Your one-time PIN is below. Please do not share this PIN with others, as it is confidential.<br>";
			message += "This PIN will expire in " + config.getConfigValue() + " seconds. <br>";
			message += "<b>PIN : " + content + "</b><br>";
			message += "<b>Logged In IP : " + ipAddress + "</b><br>";
			message += "<a href=https://remittancetoken.io/SignIn.aspx?EmailAddress=" + toEamilId + "&OTP=" + content
					+ ">Click here to login to your account</a><br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			// https://
			// www.remittancetoken.io/SignIn.aspx?EmailAddress=it@celexsa.com&OTP=458984
			// message += "<a
			// href=http://remittancetoken.io/ResetPassword.aspx?Reference="+encryptUser+">
			// Click here to reset your password. </a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail
			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			LOG.info("Attempting to send an email");

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailforRegistration(String toEamilId, String subject, String name, String userName,
			String walletAddress) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String encryptUser = EncryptDecrypt.encrypt(toEamilId);

			String message = "<b>Dear " + name + ",</b><br><br>";

			message += "<b>You are Registered successfully.Please Find Your Details Below:</b><br>";
			message += "<b>User Name :  " + userName + "<br>";
			message += "<b>Email Id :  " + toEamilId + "<br>";
			message += "<b>Wallet Address :  " + walletAddress + "<br><br>";
			message += "<br>We hope that you will have a wonderful experience using our website. Any suggestions or feedback is more than welcome, we are always happy to hear from you in order to improve our services.<br>";
			message += "<a href=" + envi.getProperty("verify.mailId") + encryptUser
					+ ">Click here to activate your account</a><br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();
			LOG.info("Attempting to send an email");

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean resendEmailforRegistration(String toEamilId, String subject, String name) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String encryptUser = EncryptDecrypt.encrypt(toEamilId);

			String message = "<b>Dear " + name + ",</b><br><br>";
			message += "<a href=" + envi.getProperty("verify.mailId") + encryptUser
					+ ">Click here to activate your account</a><br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unused")
	public boolean sendEmailforReferral(String toEamilId, String subject, String refId, String userName,
			String RefUserName) {
		try {

			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);
			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String encryptUser = EncryptDecrypt.encrypt(toEamilId);

			String message = "<b>Dear " + RefUserName + ",</b><br><br>";
			message += "<b>You Have Been Referred By " + userName + " to buy Remco Token.</b><br>";
			message += "<br>";
			message += "<h><b>Few facts about REMCO:</h></b><br>";
			message += "<br>";
			message += "- $700m processed by core tech in 24 months !<br>";
			message += "- Parent company licensed in many jurisdictions !<br>";
			message += "- Over 3.6m users by core tech. Ready to go !<br>";
			message += "- 10 times faster and more affordable!<br>";
			message += "- Core tech process payments for Western Union  and many others<br>";
			message += "<br>";
			message += "<b>Clearly a winner ! Take advantange of the low price and discount!<br></b>";
			message += "<br>";
			message += "<h><b>Key Stats</b></h><br>";
			message += "<li>Name/Token Symbol: REMCO</li>";
			message += "<li>Platform: ERC20 crowdsale with post ICO swap to private chain</li>";
			message += "<li>Industry: Money Transfer</li>";
			message += "<li>Price: $0.08</li>";
			message += "<li>Soft Cap: $6 million (Met. Project will go ahead)</li>";
			message += "<li>Hard Cap: $60 million</li>";
			message += "<li>Token Supply: 2B</li>";
			message += "<li>Start Private sale Date: September 15, 2018</li>";
			message += "<li>End Private sale Date: October 15, 2018</li>";
			message += "<li>Start Presale Date: November 01, 2018</li>";
			message += "<li>End Presale Date: November 30, 2018</li>";
			message += "<li>Start Public Sale Date: December 01, 2018</li>";
			message += "<li>End Public Sale Date: March 31, 2019</li>";
			message += "<li>KYC/AML: Yes</li>";
			message += "<li>Website Url: <a href=https://remittancetoken.io>https://remittancetoken.io</a></li>";
			message += "<li>White Paper Url: <a href=https://remittancetoken.io/WhitePaper.aspx>https://remittancetoken.io/WhitePaper.aspx</a></li>";
			message += "<br>";
			message += "<a href=" + envi.getProperty("referral.link") + refId + "><br>Click here to register.</a><br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail
			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			LOG.info("Attempting to send an email");

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unused")
	public boolean sendEmailforMailError(String toEamilId, String subject, String content) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", althost);
			properties.put("mail.smtp.port", altport);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", althost);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(ALT_SMTP_USERNAME, ALT_SMTP_PASSWORD);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(ALT_SMTP_USERNAME));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String encryptUser = EncryptDecrypt.encrypt(toEamilId);

			String message = "<b>Dear Admin,</b><br><br>";
			message += "<b>Problem in sending Email to users. Please verify your Username and Password.</b><br>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail
			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(althost, ALT_SMTP_USERNAME, ALT_SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			LOG.info("Attempting to send an email");

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unused")
	public boolean sendEmailforOTPforTransfer(String toEamilId, String subject, String content, String userName) {
		try {

			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);
			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String encryptUser = EncryptDecrypt.encrypt(toEamilId);
			Config config = configInfoRepository.findConfigByConfigKey("keyvalidity");

			String message = "<b>Dear " + userName + ",</b><br><br>";
			message += "Your one-time PIN for Token Transfer is given below. Please do not share this PIN with others, as it is confidential.<br>";
			message += "This PIN will expire in " + config.getConfigValue() + " seconds. <br>";
			message += "<b>PIN : " + content + "</b><br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail
			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			LOG.info("Attempting to send an email");

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailforReferralRegister(String toEamilId, String subject, String userName,
			String registeredUserEmail) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String message = "<b>Dear " + userName + ",</b><br><br>";
			message += "<b>The User " + registeredUserEmail
					+ " you referred has been Registered with REMCO Succesfully!</b><br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailforPurchaseRequest(String toEamilId, String subject, String name, Double tokenCount,
			String paymentMode) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String message = "<b>Dear " + name + ",</b><br><br>";

			message += "Your REMCO Token Purchase Request has been sent successfully. The next step is for REMCO to transfer the tokens to your wallet at the end of the ICO.<br>";
			message += "Please look for a separate email after the ICO is completed.<br>";
			message += "<b>No.Of REMCO Tokens Requested : " + tokenCount + "<br>";
			message += "<b>Payment Mode : " + paymentMode + "<br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			LOG.info("Attempting to send an email");

			Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailforPurchaseRequestFailure(String toEamilId, String subject, String name, Double tokenCount,
			String paymentMode) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String message = "<b>Dear " + name + ",</b><br><br>";

			message += "Your REMCO Token Purchase Request has been Failed. Please get support from our support team.<br>";
			message += "<b>No.Of REMCO Tokens Requested : " + tokenCount + "<br>";
			message += "<b>Payment Mode : " + paymentMode + "<br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			LOG.info("Attempting to send an email");

			Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailforPurchaseTransfer(String toEamilId, String subject, String name, Double tokenCount,
			String paymentMode) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String message = "<b>Dear " + name + ",</b><br><br>";

			message += "Tokens you have purchased from REMCO has been transferred to your wallet successfully.<br>";
			message += "<b>No.Of REMCO Tokens Requested : " + tokenCount + "<br>";
			message += "<b>Payment Mode : " + paymentMode + "<br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			LOG.info("Attempting to send an email");

			Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailforAirdropRegistration(String toEamilId, String subject, String name, String password,
			String walletAddress) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String encryptUser = EncryptDecrypt.encrypt(toEamilId);

			String message = "<b>Dear " + name + ",</b><br><br>";

			message += "<b>You are Registered successfully.Please Find Your Details Below:</b><br>";
			message += "<b>Email Id :  " + toEamilId + "<br>";
			message += "<b>Wallet Address :  " + walletAddress + "<br>";
			message += "<b>Login Password : " + password + "<br><br>";
			message += "<br>We hope that you will have a wonderful experience using our website. Any suggestions or feedback is more than welcome, we are always happy to hear from you in order to improve our services.<br>";
			message += "<a href=" + envi.getProperty("verify.mailId") + encryptUser
					+ ">Click here to activate your account</a><br>";
			message += "<br>";
			message += "<b>If you need to contact us, please click on the contact us link at the bottom of our website or below :</b>";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			Transport transport = session.getTransport();
			// transport = session.getTransport();
			LOG.info("Attempting to send an email through the Amazon SES SMTP interface...");
			transport.connect(host, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			LOG.info("Email sent successfully !");
			transport.close();
			LOG.info("Attempting to send an email");

			// Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailforPromoTokenTransfer(String toEamilId, String subject, String name, Double tokenCount,
			String walletaddress, String referralLink) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEamilId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEamilId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			String message = "<b>Dear " + name + ",</b><br><br>";
			message += "Thank you for participating in the program: Earn " + tokenCount
					+ " of REMCO token for learning about how tokenization will reduce money transfer transaction cost globally.<br>";
			message += "<br>";
			message += "<b>Your token of " + tokenCount
					+ " is now available in your wallet. You can view your token on ether scan at:";
			message += "<br><a href=https://etherscan.io/token/" + envi.getProperty("token.address") + "?a="
					+ walletaddress + ">Click to see your balance in etherscan.<br>" + "</a></b>";
			message += "<br><b>Copy your referral link below to refer friends : ";
			message += "<a href = " + referralLink + "><br>" + referralLink + "</a></b>";
			message += "<br>Use it on facebook, twitter, LinkedIn";
			message += "<br>Download marketing Materials below and re-use";
			message += "<a href =http://www.remittancetoken.io/pr/marketing/logo.jpg><br>"
					+ "http://www.remittancetoken.io/pr/marketing/logo.jpg" + "</a></b>";
			message += "<a href =http://www.remittancetoken.io/pr/marketing/logopdf.pdf><br>"
					+ "http://www.remittancetoken.io/pr/marketing/logopdf.pdf" + "</a></b>";
			message += "<a href =http://www.remittancetoken.io/pr/marketing/coingold1.png><br>"
					+ "http://www.remittancetoken.io/pr/marketing/coingold1.png" + "</a></b>";
			message += "<a href =http://www.remittancetoken.io/pr/marketing/coingold2.jpg><br>"
					+ "http://www.remittancetoken.io/pr/marketing/coingold2.jpg" + "</a></b>";
			message += "<a href =http://www.remittancetoken.io/pr/marketing/coinsilver1.jpg><br>"
					+ "http://www.remittancetoken.io/pr/marketing/coinsilver1.jpg" + "</a></b>";
			message += "<a href =http://www.remittancetoken.io/pr/marketing/coinsilver2.jpg><br>"
					+ "http://www.remittancetoken.io/pr/marketing/coinsilver2.jpg" + "</a></b>";
			message += "<br>If you need to contact us, please click on the contact us link at the bottom of our website or below :";
			message += "<a href=https://www.remittancetoken.io/ContactUs.aspx><br>Contact Us.</a>";
			message += "<br><br>";
			message += "<b>Thank you,</b><br>";
			message += "<b>Remco team.</b>";

			helper.setText(message);
			// set plain text message
			msg.setContent(message, "text/html");
			// sends the e-mail

			LOG.info("Attempting to send an email");

			Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendEmailToUsers(String firstName, String lastname, String mobileNo, String toEmailId, String subject, String message) {
		try {
			LOG.info("In  sendEmailDEV  START: " + env);

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(toEmailId);
			mail.setFrom(username);
			mail.setSubject(subject);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", host);

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};

			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username, "REMCO Team"));
			InternetAddress[] toAddresses = { new InternetAddress(toEmailId) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			String subj = subject;
			subj = subj.replaceFirst("###Name###",  firstName + " " + lastname);
			subj = subj.replaceFirst("###FirstName###", firstName);
			subj = subj.replaceFirst("###LastName###", lastname);
			subj = subj.replaceFirst("###EmailAddress###", toEmailId);
			subj = subj.replaceFirst("###PhoneNumber###", mobileNo);
			
			msg.setSubject(subj);
			msg.setSentDate(new Date());
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			
			String value = message;
			value = value.replaceFirst("###WebsiteURL###", "https://www.remittancetoken.io/");
			value = value.replaceFirst("###WebsiteName###", "remittancetoken.io");
			value = value.replaceFirst("###Name###",  firstName + " " + lastname);
			value = value.replaceFirst("###FirstName###", firstName);
			value = value.replaceFirst("###LastName###", lastname);
			value = value.replaceFirst("###EmailAddress###", toEmailId);
			value = value.replaceFirst("###PhoneNumber###", mobileNo);
			
			helper.setText(value);
			// set plain text message
			msg.setContent(value, "text/html");
			// sends the e-mail

			LOG.info("Attempting to send an email");

			Transport.send(msg);

			LOG.info("Email sent successfully !");
			LOG.info("In  sendEmailDEV  END: " + env);
			return true;
		} catch (MailException e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			LOG.info("Problem in sending mail sendEmailDEV  END: " + env);
			e.printStackTrace();
			return false;
		}	
	}

}
