package com.sjsu.HealthConnect.util;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sjsu.HealthConnect.dao.AppointmentDao;
import com.sjsu.HealthConnect.dao.UserDao;
import com.sjsu.HealthConnect.dto.Prescription;
import com.sjsu.HealthConnect.entity.Appointment;
import com.sjsu.HealthConnect.entity.Medication;
import com.sjsu.HealthConnect.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.Thread.sleep;

@Component
public class EmailNotificationHelper {

    @Value("${email.user-name}")
    private String username;

    @Value("${email.password}")
    private String password;

    @Value("${meeting-link}")
    private String meetLink;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AppointmentDao appointmentDao;

    Logger LOG = LoggerFactory.getLogger(EmailNotificationHelper.class);
    private final String apptScheduledEmail = "<html>" +
            "<body>" +
            "<p>Dear {0},</p>" +
            "<p>We are pleased to confirm that your online appointment has been successfully scheduled with HealthConnect.</p>" +
            "<p><b>Appointment Details:</b></p>" +
            "<ul>" +
            "<li>Date: {1}</li>" +
            "<li>Time: {2} (PDT)</li>" +
            "<li>Online link: <a href=\"{3}\">Click here to join</a></li>" +
            "<li>Doctor: {4}</li>" +
            "</ul>" +
            "<p>Please ensure to join the link 10 minutes before your scheduled appointment time.</p>" +
            "<p>If you need to reschedule or cancel your appointment, please log in to your HealthConnect account or contact us at least 24 hours in advance.</p>" +
            "<p>If you have any questions or need further assistance, feel free to contact us at <a href=\"mailto:healthconnect295b@gmail.com\">healthconnect295b@gmail.com</a>.</p>" +
            "<p>We look forward to connecting with you during your appointment.</p>" +
            "<p>Warm regards,<br>HealthConnect Team</p>" +
            "</body>" +
            "</html>";

    private final String apptCancelledEmail = "<html>" +
            "<body>" +
            "<p>Dear {0},</p>" +
            "<p>We regret to inform you that your online appointment scheduled with HealthConnect has been cancelled.</p>" +
            "<p><b>Appointment Details:</b></p>" +
            "<ul>" +
            "<li>Date: {1}</li>" +
            "<li>Time: {2} (PDT)</li>" +
            "<li>Doctor: {3}</li>" +
            "</ul>" +
            "<p>If you have any questions or concerns, please don't hesitate to contact us at <a href=\"mailto:healthconnect295b@gmail.com\">healthconnect295b@gmail.com</a>.</p>" +
            "<p>We apologize for any inconvenience caused.</p>" +
            "<p>Warm regards,<br>HealthConnect Team</p>" +
            "</body>" +
            "</html>";



    private final String prescEmailBody = "Dear {0},\n" +
            "\n" +
            "Please find attached your prescription as discussed during your recent appointment with {1} at HealthConnect.\n" +
            "\n" +
            "If you have any questions or need further assistance, feel free to reach out to us.\n" +
            "\n" +
            "Warm regards,\n" +
            "HealthConnect Team\n";

    private final String apptUpdatedEmail = "<html>" +
            "<body>" +
            "<p>Dear {0},</p>" +
            "<p>We would like to inform you that there have been updates to your online appointment scheduled with HealthConnect.</p>" +
            "<p><b>Updated Appointment Details:</b></p>" +
            "<ul>" +
            "<li>Date: {1}</li>" +
            "<li>Time: {2} (PDT)</li>" +
            "<li>Online link: <a href=\"{3}\">Click here to join</a></li>" +
            "<li>Doctor: {4}</li>" +
            "</ul>" +
            "<p>Please ensure to review the updated details and join the appointment accordingly.</p>" +
            "<p>If you have any questions or need further assistance, feel free to contact us at <a href=\"mailto:healthconnect295b@gmail.com\">healthconnect295b@gmail.com</a>.</p>" +
            "<p>We look forward to connecting with you during your appointment.</p>" +
            "<p>Warm regards,<br>HealthConnect Team</p>" +
            "</body>" +
            "</html>";

