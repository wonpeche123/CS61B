package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // handle the `init` command
                validateNumArgs("init", args, 1);
                Repository.init();
                break;
            case "add":
                // handle the `add [filename]` command
                validateNumArgs("add", args, 2);
                Repository.checkIfInit();
                Repository.add(args[1]);
                break;
            case "commit":
                // handle the `commit [message]` command
                validateNumArgs("commit", args, 2);
                Repository.checkIfInit();
                Repository.commit(args[1]);
                break;
            case "rm":
                // handle the `rm [filename]` command
                validateNumArgs("rm", args, 2);
                Repository.checkIfInit();
                Repository.rm(args[1]);
                break;
            case "log":
                // handle the `rm [filename]` command
                validateNumArgs("log", args, 1);
                Repository.checkIfInit();
                Repository.log();
                break;
            case "global-log":
                // handle the `rm [filename]` command
                validateNumArgs("global-log", args, 1);
                Repository.checkIfInit();
                Repository.global_log();
                break;
            case "find":
                // handle the `find [commit message]` command
                validateNumArgs("find", args, 2);
                Repository.checkIfInit();
                Repository.find(args[1]);
                break;
            case "status":
                // handle the `status` command
                validateNumArgs("status", args, 1);
                Repository.checkIfInit();
                Repository.status();
                break;
            case "checkout":
                // handle the `checkout` command
                if (args.length <= 1 && args.length >= 5) {
                    throw new RuntimeException(
                            String.format("Invalid number of arguments for: checkout."));
                }
                Repository.checkIfInit();
                Repository.checkout(args);
                break;
            case "branch":
                // handle the `branch [branch name]` command
                validateNumArgs(" branch", args, 2);
                Repository.checkIfInit();
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                // handle the `rm-branch [branch name]` command
                validateNumArgs("rm-branch", args, 2);
                Repository.checkIfInit();
                Repository.rm_branch(args[1]);
                break;
            case "reset":
                // handle the `reset [commit id]` command
                validateNumArgs("rest", args, 2);
                Repository.checkIfInit();
                Repository.reset(args[1]);
                break;
            case "merge":
                // handle the `merge [branch name]` command
                validateNumArgs("merge", args, 2);
                Repository.checkIfInit();
                Repository.merge(args[1]);
                break;
            default :
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }

}
