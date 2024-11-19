package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    public static File currHead;

    /** init */
    public static void init () {
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

    private static void initCommit() {
        Commit initial_commit  = new Commit();
        currCommit = initial_commit;
        initial_commit.save();
    }

    private static void initHeads() {
        File headOfBranch = join(HEADS_DIR, "master");
        writeContents(headOfBranch, currCommit.getId());
    }

    private static void initHEAD() {
        File HEADPath = join(HEADS_DIR, "master");
        writeObject(HEAD, HEADPath);
    }

//    initial STAGE with two empty HashMap
    private static void initStage() {
        Stage stage = new Stage();
        stage.save();
    }

    public static void checkIfInit() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

    }

//  ########## add ############
    public static void add(String filename) {
        resumeEnv();
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
        if (isSameWithCurrCommit(absFilePathString, id)) {
            if (stage.isInAddition(absFilePathString)) {
                stage.removeFromAddition(absFilePathString);
            }
            if (stage.isInRemoval(absFilePathString)) {
                stage.removeFromRemoval(absFilePathString);
            }
        }
        stage.save();
    }

    /** generate a new blob when add and get its id. */
    private static String generateNewBlob(File file) {
        String id;
        Blob newBlob = new Blob(file, BLOB_DIR);
        id = Blob.getId(newBlob);
        Blob.save(newBlob);
        return id;
    }

    /** new add is same with commit's blob. */
    private static boolean isSameWithCurrCommit(String absFilePath, String blobId) {
        return currCommit.isSameWithCommit(absFilePath, blobId);
    }
//  ########## add ############

//  ########## commit ############
    public static void commit(String message) {
        resumeEnv();
        Stage stage = Stage.fromFile();
//        commit failures : stage not empty and message not blank
        if (stage.empty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
//        TODO：未考虑merge
//        替换add stage内信息
        HashMap<String, String> addition = stage.getAddition();
        HashMap<String, String> removal = stage.getRemoval();
        //        继承父的blobs
        HashMap<String, String> newBlobMap = currCommit.getBlobMap();
//      addition
        newBlobMap.putAll(addition);
//      remove
        for (String key : removal.keySet()) {
            newBlobMap.remove(key);
        }
//      clear STAGE
        stage = new Stage();
        stage.save();
//      后续目录文件信息处理
        Commit newCommit = new Commit(message, currCommit, null, newBlobMap);
        newCommit.save();
        currCommit = newCommit;
        writeContents(currHead, currCommit.getId());
    }

    /** 每次新输入命令后，恢复currCommit和currHead变量方便使用*/
    private static void resumeEnv() {
        currHead = readObject(HEAD, File.class);
        String currCommitName = readContentsAsString(currHead);
        File currCommitPath = join(COMMIT_DIR, currCommitName);
        currCommit = readObject(currCommitPath, Commit.class);
    }
//  ########## commit ############

//  ########## rm ############
    public static void rm(String filename) {
        resumeEnv();
        File absFilePath = join(CWD, filename);
        String absFilePathString = absFilePath.getAbsolutePath();
        Stage stage = Stage.fromFile();
        if (!currCommit.getBlobMap().containsKey(absFilePathString) && !stage.isInAddition(absFilePathString)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        //      清理addition或者删除文件（本来未被追踪则不删）加入removal
        if (stage.isInAddition(absFilePathString)) {
            stage.removeFromAddition(absFilePathString);
        }else {
            absFilePath.delete();
            stage.addToRemoval(absFilePathString);
        }
        stage.save();
    }
//  ########## rm ############

//  ########## log ############
    /** 打印当前分支所有Commit的相关信息*/
    public static void log() {
        resumeEnv();
//        未处理merge结点
        Commit pointer = currCommit;
        while (pointer != null) {
            logPrintCommit(pointer);
            pointer = pointer.getParent_1();
        }
    }
//  ########## log ############

//  ########## global_log ############
    /** 打印所有Commit的相关信息*/
    public static void global_log() {
        resumeEnv();
//        未处理merge结点
//        获取整个commits文件夹所有commit的文件名，并保存为一个List
        List<String> fileNames = plainFilenamesIn(COMMIT_DIR);
        if (fileNames != null) {
//        分别读出每个commit并打印
            for (String filename : fileNames) {
                File pointer = join(COMMIT_DIR, filename);
                Commit pointerCommit = readObject(pointer, Commit.class);
                logPrintCommit(pointerCommit);
            }
        }
    }

    /** 打印一个Commit的相关信息*/
    private static void logPrintCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getId());
        System.out.println("Date: " + commit.getTimestamp());
        System.out.println(commit.getMessage());
        System.out.println(" ");
    }
