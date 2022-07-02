package com.example.homework1;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GeolocationSaver {

    public static Boolean savePath(Context context, List<Location> points) {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e("storage", "No storage");
            return false;
        }

        DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String name = "Path_" + df.format(new Date()) + ".gpx";

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath(), Environment.DIRECTORY_DOCUMENTS);
        dir.mkdirs();

        File file = new File(dir, name);

        return generateGfx(file, name, points);
    }

    private static Boolean generateGfx(
            File file, String name, List<Location> points) {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" " +
                "standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" " +
                "creator=\"MapSource 6.15.5\" version=\"1.1\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  " +
                "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 " +
                "http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        name = "<name>" + name + "</name><trkseg>\n";

        String segments = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (Location location : points) {
            segments += "<trkpt lat=\"" + location.getLatitude() +
                    "\" lon=\"" + location.getLongitude() +
                    "\"><time>" + df.format(new Date(location.getTime())) +
                    "</time></trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        try {
            FileWriter writer = new FileWriter(file, true);
            writer.append(header);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            Log.e("generateGfx", "Error Writing Path", e);
            return false;
        }
    }
}
