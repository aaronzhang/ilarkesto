package ilarkesto.form;

import ilarkesto.base.Bytes;
import ilarkesto.id.CountingIdGenerator;
import ilarkesto.id.IdGenerator;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

public class UploadFormField extends AFormField {

    private File file;
    private Integer maxFilesize = new Integer(10000000);

    public UploadFormField(String name) {
        super(name);
        setRequired(true);
    }

    public String getValueAsString() {
        return file == null ? null : file.getName();
    }

    private boolean maxFileSizeExceeded;

    public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {
        maxFileSizeExceeded = false;
        for (FileItem item : uploadedFiles) {
            if (item.getFieldName().equals(getName())) {
                if (item.getSize() == 0) {
                    file = null;
                    return;
                }
                if (maxFilesize != null && item.getSize() > maxFilesize) {
                    maxFileSizeExceeded = true;
                    return;
                }
                file = new File(applicationTempDir + "/uploadedFiles/" + folderIdGenerator.generateId() + "/"
                        + item.getName());
                file.getParentFile().mkdirs();
                try {
                    item.write(file);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public File getValue() {
        return file;
    }

    public void validate() throws ValidationException {
        if (maxFileSizeExceeded) { throw new ValidationException(
                "Die Datei ist zu gro\u00DF. Maximale Dateigr\u00F6\u00DFe: "
                        + new Bytes(maxFilesize).toRoundedString()); }
        if (file == null && isRequired()) { throw new ValidationException("Eingabe erforderlich."); }
    }

    public Integer getMaxFilesize() {
        return maxFilesize;
    }

    public UploadFormField setMaxFilesize(Integer maxFilesize) {
        this.maxFilesize = maxFilesize;
        return this;
    }

    // --- dependencies ---

    private static IdGenerator folderIdGenerator = new CountingIdGenerator(UploadFormField.class.getSimpleName());

    private String applicationTempDir = "";

    public void setApplicationTempDir(String applicationTempDir) {
        this.applicationTempDir = applicationTempDir;
    }

}
