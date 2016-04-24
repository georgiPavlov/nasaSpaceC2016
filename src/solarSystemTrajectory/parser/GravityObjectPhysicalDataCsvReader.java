package solarSystemTrajectory.parser;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class GravityObjectPhysicalDataCsvReader {

    public Map<String, GravityObject> read(Reader in) throws IOException {
        Map<String, GravityObject> data = new HashMap<String, GravityObject>();
        CSVReader reader = new CSVReader(in);
        String[] line;
        while ((line = reader.readNext()) != null) {
            String id = line[0];
            if (id.isEmpty()) {
                continue;
            }
            try {
                GravityObject gravityObject = new GravityObject();
                gravityObject.setName(line[1]);
                gravityObject.setMass(Double.parseDouble(line[2]));
                gravityObject.setDensity(Double.parseDouble(line[3]));
                data.put(id, gravityObject);
            } catch (NumberFormatException ex) {
            }
        }
        reader.close();
        return data;
    }
}