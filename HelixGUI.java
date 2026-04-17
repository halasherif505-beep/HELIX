package system;

import Database.*;
import Tracking.Ambulance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HelixGUI {
    private JFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Database DAOs
    private Connection connection;
    private HospitalModuleDAO hospitalDAO;
    private PatientModuleDAO patientDAO;
    private StaffModuleDAO staffDAO;
    private MedicalModuleDAO medicalDAO;
    private AppointmentModuleDAO appointmentDAO;
    private BillingModuleDAO billingDAO;
    private TrackingModuleDAO trackingDAO;

    // Formatters
    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public HelixGUI() {
        initializeDatabase();
        createMainWindow();
        showMainMenu();
    }

    private void initializeDatabase() {
        try {
            connection = DBConnection.getConnection();
            if (connection == null) {
                JOptionPane.showMessageDialog(null, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            // Initialize DAOs
            hospitalDAO = new HospitalModuleDAO(connection);
            patientDAO = new PatientModuleDAO(connection);
            staffDAO = new StaffModuleDAO(connection);
            medicalDAO = new MedicalModuleDAO(connection);
            appointmentDAO = new AppointmentModuleDAO(connection);
            billingDAO = new BillingModuleDAO(connection);
            trackingDAO = new TrackingModuleDAO(connection);

            // Create tables if not exist
            createAllTables();

            // Seed initial data (EXACTLY like CLI)
            seedInitialData();

        } catch (Exception e) {
            showError("Database initialization error: " + e.getMessage());
        }
    }

    private void createAllTables() throws Exception {
        patientDAO.createTables();
        staffDAO.createTables();
        medicalDAO.createTables();
        appointmentDAO.createTables();
        billingDAO.createTables();
        trackingDAO.createTables();
        hospitalDAO.createTables();
        hospitalDAO.seedRoomsAndBedsIfEmpty();

        // Create patient_monitoring table if it doesn't exist
        createPatientMonitoringTable();
    }

    private void createPatientMonitoringTable() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS patient_monitoring (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "patient_id TEXT NOT NULL," +
                    "monitoring_details TEXT NOT NULL," +
                    "timestamp TEXT NOT NULL," +
                    "FOREIGN KEY (patient_id) REFERENCES patients(national_id)" +
                    ")";
            java.sql.Statement stmt = connection.createStatement();
            stmt.execute(sql);
            stmt.close();
            System.out.println("✅ Patient monitoring table created/verified");
        } catch (Exception e) {
            System.err.println("❌ Error creating patient_monitoring table: " + e.getMessage());
        }
    }

    private void seedInitialData() {
        try {
            // Add sample patient (EXACTLY like CLI)
            patientDAO.savePatient("P1", "Ahmed", 22);

            // Add sample bills (EXACTLY like CLI)
            billingDAO.createBill("P1", 500.0, "UNPAID");
            billingDAO.createBill("P1", 200.0, "PAID");

            // Add sample hospital (EXACTLY like CLI)
            hospitalDAO.insertHospital("Main Hospital", "Cairo");

            // Check available beds (EXACTLY like CLI)
            hospitalDAO.checkAvailableBeds();

        } catch (Exception e) {
            // Ignore if data already exists
        }
    }

    private void createMainWindow() {
        mainFrame = new JFrame("HELIX Smart Healthcare System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800); // Increased height
        mainFrame.setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Navigation Panel (Left) - Using ScrollPane to fit all buttons
        JScrollPane navScrollPane = new JScrollPane(createNavigationPanel());
        navScrollPane.setPreferredSize(new Dimension(220, 700));
        navScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        navScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(navScrollPane, BorderLayout.WEST);

        // Content Panel (Center)
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Initialize all screens
        initializeScreens();

        // Status Bar (Bottom)
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 102, 204));
        header.setPreferredSize(new Dimension(1200, 80));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("HELIX SMART HEALTHCARE SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Your Bridge To Every Medical Need");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(Color.LIGHT_GRAY);

        JLabel timeLabel = new JLabel(LocalDateTime.now().format(DTF));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.CENTER);
        header.add(timeLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(240, 240, 240));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] navItems = {
                "Dashboard",
                "Register Hospital",
                "Register Doctor",
                "Register Nurse",
                "Register Patient",
                "Schedule Staff Shift",
                "Schedule Operation",
                "Medication Schedule",
                "Ambulance Register / Track",
                "Full System Report",
                "Bed Record",
                "Room Management",
                "Notification",
                "Test Result",
                "Appointment Scheduling",
                "Ambulance ETA",
                "Create Patient Billing",
                "Save & Exit"
        };

        for (String item : navItems) {
            JButton navButton = new JButton("<html><center>" + item.replace(" ", "<br>") + "</center></html>");
            navButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            navButton.setMaximumSize(new Dimension(180, 60)); // Increased height for wrapped text
            navButton.setMinimumSize(new Dimension(180, 60));
            navButton.setPreferredSize(new Dimension(180, 60));
            navButton.setBackground(new Color(220, 230, 240));
            navButton.setFocusPainted(false);
            navButton.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            navButton.setFont(new Font("Arial", Font.PLAIN, 11));

            navButton.addActionListener(e -> handleNavigation(item));

            navPanel.add(navButton);
            navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Add filler to push buttons to top
        navPanel.add(Box.createVerticalGlue());

        return navPanel;
    }

    private void handleNavigation(String item) {
        switch (item) {
            case "Dashboard":
                switchScreen("Dashboard");
                break;
            case "Register Hospital":
                showRegisterHospitalDialog();
                break;
            case "Register Doctor":
                showRegisterDoctorDialog();
                break;
            case "Register Nurse":
                showRegisterNurseDialog();
                break;
            case "Register Patient":
                showRegisterPatientDialog();
                break;
            case "Schedule Staff Shift":
                showScheduleStaffShiftDialog();
                break;
            case "Schedule Operation":
                showScheduleOperationDialog();
                break;
            case "Medication Schedule":
                showMedicationScheduleDialog();
                break;
            case "Ambulance Register / Track":
                showAmbulanceMenuDialog();
                break;
            case "Full System Report":
                showSystemReports();
                break;
            case "Bed Record":
                showBedManagementDialog();
                break;
            case "Room Management":
                showRoomManagementDialog();
                break;
            case "Notification":
                showAddNotificationDialog();
                break;
            case "Test Result":
                showAddTestResultDialog();
                break;
            case "Appointment Scheduling":
                showScheduleAppointmentDialog();
                break;
            case "Ambulance ETA":
                showUpdateETADialog();
                break;
            case "Create Patient Billing":
                showPatientBillingDialog();
                break;
            case "Save & Exit":
                saveAndExit();
                break;
        }
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        statusPanel.setPreferredSize(new Dimension(1200, 30));

        JLabel statusLabel = new JLabel(" Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel dbStatus = new JLabel(" Database: Connected ");
        dbStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        dbStatus.setForeground(new Color(0, 150, 0));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(dbStatus, BorderLayout.EAST);

        return statusPanel;
    }

    private void initializeScreens() {
        contentPanel.add(createDashboardScreen(), "Dashboard");
    }

    private void switchScreen(String screenName) {
        cardLayout.show(contentPanel, screenName);
    }

    private void showMainMenu() {
        switchScreen("Dashboard");
    }

    // ======================= DIALOG METHODS =======================

    private void showRegisterHospitalDialog() {
        JDialog dialog = createDialog("Register Hospital", 500, 300);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Hospital name:");
        JTextField nameField = new JTextField(30);
        nameField.setPreferredSize(new Dimension(300, 30));

        JLabel locLabel = new JLabel("Location:");
        JTextField locField = new JTextField(30);
        locField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(locLabel, gbc);
        gbc.gridx = 1;
        panel.add(locField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> {
            try {
                hospitalDAO.insertHospital(nameField.getText(), locField.getText());
                showSuccess("Hospital registered to database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });
        panel.add(registerBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showRegisterDoctorDialog() {
        JDialog dialog = createDialog("Register Doctor", 500, 250);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Doctor name:");
        JTextField nameField = new JTextField(30);
        nameField.setPreferredSize(new Dimension(300, 30));

        JLabel idLabel = new JLabel("Staff ID:");
        JTextField idField = new JTextField(30);
        idField.setPreferredSize(new Dimension(300, 30));

        JLabel specLabel = new JLabel("Specialization:");
        JTextField specField = new JTextField(30);
        specField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(specLabel, gbc);
        gbc.gridx = 1;
        panel.add(specField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> {
            try {
                staffDAO.saveDoctor(idField.getText(), nameField.getText(), specField.getText());
                showSuccess("Doctor added to database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });
        panel.add(registerBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showRegisterNurseDialog() {
        JDialog dialog = createDialog("Register Nurse", 500, 250);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Nurse name:");
        JTextField nameField = new JTextField(30);
        nameField.setPreferredSize(new Dimension(300, 30));

        JLabel idLabel = new JLabel("Staff ID:");
        JTextField idField = new JTextField(30);
        idField.setPreferredSize(new Dimension(300, 30));

        JLabel deptLabel = new JLabel("Department:");
        JTextField deptField = new JTextField(30);
        deptField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(deptLabel, gbc);
        gbc.gridx = 1;
        panel.add(deptField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> {
            try {
                staffDAO.saveNurse(idField.getText(), nameField.getText(), deptField.getText());
                showSuccess("Nurse added to database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });
        panel.add(registerBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showRegisterPatientDialog() {
        JDialog dialog = createDialog("Register Patient", 700, 800);
        JTabbedPane tabbedPane = new JTabbedPane();

        // Basic Info Tab
        JPanel basicInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(25);
        nameField.setPreferredSize(new Dimension(300, 30));

        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField(25);
        ageField.setPreferredSize(new Dimension(300, 30));

        JLabel nidLabel = new JLabel("National ID:");
        JTextField nidField = new JTextField(25);
        nidField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        basicInfoPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        basicInfoPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        basicInfoPanel.add(ageLabel, gbc);
        gbc.gridx = 1;
        basicInfoPanel.add(ageField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        basicInfoPanel.add(nidLabel, gbc);
        gbc.gridx = 1;
        basicInfoPanel.add(nidField, gbc);

        // Medical History Tab - EXACTLY like CLI
        JPanel medicalPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcMedical = new GridBagConstraints();
        gbcMedical.insets = new Insets(5, 5, 5, 5);
        gbcMedical.fill = GridBagConstraints.HORIZONTAL;

        // Chronic Diseases (EXACTLY like CLI)
        JLabel chronicLabel = new JLabel("Number of chronic diseases:");
        JTextField chronicCountField = new JTextField(5);
        chronicCountField.setPreferredSize(new Dimension(50, 30));

        JTextArea chronicArea = new JTextArea(5, 40);
        chronicArea.setLineWrap(true);
        chronicArea.setWrapStyleWord(true);
        chronicArea.setEditable(false);
        JScrollPane chronicScroll = new JScrollPane(chronicArea);

        // Allergies (EXACTLY like CLI)
        JLabel allergyLabel = new JLabel("Number of allergies:");
        JTextField allergyCountField = new JTextField(5);
        allergyCountField.setPreferredSize(new Dimension(50, 30));

        JTextArea allergyArea = new JTextArea(5, 40);
        allergyArea.setLineWrap(true);
        allergyArea.setWrapStyleWord(true);
        allergyArea.setEditable(false);
        JScrollPane allergyScroll = new JScrollPane(allergyArea);

        // Family History (EXACTLY like CLI)
        JLabel familyLabel = new JLabel("Number of family history:");
        JTextField familyCountField = new JTextField(5);
        familyCountField.setPreferredSize(new Dimension(50, 30));

        JTextArea familyArea = new JTextArea(5, 40);
        familyArea.setLineWrap(true);
        familyArea.setWrapStyleWord(true);
        familyArea.setEditable(false);
        JScrollPane familyScroll = new JScrollPane(familyArea);

        // Past Surgeries (EXACTLY like CLI)
        JLabel surgeryLabel = new JLabel("Number of past surgeries:");
        JTextField surgeryCountField = new JTextField(5);
        surgeryCountField.setPreferredSize(new Dimension(50, 30));

        JTextArea surgeryArea = new JTextArea(5, 40);
        surgeryArea.setLineWrap(true);
        surgeryArea.setWrapStyleWord(true);
        surgeryArea.setEditable(false);
        JScrollPane surgeryScroll = new JScrollPane(surgeryArea);

        // Layout for medical panel
        gbcMedical.gridx = 0; gbcMedical.gridy = 0;
        medicalPanel.add(chronicLabel, gbcMedical);
        gbcMedical.gridx = 1;
        medicalPanel.add(chronicCountField, gbcMedical);

        JButton chronicBtn = new JButton("Enter Chronic Diseases");
        gbcMedical.gridx = 2;
        medicalPanel.add(chronicBtn, gbcMedical);

        gbcMedical.gridx = 0; gbcMedical.gridy = 1; gbcMedical.gridwidth = 3;
        medicalPanel.add(chronicScroll, gbcMedical);

        gbcMedical.gridy = 2; gbcMedical.gridwidth = 1;
        medicalPanel.add(allergyLabel, gbcMedical);
        gbcMedical.gridx = 1;
        medicalPanel.add(allergyCountField, gbcMedical);

        JButton allergyBtn = new JButton("Enter Allergies");
        gbcMedical.gridx = 2;
        medicalPanel.add(allergyBtn, gbcMedical);

        gbcMedical.gridy = 3; gbcMedical.gridwidth = 3;
        medicalPanel.add(allergyScroll, gbcMedical);

        gbcMedical.gridy = 4; gbcMedical.gridwidth = 1;
        medicalPanel.add(familyLabel, gbcMedical);
        gbcMedical.gridx = 1;
        medicalPanel.add(familyCountField, gbcMedical);

        JButton familyBtn = new JButton("Enter Family History");
        gbcMedical.gridx = 2;
        medicalPanel.add(familyBtn, gbcMedical);

        gbcMedical.gridy = 5; gbcMedical.gridwidth = 3;
        medicalPanel.add(familyScroll, gbcMedical);

        gbcMedical.gridy = 6; gbcMedical.gridwidth = 1;
        medicalPanel.add(surgeryLabel, gbcMedical);
        gbcMedical.gridx = 1;
        medicalPanel.add(surgeryCountField, gbcMedical);

        JButton surgeryBtn = new JButton("Enter Past Surgeries");
        gbcMedical.gridx = 2;
        medicalPanel.add(surgeryBtn, gbcMedical);

        gbcMedical.gridy = 7; gbcMedical.gridwidth = 3;
        medicalPanel.add(surgeryScroll, gbcMedical);

        // Habits Tab
        JPanel habitsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);

        JLabel smokerLabel = new JLabel("Smoker? (yes/no):");
        JTextField smokerField = new JTextField(15);
        smokerField.setPreferredSize(new Dimension(200, 30));

        JLabel sleepLabel = new JLabel("Sleep hours per day:");
        JTextField sleepField = new JTextField(15);
        sleepField.setPreferredSize(new Dimension(200, 30));

        JLabel exerciseLabel = new JLabel("Exercises? (yes/no):");
        JTextField exerciseField = new JTextField(15);
        exerciseField.setPreferredSize(new Dimension(200, 30));

        gbc2.gridx = 0; gbc2.gridy = 0;
        habitsPanel.add(smokerLabel, gbc2);
        gbc2.gridx = 1;
        habitsPanel.add(smokerField, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1;
        habitsPanel.add(sleepLabel, gbc2);
        gbc2.gridx = 1;
        habitsPanel.add(sleepField, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 2;
        habitsPanel.add(exerciseLabel, gbc2);
        gbc2.gridx = 1;
        habitsPanel.add(exerciseField, gbc2);

        tabbedPane.addTab("Basic Info", basicInfoPanel);
        tabbedPane.addTab("Medical History", medicalPanel);
        tabbedPane.addTab("Habits", habitsPanel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton registerBtn = new JButton("Register Patient");
        JButton cancelBtn = new JButton("Cancel");

        // Arrays to store medical history entries
        java.util.List<String> chronicDiseases = new java.util.ArrayList<>();
        java.util.List<String> allergies = new java.util.ArrayList<>();
        java.util.List<String> familyHistory = new java.util.ArrayList<>();
        java.util.List<String> surgeries = new java.util.ArrayList<>();

        // Add listeners to buttons
        chronicBtn.addActionListener(e -> {
            try {
                int count = Integer.parseInt(chronicCountField.getText());
                showMedicalEntryDialog("Chronic Disease", count, chronicDiseases, chronicArea);
            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
            }
        });

        allergyBtn.addActionListener(e -> {
            try {
                int count = Integer.parseInt(allergyCountField.getText());
                showMedicalEntryDialog("Allergy", count, allergies, allergyArea);
            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
            }
        });

        familyBtn.addActionListener(e -> {
            try {
                int count = Integer.parseInt(familyCountField.getText());
                showMedicalEntryDialog("Family History", count, familyHistory, familyArea);
            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
            }
        });

        surgeryBtn.addActionListener(e -> {
            try {
                int count = Integer.parseInt(surgeryCountField.getText());
                showMedicalEntryDialog("Past Surgery", count, surgeries, surgeryArea);
            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
            }
        });

        registerBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String nid = nidField.getText();

                // Save patient (EXACTLY like CLI)
                patientDAO.savePatient(nid, name, age);

                // Save chronic diseases (EXACTLY like CLI)
                for (String disease : chronicDiseases) {
                    medicalDAO.saveMedicalHistory(nid, "CHRONIC", disease);
                }

                // Save allergies (EXACTLY like CLI)
                for (String allergy : allergies) {
                    medicalDAO.saveMedicalHistory(nid, "ALLERGY", allergy);
                }

                // Save family history (EXACTLY like CLI)
                for (String history : familyHistory) {
                    medicalDAO.saveMedicalHistory(nid, "FAMILY", history);
                }

                // Save past surgeries (EXACTLY like CLI)
                for (String surgery : surgeries) {
                    medicalDAO.saveMedicalHistory(nid, "SURGERY", surgery);
                }

                // Save habits (EXACTLY like CLI)
                boolean smoker = parseBoolean(smokerField.getText());
                int sleep = Integer.parseInt(sleepField.getText());
                boolean exercise = parseBoolean(exerciseField.getText());

                int lastStreak = medicalDAO.getLastStreak(nid);
                int newStreak;

                if (!smoker && exercise && sleep >= 7 && sleep <= 9) {
                    newStreak = lastStreak + 1;
                } else {
                    newStreak = 0;
                }

                medicalDAO.saveDailyHabits(nid, smoker, sleep, exercise, newStreak);

                // Show success message with streak info (EXACTLY like CLI)
                String message;
                if (newStreak == 0) {
                    message = "Patient registered successfully!\n🌱 Fresh start! Today is a new chance to build better habits.";
                } else {
                    message = "Patient registered successfully!\n❤️ Great job! You're staying consistent—keep going!";
                }

                showSuccess(message);
                dialog.dispose();

                // Show nurse actions dialog (EXACTLY like CLI)
                showNurseActionsDialog(nid);

            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for age and sleep hours.");
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showMedicalEntryDialog(String type, int count, java.util.List<String> entries, JTextArea displayArea) {
        entries.clear();
        displayArea.setText("");

        for (int i = 0; i < count; i++) {
            String input = JOptionPane.showInputDialog(mainFrame,
                    "Enter " + type + " #" + (i + 1) + ":",
                    "Medical History Entry",
                    JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.trim().isEmpty()) {
                entries.add(input.trim());
                displayArea.append((i + 1) + ". " + input.trim() + "\n");
            }
        }
    }

    private void showNurseActionsDialog(String patientId) {
        JDialog dialog = createDialog("Nurse Actions for Patient: " + patientId, 500, 350);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nurseLabel = new JLabel("Nurse ID:");
        JTextField nurseField = new JTextField(25);
        nurseField.setPreferredSize(new Dimension(300, 30));

        JLabel statusLabel = new JLabel("New patient status:");
        JTextField statusField = new JTextField(25);
        statusField.setPreferredSize(new Dimension(300, 30));

        JLabel vitalLabel = new JLabel("Vital Signs (optional):");
        JTextField vitalField = new JTextField(25);
        vitalField.setPreferredSize(new Dimension(300, 30));

        JLabel notesLabel = new JLabel("Notes (optional):");
        JTextArea notesArea = new JTextArea(4, 40);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nurseLabel, gbc);
        gbc.gridx = 1;
        panel.add(nurseField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(statusLabel, gbc);
        gbc.gridx = 1;
        panel.add(statusField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(vitalLabel, gbc);
        gbc.gridx = 1;
        panel.add(vitalField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(notesLabel, gbc);
        gbc.gridx = 1;
        panel.add(notesScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton updateBtn = new JButton("Update Patient Monitoring");
        updateBtn.addActionListener(e -> {
            try {
                String nurseId = nurseField.getText().trim();
                String status = statusField.getText().trim();
                String vitalSigns = vitalField.getText().trim();
                String notes = notesArea.getText().trim();

                if (nurseId.isEmpty() || status.isEmpty()) {
                    showError("Please enter Nurse ID and Status.");
                    return;
                }

                // Save patient monitoring to database
                String monitoringDetails = "Nurse: " + nurseId + " | Status: " + status;
                if (!vitalSigns.isEmpty()) {
                    monitoringDetails += " | Vitals: " + vitalSigns;
                }
                if (!notes.isEmpty()) {
                    monitoringDetails += " | Notes: " + notes;
                }

                // Save to database with CORRECT column name: monitoring_details (not ponitoring_details)
                savePatientMonitoring(patientId, monitoringDetails);

                showSuccess("Patient monitoring saved to database.\nStatus: " + status);
                dialog.dispose();
            } catch (Exception ex) {
                showError("Error saving patient monitoring: " + ex.getMessage());
            }
        });
        panel.add(updateBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void savePatientMonitoring(String patientId, String details) {
        try {
            // Create a simple SQL insert statement for patient monitoring
            // CORRECT column name: monitoring_details (was misspelled as ponitoring_details)
            String sql = "INSERT INTO patient_monitoring (patient_id, monitoring_details, timestamp) VALUES (?, ?, ?)";
            java.sql.PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, patientId);
            stmt.setString(2, details);
            stmt.setString(3, LocalDateTime.now().toString());
            stmt.executeUpdate();
            stmt.close();
            System.out.println("✅ Patient monitoring saved for patient: " + patientId);
        } catch (Exception e) {
            // If table doesn't exist or has wrong structure, recreate it
            if (e.getMessage().contains("no such table") || e.getMessage().contains("no column")) {
                recreatePatientMonitoringTable();
                // Try again
                savePatientMonitoring(patientId, details);
            } else {
                throw new RuntimeException("Failed to save patient monitoring: " + e.getMessage());
            }
        }
    }

    private void recreatePatientMonitoringTable() {
        try {
            // Drop table if exists
            String dropSql = "DROP TABLE IF EXISTS patient_monitoring";
            java.sql.Statement dropStmt = connection.createStatement();
            dropStmt.execute(dropSql);
            dropStmt.close();

            // Recreate table with correct schema
            String createSql = "CREATE TABLE patient_monitoring (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "patient_id TEXT NOT NULL," +
                    "monitoring_details TEXT NOT NULL," +
                    "timestamp TEXT NOT NULL," +
                    "FOREIGN KEY (patient_id) REFERENCES patients(national_id)" +
                    ")";
            java.sql.Statement createStmt = connection.createStatement();
            createStmt.execute(createSql);
            createStmt.close();
            System.out.println("✅ Patient monitoring table recreated with correct schema");
        } catch (Exception e) {
            System.err.println("❌ Error recreating patient_monitoring table: " + e.getMessage());
        }
    }

    private void showScheduleStaffShiftDialog() {
        JDialog dialog = createDialog("Schedule Staff Shift", 500, 250);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel staffLabel = new JLabel("Staff ID:");
        JTextField staffField = new JTextField(25);
        staffField.setPreferredSize(new Dimension(300, 30));

        JLabel shiftLabel = new JLabel("Shift Time (yyyy-MM-dd HH:mm):");
        JTextField shiftField = new JTextField(25);
        shiftField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(staffLabel, gbc);
        gbc.gridx = 1;
        panel.add(staffField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(shiftLabel, gbc);
        gbc.gridx = 1;
        panel.add(shiftField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton scheduleBtn = new JButton("Schedule Shift");
        scheduleBtn.addActionListener(e -> {
            try {
                staffDAO.assignShift(staffField.getText(), shiftField.getText());
                showSuccess("Shift scheduled in database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });
        panel.add(scheduleBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showScheduleOperationDialog() {
        JDialog dialog = createDialog("Schedule Operation", 700, 500); // Larger dialog
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel patientLabel = new JLabel("Patient ID:");
        JTextField patientField = new JTextField(30);
        patientField.setPreferredSize(new Dimension(400, 30));

        JLabel opLabel = new JLabel("Operation Name:");
        JTextField opField = new JTextField(30);
        opField.setPreferredSize(new Dimension(400, 30));

        JLabel dateLabel = new JLabel("Operation date (yyyy-MM-dd HH:mm):");
        JTextField dateField = new JTextField(30);
        dateField.setPreferredSize(new Dimension(400, 30));
        dateField.setText(LocalDateTime.now().plusDays(1).format(DTF));

        JLabel detailsLabel = new JLabel("Operation Details:");
        JTextArea detailsArea = new JTextArea(10, 50); // Larger text area
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setPreferredSize(new Dimension(500, 150));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(patientLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(patientField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(opLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(opField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(dateLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(detailsLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(detailsScroll, gbc);

        JPanel buttonPanel = new JPanel();
        JButton scheduleBtn = new JButton("Schedule Operation");
        JButton cancelBtn = new JButton("Cancel");

        scheduleBtn.addActionListener(e -> {
            try {
                String opDate = dateField.getText();
                String operationDetails = opField.getText() + " @ " + opDate;
                if (!detailsArea.getText().trim().isEmpty()) {
                    operationDetails += "\nDetails: " + detailsArea.getText();
                }
                medicalDAO.saveMedicalHistory(patientField.getText(), "OPERATION", operationDetails);
                showSuccess("Operation scheduled in database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(scheduleBtn);
        buttonPanel.add(cancelBtn);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showMedicationScheduleDialog() {
        JDialog dialog = createDialog("Medication Schedule", 600, 400);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientLabel = new JLabel("Patient ID:");
        JTextField patientField = new JTextField(25);
        patientField.setPreferredSize(new Dimension(300, 30));

        JLabel medLabel = new JLabel("Medication name:");
        JTextField medField = new JTextField(25);
        medField.setPreferredSize(new Dimension(300, 30));

        JLabel timeLabel = new JLabel("Dosage time:");
        JTextField timeField = new JTextField(25);
        timeField.setPreferredSize(new Dimension(300, 30));

        JLabel qtyLabel = new JLabel("Quantity:");
        JTextField qtyField = new JTextField(25);
        qtyField.setPreferredSize(new Dimension(300, 30));
        qtyField.setText("1");

        JLabel detailsLabel = new JLabel("Additional Details:");
        JTextArea detailsArea = new JTextArea(4, 40);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(patientLabel, gbc);
        gbc.gridx = 1;
        panel.add(patientField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(medLabel, gbc);
        gbc.gridx = 1;
        panel.add(medField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(timeLabel, gbc);
        gbc.gridx = 1;
        panel.add(timeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(qtyLabel, gbc);
        gbc.gridx = 1;
        panel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(detailsLabel, gbc);
        gbc.gridx = 1;
        panel.add(detailsScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton saveBtn = new JButton("Save Medication");
        saveBtn.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(qtyField.getText());
                String medicationDetails = medField.getText();
                if (!detailsArea.getText().trim().isEmpty()) {
                    medicationDetails += " - " + detailsArea.getText();
                }
                medicalDAO.saveMedicationSchedule(patientField.getText(), medicationDetails,
                        timeField.getText(), quantity, "ON");
                showSuccess("Medication saved to database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });
        panel.add(saveBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAmbulanceMenuDialog() {
        JDialog dialog = createDialog("Ambulance Management", 500, 300);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JButton registerBtn = new JButton("Register Ambulance");
        registerBtn.setPreferredSize(new Dimension(200, 40));

        JButton trackBtn = new JButton("Track Ambulance");
        trackBtn.setPreferredSize(new Dimension(200, 40));

        registerBtn.addActionListener(e -> {
            dialog.dispose();
            showRegisterAmbulanceDialog();
        });

        trackBtn.addActionListener(e -> {
            dialog.dispose();
            showTrackAmbulanceDialog();
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(registerBtn, gbc);

        gbc.gridy = 1;
        panel.add(trackBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showRegisterAmbulanceDialog() {
        JDialog dialog = createDialog("Register Ambulance", 500, 250);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Ambulance ID:");
        JTextField idField = new JTextField(25);
        idField.setPreferredSize(new Dimension(300, 30));

        JLabel locLabel = new JLabel("Location:");
        JTextField locField = new JTextField(25);
        locField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(locLabel, gbc);
        gbc.gridx = 1;
        panel.add(locField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> {
            try {
                Ambulance ambulance = new Ambulance(idField.getText(), locField.getText());
                trackingDAO.saveAmbulance(ambulance);
                showSuccess("Ambulance added to database");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });
        panel.add(registerBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showTrackAmbulanceDialog() {
        JDialog dialog = createDialog("Track Ambulance", 500, 200);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Ambulance ID:");
        JTextField idField = new JTextField(25);
        idField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JButton trackBtn = new JButton("Start Tracking");
        trackBtn.addActionListener(e -> {
            try {
                Ambulance amb = trackingDAO.getAmbulanceById(idField.getText());
                if (amb != null) {
                    dialog.dispose();
                    startAmbulanceTracking(amb);
                } else {
                    showError("Ambulance not found");
                }
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });
        panel.add(trackBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void startAmbulanceTracking(Ambulance ambulance) {
        JDialog trackingDialog = createDialog("Ambulance Tracking - " + ambulance.getId(), 900, 700);

        JPanel trackingPanel = new JPanel(new BorderLayout());
        JTextArea mapArea = new JTextArea(25, 60);
        mapArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        mapArea.setEditable(false);

        // Use arrays for mutable state in lambda
        final int[] ambPosition = {ambulance.getX(), ambulance.getY()};
        final int[] targetPosition = {9, 9}; // EXACTLY like CLI
        final boolean[] running = {true};
        final Thread[] trackingThread = new Thread[1];

        JButton startBtn = new JButton("Start Simulation");
        JButton stopBtn = new JButton("Stop");

        startBtn.addActionListener(e -> {
            trackingThread[0] = new Thread(() -> {
                while (running[0] && (ambPosition[0] != targetPosition[0] || ambPosition[1] != targetPosition[1])) {
                    // Update position (EXACTLY like CLI)
                    if (ambPosition[0] < targetPosition[0]) ambPosition[0]++;
                    else if (ambPosition[0] > targetPosition[0]) ambPosition[0]--;

                    if (ambPosition[1] < targetPosition[1]) ambPosition[1]++;
                    else if (ambPosition[1] > targetPosition[1]) ambPosition[1]--;

                    // Update ambulance object
                    ambulance.setX(ambPosition[0]);
                    ambulance.setY(ambPosition[1]);

                    // Create map (EXACTLY like CLI)
                    StringBuilder map = new StringBuilder();
                    map.append("AMBULANCE TRACKING\n\n");
                    for (int i = 0; i < 15; i++) {
                        for (int j = 0; j < 15; j++) {
                            if (i == ambPosition[1] && j == ambPosition[0]) {
                                map.append("🚑 ");
                            } else if (i == targetPosition[1] && j == targetPosition[0]) {
                                map.append("🏥 ");
                            } else {
                                map.append("⬜ ");
                            }
                        }
                        map.append("\n");
                    }

                    final String finalMap = map.toString();
                    final int currentX = ambPosition[0];
                    final int currentY = ambPosition[1];

                    SwingUtilities.invokeLater(() -> {
                        mapArea.setText(finalMap);
                        mapArea.append(String.format("\nAmbulance Position: (%d, %d)", currentX, currentY));
                    });

                    try {
                        Thread.sleep(400); // EXACTLY like CLI
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                if (ambPosition[0] == targetPosition[0] && ambPosition[1] == targetPosition[1]) {
                    SwingUtilities.invokeLater(() -> {
                        mapArea.append("\n\nAmbulance arrived at hospital!");
                    });
                }
            });
            trackingThread[0].start();
        });

        stopBtn.addActionListener(e -> {
            running[0] = false;
            if (trackingThread[0] != null) {
                trackingThread[0].interrupt();
            }
            trackingDialog.dispose();
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(startBtn);
        controlPanel.add(stopBtn);

        trackingPanel.add(new JScrollPane(mapArea), BorderLayout.CENTER);
        trackingPanel.add(controlPanel, BorderLayout.SOUTH);

        trackingDialog.add(trackingPanel);
        trackingDialog.setVisible(true);
    }

    private void showBedManagementDialog() {
        JDialog dialog = createDialog("Bed Assignment", 500, 250);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientLabel = new JLabel("Patient ID:");
        JTextField patientField = new JTextField(25);
        patientField.setPreferredSize(new Dimension(300, 30));

        JLabel bedLabel = new JLabel("Bed Number:");
        JTextField bedField = new JTextField(25);
        bedField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(patientLabel, gbc);
        gbc.gridx = 1;
        panel.add(patientField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(bedLabel, gbc);
        gbc.gridx = 1;
        panel.add(bedField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton assignBtn = new JButton("Assign Bed");
        assignBtn.addActionListener(e -> {
            try {
                hospitalDAO.assignBed(patientField.getText(), bedField.getText());
                showSuccess("Bed assigned successfully");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });
        panel.add(assignBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showRoomManagementDialog() {
        JDialog dialog = createDialog("Room Management", 600, 500);
        JPanel panel = new JPanel(new BorderLayout());

        // Show available rooms
        JTextArea roomsArea = new JTextArea(15, 50);
        roomsArea.setEditable(false);
        roomsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        roomsArea.setText("Available Rooms:\n================\n\n" +
                "Room 101: Available - Single\n" +
                "Room 102: Occupied - Double\n" +
                "Room 103: Available - ICU\n" +
                "Room 104: Available - General\n" +
                "Room 105: Maintenance - Private\n\n" +
                "Call hospitalDAO.showAvailableRooms() for actual data");

        JScrollPane scrollPane = new JScrollPane(roomsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel assignPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientLabel = new JLabel("Patient ID:");
        JTextField patientField = new JTextField(20);
        patientField.setPreferredSize(new Dimension(200, 30));

        JLabel roomLabel = new JLabel("Room Number:");
        JTextField roomField = new JTextField(20);
        roomField.setPreferredSize(new Dimension(200, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        assignPanel.add(patientLabel, gbc);
        gbc.gridx = 1;
        assignPanel.add(patientField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        assignPanel.add(roomLabel, gbc);
        gbc.gridx = 1;
        assignPanel.add(roomField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton assignBtn = new JButton("Assign Room");
        assignBtn.addActionListener(e -> {
            try {
                boolean assigned = hospitalDAO.assignRoom(patientField.getText(), roomField.getText());
                if (assigned) {
                    showSuccess("Room assigned successfully");
                    dialog.dispose();
                } else {
                    showError("Room not available");
                }
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });
        assignPanel.add(assignBtn, gbc);

        panel.add(assignPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddNotificationDialog() {
        JDialog dialog = createDialog("Add Notification", 500, 350);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientLabel = new JLabel("Patient ID:");
        JTextField patientField = new JTextField(25);
        patientField.setPreferredSize(new Dimension(300, 30));

        JLabel messageLabel = new JLabel("Message:");
        JTextArea messageArea = new JTextArea(5, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        JLabel typeLabel = new JLabel("Type (medication/appointment):");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"medication", "appointment"});
        typeCombo.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(patientLabel, gbc);
        gbc.gridx = 1;
        panel.add(patientField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(typeLabel, gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(messageLabel, gbc);
        gbc.gridx = 1;
        panel.add(messageScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton addBtn = new JButton("Add Notification");
        addBtn.addActionListener(e -> {
            try {
                patientDAO.saveNotification(patientField.getText(), messageArea.getText(),
                        (String) typeCombo.getSelectedItem(), LocalDateTime.now().toString());
                showSuccess("Notification saved to database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });
        panel.add(addBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddTestResultDialog() {
        JDialog dialog = createDialog("Add Test Result", 500, 350);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientLabel = new JLabel("Patient ID:");
        JTextField patientField = new JTextField(25);
        patientField.setPreferredSize(new Dimension(300, 30));

        JLabel testLabel = new JLabel("Test name:");
        JTextField testField = new JTextField(25);
        testField.setPreferredSize(new Dimension(300, 30));

        JLabel resultLabel = new JLabel("Result:");
        JTextArea resultArea = new JTextArea(5, 40);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resultScroll = new JScrollPane(resultArea);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(patientLabel, gbc);
        gbc.gridx = 1;
        panel.add(patientField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(testLabel, gbc);
        gbc.gridx = 1;
        panel.add(testField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(resultLabel, gbc);
        gbc.gridx = 1;
        panel.add(resultScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton addBtn = new JButton("Add Test Result");
        addBtn.addActionListener(e -> {
            try {
                String testDate = LocalDateTime.now().format(dateFormatter);
                medicalDAO.saveTestResult(patientField.getText(), testField.getText(), testDate, resultArea.getText());
                showSuccess("Result saved to database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });
        panel.add(addBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showScheduleAppointmentDialog() {
        JDialog dialog = createDialog("Schedule Appointment", 600, 450);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientLabel = new JLabel("Patient ID:");
        JTextField patientField = new JTextField(25);
        patientField.setPreferredSize(new Dimension(300, 30));

        JLabel doctorLabel = new JLabel("Doctor ID:");
        JTextField doctorField = new JTextField(25);
        doctorField.setPreferredSize(new Dimension(300, 30));

        JLabel dateLabel = new JLabel("Appointment date (yyyy-MM-dd HH:mm):");
        JTextField dateField = new JTextField(25);
        dateField.setPreferredSize(new Dimension(300, 30));
        dateField.setText(LocalDateTime.now().plusHours(1).format(DTF));

        JLabel notesLabel = new JLabel("Appointment Notes:");
        JTextArea notesArea = new JTextArea(5, 40);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(patientLabel, gbc);
        gbc.gridx = 1;
        panel.add(patientField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(doctorLabel, gbc);
        gbc.gridx = 1;
        panel.add(doctorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(dateLabel, gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(notesLabel, gbc);
        gbc.gridx = 1;
        panel.add(notesScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton scheduleBtn = new JButton("Schedule Appointment");
        scheduleBtn.addActionListener(e -> {
            try {
                String[] dateTime = dateField.getText().split(" ");
                String appointmentDetails = "Doctor: " + doctorField.getText();
                if (!notesArea.getText().trim().isEmpty()) {
                    appointmentDetails += " | Notes: " + notesArea.getText();
                }

                appointmentDAO.saveAppointment(patientField.getText(), doctorField.getText(),
                        dateTime[0], dateTime[1], "SCHEDULED");
                showSuccess("Appointment saved to database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });
        panel.add(scheduleBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showUpdateETADialog() {
        JDialog dialog = createDialog("Update Ambulance ETA", 500, 250);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel ambLabel = new JLabel("Ambulance ID:");
        JTextField ambField = new JTextField(25);
        ambField.setPreferredSize(new Dimension(300, 30));

        JLabel etaLabel = new JLabel("ETA minutes:");
        JTextField etaField = new JTextField(25);
        etaField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(ambLabel, gbc);
        gbc.gridx = 1;
        panel.add(ambField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(etaLabel, gbc);
        gbc.gridx = 1;
        panel.add(etaField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton updateBtn = new JButton("Update ETA");
        updateBtn.addActionListener(e -> {
            try {
                int minutes = Integer.parseInt(etaField.getText());
                trackingDAO.saveETA(ambField.getText(), minutes, LocalDateTime.now().toString());
                showSuccess("ETA updated in database.");
                dialog.dispose();
            } catch (Exception ex) {
                showError("Database error: " + ex.getMessage());
            }
        });
        panel.add(updateBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showPatientBillingDialog() {
        JDialog dialog = createDialog("Create Patient Billing", 700, 550);
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientLabel = new JLabel("Patient National ID:");
        JTextField patientField = new JTextField(25);
        patientField.setPreferredSize(new Dimension(300, 30));

        JLabel serviceLabel = new JLabel("Service description:");
        JTextArea serviceArea = new JTextArea(4, 40);
        serviceArea.setLineWrap(true);
        serviceArea.setWrapStyleWord(true);
        JScrollPane serviceScroll = new JScrollPane(serviceArea);

        JLabel costLabel = new JLabel("Service cost:");
        JTextField costField = new JTextField(25);
        costField.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(patientLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(patientField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(serviceLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(serviceScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(costLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(costField, gbc);

        JTextArea totalArea = new JTextArea(5, 50);
        totalArea.setEditable(false);
        totalArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel buttonPanel = new JPanel();
        JButton addServiceBtn = new JButton("Add Service");
        JButton finishBtn = new JButton("Finish Billing");
        JButton viewTotalBtn = new JButton("View Total");
        JButton cancelBtn = new JButton("Cancel");

        addServiceBtn.addActionListener(e -> {
            try {
                double cost = Double.parseDouble(costField.getText());
                billingDAO.createBill(patientField.getText(), cost, "UNPAID");

                double total = billingDAO.getTotalByPatientId(patientField.getText());
                totalArea.setText(String.format("Total Bill = %.2f EGP\nService added: %s",
                        total, serviceArea.getText()));

                // Clear fields for next service
                serviceArea.setText("");
                costField.setText("");

            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });

        viewTotalBtn.addActionListener(e -> {
            try {
                double total = billingDAO.getTotalByPatientId(patientField.getText());
                totalArea.setText(String.format("Current Total for Patient %s:\n%.2f EGP",
                        patientField.getText(), total));
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });

        finishBtn.addActionListener(e -> {
            try {
                double total = billingDAO.getTotalByPatientId(patientField.getText());
                showSuccess(String.format("Billing completed!\nTotal for patient %s: %.2f EGP",
                        patientField.getText(), total));
                dialog.dispose();
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addServiceBtn);
        buttonPanel.add(viewTotalBtn);
        buttonPanel.add(finishBtn);
        buttonPanel.add(cancelBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(totalArea), BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showSystemReports() {
        JDialog dialog = createDialog("System Reports", 800, 600);
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea reportArea = new JTextArea(25, 70);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton generateBtn = new JButton("Generate Full System Report");
        generateBtn.addActionListener(e -> {
            try {
                StringBuilder report = new StringBuilder();
                report.append("╔════════════════════════════════════════════════════╗\n");
                report.append("║          HELIX SMART HEALTHCARE SYSTEM             ║\n");
                report.append("║             COMPREHENSIVE SYSTEM REPORT            ║\n");
                report.append("╚════════════════════════════════════════════════════╝\n\n");

                report.append("===== SYSTEM REPORT =====\n\n");

                // 1. Hospital Status
                report.append("1. HOSPITAL STATUS:\n");
                report.append("   ");
                try {
                    hospitalDAO.checkAvailableBeds();
                    report.append("✓ Available beds checked\n");
                } catch (Exception ex) {
                    report.append("✗ Error checking beds: ").append(ex.getMessage()).append("\n");
                }
                report.append("\n");

                // 2. Patient Statistics
                report.append("2. PATIENT STATISTICS:\n");
                report.append("   ");
                try {
                    int patientCount = patientDAO.countPatients();
                    report.append("✓ Total Patients: ").append(patientCount).append("\n");
                } catch (Exception ex) {
                    report.append("✗ Error counting patients: ").append(ex.getMessage()).append("\n");
                }
                report.append("\n");

                // 3. Appointment Statistics
                report.append("3. APPOINTMENT STATISTICS:\n");
                report.append("   ");
                try {
                    int appointmentCount = appointmentDAO.countAppointment();
                    report.append("✓ Total Appointments: ").append(appointmentCount).append("\n");
                } catch (Exception ex) {
                    report.append("✗ Error counting appointments: ").append(ex.getMessage()).append("\n");
                }
                report.append("\n");

                // 4. Financial Report
                report.append("4. FINANCIAL REPORT:\n");
                report.append("   ");
                try {
                    double totalRevenue = billingDAO.calculateTotalRevenue();
                    report.append(String.format("✓ Total Revenue: %.2f EGP\n", totalRevenue));
                } catch (Exception ex) {
                    report.append("✗ Error calculating revenue: ").append(ex.getMessage()).append("\n");
                }
                report.append("\n");

                // 5. Ambulance Status
                report.append("5. AMBULANCE STATUS:\n");
                report.append("   ");
                try {
                    int activeAmbulances = trackingDAO.getActiveAmbulances();
                    report.append("✓ Active Ambulances: ").append(activeAmbulances).append("\n");
                } catch (Exception ex) {
                    report.append("✗ Error getting ambulance status: ").append(ex.getMessage()).append("\n");
                }
                report.append("\n");

                // 6. Additional Statistics
                report.append("6. ADDITIONAL STATISTICS:\n");
                report.append("   ");
                try {
                    // Get today's date for context
                    String today = LocalDateTime.now().format(dateFormatter);
                    report.append("✓ Report Date: ").append(today).append("\n");
                    report.append("✓ Report Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");
                    report.append("✓ Database Status: CONNECTED\n");
                } catch (Exception ex) {
                    report.append("✗ Error getting additional stats: ").append(ex.getMessage()).append("\n");
                }
                report.append("\n");

                report.append("=".repeat(55)).append("\n");
                report.append("=== END OF SYSTEM REPORT ===\n");
                report.append("=".repeat(55)).append("\n");
                report.append("Report generated successfully!\n");
                report.append("All data saved to database.\n");

                reportArea.setText(report.toString());

            } catch (Exception ex) {
                reportArea.setText("Error generating system report: " + ex.getMessage());
            }
        });

        // Add individual report buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        JButton bedsBtn = new JButton("Check Available Beds");
        bedsBtn.addActionListener(e -> {
            try {
                hospitalDAO.checkAvailableBeds();
                showSuccess("Available beds checked successfully!");
            } catch (Exception ex) {
                showError("Error checking beds: " + ex.getMessage());
            }
        });

        JButton patientsBtn = new JButton("Count Patients");
        patientsBtn.addActionListener(e -> {
            try {
                int count = patientDAO.countPatients();
                showSuccess("Total Patients: " + count);
            } catch (Exception ex) {
                showError("Error counting patients: " + ex.getMessage());
            }
        });

        JButton appointmentsBtn = new JButton("Count Appointments");
        appointmentsBtn.addActionListener(e -> {
            try {
                int count = appointmentDAO.countAppointment();
                showSuccess("Total Appointments: " + count);
            } catch (Exception ex) {
                showError("Error counting appointments: " + ex.getMessage());
            }
        });

        JButton revenueBtn = new JButton("Calculate Revenue");
        revenueBtn.addActionListener(e -> {
            try {
                double revenue = billingDAO.calculateTotalRevenue();
                showSuccess(String.format("Total Revenue: %.2f EGP", revenue));
            } catch (Exception ex) {
                showError("Error calculating revenue: " + ex.getMessage());
            }
        });

        JButton ambulancesBtn = new JButton("Check Ambulances");
        ambulancesBtn.addActionListener(e -> {
            try {
                int count = trackingDAO.getActiveAmbulances();
                showSuccess("Active Ambulances: " + count);
            } catch (Exception ex) {
                showError("Error getting ambulance status: " + ex.getMessage());
            }
        });

        buttonPanel.add(bedsBtn);
        buttonPanel.add(patientsBtn);
        buttonPanel.add(appointmentsBtn);
        buttonPanel.add(revenueBtn);
        buttonPanel.add(ambulancesBtn);
        buttonPanel.add(generateBtn); // Full report button at the end

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("System Reports - Select an option:"), BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void saveAndExit() {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Save all data and exit system?", "Exit System",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            showSuccess("Saving all system data to database...\n" +
                    "✅ All Data Saved Successfully to Database\n\n" +
                    "🚀 HELIX Smart Healthcare System\n" +
                    "   Redefining healthcare through intelligent systems.");
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ex) {
                // Ignore
            }
            System.exit(0);
        }
    }

    // ======================= SCREEN CREATION METHODS =======================

    private JPanel createDashboardScreen() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("HELIX System Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        dashboard.add(title, BorderLayout.NORTH);

        // Welcome message
        JTextArea welcomeArea = new JTextArea(15, 70);
        welcomeArea.setEditable(false);
        welcomeArea.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeArea.setLineWrap(true);
        welcomeArea.setWrapStyleWord(true);
        welcomeArea.setText(
                "Welcome to HELIX Smart Healthcare System!\n\n" +
                        "This system provides comprehensive healthcare management including:\n\n" +
                        "• Patient Registration and Management\n" +
                        "• Doctor and Nurse Staff Management\n" +
                        "• Appointment Scheduling\n" +
                        "• Medical Records and Test Results\n" +
                        "• Hospital Room and Bed Management\n" +
                        "• Operation Scheduling\n" +
                        "• Medication Schedules\n" +
                        "• Ambulance Tracking and ETA\n" +
                        "• Patient Billing and Invoicing\n" +
                        "• Notifications and Reminders\n\n" +
                        "Use the navigation panel on the left to access all features.\n" +
                        "All data is automatically saved to the database.\n\n" +
                        "Status: System ONLINE | Database: CONNECTED"
        );

        dashboard.add(new JScrollPane(welcomeArea), BorderLayout.CENTER);

        // Quick stats
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statsPanel.add(createStatBox("Total Patients", "156"));
        statsPanel.add(createStatBox("Active Staff", "72"));
        statsPanel.add(createStatBox("Today's Appointments", "24"));
        statsPanel.add(createStatBox("Available Beds", "12"));

        dashboard.add(statsPanel, BorderLayout.SOUTH);

        return dashboard;
    }

    private JPanel createStatBox(String title, String value) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        box.setBackground(new Color(240, 245, 250));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(new Color(0, 102, 204));

        box.add(titleLabel);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
        box.add(valueLabel);

        return box;
    }

    // ======================= HELPER METHODS =======================

    private JDialog createDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(mainFrame, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(mainFrame);
        return dialog;
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private boolean parseBoolean(String s) {
        if (s == null) return false;
        s = s.toLowerCase().trim();
        return s.equals("yes") || s.equals("y") || s.equals("true") || s.equals("1");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Error setting look and feel: " + e.getMessage());
            }
            new HelixGUI();
        });
    }
}