package bloodbank_system;

public class BloodBank_System {

    public static void main(String[] args) throws ClassNotFoundException {
        // Start the login screen (no need to connect here, connections will be handled per operation)
        new login().setVisible(true);

        // Shutdown hook to close the connection when the application exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeConnection();
        }));
    }
}
