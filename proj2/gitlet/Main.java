package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 * @author winter
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.print("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                checkArguments(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                checkArguments(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                checkArguments(args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                checkArguments(args, 1);
                Repository.log();
                break;
            case "global-log":
                checkArguments(args, 1);
                Repository.globalLog();
                break;
            case "find":
                checkArguments(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                checkArguments(args, 1);
                Repository.status();
                break;
            case "checkout":
                argumentsAtLeast(args, 2);
                Repository.checkout(args);
                break;
            case "branch":
                checkArguments(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                checkArguments(args, 2);
                Repository.removeBranch(args[1]);
                break;
            case "reset":
                checkArguments(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                checkArguments(args, 2);
                Repository.merge(args[1]);
                break;
            default:
                System.out.print("No command with that name exists.");
        }
    }

    private static void checkArguments(String[] args, int required) {
        if (args.length != required) {
            if (args[0].equals("commit") && (args.length == 1) || args[1].equals("")) {
                System.out.print("Please enter a commit message.");
                System.exit(0);
            } else {
                System.out.print("Incorrect operands.");
                System.exit(0);
            }
        }
        if (!Repository.inGit()) {
            System.out.print("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void argumentsAtLeast(String[] args, int required) {
        if (args.length < required) {
            System.out.print("Incorrect operands.");
            System.exit(0);
        }
        if (!Repository.inGit()) {
            System.out.print("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
