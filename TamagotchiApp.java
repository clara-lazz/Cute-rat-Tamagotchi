import conexão.ConnectFactory;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class TamagotchiApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tamagotchi_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";  
    private static final String DB_PASSWORD = "";
    
    public static Connection getConnection() throws SQLException {
        try {
            // Registra o driver JDBC para MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado.", e);
        }
    }
   
    private JFrame frame;
    private JTextField nameField;
    private JComboBox<String> colorComboBox;
    private JTextArea statusArea;
    private JButton feedButton, playButton, sleepButton, saveButton;
    private JLabel petImageLabel;
    private Pet pet;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TamagotchiApp::new);
    }

    public static class TocarMusicaLoop {
        public void playMusic() {
            try {
                File audioFile = new File("src/sparklesong.wav");

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
    FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
    volumeControl.setValue(1.0f);  // Ajusta o volume para máximo
     }
                clip.loop(Clip.LOOP_CONTINUOUSLY);

                System.out.println("Reproduzindo música em loop...");
                Thread.sleep(5000);  
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public TamagotchiApp() {
        initialize();
        TocarMusicaLoop musica = new TocarMusicaLoop();
        musica.playMusic();
    }
   
private void initialize() {
    //COLOR SET
    Color PINKColor = new Color(233, 78, 133);
            
    frame = new JFrame("Tamagotchi Rat");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 400);
    frame.setLayout(new BorderLayout());

    // Input panel
    JPanel inputPanel = new JPanel();
    inputPanel.add(new JLabel("Nome:"));
    nameField = new JTextField(10);
    inputPanel.add(nameField);
    inputPanel.add(new JLabel("Cor:"));
    inputPanel.setBackground(Color.WHITE);
    inputPanel.setForeground(PINKColor);
    colorComboBox = new JComboBox<>(new String[]{"black", "grey", "white"});
    inputPanel.add(colorComboBox);
    JButton createButton = new JButton("Nascer");
    createButton.setBackground(Color.WHITE);
    createButton.setForeground(PINKColor);
    createButton.addActionListener(e -> createPet());
    inputPanel.add(createButton);
    frame.add(inputPanel, BorderLayout.NORTH);

    // Status area
    statusArea = new JTextArea();
    statusArea.setEditable(false);
    frame.add(new JScrollPane(statusArea), BorderLayout.CENTER);

    // Pet image label
    petImageLabel = new JLabel();
    petImageLabel.setHorizontalAlignment(JLabel.CENTER);
    petImageLabel.setOpaque(true);
    petImageLabel.setBackground(Color.WHITE);
    frame.add(petImageLabel, BorderLayout.WEST);
    
    // Status label para exibir mensagens de status
    statusLabel = new JLabel("");  // Inicializando o JLabel
    statusLabel.setHorizontalAlignment(JLabel.CENTER); // Alinhamento central
    frame.add(statusLabel, BorderLayout.SOUTH); // Adicionando ao painel inferior
    
    // Action buttons
    JPanel actionPanel = new JPanel();
    actionPanel.setLayout(new GridLayout(1, 4)); 
    feedButton = new JButton("Alimentar");
    feedButton.setBackground(PINKColor);
    feedButton.setForeground(Color.WHITE);
    feedButton.addActionListener(e -> feedPet());
    actionPanel.add(feedButton);
    playButton = new JButton("Brincar");
    playButton.setBackground(PINKColor);
    playButton.setForeground(Color.WHITE);
    playButton.addActionListener(e -> playWithPet());
    actionPanel.add(playButton);
    sleepButton = new JButton("Dormir");
    sleepButton.setBackground(PINKColor);
    sleepButton.setForeground(Color.WHITE);
    sleepButton.addActionListener(e -> putPetToSleep());
    actionPanel.add(sleepButton);
    saveButton = new JButton("Salvar");
    saveButton.setBackground(Color.WHITE);
    saveButton.setForeground(PINKColor);
    saveButton.addActionListener(e -> savePet());
    actionPanel.add(saveButton);
    frame.add(actionPanel, BorderLayout.SOUTH);

    frame.setVisible(true);
}

    private void createPet() {
        String name = nameField.getText();
        String color = (String) colorComboBox.getSelectedItem();
        pet = new Pet(name, color);
        statusArea.setText("Created a " + color + " rat named " + name + "!");
        updatePetStatus();
      updatePetImage("rat_hand_" + color + ".png");
    }

    private void feedPet() {
        if (pet != null) {
            pet.feed();
            updatePetStatus();
            updatePetImage("rat_eating_" + pet.getColor() + ".png");
        }
    }

    private void playWithPet() {
        if (pet != null) {
            pet.play();
            updatePetStatus();
            updatePetImage("rat_play_" + pet.getColor() + ".png");
        }
    }

    private void putPetToSleep() {
        if (pet != null) {
            pet.sleep();
            updatePetStatus();
            updatePetImage("rat_sleep_" + pet.getColor() + ".png");
        }
    }
    
    private void savePet() {
    if (pet != null) {
        // Conectando ao banco de dados
        try (Connection conn = ConnectFactory.getConnection()) {
            String sql = "INSERT INTO pets (nome, color, hunger, happiness, health, age) VALUES (?, ?, ?, ?, ?, ?)";
            
            // Usando PreparedStatement para evitar SQL injection
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, pet.getName());
                pstmt.setString(2, pet.getColor());
                pstmt.setInt(3, pet.getHunger());
                pstmt.setInt(4, pet.getHappiness());
                pstmt.setInt(5, pet.getHealth());
                pstmt.setInt(6, pet.getAge());

                // Executando o insert no banco
                pstmt.executeUpdate();
                statusArea.append("\nPet saved to database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusArea.append("\nError saving pet to database: " + e.getMessage());
            statusArea.append("\nSQL State: " + e.getSQLState());
            statusArea.append("\nError Code: " + e.getErrorCode());
        }
    }
}
  private JLabel statusLabel = new JLabel("");

private void updatePetStatus() {
    if (pet != null) {
        // Atualizando o status na JTextArea
        statusArea.setText(pet.getStatus());
        
        // Atualizando o status no JLabel
        if (pet.getHealth() <= 40 || pet.getHappiness() <= 40 || pet.getHunger() <= 40) {
            statusLabel.setText("Pet está triste! Atualizando imagem...");
            updatePetImage("rat_sad_" + pet.getColor() + ".png");
        } else {
            statusLabel.setText("Pet está feliz! Atualizando imagem...");
            updatePetImage("rat_happy_" + pet.getColor() + ".png");
        }
    }
}
    
private void updatePetImage(String imageName) {
    String imagePath = "images/" + imageName;  // Caminho da pasta images dentro de src
    ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(imagePath));
    if (icon != null) { // Verifica se a imagem foi carregada com sucesso
        Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        petImageLabel.setIcon(new ImageIcon(image));
    } else {
        statusArea.append("\nErro: Imagem " + imageName + " não encontrada.");
    }
}
        
        
    }
