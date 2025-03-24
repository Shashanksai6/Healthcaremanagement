package com.sjsu.HealthConnect.util;


import com.sjsu.HealthConnect.dao.AppointmentDao;
import com.sjsu.HealthConnect.dao.UserDao;
import com.sjsu.HealthConnect.entity.Appointment;
import com.sjsu.HealthConnect.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**

 This class defines a scheduled cron job that runs every day at midnight. The job is responsible for sending
 appointment reminders to patients for appointments scheduled for the following day, and vaccination appointment
 reminders to patients for vaccinations that are due within the next 4 days.
 */

@Component
public class CronJob {

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailNotificationHelper notificationHelper;
    Logger LOG = LoggerFactory.getLogger(EmailNotificationHelper.class);

    private final String apptReminderEmail = "Dear {0},\n" +
            "\n" +
            "We hope this email finds you well. This is a friendly reminder that you have an appointment scheduled with us today.\n" +
            "\n" +
            "Appointment Details:\n" +
            "\n" +
            "Time: {2} (PDT)\n" +
            "Online link: {3}\n" +
            "Doctor: {4}\n" +
            "Please ensure to join the link 10 minutes before your scheduled appointment time.\n" +
            "\n" +
            "If you need to reschedule or cancel your appointment, please log in to your HealthConnect account or contact us at least 24 hours in advance.\n" +
            "\n" +
            "If you have any questions or need further assistance, feel free to contact us at healthconnect295b@gmail.com.\n" +
            "\n" +
            "We look forward to connecting with you during your appointment.\n" +
            "\n" +
            "Warm regards,\n" +
            "HealthConnect Team\n" +
            "\n" +
            "\n" +
            "\n";;


    // The cron expression specifies that this method should run every day at midnight
    //@Scheduled(cron = "0 0 0 * * *") // every day
    @Scheduled(cron = "0 0 0 * * *") // every 10 seconds
    public void runJob() throws ParseException {

        LOG.info("Cron Job running now");
        // Calculate the date for tomorrow
        LocalDate today = LocalDate.now(); // calculate the date for tomorrow

        System.out.println("date"+today);

        // Retrieve a list of appointments that are scheduled for tomorrow and have the status SCHEDULED
        List<Appointment> appointments = appointmentDao.findAll()
                .stream()
                //.filter(app -> app.getDate().toString().equals(today.toString()))
                .collect(Collectors.toList());

        // For each appointment, generate a reminder email and send it to the patient's email address
        for(Appointment appointment : appointments){

            LOG.info("Sending Appointment reminder email");
            System.out.println(appointment.getDate());
            Optional<User> patient = userDao.findById(appointment.getPatientId());
            Optional<User> doctor = userDao.findById(appointment.getDoctorId());
            String body = MessageFormat.format(apptReminderEmail, patient.get().getFirstName(), appointment.getDate(),
                    appointment.getDate(), "Zoom Link", doctor.get().getFirstName() +" " + doctor.get().getLastName());

            System.out.println("Sending app reminder to " + patient.get().getFirstName() + " on " + patient.get().getEmailId());
            notificationHelper.sendEmail(patient.get().getEmailId(), "Reminder: Upcoming Vaccination Appointment", body,null);
        }

    }
}
