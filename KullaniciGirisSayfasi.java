import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KullaniciGirisSayfasi extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/otel"; // MySQL bağlantı URL'si
    private static final String USERNAME = "root"; // MySQL kullanıcı adı
    private static final String PASSWORD = ""; // MySQL şifre (varsa)

    public KullaniciGirisSayfasi() {
        setTitle("Kullanıcı Girişi");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JLabel kullaniciAdiLabel = new JLabel("Kullanıcı Adı:");
        JTextField kullaniciAdiField = new JTextField();

        JLabel sifreLabel = new JLabel("Şifre:");
        JPasswordField sifreField = new JPasswordField();

        JButton girisButton = new JButton("Giriş");

        girisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String kullaniciAdi = kullaniciAdiField.getText();
                String sifre = new String(sifreField.getPassword());

                if (girisKontrol(kullaniciAdi, sifre)) {
                    JOptionPane.showMessageDialog(null, "Hoş Geldiniz, " + kullaniciAdi + "!");
                    dispose(); // Bu pencereyi kapat
                    new RezervasyonYap(kullaniciAdi); // Yeni sayfayı aç
                } else {
                    JOptionPane.showMessageDialog(null, "Geçersiz kullanıcı adı veya şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton kayitOlButton = new JButton("Kayıt Ol"); // Kayıt ol butonu eklendi
        kayitOlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Bu pencereyi kapat
                new KullaniciKayitSayfasi(); // Kayıt sayfasını aç
            }
        });

        panel.add(kullaniciAdiLabel);
        panel.add(kullaniciAdiField);
        panel.add(sifreLabel);
        panel.add(sifreField);
        panel.add(girisButton);
        panel.add(kayitOlButton); // Kayıt ol butonu panel'e eklendi

        add(panel);
        setVisible(true);
    }

    private boolean girisKontrol(String kullaniciAdi, String sifre) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String query = "SELECT * FROM kullanici WHERE kullaniciadi=? AND sifre=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, kullaniciAdi);
            statement.setString(2, sifre);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new KullaniciGirisSayfasi();
            }
        });
    }
}
