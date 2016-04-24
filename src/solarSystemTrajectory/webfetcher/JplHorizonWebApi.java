package solarSystemTrajectory.webfetcher;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.GregorianCalendar;
        import java.util.List;

        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.message.BasicNameValuePair;
import solarSystemTrajectory.webfetcher.parameters.*;
import utils.HttpClient;


public class JplHorizonWebApi extends HttpClient {
    private static final DateFormat yyyymmddhhmmFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void setEphemerisType(EphemerisType type) throws UnsupportedEncodingException, ClientProtocolException, IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("table_type", type.getValue()));
        nameValuePairs.add(new BasicNameValuePair("set_table_type", "Use Selection Above"));
        postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
    }

    public void setTargetGravityObject(String targetGravityObject) throws UnsupportedEncodingException, ClientProtocolException, IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("body", targetGravityObject));
        nameValuePairs.add(new BasicNameValuePair("select_body", "Select Indicated Body"));
        postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
        }

    public void setTimeSpan(Date startTime, Date stopTime, int step, IntervalMode intervalMode) throws UnsupportedEncodingException, ClientProtocolException, IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("start_time", yyyymmddhhmmFormat.format(startTime)));
        nameValuePairs.add(new BasicNameValuePair("stop_time", yyyymmddhhmmFormat.format(stopTime)));
        nameValuePairs.add(new BasicNameValuePair("step_size", Integer.toString(step)));
        nameValuePairs.add(new BasicNameValuePair("interval_mode", intervalMode.getValue()));
        nameValuePairs.add(new BasicNameValuePair("set_time_span", "Use Specified Times"));
        postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
    }

    public void setDate(Date date) throws UnsupportedEncodingException, ClientProtocolException, IOException {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = calendar.getTime();
        this.setTimeSpan(date, endDate, 1, IntervalMode.DAYS);

    }

    public void setTableSettings(OutputUnit outputUnit, QuantityCode quantityCode, ReferencePlane referencePlane, ReferenceSystem referenceSystem, VectorCorrection vectorCorrection, boolean labels, boolean csvFormat, boolean objectPage) throws UnsupportedEncodingException, ClientProtocolException, IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("output_units", outputUnit.getValue()));
        nameValuePairs.add(new BasicNameValuePair("vec_quan", quantityCode.getValue()));
        nameValuePairs.add(new BasicNameValuePair("ref_plane", referencePlane.getValue()));
        nameValuePairs.add(new BasicNameValuePair("ref_system", referenceSystem.getValue()));
        nameValuePairs.add(new BasicNameValuePair("vect_corr", vectorCorrection.getValue()));
        if (labels) {
            nameValuePairs.add(new BasicNameValuePair("vec_labels", "YES"));
        }
        if (csvFormat) {
            nameValuePairs.add(new BasicNameValuePair("csv_format", "YES"));
        }
        if (objectPage) {
            nameValuePairs.add(new BasicNameValuePair("obj_data", "YES"));
        }
        nameValuePairs.add(new BasicNameValuePair("set_table", "Use Settings Above"));
        nameValuePairs.add(new BasicNameValuePair("set_table_settings", "1"));
        postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
    }

    public byte[] setOutputType(OutputType outputType) throws UnsupportedEncodingException, ClientProtocolException, IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("display", outputType.getValue()));
        nameValuePairs.add(new BasicNameValuePair("set_display", "Use Selection Above"));
        return postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
    }



    public byte[] getResult() throws UnsupportedEncodingException, ClientProtocolException, IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("go", "Generate Ephemeris"));
        return postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
    }
}