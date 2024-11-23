package gitlet;


import java.io.File;

import static gitlet.Utils.readObject;

public class FromStage {
    public static final File STAGE = new File("/Users/a1-6/Documents/csdiy/cs61b/proj2/testing/test25-successful-find_4/.gitlet/stage");

    public static void main(String[] args) {
        Stage stage = readObject(STAGE, Stage.class);
        System.out.println("Begin:");
        for (String key : stage.getAddition().keySet()) {
            System.out.println(key);
        }
    }
}
