package shared;

import java.io.File;
import java.io.Serializable;

// This class is inside package 'shared', because it is shared with the server
// For java to serialize it, it needs the same package name and class name on both ends.
public class VideoFile implements Serializable {
    private File file;
    private String name;
    private String format;
    private int width;
    private int height;

    public VideoFile(File file, String name, String format, int width, int height) {
        this.file = file;
        this.name = name;
        this.format = format;
        this.width = width;
        this.height = height;
    }

    public VideoFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\t\t"
                + "Format: " + format + "\t\t"
                + "Resolution: " + width + 'x' + height;
    }
}
