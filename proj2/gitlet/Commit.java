package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import static gitlet.Utils.*;
import static gitlet.Repository.COMMIT_DIR;
import static gitlet.Repository.BLOB_DIR;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author wonpeche
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The timestamp of this Commit. */
    private String timestamp;
    /** The Unix time. */
    private Date time;
    /** The parent1 of this Commit. */
    private String parent1;
    /** The parent2 of this Commit. */
    private String parent2;
    /** The blobs of this Commit. */
    private HashMap<String, String> blobMap;
    /** The hashcode of this Commit. */
    private String id;

    /** The .gitlet directory. */



    public Commit() {

        message = "initial commit";
        time = new Date(0);
        timestamp = timeToTimeStamp(time);
        parent1 = null;
        parent2 = null;
        blobMap = new HashMap<>();
//  只针对初始化时没有parents指针的情况，这种特殊的对应也能保证hash的区分度
        id = sha1(message, timestamp, "null", "null", blobMap.toString());
    }

    public Commit(String message, String parent1, String parent2, HashMap<String, String> blobs) {
        this.message = message;
        time = new Date();
        timestamp = timeToTimeStamp(time);
        this.parent1 = parent1;
        this.parent2 = parent2;

        String p1, p2;
        if (parent1 == null) {
            p1 = "null";
        } else {
            p1 = parent1;
        }
        if (parent2 == null) {
            p2 = "null";
        } else {
            p2 = parent2;
        }

        this.blobMap = blobs;
        id = sha1(message, timestamp, p1, p2, blobMap.toString());
    }

    /** 将UNIX时间转化成特定的时间戳格式*/
    private static String timeToTimeStamp(Date time) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(time);
    }
    /** Commit Methods*/

    /** 获得当前Commit的message*/
    public String getMessage() {
        return message;
    }

    /** 获得当前Commit的timestamp*/
    public String getTimestamp() {
        return timestamp;
    }

    /** 获得当前Commit的parent1的绝对路径*/
    public String getParent1() {
        return parent1;
    }

    /** 获得当前Commit的parent2的绝对路径*/
    public String getParent2() {
        return parent2;
    }

    /** 获得当前Commit的blobMap*/
    public HashMap<String, String> getBlobMap() {
        return blobMap;
    }

    /** 获得当前Commit的hashcode*/
    public String getId() {
        return id;
    }

    /** 是否存在对应路径blob*/
    public boolean containBlob(String absFilePath) {
        String targetBlobId = blobMap.get(absFilePath);
        return targetBlobId != null;
    }

    /** 获得对应路径blob的文本内容*/
    public String getBlobContent(String absFilePath) {
        String targetBlobId = blobMap.get(absFilePath);
        File tagetFile = join(BLOB_DIR, targetBlobId);
        return readContentsAsString(tagetFile);
    }

    public void save() {
        File COMMIT_FILE = join(COMMIT_DIR, id);
        writeObject(COMMIT_FILE, this);
    }

    public boolean isSameWithCommit(String absFilePath, String blobId) {
        String value = blobMap.get(absFilePath);
        return (value != null) && value.equals(blobId);
    }

    public boolean isSameCommit(Commit target) {
        String id1 = id;
        String id2 = target.getId();
        return id1.equals(id2);
    }
}
