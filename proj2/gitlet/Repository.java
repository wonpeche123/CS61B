package gitlet;

import java.io.File;
import java.util.*;

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

    /** generate a new blob when add and get its id. 并不会相同blob重复，因为save方法只会指定同一个文件*/
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
        File newHead = join(HEADS_DIR, branchName);
//      检查是否存在对应分支
        if (!branchIsExist(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
//      检查是否是当前分支
        if (branchIsSame(branchName)) {
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
    /** 检查是否存在对应分支,不负责报错*/
    private static boolean branchIsExist(String branchName) {
        List<String> branchNames = plainFilenamesIn(HEADS_DIR);
        if (branchNames == null) return false;
        return branchNames.contains(branchName);
    }
    /** 检查是否是当前分支，不负责报错*/
    private static boolean branchIsSame(String branchName) {
        String currHeadName = currHead.getName();
        return branchName.equals(currHeadName);
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
    /** 检查是否有未被追踪(即不被commit或addition记录的文件)的文件后再删除CWD所有文件，防止彻底失去，并报错*/
    private static void deleteCheckUntracked(Commit currCommit) {
        if (checkUntracked(currCommit)) {
            File[] files = CWD.listFiles(); // 获取CWD文件夹中的所有文件
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
    /** 检查是否有未被追踪(即不被commit或addition记录的文件)的文件，防止彻底失去，并报错*/
    private static boolean checkUntracked(Commit currCommit) {
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
            }
        }
        return true;
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
        if (branchIsExist(newBranchName)) {
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
        //待删除的分支名称不存在
        if (!branchIsExist(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        //待删除的分支为当前所在分支
        if (branchIsSame(branchName)) {
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
    /** merge commit_2 to commit_1*/
    public static void merge(String branchName) {
        resumeEnv();
        //检查是否存在对应分支
        if(!branchIsExist(branchName)) {
            System.out.println("A branch with that name does not exist. ");
            System.exit(0);
        }
        //检查是否是当前分支
        if (branchIsSame(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        //检查stage区是否有未提交文件
        Stage stage = Stage.fromFile();
        if (!stage.empty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        //检查CWD是否有未被追踪文件,不能清空CWD开始merge
        checkUntracked(currCommit);

        Commit commit_1 = currCommit;
        File mergeBranch = join(HEADS_DIR, branchName);
        String commitName_2 = readContentsAsString(mergeBranch);
        Commit commit_2 = getCommit(commitName_2);

        Commit splitPoint = findSplitPoint(commit_1, commit_2);
        //被合并的分支落后或等长
        if (splitPoint.isSameCommit(commit_2)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        //被合并的分支领先，快速移动并修改当前分支head指向的commit
        if (splitPoint.isSameCommit(commit_1)) {
            String newHeadId = commit_2.getId();
            writeContents(currHead, newHeadId);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        //两条分支不共线
        //commit_1 发生变化的文件
        HashMap<String, Integer> changedFiles_1 = branchChanged(splitPoint, commit_1);
        //commit_2 发生变化的文件
        HashMap<String, Integer> changedFiles_2 = branchChanged(splitPoint, commit_2);
        Set<String> allFiles = getAllFiles(splitPoint, commit_1, commit_2);

        for (String fileString : allFiles) {
            // case 0: file not changed in both commit_2 and commit_1，不用操作，此时CWD存在该文件最终副本
            // case 1: file changed in commit_2 not in commit_1
            if (!changedFiles_1.containsKey(fileString) && changedFiles_2.containsKey(fileString)) {
                int flag = changedFiles_2.get(fileString);
                onlyChanged(fileString, flag, commit_2, 2);
            }
            // case 2: file changed in commit_1 not in commit_2
            if (changedFiles_1.containsKey(fileString) && !changedFiles_2.containsKey(fileString)) {
                int flag = changedFiles_1.get(fileString);
                onlyChanged(fileString, flag, commit_1, 1);
            }
            // case 3: file changed the same both in commit_1 and in commit_2
            // case 4: file changed differently in commit_1 and in commit_2
            if (changedFiles_1.containsKey(fileString) && changedFiles_2.containsKey(fileString)) {
                int flag_1 = changedFiles_1.get(fileString);
                int flag_2 = changedFiles_2.get(fileString);
                bothChanged(fileString, flag_1, flag_2, commit_1, commit_2);
            }
        }
        String curBranchName = currHead.getName();
        commit("Merged " + branchName + " into " + curBranchName + ".");
    }

    /** 找到分裂点，只沿着第一个父结点(对于merge后的再次merge找到的分裂点不是最近的)*/
    private static Commit findSplitPoint(Commit commit_1, Commit commit_2) {

        LinkedList<Commit> branch_1 = new LinkedList<>();
        LinkedList<Commit> branch_2 = new LinkedList<>();

        Commit pointer_1 = commit_1;
        Commit pointer_2 = commit_2;
        while (pointer_1 != null) {
            branch_1.addFirst(pointer_1);
            pointer_1 = pointer_1.getParent_1();
        }
        while (pointer_2 != null) {
            branch_2.addFirst(pointer_2);
            pointer_2 = pointer_2.getParent_1();
        }
        int length_1 = branch_1.size();
        int length_2 =branch_2.size();
        int i;
        for(i = 0; i < length_1 && i < length_2; i++) {
            //  第一个不同的就是split point
            if (!branch_1.get(i).isSameCommit(branch_2.get(i))) {
                return branch_1.get(i-1);
            }
        }
        if (length_1 >= length_2) {
            return branch_2.get(i-1);
        }else return branch_1.get(i-1);
    }
    /** 寻找某分支中相对于分裂点变化的blobs,用一个HashMap记录变化的文件（绝对路径），对应的value为变化的类型*/
    private static HashMap<String, Integer> branchChanged(Commit splitPoint, Commit targetCommit) {
        HashMap<String, Integer> changedFiles = new HashMap<>();
        HashMap<String, String> splitPointFiles = splitPoint.getBlobMap();
        HashMap<String, String> branchFiles = targetCommit.getBlobMap();
        //先找分支中发生修改和增加的文件
        for (String branchFile : branchFiles.keySet()) {
            if (splitPointFiles.containsKey(branchFile)) {
                String splitFileId = splitPointFiles.get(branchFile);
                String branchFileId = branchFiles.get(branchFile);
                //文件内容发生了修改（记为2）
                if (!branchFileId.equals(splitFileId)) {
                    changedFiles.put(branchFile, 2);
                }
            }else {
                //新增的文件（记为1）
                changedFiles.put(branchFile, 1);
            }
        }
        //先找分支中删除了的文件
        for (String splitPointFile : splitPointFiles.keySet()) {
            if (!branchFiles.containsKey(splitPointFile)) {
                //删除的文件（记为0）
                changedFiles.put(splitPointFile, 0);
            }
        }
        return changedFiles;
    }
    /** 返回三个commit中所有文件的集合*/
    private static Set<String> getAllFiles(Commit splitPoint, Commit commit_1, Commit commit_2) {
        Set<String> allFiles = new HashSet<>();
        Set<String> splitPointFiles = splitPoint.getBlobMap().keySet();
        Set<String> commit_1_Files = commit_1.getBlobMap().keySet();
        Set<String> commit_2_Files = commit_2.getBlobMap().keySet();

        allFiles.addAll(splitPointFiles);
        allFiles.addAll(commit_1_Files);
        allFiles.addAll(commit_2_Files);

        return allFiles;
    }
    /** 只在一个commit改动过的文件*/
    private static void onlyChanged(String fileString, int flag, Commit commit,int commitNumber) {
        // 只要文件的名字，作为add等操作的参数
        File file = new File(fileString);
        String filename = file.getName();
        // 删除的文件，如果是主分支无操作（因为已经删除了），如果是被合并分支，直接执行rm命令删除
        if (flag == 0) {
            if (commitNumber == 2) {
                //  rm会报错 rm(filename),因此直接使用rm的一部分代码
                Stage stage = Stage.fromFile();
                file.delete();
                stage.addToRemoval(fileString);
                stage.save();
            }
        }else {     // 其他的文件修改后，使用add命令加入stage区
            String content = commit.getBlobContent(fileString);
            File currfile = new File(fileString);
            writeContents(currfile, content);
            add(filename);
        }
    }
    /**在两个commit中都属于改动过的文件,格式如下：
     <<<<<<< HEAD
     contents of file in current branch
     =======
     contents of file in given branch
     >>>>>>>
     */
    private static void bothChanged(String fileString, int flag_1, int flag_2, Commit commit_1, Commit commit_2) {
        File currfile = new File(fileString);
        // 只要文件的名字，作为add等操作的参数
        String filename = currfile.getName();
        // (0,0):同时删除的文件，不用操作，因为此时CWD并没有该文件
        if (flag_1 == 0 && flag_2 == 2) {
            //不可能存在(0,1) or (1,0)
            //(0,2):current 删除，given修改
            String content_2 = commit_2.getBlobContent(fileString);
            conflictFileContent(currfile, "", content_2);
            add(filename);
        }else if (flag_1 == 2 && flag_2 == 0) {
            //(2,0): given删除，current修改
            String content_1 = commit_1.getBlobContent(fileString);
            conflictFileContent(currfile, content_1, "");
            add(filename);
        }else if (flag_1 == 1 && flag_2 == 1) {
            //(1,1): given新增，current新增
            bothAddOrChange(fileString, commit_1, commit_2);
            add(filename);
        }else if (flag_1 == 2 && flag_2 == 2) {
            //不可能存在(1,2) or (2,1)
            //(2,2): given修改，current修改
            bothAddOrChange(fileString, commit_1, commit_2);
            add(filename);
        }
    }
    /** 文件同时新增或修改，修改为最终的文本*/
    private static void bothAddOrChange(String fileString, Commit commit_1, Commit commit_2) {
        File currfile = new File(fileString);
        String commit_1_BlobId = commit_1.getBlobMap().get(fileString);
        String commit_2_BlobId = commit_1.getBlobMap().get(fileString);
        //同时新增或修改，但是内容相同
        if (commit_1_BlobId.equals(commit_2_BlobId)) {
            String content = commit_1.getBlobContent(fileString);
            writeContents(currfile, content);
        }else { //新增或修改，但是内容不同
            String content_1 = commit_1.getBlobContent(fileString);
            String content_2 = commit_2.getBlobContent(fileString);
            conflictFileContent(currfile, content_1, content_2);
        }
    }
    /** 处理冲突文件文本内容*/
    private static void conflictFileContent(File file, String content_1, String content_2) {
        System.out.println("Encountered a merge conflict.");
        String content = "<<<<<<< HEAD\n" + content_1 + "\n=======\n" + content_2 + "\n>>>>>>>";
        writeContents(file, content);
    }

}




