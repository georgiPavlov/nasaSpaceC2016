package utils;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ProxySelector;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.util.EntityUtils;

public class HttpClient {
    private DefaultHttpClient httpclient = new DefaultHttpClient();

    private ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>() {
        @Override
        public byte[] handleResponse(org.apache.http.HttpResponse httpResponse) throws ClientProtocolException, IOException {
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                return EntityUtils.toByteArray(entity);
            } else {
                return null;
            }
        }


    };

    public HttpClient() {
        ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(httpclient.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
        httpclient.setRoutePlanner(routePlanner);
    }

    /**
     * Sends an HTTP POST request with the given data to the given URL
     *
     * @param url
     *            the url to send the request
     * @param nameValuePairs
     *            the list of parameters to send
     * @return the response to the request.
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public byte[] postData(String url, List<NameValuePair> nameValuePairs) throws UnsupportedEncodingException, IOException, ClientProtocolException {
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        return httpclient.execute(httppost, handler);
    }

    /**
     * Sends an HTTP GET request to the given URL
     *
     * @param url
     *            the url to send the request
     * @return the response to the request.
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public byte[] getData(String url) throws UnsupportedEncodingException, IOException, ClientProtocolException {
        HttpGet httpget = new HttpGet(url);
        return httpclient.execute(httpget, handler);
    }
}
