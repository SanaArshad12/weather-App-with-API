import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

 class AdvancedWeatherApp extends JFrame {

    private static final String API_KEY = "d7ec9ff81a91c3c42fdc3e9e38c02af4";  // Replace with your OpenWeatherMap API Key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private JTextField cityField;
    private JLabel temperatureLabel;
    private JLabel descriptionLabel;
    private JLabel weatherIconLabel;
    private JPanel contentPanel;

    public AdvancedWeatherApp() {
        setTitle("Weather App");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Content Panel with gradient background
        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(135, 206, 250);
                Color color2 = new Color(25, 25, 112);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPanel.setLayout(new BorderLayout());
        setContentPane(contentPanel);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.setLayout(new FlowLayout());

        JLabel cityLabel = new JLabel("Enter City: ");
        cityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cityLabel.setForeground(Color.WHITE);

        cityField = new JTextField(15);
        cityField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cityField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cityField.setPreferredSize(new Dimension(200, 30));

        JButton getWeatherButton = new JButton("Get Weather");
        getWeatherButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        getWeatherButton.setForeground(Color.WHITE);
        getWeatherButton.setBackground(new Color(30, 144, 255));
        getWeatherButton.setFocusPainted(false);
        getWeatherButton.setPreferredSize(new Dimension(150, 30));
        getWeatherButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityField.getText().trim();
                if (!city.isEmpty()) {
                    try {
                        String weatherData = getWeatherData(city, "metric");
                        displaySimplifiedWeatherData(weatherData);
                    } catch (IOException | InterruptedException ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a city name.");
                }
            }
        });

        inputPanel.add(cityLabel);
        inputPanel.add(cityField);
        inputPanel.add(getWeatherButton);

        // Output Panel
        JPanel outputPanel = new JPanel();
        outputPanel.setOpaque(false);
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        outputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        temperatureLabel = new JLabel("Temperature: ", SwingConstants.CENTER);
        temperatureLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        temperatureLabel.setForeground(Color.WHITE);
        temperatureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        descriptionLabel = new JLabel("Description: ", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        weatherIconLabel = new JLabel("", SwingConstants.CENTER);
        weatherIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        outputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        outputPanel.add(weatherIconLabel);
        outputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        outputPanel.add(temperatureLabel);
        outputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        outputPanel.add(descriptionLabel);

        // Add panels to contentPanel
        contentPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel.add(outputPanel, BorderLayout.CENTER);
    }

    private String getWeatherData(String city, String units) throws IOException, InterruptedException {
        String url = BASE_URL + "?q=" + city + "&units=" + units + "&appid=" + API_KEY;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private void displaySimplifiedWeatherData(String weatherData) {
        String tempKeyword = "\"temp\":";
        String descriptionKeyword = "\"description\":\"";
        String iconKeyword = "\"icon\":\"";

        int tempIndex = weatherData.indexOf(tempKeyword);
        int descriptionIndex = weatherData.indexOf(descriptionKeyword);
        int iconIndex = weatherData.indexOf(iconKeyword);

        if (tempIndex != -1 && descriptionIndex != -1 && iconIndex != -1) {
            String temp = weatherData.substring(tempIndex + tempKeyword.length(), weatherData.indexOf(",", tempIndex)) + "°C";
            String description = weatherData.substring(descriptionIndex + descriptionKeyword.length(), weatherData.indexOf("\"", descriptionIndex + descriptionKeyword.length()));
            String iconCode = weatherData.substring(iconIndex + iconKeyword.length(), weatherData.indexOf("\"", iconIndex + iconKeyword.length()));

            temperatureLabel.setText("Temperature: " + temp);
            descriptionLabel.setText("Description: " + description);
            weatherIconLabel.setIcon(new ImageIcon(new ImageIcon("http://openweathermap.org/img/wn/" + iconCode + "@2x.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        } else {
            temperatureLabel.setText("Temperature: N/A");
            descriptionLabel.setText("Description: N/A");
            weatherIconLabel.setIcon(null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdvancedWeatherApp().setVisible(true);
            }
        });
    }
}
