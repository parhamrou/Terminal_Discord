package Discord.Server;

public class ErrorLogger {
    protected static void log(int errorCode) {
        System.out.println("An error occurred. code:" + errorCode);
    }
}
