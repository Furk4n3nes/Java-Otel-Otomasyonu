import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class anasayfa extends JFrame {
    public anasayfa() {
        setTitle("Ana Sayfa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        JButton kullaniciGirisButton = new JButton("Kullanıcı Girişi");
        JButton adminGirisButton = new JButton("Admin Girişi");

        kullaniciGirisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kullanıcı giriş sayfasına yönlendirme
                new KullaniciGirisSayfasi().setVisible(true);
                dispose(); // Ana sayfayı kapat
            }
        });

        adminGirisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Admin giriş sayfasına yönlendirme
                new YoneticiGirisPaneli().setVisible(true);
                dispose(); // Ana sayfayı kapat
            }
        });

        panel.add(kullaniciGirisButton);
        panel.add(adminGirisButton);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new anasayfa();
    }
}
