import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KullaniciKayitSayfasi extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/otel"; // MySQL bağlantı URL'si
    private static final String USERNAME = "root"; // MySQL kullanıcı adı
    private static final String PASSWORD = ""; // MySQL şifre (varsa)

    public KullaniciKayitSayfasi() {
        setTitle("Kullanıcı Kayıt");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JLabel kullaniciAdiLabel = new JLabel("Kullanıcı Adı:");
        JTextField kullaniciAdiField = new JTextField();

        JLabel sifreLabel = new JLabel("Şifre:");
        JPasswordField sifreField = new JPasswordField();

        JButton kayitOlButton = new JButton("Kayıt Ol");

        kayitOlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String kullaniciAdi = kullaniciAdiField.getText();
                String sifre = new String(sifreField.getPassword());

                if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Lütfen kullanıcı adı ve şifre giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (kullaniciKaydet(kullaniciAdi, sifre)) {
                    JOptionPane.showMessageDialog(null, "Kayıt başarıyla tamamlandı, lütfen giriş yapın.");
                    dispose(); // Bu pencereyi kapat
                    new KullaniciGirisSayfasi(); // Giriş sayfasını aç
                } else {
                    JOptionPane.showMessageDialog(null, "Kayıt sırasında bir hata oluştu. Lütfen tekrar deneyin.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(kullaniciAdiLabel);
        panel.add(kullaniciAdiField);
        panel.add(sifreLabel);
        panel.add(sifreField);
        panel.add(kayitOlButton);

        add(panel);
        setVisible(true);
    }

    private boolean kullaniciKaydet(String kullaniciAdi, String sifre) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String query = "INSERT INTO kullanici (kullaniciadi, sifre) VALUES (?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, kullaniciAdi);
            statement.setString(2, sifre);
            int rowsInserted = statement.executeUpdate();
            conn.close();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new KullaniciKayitSayfasi();
            }
        });
    }
}
