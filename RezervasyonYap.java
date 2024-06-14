import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RezervasyonYap extends JFrame {
    private String kullaniciAdi;
    private JLabel fiyatLabel;

    public RezervasyonYap(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;

        setTitle("Rezervasyon Yap");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 1));

        JLabel baslikLabel = new JLabel("Rezervasyon Yap Sayfası");
        baslikLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Oda tipi seçimi
        JPanel odaTipiPanel = new JPanel(new FlowLayout());
        JLabel odaTipiLabel = new JLabel("Oda Tipi:");
        String[] odaTipleri = {"Tek", "Çift", "Suite"};
        JComboBox<String> odaTipiComboBox = new JComboBox<>(odaTipleri);
        odaTipiPanel.add(odaTipiLabel);
        odaTipiPanel.add(odaTipiComboBox);

        // Oda numarası seçimi
        JPanel odaNumarasiPanel = new JPanel(new FlowLayout());
        JLabel odaNumarasiLabel = new JLabel("Oda Numarası:");
        Integer[] odaNumaralari = new Integer[100];
        for (int i = 0; i < 100; i++) {
            odaNumaralari[i] = i + 1;
        }
        JComboBox<Integer> odaNumarasiComboBox = new JComboBox<>(odaNumaralari);
        odaNumarasiPanel.add(odaNumarasiLabel);
        odaNumarasiPanel.add(odaNumarasiComboBox);

        // Tarih aralığı seçimi
        JPanel tarihPaneli = new JPanel(new FlowLayout());
        JLabel baslangicLabel = new JLabel("Başlangıç Tarihi (dd.MM.yyyy):");
        JTextField baslangicTarihField = new JTextField(10);
        JLabel bitisLabel = new JLabel("Bitiş Tarihi (dd.MM.yyyy):");
        JTextField bitisTarihField = new JTextField(10);
        tarihPaneli.add(baslangicLabel);
        tarihPaneli.add(baslangicTarihField);
        tarihPaneli.add(bitisLabel);
        tarihPaneli.add(bitisTarihField);

        // Fiyat gösterme alanı
        JPanel fiyatPanel = new JPanel(new FlowLayout());
        JLabel fiyatTextLabel = new JLabel("Toplam Fiyat:");
        fiyatLabel = new JLabel("");
        fiyatPanel.add(fiyatTextLabel);
        fiyatPanel.add(fiyatLabel);

        // Tarih alanlarının değişikliklerini dinle
        FocusAdapter tarihDinleyici = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    Date baslangicDate = dateFormat.parse(baslangicTarihField.getText());
                    Date bitisDate = dateFormat.parse(bitisTarihField.getText());

                    // Gün sayısını hesapla
                    long gunSayisi = ((bitisDate.getTime() - baslangicDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;

                    // Oda tipine göre fiyatı belirle
                    double fiyat = 0;
                    String odaTipi = (String) odaTipiComboBox.getSelectedItem();
                    switch (odaTipi) {
                        case "Tek":
                            fiyat = 100;
                            break;
                        case "Çift":
                            fiyat = 150;
                            break;
                        case "Suite":
                            fiyat = 250;
                            break;
                    }

                    // Toplam ücreti hesapla
                    double toplamUcret = gunSayisi * fiyat;

                    // Fiyatı göster
                    fiyatLabel.setText(String.valueOf(toplamUcret) + " TL");
                } catch (ParseException ex) {
                    fiyatLabel.setText("Geçersiz tarih formatı!");
                }
            }
        };
        baslangicTarihField.addFocusListener(tarihDinleyici);
        bitisTarihField.addFocusListener(tarihDinleyici);

        JButton rezervasyonYapButton = new JButton("Rezervasyon Yap");
        rezervasyonYapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    Date baslangicDate = dateFormat.parse(baslangicTarihField.getText());
                    Date bitisDate = dateFormat.parse(bitisTarihField.getText());

                    // Veritabanına ekleme işlemi
                    addRezervasyonToDatabase(kullaniciAdi, (String) odaTipiComboBox.getSelectedItem(), (Integer) odaNumarasiComboBox.getSelectedItem(), baslangicDate, bitisDate, Double.parseDouble(fiyatLabel.getText().replace(" TL", "")));
                    JOptionPane.showMessageDialog(null, "Rezervasyon yapıldı!");
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Geçersiz tarih formatı!");
                }
            }
        });

        JButton cikisButton = new JButton("Çıkış");
        cikisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Rezervasyon yap sayfasını kapat
                new HosGeldinizSayfasi(kullaniciAdi); // Kullanıcı giriş sayfasını aç
            }
        });

        panel.add(baslikLabel);
        panel.add(odaTipiPanel);
        panel.add(odaNumarasiPanel);
        panel.add(tarihPaneli);
        panel.add(fiyatPanel);
        panel.add(rezervasyonYapButton);
        panel.add(cikisButton);

        add(panel);
        setVisible(true);
    }

    // Veritabanında rezervasyonu ekler
    private void addRezervasyonToDatabase(String kullaniciAdi, String odaTipi, int odaNumarasi, Date baslangicTarihi, Date bitisTarihi, double fiyat) {
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
            String query = "INSERT INTO rezervasyon (kullaniciadi, odatipi, odano, giristarih, cikistarih, ucret) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, kullaniciAdi);
            pstmt.setString(2, odaTipi);
            pstmt.setInt(3, odaNumarasi);
            pstmt.setDate(4, new java.sql.Date(baslangicTarihi.getTime())); // java.util.Date -> java.sql.Date
            pstmt.setDate(5, new java.sql.Date(bitisTarihi.getTime())); // java.util.Date -> java.sql.Date
            pstmt.setDouble(6, fiyat);

            // Sorguyu çalıştırma
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(null, "Rezervasyon başarıyla eklendi.");
                refreshRezervasyonlar(); // Rezervasyonları yeniden yükle
            } else {
                JOptionPane.showMessageDialog(null, "Rezervasyon eklenirken bir hata oluştu.");
            }

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Rezervasyon eklenirken bir hata oluştu: " + ex.getMessage());
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

        // Veritabanında rezervasyonu günceller
      
        // Veritabanındaki rezervasyonları yeniden yükler
        private void refreshRezervasyonlar() {
            // Burada rezervasyonları tekrar yükleyecek kodu ekleyebilirsiniz.
            // Örneğin, tabloyu güncellemeniz gerekiyorsa burada bu işlemi yapabilirsiniz.
            // Bu kısmı projenizin geri kalanına göre uyarlayabilirsiniz.
        }
    
        public static void main(String[] args) {
            new RezervasyonYap("KullanıcıAdi");
        }
    }
    