//  ########## global_log ############

//  ########## find ############
    /** 打印对应message的所有Commit的id*/
    public static void find(String message) {
        resumeEnv();
//        获取整个commits文件夹所有commit的文件名，并保存为一个List
        boolean flag = false;
        List<String> fileNames = plainFilenamesIn(COMMIT_DIR);
        if (fileNames != null) {
//        分别读出每个commit并打印
            for (String filename : fileNames) {
                File pointer = join(COMMIT_DIR, filename);
                Commit pointerCommit = readObject(pointer, Commit.class);
                if (pointerCommit.getMessage().equals(message)) {
                    System.out.println(pointerCommit.getId());
                    flag = true;
                }
            }
            if (!flag) {
                System.out.println("Found no commit with that message.");
                System.exit(0);
            }
        }
    }
//  ########## find ############

//  ########## status ############
    /** 打印当前所有状态信息*/
    public static void status() {
        resumeEnv();
        printBranches();
        printStage();
        printModiNotStage();
        printUntracked();
    }

    /** 打印当前分支信息*/
    private static void printBranches() {
        System.out.println("=== Branches ===");
        List<String> branchNames = plainFilenamesIn(HEADS_DIR);
        if (branchNames != null) {
//        分别打印每个分支名
            String currHeadString = currHead.getAbsolutePath();
            for (String branchName : branchNames) {
                File pointer = join(HEADS_DIR, branchName);
                String pointerString = pointer.getAbsolutePath();
                if (pointerString.equals(currHeadString)) {
                    System.out.println("*" + branchName);
                }else {
                    System.out.println(branchName);
                }
            }
        }
        System.out.println(" ");
    }

    /** 打印当前暂存区信息*/
    private static void printStage() {
        System.out.println("=== Staged Files ===");
        Stage stage = Stage.fromFile();
        for (String key : stage.getAddition().keySet()) {
            //绝对路径-->相对路径
            String filename = new File(key).getName();
            System.out.println(filename);
        }
        System.out.println(" ");
        System.out.println("=== Removed Files ===");
        for (String key : stage.getRemoval().keySet()) {
            //绝对路径-->相对路径
            String filename = new File(key).getName();
            System.out.println(filename);
        }
        System.out.println(" ");
    }

    /** 打印当前修改未被stage记录信息*/
    private static void printModiNotStage() {
        // TODO:
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println(" ");
    }

    /** 打印当前未被追踪文件信息*/
    private static void printUntracked() {
        // TODO:
        System.out.println("=== Untracked Files ===");
        System.out.println(" ");
    }
//  ########## status ############

