package tie.hackathon.travelguide;

/**
 * Created by sunny on 6/8/16.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import Util.Constants;

public class ACRAReportSender implements ReportSender {

    SharedPreferences s ;
    private String emailUsername;
    private String emailPassword;

    public ACRAReportSender(String emailUsername, String emailPassword) {
        super();
        this.emailUsername = emailUsername;
        this.emailPassword = emailPassword;
    }


    @Override
    public void send(Context context, CrashReportData report)
            throws ReportSenderException {

        s = PreferenceManager.getDefaultSharedPreferences(context);

        // Extract the required data out of the crash report.
        String reportBody = createCrashReport(context, report);

        // instantiate the email sender
        GMailSender gMailSender = new GMailSender(emailUsername, emailPassword);

        try {
            // specify your recipients and send the email
            gMailSender.sendMail("CRASH REPORT", reportBody, "sunny.kachhia@gmail.com", "sunny.kachhia@gmail.com");
        } catch (Exception e) {
            Log.d("Error Sending email", e.toString());
        }
    }


    /**
     * Extract the required data out of the crash report.
     */
    private String createCrashReport(Context context, CrashReportData report) {

        // I've extracted only basic information.
        // U can add loads more data using the enum ReportField. See below.
        StringBuilder body = new StringBuilder();
        body
                .append("Device : " + report.getProperty(ReportField.BRAND) + "-" + report.getProperty(ReportField.PHONE_MODEL))
                .append("\n")
                .append("Android Version : " + report.getProperty(ReportField.ANDROID_VERSION))
                .append("\n")
                .append("App Version Code : " + report.getProperty(ReportField.APP_VERSION_CODE))
                .append("\n")
                .append("App Version Name : " + report.getProperty(ReportField.APP_VERSION_NAME))
                .append("\n")
                .append("User IP : " + report.getProperty(ReportField.USER_IP))
                .append("\n")
                .append("User Email : " + s.getString(Constants.USER_EMAIL,"null"))
                .append("\n\n")
                .append("STACK TRACE : \n" + report.getProperty(ReportField.STACK_TRACE))
                .append("\n\n")
                .append("LOG CAT : \n" + report.getProperty(ReportField.LOGCAT) + "\n\n");

        return body.toString();
    }
}