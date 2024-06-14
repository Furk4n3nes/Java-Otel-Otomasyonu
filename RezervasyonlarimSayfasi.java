import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class RezervasyonlarimSayfasi extends JFrame {
    private String kullaniciAdi;
    private JTextArea rezervasyonlarTextArea;
    private JScrollPane scrollPane;

    public RezervasyonlarimSayfasi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;

        setTitle("Rezervasyonlarım");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel baslikLabel = new JLabel("Rezervasyonlarım");
        baslikLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(baslikLabel, BorderLayout.NORTH);

        rezervasyonlarTextArea = new JTextArea(10, 30);
        rezervasyonlarTextArea.setEditable(false);
        scrollPane = new JScrollPane(rezervasyonlarTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton cikisButton = new JButton("Çıkış");
        cikisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Rezervasyonlarım sayfasını kapat
                new HosGeldinizSayfasi(kullaniciAdi); // Hoşgeldiniz sayfasını aç
            }
        });
        panel.add(cikisButton, BorderLayout.SOUTH);

        JButton silButton = new JButton("Seçileni Sil");
        silButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedOption = JOptionPane.showConfirmDialog(null,
                        "Seçilen rezervasyonu silmek istediğinizden emin misiniz?",
                        "Rezervasyon Sil",
                        JOptionPane.YES_NO_OPTION);
                if (selectedOption == JOptionPane.YES_OPTION) {
                    // Seçilen rezervasyonun ID'sini al
                    String selectedRezervasyon = rezervasyonlarTextArea.getSelectedText();
                    if (selectedRezervasyon != null) {
                        int id = Integer.parseInt(selectedRezervasyon.substring(selectedRezervasyon.indexOf(":") + 2));
                        // Veritabanından rezervasyonu sil
                        deleteRezervasyon(id);
                    } else {
                        JOptionPane.showMessageDialog(null, "Lütfen silmek istediğiniz rezervasyonu seçin.");
                    }
                }
            }
        });
        panel.add(silButton, BorderLayout.WEST);

        JButton guncelleButton = new JButton("Seçileni Güncelle");
        guncelleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRezervasyon = rezervasyonlarTextArea.getSelectedText();
                if (selectedRezervasyon != null) {
                    int id = Integer.parseInt(selectedRezervasyon.substring(selectedRezervasyon.indexOf(":") + 2));
                    showUpdateForm(id);
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen güncellemek istediğiniz rezervasyonu seçin.");
                }
            }
        });
        panel.add(guncelleButton, BorderLayout.EAST);

        // Kullanıcının rezervasyonlarını ekrana yazdır
        refreshRezervasyonlar();

        add(panel);
        setVisible(true);
    }

    // Veritabanından kullanıcının rezervasyonlarını al ve ekrana yazdır
    private void refreshRezervasyonlar() {
        StringBuilder rezervasyonlar = new StringBuilder();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBC sürücüsünü yükleme ve veritabanına bağlanma
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/otel";
            String dbUsername = "root";
            String dbPassword = "";
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            // SQL sorgusu oluşturma
            String query = "SELECT * FROM rezervasyon WHERE kullaniciadi = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, kullaniciAdi);
            rs = pstmt.executeQuery();

            // Sonuçları işleme
            while (rs.next()) {
                int id = rs.getInt("id");
                String odaTipi = rs.getString("odatipi");
                int odaNumarasi = rs.getInt("odano");
                Date baslangicTarihi = rs.getDate("giristarih");
                Date bitisTarihi = rs.getDate("cikistarih");
                double fiyat = rs.getDouble("ucret");

                // Rezervasyon süresi hesaplama
                long diffInMillies = Math.abs(bitisTarihi.getTime() - baslangicTarihi.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                // Ücretlendirme
                if (odaTipi.equals("tek kişilik")) {
                    fiyat = diff * 100;
                } else if (odaTipi.equals("çift kişilik")) {
                    fiyat = diff * 150;
                } else if (odaTipi.equals("suite")) {
                    fiyat = diff * 250;
                }

                // Verileri JTextArea'ya ekle
                rezervasyonlar.append("Rezervasyon ID: ").append(id).append("\n");
                rezervasyonlar.append("Oda Tipi: ").append(odaTipi).append("\n");
                rezervasyonlar.append("Oda Numarası: ").append(odaNumarasi).append("\n");
                rezervasyonlar.append("Başlangıç Tarihi: ").append(baslangicTarihi).append("\n");
                rezervasyonlar.append("Bitiş Tarihi: ").append(bitisTarihi).append("\n");
                rezervasyonlar.append("Fiyat: ").append(fiyat).append(" TL\n\n");
            }

            // JTextArea içeriğini güncelle
            rezervasyonlarTextArea.setText(rezervasyonlar.toString());

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        } finally {
            // Kaynakları serbest bırak
            try {
                if (rs != null) {
                    rs.close();
                }
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

    // Veritabanından rezervasyonu sil
    private void deleteRezervasyon(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // JDBC sürücüsünü yükleme ve veritabanına bağlanma
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/otel";
            String dbUsername = "root";
            String dbPassword = "";
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            // SQL sorgusu oluşturma
            String query = "DELETE FROM rezervasyon WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            // Sorguyu çalıştırma
            int deleted = pstmt.executeUpdate();
            if (deleted > 0) {
                JOptionPane.showMessageDialog(null, "Rezervasyon başarıyla silindi.");
                refreshRezervasyonlar(); // Rezervasyonları yeniden yükle
            } else {
                JOptionPane.showMessageDialog(null, "Rezervasyon silinirken bir hata oluştu.");
            }

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Rezervasyon silinirken bir hata oluştu: " + ex.getMessage());
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

    // Seçilen rezervasyonu güncellemek için formu göster
    private void showUpdateForm(int id) {
        JFrame updateFrame = new JFrame("Rezervasyon Güncelleme");
        updateFrame.setSize(400, 300);
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        JLabel odaTipiLabel = new JLabel("Oda Tipi:");
        JTextField odaTipiField = new JTextField(20);
        panel.add(odaTipiLabel);
        panel.add(odaTipiField);
        JLabel odaNoLabel = new JLabel("Oda Numarası:");
        JTextField odaNoField = new JTextField(20);
        panel.add(odaNoLabel);
        panel.add(odaNoField);
        
        JLabel girisTarihiLabel = new JLabel("Giriş Tarihi (yyyy-MM-dd):");
        JTextField girisTarihiField = new JTextField(20);
        panel.add(girisTarihiLabel);
        panel.add(girisTarihiField);
        
        JLabel cikisTarihiLabel = new JLabel("Çıkış Tarihi (yyyy-MM-dd):");
        JTextField cikisTarihiField = new JTextField(20);
        panel.add(cikisTarihiLabel);
        panel.add(cikisTarihiField);
        
        JLabel ucretLabel = new JLabel("Ücret:");
        JTextField ucretField = new JTextField(20);
        panel.add(ucretLabel);
        panel.add(ucretField);
    
        // Eski verileri al ve JTextField'lara yerleştir
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    
        try {
            // JDBC sürücüsünü yükleme ve veritabanına bağlanma
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/otel";
            String dbUsername = "root";
            String dbPassword = "";
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);
    
            // SQL sorgusu oluşturma
            String query = "SELECT * FROM rezervasyon WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
    
            // Sonuçları işleme
            if (rs.next()) {
                odaTipiField.setText(rs.getString("odatipi"));
                odaNoField.setText(String.valueOf(rs.getInt("odano")));
                girisTarihiField.setText(String.valueOf(rs.getDate("giristarih")));
                cikisTarihiField.setText(String.valueOf(rs.getDate("cikistarih")));
                ucretField.setText(String.valueOf(rs.getDouble("ucret")));
            }
    
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + ex.getMessage());
        } finally {
            // Kaynakları serbest bırak
            try {
                if (rs != null) {
                    rs.close();
                }
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
    
        JButton updateButton = new JButton("Güncelle");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Yeni verileri al
                String yeniOdaTipi = odaTipiField.getText();
                int yeniOdaNo = Integer.parseInt(odaNoField.getText());
                String yeniGirisTarihi = girisTarihiField.getText();
                String yeniCikisTarihi = cikisTarihiField.getText();
                double yeniUcret = Double.parseDouble(ucretField.getText());
    
                // Güncelleme işlemi
                updateRezervasyon(id, yeniOdaTipi, yeniOdaNo, yeniGirisTarihi, yeniCikisTarihi, yeniUcret);
    
                // Güncelleme formunu kapat
                updateFrame.dispose();
    
                // Rezervasyonlarım sayfasını yenile
                refreshRezervasyonlar();
            }
        });
        panel.add(updateButton);
    
        updateFrame.add(panel);
        updateFrame.setVisible(true);
    }
    
    // Veritabanında rezervasyonu güncelle
    private void updateRezervasyon(int id, String odaTipi, int odaNo, String girisTarihi, String cikisTarihi, double ucret) {
        Connection conn = null;
        PreparedStatement pstmt = null;
    
        try {
            // JDBC sürücüsünü yükleme ve veritabanına bağlanma
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/otel";
            String dbUsername = "root";
            String dbPassword = "";
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);
    
            // SQL sorgusu oluşturma
            String query = "UPDATE rezervasyon SET odatipi = ?, odano = ?, giristarih = ?, cikistarih = ?, ucret = ? WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, odaTipi);
            pstmt.setInt(2, odaNo);
            pstmt.setDate(3, Date.valueOf(girisTarihi));
            pstmt.setDate(4, Date.valueOf(cikisTarihi));
            pstmt.setDouble(5, ucret);
            pstmt.setInt(6, id);
    
            // Sorguyu çalıştırma
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(null, "Rezervasyon başarıyla güncellendi.");
            } else {
                JOptionPane.showMessageDialog(null, "Rezervasyon güncellenirken bir hata oluştu.");
            }
    
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Rezervasyon güncellenirken bir hata oluştu: " + ex.getMessage());
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
            @Override
            public void run() {
                new RezervasyonlarimSayfasi("testkullanici");
            }
        });
    }
}
    