    private final String apptUpdatedSubject = "HealthConnect Appointment Update";


    public void sendApptCreateEmail(Appointment appointment) {
        LOG.info("Sending Appointment confirmation email");
        Optional<User> patient = userDao.findById(appointment.getPatientId());
        Optional<User> doctor = userDao.findById(appointment.getDoctorId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");

        LocalDateTime dateTime = LocalDateTime.parse(appointment.getAppointmentDate().toUpperCase(), formatter);

        String body = MessageFormat.format(apptScheduledEmail, patient.get().getFirstName(), dateTime.toLocalDate(),
                dateTime.toLocalTime() + " " + (dateTime.getHour() < 12 ? "AM" : "PM"), meetLink+appointment.getId(), doctor.get().getFirstName() + " " + doctor.get().getLastName());
        sendEmail(patient.get().getEmailId(), "Appointment Confirmation:" + dateTime.toLocalDate() + " - HealthConnect",
                body, null);
    }

    public void sendApptUpdateEmail(Appointment appointment) {
        LOG.info("Sending Appointment confirmation email");
        Optional<User> patient = userDao.findById(appointment.getPatientId());
        Optional<User> doctor = userDao.findById(appointment.getDoctorId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");

        LocalDateTime dateTime = LocalDateTime.parse(appointment.getAppointmentDate().toUpperCase(), formatter);

        String body = MessageFormat.format(apptUpdatedEmail, patient.get().getFirstName(), dateTime.toLocalDate(),
                dateTime.toLocalTime() + " " + (dateTime.getHour() < 12 ? "AM" : "PM"), meetLink+appointment.getId(), doctor.get().getFirstName() + " " + doctor.get().getLastName());
        sendEmail(patient.get().getEmailId(), "Appointment Update:" + dateTime.toLocalDate() + " - HealthConnect",
                body, null);
    }

    public void sendApptCancelEmail(Appointment appointment) {
        LOG.info("Sending Appointment confirmation email");
        Optional<User> patient = userDao.findById(appointment.getPatientId());
        Optional<User> doctor = userDao.findById(appointment.getDoctorId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");

        LocalDateTime dateTime = LocalDateTime.parse(appointment.getAppointmentDate().toUpperCase(), formatter);

        String body = MessageFormat.format(
                apptCancelledEmail, patient.get().getFirstName(), dateTime.toLocalDate(),
                dateTime.toLocalTime() + " " + (dateTime.getHour() < 12 ? "AM" : "PM"), doctor.get().getFirstName() + " " + doctor.get().getLastName());
        sendEmail(patient.get().getEmailId(), "Appointment Cancellation Confirmation:" + dateTime.toLocalDate() + " - HealthConnect",
                body, null);
    }

    public void sendEmail(String recipientEmail, String subject, String body, String attachmentFilePath) {
        LOG.info("Sending email to" + recipientEmail);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            //messageBodyPart.setText(body);
            messageBodyPart.setContent(body, "text/html");

            if (attachmentFilePath != null) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(new File(attachmentFilePath));
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(source.getName());
                multipart.addBodyPart(attachmentBodyPart);
            }

            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);

            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            System.out.println("Exception:" + e.getMessage());
        }
    }

    public ResponseEntity<?> sendPrescription(Prescription prescription) {

        Document document = new Document();
        List<Appointment> applist = appointmentDao.findAll();
        Optional<Appointment> apptOpt = appointmentDao.findById(prescription.getAppointmentId());
        if (!apptOpt.isPresent()) {
            return new ResponseEntity("No such appointment found", HttpStatus.BAD_REQUEST);
        }
        Appointment appointment = apptOpt.get();
        Optional<User> patientOpt = userDao.findById(appointment.getPatientId());
        if (!patientOpt.isPresent()) {
            return new ResponseEntity("No such Patient found", HttpStatus.BAD_REQUEST);
        }
        User patient = patientOpt.get();
        Optional<User> doctorOpt = userDao.findById(appointment.getDoctorId());
        if (!doctorOpt.isPresent()) {
            return new ResponseEntity("No such doctor found", HttpStatus.BAD_REQUEST);
        }
        User doctor = doctorOpt.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy HH:MM");
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(formatter);
        String filename = patient.getFirstName() + "_" + appointment.getId() + "_" + formattedDateTime + ".pdf";
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Add logo and title side by side in the center at the top
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(35);
            PdfPCell logoCell = new PdfPCell();
            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 20);
            Paragraph title = new Paragraph(boldText("HealthConnect"));
            title.setFont(font);
            PdfPCell titleCell = new PdfPCell(title);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setBorder(Rectangle.NO_BORDER);

