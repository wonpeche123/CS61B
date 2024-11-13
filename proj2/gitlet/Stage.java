package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import static gitlet.Utils.*;


public class Stage implements Serializable {
//    HashMap<path, SHA1>
    private HashMap<String, String> addition;
    private HashMap<String, String> removal;
    private static File StagePath;

    public Stage(File path) {
        addition = new HashMap<>();
        removal = new HashMap<>();
        StagePath = path;
    }

    public void save() {
        writeObject(StagePath, this);
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

    public void addToRemoval(String absPath, String SHA1) {
        removal.put(absPath, SHA1);
    }

    public void removeFromAddition(String item){
        addition.remove(item);
    }

    public void removeFromRemoval(String item){
        removal.remove(item);
    }

}
