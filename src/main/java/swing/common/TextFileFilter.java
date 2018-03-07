package swing.common;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.time.LocalDate;

public class TextFileFilter extends FileFilter {

    private String description = "Text File include URL(*.txt,*.md)";
    private String[] exts;
    private CharSequence[] containStrs;

    public TextFileFilter(String description,String[] extensions,CharSequence... searchCharSequences){
        this.description = description;
        exts = extensions;
        containStrs = searchCharSequences;
        if (ArrayUtils.isEmpty(containStrs)) {
            containStrs = new String[]{""};
        }
    }

    @Override
    public boolean accept(File f) {

        if (f.isDirectory()) {
            return true;
        }

        String name = f.getName();
        String extension = FilenameUtils.getExtension(name);

        if (StringUtils.equalsAnyIgnoreCase(extension, exts) && StringUtils.containsAny(name,containStrs)) {
            return true;
        }

        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public java.io.FileFilter getFileNameFilter(){
        java.io.FileFilter filter = pathname -> {

            if (pathname.isDirectory()) {
                return false;
            }

            String name = pathname.getName();
            String extension = FilenameUtils.getExtension(name);

            if (StringUtils.equalsAnyIgnoreCase(extension, exts) && StringUtils.containsAny(name,containStrs)) {
                if (!StringUtils.contains(name, LocalDate.now().toString())) {
                    return true;
                }
            }

            return false;
        };
        return filter;
    }
}
