package ai.keye.playground.j4make;

public class LOG {
    public static void debug(String parsing_stdin) {
        System.out.println(parsing_stdin);
    }

    public static void error(String s) {
        System.out.println(s);
    }

    public static void info(String s) {
        System.out.println(s);
    }

    public static void error(String failure, Exception e) {
        System.out.println(failure + "    " + e.toString());
    }
}
