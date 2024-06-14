import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RezervasyonIslemPaneli extends JFrame {
    private JTable rezervasyonTable;
    private DefaultTableModel tableModel;

    public RezervasyonIslemPaneli() {
        setTitle("Rezervasyonlar");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tablo modeli oluştur
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Kullanıcı Adı");
        tableModel.addColumn("Oda Tipi");
        tableModel.addColumn("Oda Numarası");
        tableModel.addColumn("Giriş Tarihi");
        tableModel.addColumn("Çıkış Tarihi");
        tableModel.addColumn("Ücret");

        // Verileri tabloya yükle
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/otel", "root", "");
            String query = "SELECT * FROM rezervasyon";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String kullaniciAdi = rs.getString("kullaniciadi");
                String odaTipi = rs.getString("odatipi");
                int odaNo = rs.getInt("odano");
                Date girisTarihi = rs.getDate("giristarih");
                Date cikisTarihi = rs.getDate("cikistarih");
                double ucret = rs.getDouble("ucret");

                tableModel.addRow(new Object[]{kullaniciAdi, odaTipi, odaNo, girisTarihi, cikisTarihi, ucret});
            }

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        }

        // Tabloyu oluştur
        rezervasyonTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(rezervasyonTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Paneli görünür yap
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RezervasyonIslemPaneli();
            }
        });
    }
}
