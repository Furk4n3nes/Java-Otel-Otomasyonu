import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class YoneticiGirisPaneli extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    public YoneticiGirisPaneli() {
        setTitle("Admin Giriş");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana kart paneli oluştur
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Giriş paneli
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        loginPanel.add(usernameLabel);
        usernameField = new JTextField();
        loginPanel.add(usernameField);
        JLabel passwordLabel = new JLabel("Şifre:");
        loginPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);
        loginButton = new JButton("Giriş");
        loginButton.addActionListener(this);
        loginPanel.add(loginButton);

        // Admin paneli
        JPanel adminPanel = new JPanel(new GridLayout(3, 1));

        JButton usersButton = new JButton("Kullanıcılar");
        usersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Kullanıcılar butonuna tıklandığında kullanıcı işlemleri paneline yönlendir
                KullaniciIslemPaneli kullaniciPanel = new KullaniciIslemPaneli();
                kullaniciPanel.setVisible(true);
            }
        });
        adminPanel.add(usersButton);

        JButton reservationsButton = new JButton("Rezervasyonlar");
        reservationsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Rezervasyonlar butonuna tıklandığında rezervasyon işlemleri paneline yönlendir
                RezervasyonIslemPaneli rezervasyonPanel = new RezervasyonIslemPaneli();
                rezervasyonPanel.setVisible(true);
            }
        });
        adminPanel.add(reservationsButton);

        // Ana kart paneline alt panelleri ekle
        cardPanel.add(loginPanel, "login");
        cardPanel.add(adminPanel, "admin");

        add(cardPanel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Veritabanı bağlantısı
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBC sürücüsü yükleme ve veritabanına bağlanma
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/otel";
            String dbUsername = "root";
            String dbPassword = "";
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            // Veritabanında kullanıcıyı sorgula
            String query = "SELECT * FROM yonetici WHERE kullaniciadi = ? AND sifre = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Başarıyla giriş yaptınız!");
                // Giriş başarılıysa admin paneline geçiş yap
                cardLayout.show(cardPanel, "admin");
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre. Lütfen tekrar deneyin.");
            }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new YoneticiGirisPaneli();
            }
        });
    }
}
