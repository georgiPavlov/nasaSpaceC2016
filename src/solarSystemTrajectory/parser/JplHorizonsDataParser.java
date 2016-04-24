package solarSystemTrajectory.parser;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class JplHorizonsDataParser {
    private static Log log = LogFactory.getLog(JplHorizonsDataParser.class);
    private Pattern replaceNonAlphanumericPattern = Pattern.compile(",| |\\(|\\)");
    private Pattern massKeyMultiplierExtractPattern = Pattern.compile("^[a-zA-Z]+([0-9]+)\\^([0-9]+).*");
    private Pattern massValueImprecisionRemovePattern = Pattern.compile("(\\+\\-|\\-\\+)([0-9|\\.]+)");
    private Pattern massValueExtractPattern = Pattern.compile("^([0-9]+\\.?[0-9]+)( *\\(?([0-9]+)\\^(-?[0-9]+).*)?");
    private Map<String, GravityObject> additionalData;


    public JplHorizonsDataParser(Map<String, GravityObject> additionalData) {
        this.additionalData = additionalData;
    }


    public List<GravityObject> parseInfos(Map<String, String> rawDataMap) throws Exception {
        List<GravityObject> list = new ArrayList<GravityObject>();
        for (Entry<String, String> rawData : rawDataMap.entrySet()) {
            String jplId = rawData.getKey();
            String rawGravityObjectData = rawData.getValue();// raw Horizon system data for the current object
            GravityObject object = parseGravityObjectInfos(jplId, rawGravityObjectData);
            if (object != null) {
                list.add(object);
                log.info("Parsed : " + jplId+ " [ "+object.getName()+" ]");
            } else {
                log.info(jplId + " : error");
            }
        }
        return list;
    }


    public GravityObject parseGravityObjectInfos(String jplId, String rawGravityObjectData) throws Exception {
        try {
            Map<String, String> objectPropertiesMap = parseKeyValues(rawGravityObjectData);
            if (objectPropertiesMap == null) {
                return null;
            }
            GravityObject gravityObject = new GravityObject();
            gravityObject.setId(jplId);
            // some items don't have those fields, set a default value
            gravityObject.setDensity(-1);
            gravityObject.setMass(-1);
            gravityObject.setType(null);
            for (Entry<String, String> entry : objectPropertiesMap.entrySet()) {
                String key = entry.getKey().toLowerCase();
                String value = entry.getValue();
                if (value.isEmpty()) {
                    continue;
                }
                if (key.contains("mass ") && (key.contains(" g") || key.contains(" kg")) || value.contains(" kg")) {// for example Mass (10^24 kg)
                    // a lot of entries have the multiplier in the key ...
                    // key : Mass, 10^20 kg
                    Matcher matcher = replaceNonAlphanumericPattern.matcher(key);
                    key = matcher.replaceAll("");
                    // key : Mass10^20kg
                    matcher = massKeyMultiplierExtractPattern.matcher(key);
                    double keyMultiplier = 1;
                    if (matcher.find() && matcher.groupCount() == 2) {
                        keyMultiplier = Math.pow(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                    }

                    if (!key.contains("kg") && key.contains("g") && !key.contains("weight")) {
                        keyMultiplier *= 0.001;// convert grams in kilograms
                    }
                    if (value.contains("lb")) {
                        keyMultiplier *= 0.45359237;
                    }
                    // value can be "1.08+-0.1 (10^-4)" (second multiplier) ...
                    matcher = massValueImprecisionRemovePattern.matcher(value);
                    value = matcher.replaceAll("");
                    matcher = massValueExtractPattern.matcher(value);
                    if (matcher.find()) {
                        double mass = Double.parseDouble(matcher.group(1)) * keyMultiplier;
                        String multiplierNumber = matcher.group(3);
                        String multiplierExponent = matcher.group(4);
                        if (multiplierNumber != null && multiplierExponent != null) {
                            mass *= Math.pow(Integer.parseInt(multiplierNumber), Integer.parseInt(multiplierExponent));
                        }
                        gravityObject.setMass(Math.floor(mass + 0.5d));
                    }
                }
                if (key.toLowerCase().contains("launch mass")) {// spacecrafts
                    // Total launch mass = 1223 kg
                    // Launch Mass: 4 tonnes
                    double mass = Double.parseDouble(value.split(" +")[0]);
                    if (value.toLowerCase().contains("ton")) {
                        mass *= 1000;// tons->kg
                    }
                    gravityObject.setMass(Math.floor(mass + 0.5d));
                }
                if ("gm".equals(key)) {
                    try {
                        double gm = Double.parseDouble(value);
                        gm = gm * Math.pow(10, 20) / 6.6725985;
                        gravityObject.setMass(Math.floor(gm + 0.5d));
                    } catch (NumberFormatException ex) {
                    }
                }
                if (key.contains("density")) {
                    double density = Double.parseDouble(stripImprecisionData(value.split(" +")[0].split("\\(")[0]));
                    gravityObject.setDensity(density * 1000);// convert to kg/m3
                }
                if ("name".equals(key)) {
                    gravityObject.setName(value);
                }
                if ("coordinates".equals(key)) {
                    String[] split = value.split(",");
                    gravityObject.setX(Double.parseDouble(split[2].trim()) * 1000);// km to m
                    gravityObject.setY(Double.parseDouble(split[3].trim()) * 1000);
                    gravityObject.setZ(Double.parseDouble(split[4].trim()) * 1000);
                    gravityObject.setVx(Double.parseDouble(split[5].trim()) * 1000);
                    gravityObject.setVy(Double.parseDouble(split[6].trim()) * 1000);
                    gravityObject.setVz(Double.parseDouble(split[7].trim()) * 1000);
                }
                if ("objectType".equals(key)) {
                    if ("spacecraft".equals(value)) {
                        gravityObject.setType(GravityObjectType.SPACECRAFT);
                    }
                }
            }
            //use additional data if available
            if (additionalData != null) {
                GravityObject additional = additionalData.get(jplId);
                if (additional != null) {
                    gravityObject.setMass(additional.getMass());
                    gravityObject.setDensity(additional.getDensity());
                }
            }
            // determine the type
            if (gravityObject.getType() == null) {
                gravityObject.setType(determineType(gravityObject, jplId));
            }
            return gravityObject;
        } catch (Exception ex) {
            throw new Exception(rawGravityObjectData, ex);
        }
    }

    //data used to give a type to some objects
    private static String[] spacecraftNames = {
            "spacecraft", "telescope", "observatory", "6q0b44e"
    };
    private static String[] planetNames = {
            "mercury", "venus", "earth", "mars", "jupiter", "saturn", "uranus", "neptune"
    };
    private static String[] asteroidNames = {
            "neocp", "lovejoy"
    };
    private static String[] dwarfPlanetIds = {
            "1", "90377", "136199", "136108", "136472", "90482", "50000", "225088"
    };

    private boolean matchContains(String[] list, String name) {
        for (String string : list) {
            if (name.contains(string)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchEquals(String[] list, String name) {
        for (String string : list) {
            if (name.equals(string)) {
                return true;
            }
        }
        return false;
    }


    private GravityObjectType determineType(GravityObject gravityObject, String jplId) {
        if (gravityObject.getMass() != -1 && gravityObject.getMass() < 1000000) {
            return GravityObjectType.SPACECRAFT;
        }
        String lowerCaseName = gravityObject.getName().toLowerCase();
        if (lowerCaseName.equals("sun (10)")) {
            return GravityObjectType.STAR;
        }
        if (matchEquals(planetNames, lowerCaseName)) {
            return GravityObjectType.PLANET;
        }
        if (matchContains(spacecraftNames, lowerCaseName)) {
            return GravityObjectType.SPACECRAFT;
        }
        if (matchContains(asteroidNames, lowerCaseName)) {
            return GravityObjectType.ASTEROID;
        }
        String[] id = jplId.split(":");// like MB:1234 or SB:1234
        if ((id.length == 2 && "SB".equals(id[0]) && matchEquals(dwarfPlanetIds, id[1])) || lowerCaseName.contains("pluto")) {
            return GravityObjectType.DWARF_PLANET;
        }
        try {
            Integer.parseInt(lowerCaseName.split(" ")[0]);// main moons don't have a numeric ID in front of their names
            return GravityObjectType.ASTEROID;
        } catch (NumberFormatException ex) {
        }

        return GravityObjectType.MOON;
    }


    private Map<String, String> parseKeyValues(String rawGravityObjectData) {
        if (rawGravityObjectData == null) {
            return null;
        }
        String[] rawResultPerLine = rawGravityObjectData.split("\n");
        if (rawResultPerLine.length < 1 || rawResultPerLine[0].contains("Horizons ERROR")) {
            return null;
        }
        boolean enteredCoordinatesSection = false;
        ParserHashMap objectPropertiesMap = new ParserHashMap();
        for (String line : rawResultPerLine) {
            if ("$$SOE".equals(line)) {
                enteredCoordinatesSection = true;
                continue;
            }
            if (!enteredCoordinatesSection) {// in the first section, try to get the properties
                if (!line.contains("=") && !line.contains(":")) {
                    continue;
                }
                if (line.contains("SPACECRAFT TRAJECTORY")) {// spacecraft
                    objectPropertiesMap.put("objectType", "spacecraft");
                    continue;
                }
                if (line.contains("Target body name")) {// special handling for object names
                    String name = line.split(":")[1].trim().split("  +")[0].trim().split(" \\{")[0];
                    objectPropertiesMap.put("name", name);
                    continue;
                }
                // now we have a line like that "Mean daily motion     = 0.0831294 deg/d Mean orbit velocity    = 13.0697 km/s"
                String[] split = line.split("=|:");// array contains "Mean daily motion     " " 0.0831294 deg/d Mean orbit velocity    " " 13.0697 km/s"
                if (split.length < 2) {// no data for this key
                    continue;
                }
                if (split.length == 2) {// only one key/value for this line
                    objectPropertiesMap.putClean(split[0], split[1]);
                    continue;
                }
                String[] middle = split[1].trim().split(" +");// middle contains the first value and eventually the second key (bad data format jpl ...)
                String key1 = split[0];
                String value1 = "";
                if (middle.length > 0) {// value exists
                    int indexOfKey2 = 0;
                    if (middle[indexOfKey2].isEmpty()) {// no data for this key
                        continue;
                    }
                    while (indexOfKey2 < middle.length && looksLikeValue(middle[indexOfKey2])) {
                        value1 += " " + middle[indexOfKey2];
                        indexOfKey2++;
                    }
                    if (split.length > 2) {// a second key is present
                        String key2 = "";
                        String value2 = "";
                        for (int i = indexOfKey2; i < middle.length; i++) {
                            key2 += middle[i] + " ";
                        }
                        value2 = split[2];
                        objectPropertiesMap.putClean(key2, value2);
                    }
                }
                objectPropertiesMap.putClean(key1, value1);
            } else {// coordinates section
                // read the coordinates and exit
                // one CSV line like this : 2455946.091666667, A.D. 2012-Jan-19 14:12:00.0000, 5.471462020976968E+08, 5.037733598321121E+08, -1.434787815440596E+07, -9.008671601046395E+00, 1.023659081378351E+01, 1.589656615294805E-01,
                // JDCT , , X, Y, Z, VX, VY, VZ,
                objectPropertiesMap.put("coordinates", line);
                break;
            }
        }
        return objectPropertiesMap;
    }


    private String stripImprecisionData(String string) {
        return string.split("\\+\\-")[0];
    }


    private static boolean looksLikeValue(String string) {
        if (string.isEmpty()) {
            return false;
        }
        char firstChar = string.charAt(0);
        char toUpper = Character.toUpperCase(firstChar);

        if (firstChar >= '0' && firstChar <= '9') {//numbers are usually values
            return true;
        }
        if (toUpper < 'A' || toUpper > 'Z') {//not a letter, usually a value
            return true;
        }
        if (firstChar != toUpper) {// value units often start in low characters
            return false;
        }
        return false;

    }
}


@SuppressWarnings("serial")
class ParserHashMap extends HashMap<String, String> {

    public String putClean(String key, String value) {
        key = cleanString(key);
        if (key.isEmpty()) {
            return value;
        }
        value = cleanString(value);
        if (value.isEmpty()) {
            return value;
        }
        return super.put(key, value);
    }

    private String cleanString(String string) {
        return string.trim().replace(",", "");
    }
}