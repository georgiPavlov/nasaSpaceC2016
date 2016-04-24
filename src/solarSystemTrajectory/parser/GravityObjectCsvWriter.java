package solarSystemTrajectory.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVWriter;



public class GravityObjectCsvWriter {
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);// decimal separator will be a dot

    public GravityObjectCsvWriter() {
        numberFormat.setGroupingUsed(false);
    }

    protected String formatNumber(double number) {
        if (number < 1 && number > -1) {
            numberFormat.setMaximumFractionDigits(50);//more precision for pure decimal numbers
        } else {
            numberFormat.setMaximumFractionDigits(15);
        }
        return numberFormat.format(number);
    }

    /**
     * Writes a list of {@link GravityObject} to the given outputStream
     *
     * @param out
     *            {@link OutputStream} where to write
     * @param gravityObjects
     *            the list of {@link GravityObject} to export
     * @param date
     *            the date for the orbital data
     * @param comment
     *            a comment associated with the data
     * @throws IOException
     */
    public void write(OutputStream out, List<GravityObject> gravityObjects, Date date, String comment) throws IOException {
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // general infos section title
        String[] line = new String[] {
                "TimeStamp"
        };
        writer.writeNext(line);
        line[0] = formatNumber(date.getTime());
        writer.writeNext(line);

        // comment section title
        line = new String[] {
                "Comment"
        };
        writer.writeNext(line);
        line[0] = comment;
        writer.writeNext(line);

        // object infos section title
        line = new String[] {
                "Name", "Type", "Mass", "Density", "PositionX", "PositionY", "PositionZ", "VelocityX", "VelocityY", "VelocityZ"
        };
        writer.writeNext(line);
        for (GravityObject gravityObject : gravityObjects) {
            line[0] = gravityObject.getName();
            line[1] = gravityObject.getType().toString();
            line[2] = formatNumber(gravityObject.getMass());
            line[3] = formatNumber(gravityObject.getDensity());
            line[4] = formatNumber(gravityObject.getX());
            line[5] = formatNumber(gravityObject.getY());
            line[6] = formatNumber(gravityObject.getZ());
            line[7] = formatNumber(gravityObject.getVx());
            line[8] = formatNumber(gravityObject.getVy());
            line[9] = formatNumber(gravityObject.getVz());
            writer.writeNext(line);
        }
        writer.close();
    }
}