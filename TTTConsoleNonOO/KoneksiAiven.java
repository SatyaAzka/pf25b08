import java.sql.*;

public class KoneksiAiven {
    public static void main(String[] args) {
        String url = "jdbc:mysql://mysql-fp25b08-fp25b08farras.c.aivencloud.com:23434/defaultdb"
                + "?useSSL=true&requireSSL=true"
                + "&verifyServerCertificate=false";

        String user = "avnadmin";
        String password = "AVNS_0uiK4IIVGXGPHEr1r3G";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Koneksi berhasil ke Aiven!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NOW()");
            if (rs.next()) {
                System.out.println("Waktu di server DB: " + rs.getString(1));
            }
        } catch (SQLException e) {
            System.err.println("Gagal konek: " + e.getMessage());
            e.printStackTrace();
        }
    }
}