            // Load the logo image
            Image logo = Image.getInstance("logo.jpeg");
            logo.scaleAbsolute(50, 30); // Adjust the size of the image
            logoCell.addElement(logo);
            headerTable.addCell(logoCell);
            headerTable.addCell(titleCell);
            document.add(headerTable);

            document.add(new Chunk("\n\n")); // Add space between header and content

            // Add a line before the medication paragraph
            document.add(new LineSeparator());
            document.add(new Chunk("\n\n")); // Add space between header and content

            // Add patient information
            document.add(new Paragraph(boldText("\n\nPatient Information: ")));
            document.add(new Chunk("\n")); // Add space between header and content

            document.add(new Paragraph(boldText("\u2022 Name: ") + patient.getFirstName() + " " + patient.getLastName()));
            document.add(new Paragraph(String.valueOf(boldText("\u2022 Age:").append("" + patient.getAge()))));
            document.add(new Paragraph(boldText("\u2022 Gender: ") + patient.getSex()));
            document.add(new Paragraph(boldText("\u2022 Insurance Number: ") + getInsuranceNumber()));
            document.add(new Paragraph(boldText("\u2022 Referred By: ") + doctor.getFirstName() + " " + doctor.getLastName()));


            document.add(new Chunk("\n\n")); // Add space between header and content

            document.add(new LineSeparator());
            document.add(new Chunk("\n\n"));
            document.add(new Paragraph(boldText("Prescription:")));

            document.add(new Chunk("\n")); // Add space between header and content
            int index = 1;
            for (Medication m : prescription.getPrescription()) {
                document.add(new Paragraph(boldText(index++ + ". Medication: ") + m.getName()));
                document.add(new Paragraph(boldText("    \u2022 Dosage: ") + m.getDosage()));
                document.add(new Paragraph(String.valueOf(boldText("    \u2022 Frequency:").append("" + m.getFrequency()))));
                document.add(new Paragraph(boldText("    \u2022 Instructions: ") + m.getInstructions()));
            }
            document.add(new Chunk("\n\n"));
            document.add(new LineSeparator());
            document.add(new Chunk("\n\n"));
            document.add(new Paragraph("Please consult your healthcare provider for any questions or concerns regarding this prescription. " +
                    "If you have any questions or need further assistance, feel free to contact us at healthconnect295b@gmail.com."));
            document.add(new Chunk("\n\n"));
            document.add(new LineSeparator());

            document.close();

            appointment.setPrescription(prescription.getPrescription());
            appointmentDao.save(appointment);


            String body = MessageFormat.format(prescEmailBody, patient.getFirstName(), doctor.getFirstName() + " " + doctor.getLastName());
            System.out.println("Sending prescription to " + patient.getFirstName() + " on " + patient.getEmailId());
            sendEmail(patient.getEmailId(), "Prescription - HealthConnect", body, filename);
            sleep(2000);
            File file = new File(filename);
            if (file.delete()) {
                System.out.println("File deleted successfully");
            } else {
                System.out.println("Failed to delete file");
            }

        } catch (DocumentException | IOException /*| InterruptedException*/ e) {
            System.out.println("Message: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity("Prescription sent!   ", HttpStatus.OK);
    }

    private Chunk boldText(String text) {
        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        return new Chunk(text, boldFont);
    }

    public static String getInsuranceNumber() {
        int length = 12; // Adjust this for your desired string length
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int charIndex = random.nextInt(characters.length());
            result.append(characters.charAt(charIndex));
        }
        return result.toString();

    }

}
