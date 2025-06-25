package TTTConsoleNonOO;

public class KoneksiAiven {

    package TTTConsole;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class KoneksiAiven {
        public static void main(String[] args) {
            String host = "mysql-fp25b08-fp25b08farras.c.aivencloud.com";      // dari Aiven
            String port = "23434";                          // dari Aiven
            String dbname = "TicTacToe";                   // atau nama DB kamu
            String user = "avnadmin";                      // dari Aiven
            String password = "AVNS_0uiK4IIVGXGPHEr1r3G";             // dari Aiven
            String sslCAPath = "Downloads/ca (1).pem";         // path lokal ke CA cert

            String url = "jdbc:mysql://" + "mysql-fp25b08-fp25b08farras.c.aivencloud.com" + ":" + "23434" + "/" + "TicTacToe" +
                    "?verifyServerCertificate=true&useSSL=true&requireSSL=true&" +
                    "sslMode=VERIFY_CA&trustCertificateKeyStoreUrl=file:" + sslCAPath;

            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                System.out.println("Koneksi ke Aiven berhasil!");
                conn.close();
            } catch (SQLException e) {
                System.out.println("Koneksi gagal: " + e.getMessage()); // Dapatkan message
            }
        }
    }
}
