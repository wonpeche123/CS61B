package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import static gitlet.Utils.*;
import static gitlet.Repository.STAGE;

public class Stage implements Serializable {
//    HashMap<path, SHA1>
    private HashMap<String, String> addition;
    private HashMap<String, String> removal;
    private static final File StagePath = STAGE;

    public Stage() {
        addition = new HashMap<>();
        removal = new HashMap<>();
    }

    public void save() {
        writeObject(StagePath, this);
    }

    public boolean empty() {
        return addition.isEmpty() && removal.isEmpty();
    }

    public static Stage fromFile() {
        return readObject(StagePath, Stage.class);
    }

    public boolean isInAddition(String absPath) {
        return addition.containsKey(absPath);
    }

    public boolean isInRemoval(String absPath) {
        return removal.containsKey(absPath);
    }

    public void addToAddition(String absPath, String SHA1) {
        addition.put(absPath, SHA1);
    }

    public void addToRemoval(String absPath) {
        removal.put(absPath, "0");
    }

    public void removeFromAddition(String absPath){
        addition.remove(absPath);
    }

    public void removeFromRemoval(String absPath){
        removal.remove(absPath);
    }

    public HashMap<String, String> getAddition() {
        return addition;
    }

    public HashMap<String, String> getRemoval() {
        return removal;
    }

}
