package gitlet;

import static gitlet.Utils.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Commit extends FileTracker {

    private final String message;
    private final String time;
    private final String parent;
    private final String author = "Winter";

    Commit(String message, String parent, Map<String, String> trackedFiles) {
        super(trackedFiles);
        this.message = message;
        this.time = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z").format(new Date());
        this.parent = parent;
        this.trackedFiles = trackedFiles;
    }

    Commit(String message) {
        super();
        this.message = message;
        this.time = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z").format(new Date());
        parent = null;
    }

    Commit(Commit commit, String message) {
        super(commit.trackedFiles);
        this.message = message;
        this.time = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z").format(new Date());
        this.parent = commit.getSha1();
    }

    public String getMessage() {
        return this.message;
    }

    public String getTime() {
        return this.time;
    }

    public String getParent() {
        return this.parent;
    }

    public String getSha1() {
        return sha1(String.format("Message: %s Time: %s Author: %s\nParentSha1: %s\n",
                message, time, author, parent));
    }

    @Override
    public String toString() {
        String info = String.format("Message: %s Time: %s Author: %s\nParentSha1: %s \n"
                +
                "TrackedFiles: %s", message, time, author, parent, trackedFiles);
        return info;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Commit other = (Commit) o;
        return this.time.equals(other.time)
                &&
                this.message.equals(other.message)
                &&
                this.trackedFiles.equals(other.trackedFiles);
    }

    public void commit() {
        File commitFile = join(Repository.COMMITS, getSha1());
        createNewFile(commitFile);

        updateTrackFiles();
        writeObject(commitFile, this);
        setupHead();
        setupBranch(curBranch());
        //debugCommit();
    }

    public void initCommit() {
        File commit = join(Repository.COMMITS, getSha1());
        createNewFile(commit);

        writeObject(commit, this);
        setupHead();
        setupBranch("master");

        //debugCommit();
    }


    private void setupHead() {
        writeContents(Repository.HEAD, getSha1());
    }

    private void setupBranch(String branchName) {
        File branchFile = join(Repository.BRANCH, branchName);
        createNewFile(branchFile);
        writeContents(branchFile, getSha1());
        writeContents(Repository.CURRENT, branchName);
    }

    public static Commit getCommit(String sha1) {
        if (sha1 == null) {
            return null;
        }
        if (sha1.length() < 40) {
            List<String> commits = plainFilenamesIn(Repository.COMMITS);
            for (String commitId : commits) {
                if (commitId.startsWith(sha1)) {
                    sha1 = commitId;
                }
            }
        }
        File commit = join(Repository.COMMITS, sha1);
        if (!commit.exists()) {
            System.out.print("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(commit, Commit.class);
    }

    public static Commit getCurCommit() {
        String sha1 = readContentsAsString(Repository.HEAD);
        return getCommit(sha1);
    }

    /**
     * update tracked files according to addition stage
     */
    public void updateTrackFiles() {
        FileTracker addition = readObject(Repository.ADDITION, FileTracker.class);
        FileTracker removal = readObject(Repository.REMOVAL, FileTracker.class);
        //clear the adding stage
        writeObject(Repository.ADDITION, new FileTracker());
        writeObject(Repository.REMOVAL, new FileTracker());
        //update tracked files
        Map<String, String> toAdd = addition.trackedFiles;
        Map<String, String> toRemove = removal.trackedFiles;

        if (toAdd.size() == 0 && toRemove.size() == 0) {
            System.out.print("No changes added to the commit.");
            System.exit(0);
        }
        this.trackedFiles.putAll(toAdd);
        for (String fileToRemove : toRemove.keySet()) {
            this.trackedFiles.remove(fileToRemove);
        }
    }

    public static String curBranch() {
        String branch = readContentsAsString(Repository.CURRENT);
        return branch;
    }

    public void printCommit() {
        System.out.println("===");
        System.out.println("commit " + this.getSha1());
        System.out.println("Date: " + this.getTime());
        System.out.println(this.getMessage());
        System.out.println();
        //System.out.print("debug:\n");
        //debugCommit();
    }

    private void debugCommit() {
        System.out.println("-------------");
        System.out.print(this);
        System.out.print("\nMySha1: " + getSha1() + "\n");
    }

}
