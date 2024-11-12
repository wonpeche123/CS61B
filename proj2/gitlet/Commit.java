package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Locale;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
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
    /** The parent_1 of this Commit. */
    private Commit parent_1;
    /** The parent_2 of this Commit. */
    private Commit parent_2;
    /** The blobs of this Commit. */
    private ArrayList<Node> blobs;
    /** The hashcode of this Commit. */
    private String id;

    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    /** A Node is a blob. */
    private class Node {
        public String filename;
        public String sha1Hash;

        public Node(String filename ,String hashcode) {
            this.filename = filename;
            this.sha1Hash = hashcode;
        }
    }


    /* TODO: fill in the rest of this class. */

    public Commit() {

        message = "initial commit";
        time = new Date(0);
        timestamp = timeToTimeStamp(time);
        parent_1 = null;
        parent_2 = null;
        blobs = new ArrayList<>();
//  只针对初始化时没有parents指针的情况，这种特殊的对应也能保证hash的区分度
        id = sha1(message, timestamp, "null", "null", blobs.toString());
    }

    public Commit(String message, Commit parent_1, Commit parent_2, ArrayList<Node> blobs) {
        this.message = message;
        time = new Date(0);
        timestamp = timeToTimeStamp(time);
        this.parent_1 = parent_1;
        this.parent_2 = parent_2;
        this.blobs = blobs;
        id = sha1(message, timestamp, parent_1.toString(), parent_2.toString(), blobs.toString());
    }

    /** 将UNIX时间转化成特定的时间戳格式*/
    private static String timeToTimeStamp(Date time) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(time);
    }
    /** Commit Methods*/

    /** 获得当前Commit的hashcode*/
    public String getid() {
        return id;
    }

    public void save() {
        File COMMIT_FILE = join(GITLET_DIR, "object", id);
        writeObject(COMMIT_FILE, this);
    }

}
