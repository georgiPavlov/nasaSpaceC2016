package solarSystemTrajectory.webfetcher;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import utils.HttpClient;

/**
 * Tool to retrieve an object id from its name using the JPL HORIZONS system
 *
 * @author Kévin FERRARE
 *
 */
public class JplHorizonGravityObjectCodeRetriever extends HttpClient {
    /**
     * Tries to find an object id for the given object name
     *
     * @param name
     *            the name of the object to search
     * @return a String containing the object id if found or null else
     */
    public String getGravityObjectCode(String name) throws UnsupportedEncodingException, ClientProtocolException, IOException {
        String code = getGravityObjectCodeFromLocalDatabase(name);
        if (code == null) {
            code = getGravityObjectCodeFromNetwork(name);
        }
        return code;
    }

    /**
     * Search the given name from a local database (the JPL search page is only for small bodies so planets and major moons are not there)
     *
     * @param name
     *            the name of the object to search
     * @return a String containing the object id if found or null else
     */
    protected String getGravityObjectCodeFromLocalDatabase(String name) {
        name = name.toUpperCase();
        for (String[] bodyDescription : JplHorizonsMajorBodiesList.majorBodies) {
            if (bodyDescription[0].toUpperCase().contains(name)) {
                return bodyDescription[1];
            }
        }
        return null;
    }


    protected String getGravityObjectCodeFromNetwork(String name) throws UnsupportedEncodingException, ClientProtocolException, IOException {
        String result = new String(getResult(name));
        String[] lines = result.split("\n");
        for (String line : lines) {
            if (line.contains("find_body")) {
                String[] parameters = line.split("find_body")[1].split("\"")[0].split("&amp;");
                String objectGroup = "";
                String sstr = "";
                for (String parameter : parameters) {
                    if (parameter.contains("body_group")) {
                        objectGroup = parameter.split("=")[1].toUpperCase();
                    }
                    if (parameter.contains("sstr")) {
                        sstr = parameter.split("=")[1].toUpperCase();
                    }
                }
                return objectGroup + ":" + sstr;
            }
        }
        return null;
    }


    protected byte[] getResult(String name) throws UnsupportedEncodingException, ClientProtocolException, IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("sstr", name));
        return postData("http://ssd.jpl.nasa.gov/sbdb.cgi", nameValuePairs);
    }
}