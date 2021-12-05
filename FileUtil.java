import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Prakhar Mittal
 * @version 2.0
 * This class contains methods to facilitate saving a List of StartUpIdea's to a file
 */
public class FileUtil {

    /**
     * Traverses through the list of StartUpIdeas and saves each idea to the specified file
     * @param ideas list of StartUpIdeas
     * @param file file object
     * @return true if successful, false if unsuccessful
     */
    public static boolean saveIdeasToFile(List<StartUpIdea> ideas, File file) {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return false;
        }
        for (int i = 0; i < ideas.size(); i++) {
            printWriter.println((i + 1) + ":");
            printWriter.println(ideas.get(i).toFullString());
        }
        printWriter.close();
        return true;
    }
}
