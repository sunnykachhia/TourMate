package tie.hackathon.travelguide;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.config.ACRAConfiguration;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderFactory;

/**
 * Created by sunny on 6/8/16.
 */
public class ACRAReportSenderFactory implements ReportSenderFactory {

    @NonNull
    @Override
    public ReportSender create(@NonNull Context context, @NonNull ACRAConfiguration config) {
        return new ACRAReportSender("sunny.kachhia@gmail.com", "!lovemyparent$");
    }
}
