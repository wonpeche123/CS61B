package gitlet;

import java.io.File;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    /*
     *   .gitlet
     *      |--objects
     *      |     |--commits
     *            |--blobs
     *      |--refs
     *      |    |--heads
     *      |         |--master
     *      |--HEAD
     *      |--stage
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The refs directory. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The heads directory. */
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    /** The object directory. */
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    /** The object-commit directory. */
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commits");
    /** The object-blob directory. */
    public static final File BLOB_DIR = join(OBJECT_DIR, "blobs");
    /** 当前的HEAD指针指向的Commit */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /** 暂存区 */
    public static final File STAGE = join(GITLET_DIR, "stage");

    public static Commit currCommit;

    /** init */
    public void init () {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        initCommit();
        initHeads();
        initHEAD();
        initStage();
    }

    private void initCommit() {
        Commit initial_commit  = new Commit();
        currCommit = initial_commit;
        initial_commit.save();
    }

    private void initHeads() {
        File headOfBranch = join(HEADS_DIR, "master");
        writeObject(headOfBranch, currCommit.getid());
    }

    private void initHEAD() {
        writeObject(HEAD, HEADS_DIR.toString() + "/master");
    }

//    initial STAGE with two empty HashMap
    private void initStage() {
        Stage stage = new Stage(STAGE);
        stage.save();
    }


    public void add(String filename) {
        File absFilePath = join(CWD, filename);
        String absFilePathString = absFilePath.getAbsolutePath();
        if (!absFilePath.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String id;
//      generate a new blob when add and get its id
        id = generateNewBlob(absFilePath);
//      modify stage area
        Stage stage = Stage.fromFile();
//      存在则覆盖，不存在就创建
        stage.addToAddition(absFilePathString, id);
//      new add is same with commit's blob
        if (isSameWithCurCommit(absFilePathString, id)) {
            if (stage.isInAddition(absFilePathString)) {
                stage.removeFromAddition(absFilePathString);
            }
            if (stage.isInRemoval(absFilePathString)) {
                stage.removeFromRemoval(absFilePathString);
            }
        }
    }

    /** generate a new blob when add and get its id. */
    private String generateNewBlob(File file) {
        String id;
        Blob newBlob = new Blob(file, BLOB_DIR);
        id = Blob.getId(newBlob);
        Blob.save(newBlob);
        return id;
    }

    /** new add is same with commit's blob. */
    private boolean isSameWithCurCommit(String absFilePath, String blobId) {
        return currCommit.isSameWithCommit(absFilePath, blobId);
    }


}
