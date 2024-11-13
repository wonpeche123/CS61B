package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String content;
    private String filename;
    private String id;
    private File saveblobpath;

//    以下方法仅为add时新创建的blob对象提供方便，一旦序列化后blob文件就不返回blob对象
    public Blob(File file, File BLOB_DIR) {
        filename = file.getName();
        content = readContentsAsString(file);
        id = sha1(content);
        saveblobpath = join(BLOB_DIR, id);
    }

    public static String getId(Blob blob) {
        return blob.id;
    }

    public static String getName(Blob blob) {
        return blob.filename;
    }
//    only record content
    public static void save(Blob blob) {
        writeObject(blob.saveblobpath, blob.content);
    }
}
