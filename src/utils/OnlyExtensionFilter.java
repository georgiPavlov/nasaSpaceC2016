package utils;


import java.io.File;

public class OnlyExtensionFilter {
    String extension;

    public OnlyExtensionFilter(String extension) {
        this.extension = "." + extension;
    }

    public boolean accept(File dir, String name) {
        return name.endsWith(extension);

    }
}
