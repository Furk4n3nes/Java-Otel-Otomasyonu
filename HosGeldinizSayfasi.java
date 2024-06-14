import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HosGeldinizSayfasi extends JFrame {
    private String kullaniciAdi;

    public HosGeldinizSayfasi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        
        setTitle("Hoş Geldiniz");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1));

        JLabel hosGeldinizLabel = new JLabel("Hoş Geldiniz, " + kullaniciAdi + "!");
        hosGeldinizLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton rezervasyonYapButton = new JButton("Rezervasyon Yap");
        JButton rezervasyonlarimButton = new JButton("Rezervasyonlarım");
        JButton cikisButton = new JButton("Çıkış");

        rezervasyonYapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Rezervasyon Yap butonuna tıklandığında işlenecekler
                dispose(); // Hoş Geldiniz sayfasını kapat
                // Burada Rezervasyon Yap sayfasına yönlendirme yapılabilir
                new RezervasyonYap(kullaniciAdi).setVisible(true);
            }
        });

        rezervasyonlarimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Rezervasyonlarım butonuna tıklandığında işlenecekler
                dispose(); // Hoş Geldiniz sayfasını kapat
                // Burada Rezervasyonlarım sayfasına yönlendirme yapılabilir
                new RezervasyonlarimSayfasi(kullaniciAdi).setVisible(true);
            }
        });

        cikisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Hoş Geldiniz sayfasını kapat
                new anasayfa().setVisible(true); // AnaSayfa formuna geri dön
            }
        });

        panel.add(hosGeldinizLabel);
        panel.add(rezervasyonYapButton);
        panel.add(rezervasyonlarimButton);
        panel.add(cikisButton);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new HosGeldinizSayfasi("KullanıcıAdi");
    }
}
