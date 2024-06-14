import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class KullaniciIslemPaneli extends JFrame {
    private JTable table;
    private JButton deleteButton;
    private JButton updateButton; // Güncelleme butonu eklendi

    public KullaniciIslemPaneli() {
        setTitle("Kullanıcı İşlemleri");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Veri tablosu oluştur
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Verileri tabloya yükle
        loadUserData();

        // Silme butonu
        deleteButton = new JButton("Seçilen Kullanıcıyı Sil");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(KullaniciIslemPaneli.this, "Lütfen silmek istediğiniz kullanıcıyı seçin.");
                    return;
                }

                int userId = (int) table.getValueAt(selectedRow, 0);
                deleteUser(userId);
            }
        });

        // Güncelleme butonu
        updateButton = new JButton("Seçilen Kullanıcıyı Güncelle");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(KullaniciIslemPaneli.this, "Lütfen güncellemek istediğiniz kullanıcıyı seçin.");
                    return;
                }

                int userId = (int) table.getValueAt(selectedRow, 0);
                String newUsername = JOptionPane.showInputDialog(KullaniciIslemPaneli.this, "Yeni Kullanıcı Adı:");
                String newPassword = JOptionPane.showInputDialog(KullaniciIslemPaneli.this, "Yeni Şifre:");

                updateUser(userId, newUsername, newPassword);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton); // Güncelleme butonu eklendi
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadUserData() {
        // Veritabanı bağlantısı
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // JDBC sürücüsü yükleme ve veritabanına bağlanma
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/otel";
            String dbUsername = "root";
            String dbPassword = "";
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            // Kullanıcıları sorgula
            String query = "SELECT * FROM kullanici";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            // Verileri tabloya ekle
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Kullanıcı Adı");
            model.addColumn("Şifre");

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("kullaniciadi");
                String sifre = rs.getString("sifre");

                model.addRow(new Object[]{id, username, sifre});
            }

            table.setModel(model);

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        } finally {
            // Kaynakları serbest bırak
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteUser(int userId) {
        // Veritabanı bağlantısı
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // JDBC sürücüsü yükleme ve veritabanına bağlanma
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/otel";
            String dbUsername = "root";
            String dbPassword = "";
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            // Kullanıcıyı silme sorgusu
            String query = "DELETE FROM kullanici WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Kullanıcı başarıyla silindi.");
                // Tabloyu güncelle
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "Kullanıcı silinemedi. Lütfen tekrar deneyin.");
            }

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        } finally {
            // Kaynakları serbest bırak
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateUser(int userId, String newUsername, String newPassword) {
        // Veritabanı bağlantısı
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // JDBC sürücüsü yükleme ve veritabanına bağlanma
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/otel";
            String dbUsername = "root";
            String dbPassword = "";
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            // Kullanıcıyı güncelleme sorgusu
            String query = "UPDATE kullanici SET kullaniciadi = ?, sifre = ? WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, newUsername);
            pstmt.setString(2, newPassword);
            pstmt.setInt(3, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Kullanıcı başarıyla güncellendi.");
                // Tabloyu güncelle
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "Kullanıcı güncellenemedi. Lütfen tekrar deneyin.");
            }

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        } finally {
            // Kaynakları serbest bırak
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new KullaniciIslemPaneli();
            }
        });
    }
}
