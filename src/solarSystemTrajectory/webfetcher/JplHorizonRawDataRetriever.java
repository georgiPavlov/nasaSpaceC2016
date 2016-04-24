package solarSystemTrajectory.webfetcher;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import solarSystemTrajectory.webfetcher.parameters.*;
import utils.OnlyExtensionFilter;


public class JplHorizonRawDataRetriever {
    private static Log log = LogFactory.getLog(JplHorizonRawDataRetriever.class);
    private static final String RAW_DATA_FILE_EXTENSION = "jplrawdata";
    private JplHorizonWebApi jplHorizonSettingsSetter = new JplHorizonWebApi();
    private Date date;
    private String folderName = null;


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public String getFolderName() {
        return folderName;
    }


    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }



    protected void setup() throws UnsupportedEncodingException, ClientProtocolException, IOException {
        jplHorizonSettingsSetter.setEphemerisType(EphemerisType.VECTORS);
        jplHorizonSettingsSetter.setTableSettings(OutputUnit.KILOMETER_PER_SECOND, QuantityCode.STATE_VECTORS, ReferencePlane.ECLIPTIC, ReferenceSystem.J2000, VectorCorrection.NONE, true, true, true);
        jplHorizonSettingsSetter.setOutputType(OutputType.TEXT);

        jplHorizonSettingsSetter.setDate(date);
    }


    public Map<String, String> fetchRawData(List<String> ids) throws Exception {
        setup();
        Map<String, String> rawDataMap = new HashMap<String, String>();
        for (String id : ids) {
            if(id.isEmpty()){
                log.error("Empty id present !");
                continue;
            }
            jplHorizonSettingsSetter.setTargetGravityObject(id);
            String rawResult = new String(jplHorizonSettingsSetter.getResult());
            rawDataMap.put(id, rawResult);
            log.info("Fetched " + id);
            if (folderName != null) {
                saveEntryToFolder(id, rawResult);
            }
        }
        return rawDataMap;
    }


    public void saveToFolder(Map<String, String> rawDataMap) throws IOException {
        for (Entry<String, String> rawData : rawDataMap.entrySet()) {
            saveEntryToFolder(rawData.getKey(), rawData.getValue());
        }
    }


    protected void saveEntryToFolder(String name, String content) throws IOException {
        name = name.replace("*", "").replace("/", "");
        String path = folderName + "/" + name + "." + RAW_DATA_FILE_EXTENSION;
        FileWriter fstream = new FileWriter(path);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(content);
        out.close();
    }


    public Map<String, String> loadFromFolder() throws IOException {
        Map<String, String> rawDataMap = new HashMap<String, String>();
        File folder = new File(folderName);
        if (!folder.exists()) {
            log.error("Invalid folder " + folderName);
            return null;
        }
        //File[] listOfFiles = folder.listFiles(new OnlyExtensionFilter(RAW_DATA_FILE_EXTENSION));
        File[] listOfFiles = null;
        for (File file : listOfFiles) {
            String name = file.getName().replace("." + RAW_DATA_FILE_EXTENSION, "");
            FileInputStream stream = new FileInputStream(file);
            FileChannel fileChannel = stream.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            String content = Charset.forName("ASCII").decode(mappedByteBuffer).toString();
            stream.close();
            rawDataMap.put(name, content);
        }
        return rawDataMap;
    }
}