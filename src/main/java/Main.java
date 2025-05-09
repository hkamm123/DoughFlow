import server.Server;
import server.dataaccess.DataAccessException;

public class Main {
    public static void main(String[] args) {
        try {
            Server myServer = new Server();
            int port = myServer.run(8080);
            System.out.println(port);
        } catch (Exception | DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
