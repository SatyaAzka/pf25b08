import java.sql.*;
import java.util.Scanner;

public class LoginSystem {

    static final String URL = "jdbc:mysql://mysql-fp25b08-fp25b08farras.c.aivencloud.com:23434/TicTacToe"
            + "?useSSL=true&requireSSL=true&verifyServerCertificate=false";
    static final String USER = "avnadmin";
    static final String PASSWORD = "AVNS_0uiK4IIVGXGPHEr1r3G";

    public static void Login() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            boolean loggedIn = false;

            while (!loggedIn) {
                System.out.print("Masukkan username: ");
                String inputUsername = scanner.nextLine();

                System.out.print("Masukkan password: ");
                String inputPassword = scanner.nextLine();

                if (checkLogin(conn, inputUsername, inputPassword)) {
                    System.out.println("Login berhasil! Selamat datang, " + inputUsername + "!");
                    loggedIn = true;
                } else {
                    System.out.println("Username atau password salah. Coba lagi.\n");
                }
            }

        } catch (SQLException e) {
            System.err.println("Gagal konek ke DB: " + e.getMessage());
        }
    }

    public static boolean checkLogin(Connection conn, String username, String password) {
        String query = "SELECT * FROM gameuser WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Error saat query login: " + e.getMessage());
            return false;
        }
    }
}