//  ########## checkout ############
    /** checkout*/
    public static void checkout(String[] args) {
        resumeEnv();
//      判断命令种类
        int type = checkType(args);
        if (type == 1) {
            checkoutFile(args[2]);
        }else if (type ==2) {
            checkoutCommitFile(args[1], args[3]);
        }else {
            checkoutBranch(args[1]);
        }
    }

    private static int checkType(String[] args) {
//      checkout [branch]
        if (args.length == 2) {
            return 3;
        }
//      checkout -- [filename]
        if (args.length == 3) {
            return  1;
        }
//      checkout [commitid] -- [filename]
        if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            return 2;
        }
        return 0;
    }

    /** checkout -- [filename]*/
    private static void checkoutFile(String filename) {
        File file = join(CWD, filename);
        String absFilePath = file.getAbsolutePath();
//      若文件在commit存在则获取其id去寻找blobs中文件内容；若不存在，则报错
        if (!currCommit.containBlob(absFilePath)) {
            fileNotExistError();
        }
        String targetContent = currCommit.getBlobContent(absFilePath);
        writeContents(file, targetContent);
    }

    /** checkout [commit] -- [filename]*/
    private static void checkoutCommitFile(String commitId, String filename) {
//      还原待寻找file的绝对路径作为寻找的名称
        File file = join(CWD, filename);
        String absFilePath = file.getAbsolutePath();
//      找对应commit
        Commit targetCommit = getCommit(commitId);
//      找到对应文件内容
        if (!targetCommit.containBlob(absFilePath)) {
            fileNotExistError();
        }
        String targetContent = targetCommit.getBlobContent(absFilePath);
        writeContents(file, targetContent);
    }

    /** checkout [branch]*/
    private static void checkoutBranch(String branchName) {
        List<String> branchNames = plainFilenamesIn(HEADS_DIR);
        File newHead = join(HEADS_DIR, branchName);
        String newHeadString = newHead.getAbsolutePath();
        String currHeadString = currHead.getAbsolutePath();

//      检查是否存在对应分支
        if (!branchNames.contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
//      检查是否是当前分支
        if (newHeadString.equals(currHeadString)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
//      切换分支对应commit
        String newCommitId = readContentsAsString(newHead);
        Commit newCommit = getCommit(newCommitId);
        checkoutCommit(newCommit);
//      清除Stage区
        Stage stage = new Stage();
        stage.save();
//      更改HEAD文件内容
        writeObject(HEAD, newHead);
    }

    /** 文件不存在报错*/
    private static void fileNotExistError() {
        System.out.println("File does not exist in that commit.");
        System.exit(0);
    }
    /** 从commits文件夹中寻找特定文件名的commit*/
    private static Commit getCommit(String commitId) {
        List<String> commitNames = plainFilenamesIn(COMMIT_DIR);
//      不存在对应id的commit
        if (!commitNames.contains(commitId)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
//      存在后由路径直接定位到文件
        File targetCommitFile = join(COMMIT_DIR, commitId);
        Commit targetCommit = readObject(targetCommitFile, Commit.class);
        return  targetCommit;
    }
    /** 删除CWD所有文件并检查是否有未被追踪(即不被commit或addition记录的文件)的文件，防止彻底失去，并报错*/
    private static void deleteCheckUntracked(Commit currCommit) {
        Set<String> commitFilenames = currCommit.getBlobMap().keySet();
        List<String> currFilenames = plainFilenamesIn(CWD);

        Stage stage = Stage.fromFile();
        Set<String> additionSet = stage.getAddition().keySet();

        if (currFilenames != null) {
            for(String filename : currFilenames) {
                File file = join(CWD, filename);
                String AbsFilename = file.getAbsolutePath();
                if (!commitFilenames.contains(AbsFilename) && !additionSet.contains(AbsFilename)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
                file.delete();
            }
        }
    }

    /** 根据一个文件的绝对路径恢复一个文件*/
    private static void resumeFile(Commit newCommit, String absFilePath) {
//      文件内容
        String blobContent = newCommit.getBlobContent(absFilePath);
//      文件名
        String filename = new File(absFilePath).getName();
//      恢复文件
        File newfile = join(CWD,filename);
        writeContents(newfile, blobContent);
    }
    /** 恢复新commit的所有文件到CWD*/
    private static void resumeCWD(Commit newCommit) {
        Set<String> commitFilenames = newCommit.getBlobMap().keySet();
        for (String AbsFilename : commitFilenames) {
            resumeFile(newCommit, AbsFilename);
        }
    }
    /** 切换到对应commit*/
    private static void checkoutCommit(Commit newCommit) {
        //  清除并检查
        deleteCheckUntracked(currCommit);
        //  添加
        resumeCWD(newCommit);
    }
//  ########## checkout ############

//  ########## branch ############
    public static void branch(String newBranchName) {
        resumeEnv();
        List<String> branchNames = plainFilenamesIn(HEADS_DIR);
        if (branchNames.contains(newBranchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        File newBranch = join(HEADS_DIR, newBranchName);
        String currCommitId = currCommit.getId();
        writeContents(newBranch, currCommitId);
    }
//  ########## branch ############

//  ########## rm-branch ############
    public static void rm_branch(String branchName) {
        resumeEnv();
        List<String> branchNames = plainFilenamesIn(HEADS_DIR);
        //待删除的分支名称不存在
        if (branchNames == null || !branchNames.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        //待删除的分支为当前所在分支
        String currBranchName = currHead.getName();
        if (branchName.equals(currBranchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        //删除HEADS中对应文件
        File branch = join(HEADS_DIR, branchName);
        branch.delete();
    }
//  ########## rm-branch ############

//  ########## reset ############
    public static void reset(String commitId) {
        resumeEnv();
        Commit newHead = getCommit(commitId);
        checkoutCommit(newHead);
        //      清除Stage区
        Stage stage = new Stage();
        stage.save();
        //      更改HEADS下当前分支头的文件内容，由于分支并没有切换，HEAD不变
        writeContents(currHead, commitId);
    }
//  ########## reset ############

//  ########## merge ############
    public static void merge(String branchName) {
//        TODO:
    }
}


