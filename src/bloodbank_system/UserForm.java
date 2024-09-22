/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bloodbank_system;

import java.awt.Color;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.IOException;
import org.slf4j.*;
t


public class UserForm extends javax.swing.JFrame {
    private int userId;
    private HashMap<String, Integer> hospitalMap; 

    public UserForm(int userId) {
        this.userId = userId; 
        initComponents();
        Image icon = new ImageIcon(this.getClass().getResource("/pics/blood-icon-11.jpg")).getImage();
        this.setIconImage(icon);
        this.hospitalMap = new HashMap<>();
        populateHospitalComboBox(); 
    }
    
    private void generateUserPDF() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String userQuery = "SELECT username, email, age, phone, address, blood_type FROM users WHERE user_id = ?";
            pst = conn.prepareStatement(userQuery);
            pst.setInt(1, this.userId);
            rs = pst.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                int age = rs.getInt("age");
                String phoneNumber = rs.getString("phone");
                String address = rs.getString("address");
                String bloodType = rs.getString("blood_type");

                // Specify the path where the PDF will be saved
                String dest = "D:\\BloodBankProject_PDFS\\" + username + ".pdf";


                // Create a PDF document
                PdfWriter writer = new PdfWriter(dest);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Add content to the PDF
                document.add(new Paragraph("User Data"));
                document.add(new Paragraph("Username: " + username));
                document.add(new Paragraph("Email: " + email));
                document.add(new Paragraph("Age: " + age));
                document.add(new Paragraph("Phone Number: " + phoneNumber));
                document.add(new Paragraph("Address: " + address));
                document.add(new Paragraph("Blood Type: " + bloodType));

                document.close();
                JOptionPane.showMessageDialog(null, "Your data has been downloaded as PDF: " + dest);
            } else {
                JOptionPane.showMessageDialog(null, "User not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while fetching user data.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database driver not found.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generating PDF: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateHospitalComboBox() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT hospital_id, name FROM hospitals"; 
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();

            
            HosComboBox.removeAllItems();

            
            while (rs.next()) {
                int hospitalId = rs.getInt("hospital_id");
                String hospitalName = rs.getString("name");
                HosComboBox.addItem(hospitalName);
                hospitalMap.put(hospitalName, hospitalId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while loading hospital data.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    

    private void submitDonation() {
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        
        String donationQuantityText = donation_quantity.getText();
        if (donationQuantityText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter the donation quantity.");
            return; 
        }
        int unit = Integer.parseInt(donationQuantityText);

        
        StringBuilder diseaseCheck = new StringBuilder();
        boolean hasDisease = false;

        
        if (Aidsbox.isSelected()) { diseaseCheck.append("AIDS,"); hasDisease = true; }
        if (BleedingBox.isSelected()) { diseaseCheck.append("Bleeding,"); hasDisease = true; }
        if (HeartBox.isSelected()) { diseaseCheck.append("Heart Disease,"); hasDisease = true; }
        if (Allergybox.isSelected()) { diseaseCheck.append("Allergy,"); hasDisease = true; }
        if (HepatitisBox.isSelected()) { diseaseCheck.append("Hepatitis,"); hasDisease = true; }
        if (Pregnancybox.isSelected()) { diseaseCheck.append("Pregnancy,"); hasDisease = true; }
        if (MalariaBox.isSelected()) { diseaseCheck.append("Malaria,"); hasDisease = true; }
        if (VaccinesBox.isSelected()) { diseaseCheck.append("Vaccines,"); hasDisease = true; }
        if (HighBPressureBox.isSelected()) { diseaseCheck.append("High Blood Pressure,"); hasDisease = true; }
        if (TransfusionsBox.isSelected()) { diseaseCheck.append("Transfusions,"); hasDisease = true; }
        if (ZikaBox.isSelected()) { diseaseCheck.append("Zika Virus,"); hasDisease = true; }
        if (checkBoxAnemia.isSelected()) { diseaseCheck.append("Anemia,"); hasDisease = true; }
        if (checkBoxLymphoma.isSelected()) { diseaseCheck.append("Lymphoma,"); hasDisease = true; }
        if (checkBoxLukemia.isSelected()) { diseaseCheck.append("Lukemia,"); hasDisease = true; }
        if (checkBoxMyeloma.isSelected()) { diseaseCheck.append("Myeloma,"); hasDisease = true; }

        
        if (diseaseCheck.length() > 0 && diseaseCheck.charAt(diseaseCheck.length() - 1) == ',') {
            diseaseCheck.setLength(diseaseCheck.length() - 1);
        }

        
        String been_inAfrica = jRadioButton1.isSelected() ? "Y" : "N";
        boolean traveledToAfrica = been_inAfrica.equals("Y");

       
        conn = DatabaseConnection.getConnection();

        
        String bloodGroup = null;
        String userQuery = "SELECT blood_type FROM users WHERE user_id = ?";
        pst = conn.prepareStatement(userQuery);
        pst.setInt(1, this.userId);
        rs = pst.executeQuery();
        if (rs.next()) {
            bloodGroup = rs.getString("blood_type");
        }

        if (bloodGroup == null) {
            JOptionPane.showMessageDialog(null, "User blood group not found.");
            return; 
        }
        
        
        String donationQuery = "INSERT INTO donations (user_id, donation_date, disease_check, blood_quantity, been_to_africa) VALUES (?, NOW(), ?, ?, ?)";
        pst = conn.prepareStatement(donationQuery, Statement.RETURN_GENERATED_KEYS);
        pst.setInt(1, this.userId);
        pst.setString(2, diseaseCheck.toString());
        pst.setInt(3, unit);
        pst.setString(4, been_inAfrica);

        pst.executeUpdate();

        
        ResultSet generatedKeys = pst.getGeneratedKeys();
        if (generatedKeys.next()) {
            long donationId = generatedKeys.getLong(1); 

            
            String bloodQuery = "INSERT INTO blood (blood_group, quantity_ml, donation_id, status) VALUES (?, ?, ?, ?)";
            pst = conn.prepareStatement(bloodQuery);
            pst.setString(1, bloodGroup);
            pst.setInt(2, unit);
            pst.setLong(3, donationId);

            String status = (hasDisease || traveledToAfrica) ? "Rejected" : "Accepted";
            pst.setString(4, status);
            pst.executeUpdate();
        }

        
        JOptionPane.showMessageDialog(null, "Donation recorded successfully!");

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid input for quantity. Please enter a valid number.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error while recording the donation.");
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database driver not found.");
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


    private void Request() {
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

   
    String unitText = unittxtfield.getText();
    if (unitText.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please enter the requested quantity.");
        return; 
    }

    int unit;
    try {
        unit = Integer.parseInt(unitText);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid input for requested quantity. Please enter a valid number.");
        return;
    }

    String reason = ReasonTxtField.getText();
    String bloodType = (String) BgroupComboBox.getSelectedItem();
    String selectedHospitalName = (String) HosComboBox.getSelectedItem();

    try {
        conn = DatabaseConnection.getConnection();

        
        String bloodCheckQuery = "SELECT SUM(quantity_ml) as total_quantity FROM blood WHERE blood_group = ? AND status = 'accepted'";
        pst = conn.prepareStatement(bloodCheckQuery);
        pst.setString(1, bloodType);
        rs = pst.executeQuery();

        int availableBlood = 0;
        if (rs.next()) {
            availableBlood = rs.getInt("total_quantity");
        }

        if (availableBlood < unit) {
            JOptionPane.showMessageDialog(null, "Not enough blood available for this request.");
            return;
        }

        
        String requestQuery = "INSERT INTO requests (user_id, reason, blood_group, date_of_request, requested_quantity, hospital_id) VALUES (?, ?, ?, NOW(), ?, ?)";
        pst = conn.prepareStatement(requestQuery);

        
        int hospitalId = hospitalMap.getOrDefault(selectedHospitalName, -1);

        if (hospitalId == -1) {
            JOptionPane.showMessageDialog(null, "Selected hospital is not valid.");
            return;
        }

        
        pst.setInt(1, this.userId);
        pst.setString(2, reason);
        pst.setString(3, bloodType);
        pst.setInt(4, unit);
        pst.setInt(5, hospitalId); 

        pst.executeUpdate();
        
        String updateBloodQuery = "UPDATE blood SET quantity_ml = quantity_ml - ? WHERE blood_group = ? AND status = 'accepted' LIMIT 1";
        pst = conn.prepareStatement(updateBloodQuery);
        pst.setInt(1, unit);
        pst.setString(2, bloodType);
        pst.executeUpdate();

        JOptionPane.showMessageDialog(null, "Request recorded successfully!");

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error while recording the request.");
    } catch (ClassNotFoundException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database driver class not found.");
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        UserTitlePanel = new javax.swing.JPanel();
        UserTitleLabel = new javax.swing.JLabel();
        UserButtonsPanel = new javax.swing.JPanel();
        UserHomeButton = new javax.swing.JButton();
        UserDonationButton = new javax.swing.JButton();
        UserRequestButton = new javax.swing.JButton();
        UserLogoutButton = new javax.swing.JButton();
        AppointmentsBtn = new javax.swing.JButton();
        UserChangingPanel = new javax.swing.JPanel();
        UserHomePanel = new javax.swing.JPanel();
        PersonalInfoPanel = new javax.swing.JPanel();
        PersonalInfoTitleLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel7 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        SaveButton = new javax.swing.JButton();
        EditButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        UserAppoimentsPanel = new javax.swing.JPanel();
        RequestFormPanel1 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        EhjozBtn = new javax.swing.JButton();
        DateLabel = new javax.swing.JLabel();
        ReasonLabel6 = new javax.swing.JLabel();
        HosComboBox1 = new javax.swing.JComboBox<>();
        ReasonLabel9 = new javax.swing.JLabel();
        jDateChooser = new com.toedter.calendar.JDateChooser();
        ReasonLabel10 = new javax.swing.JLabel();
        StartTimeBox = new javax.swing.JComboBox<>();
        EndTimeBox = new javax.swing.JComboBox<>();
        EhjozBtn1 = new javax.swing.JButton();
        UserDonationPanel = new javax.swing.JPanel();
        DonationFormPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        donation_quantity = new javax.swing.JTextField();
        DonateButton = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        Aidsbox = new javax.swing.JCheckBox();
        checkBoxAnemia = new javax.swing.JCheckBox();
        BleedingBox = new javax.swing.JCheckBox();
        checkBoxLukemia = new javax.swing.JCheckBox();
        checkBoxLymphoma = new javax.swing.JCheckBox();
        checkBoxMyeloma = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        HeartBox = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Allergybox = new javax.swing.JCheckBox();
        HepatitisBox = new javax.swing.JCheckBox();
        HighBPressureBox = new javax.swing.JCheckBox();
        MalariaBox = new javax.swing.JCheckBox();
        Pregnancybox = new javax.swing.JCheckBox();
        TransfusionsBox = new javax.swing.JCheckBox();
        VaccinesBox = new javax.swing.JCheckBox();
        ZikaBox = new javax.swing.JCheckBox();
        UserRequestPanel = new javax.swing.JPanel();
        RequestFormPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        ReasonTxtField = new javax.swing.JTextField();
        SaveButton2 = new javax.swing.JButton();
        ReasonLabel = new javax.swing.JLabel();
        ReasonLabel1 = new javax.swing.JLabel();
        unittxtfield = new javax.swing.JTextField();
        ReasonLabel2 = new javax.swing.JLabel();
        BgroupComboBox = new javax.swing.JComboBox<>();
        ReasonLabel3 = new javax.swing.JLabel();
        HosComboBox = new javax.swing.JComboBox<>();
        ReasonLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        UserTitlePanel.setBackground(new java.awt.Color(204, 204, 204));

        UserTitleLabel.setFont(new java.awt.Font("Haettenschweiler", 0, 48)); // NOI18N
        UserTitleLabel.setText("You don't have to be a doctor to save lives");

        javax.swing.GroupLayout UserTitlePanelLayout = new javax.swing.GroupLayout(UserTitlePanel);
        UserTitlePanel.setLayout(UserTitlePanelLayout);
        UserTitlePanelLayout.setHorizontalGroup(
            UserTitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UserTitlePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(UserTitleLabel)
                .addGap(362, 362, 362))
        );
        UserTitlePanelLayout.setVerticalGroup(
            UserTitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserTitlePanelLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(UserTitleLabel)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        UserButtonsPanel.setBackground(new java.awt.Color(102, 102, 102));
        UserButtonsPanel.setPreferredSize(new java.awt.Dimension(100, 50));

        UserHomeButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        UserHomeButton.setForeground(new java.awt.Color(255, 255, 255));
        UserHomeButton.setText("Home");
        UserHomeButton.setBorder(null);
        UserHomeButton.setContentAreaFilled(false);
        UserHomeButton.setPreferredSize(new java.awt.Dimension(50, 31));
        UserHomeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                UserHomeButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                UserHomeButtonMouseExited(evt);
            }
        });
        UserHomeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserHomeButtonActionPerformed(evt);
            }
        });

        UserDonationButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        UserDonationButton.setForeground(new java.awt.Color(255, 255, 255));
        UserDonationButton.setText("Make a donation");
        UserDonationButton.setBorder(null);
        UserDonationButton.setContentAreaFilled(false);
        UserDonationButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                UserDonationButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                UserDonationButtonMouseExited(evt);
            }
        });
        UserDonationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserDonationButtonActionPerformed(evt);
            }
        });

        UserRequestButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        UserRequestButton.setForeground(new java.awt.Color(255, 255, 255));
        UserRequestButton.setText("Request Blood");
        UserRequestButton.setBorder(null);
        UserRequestButton.setContentAreaFilled(false);
        UserRequestButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                UserRequestButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                UserRequestButtonMouseExited(evt);
            }
        });
        UserRequestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserRequestButtonActionPerformed(evt);
            }
        });

        UserLogoutButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        UserLogoutButton.setForeground(new java.awt.Color(255, 255, 255));
        UserLogoutButton.setText("Logout");
        UserLogoutButton.setBorder(null);
        UserLogoutButton.setContentAreaFilled(false);
        UserLogoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                UserLogoutButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                UserLogoutButtonMouseExited(evt);
            }
        });
        UserLogoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserLogoutButtonActionPerformed(evt);
            }
        });

        AppointmentsBtn.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        AppointmentsBtn.setForeground(new java.awt.Color(255, 255, 255));
        AppointmentsBtn.setText("Appointments");
        AppointmentsBtn.setBorder(null);
        AppointmentsBtn.setContentAreaFilled(false);
        AppointmentsBtn.setPreferredSize(new java.awt.Dimension(50, 31));
        AppointmentsBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                AppointmentsBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                AppointmentsBtnMouseExited(evt);
            }
        });
        AppointmentsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AppointmentsBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout UserButtonsPanelLayout = new javax.swing.GroupLayout(UserButtonsPanel);
        UserButtonsPanel.setLayout(UserButtonsPanelLayout);
        UserButtonsPanelLayout.setHorizontalGroup(
            UserButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserButtonsPanelLayout.createSequentialGroup()
                .addComponent(UserHomeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(91, 91, 91)
                .addComponent(AppointmentsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90)
                .addComponent(UserDonationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(UserRequestButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(206, 206, 206)
                .addComponent(UserLogoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        UserButtonsPanelLayout.setVerticalGroup(
            UserButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(UserHomeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(AppointmentsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(UserLogoutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(UserRequestButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(UserDonationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        UserChangingPanel.setBackground(new java.awt.Color(186, 7, 7));
        UserChangingPanel.setLayout(new java.awt.CardLayout());

        UserHomePanel.setBackground(new java.awt.Color(186, 7, 7));

        PersonalInfoTitleLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        PersonalInfoTitleLabel.setText("Personal Info");

        jLabel2.setText("_______________________________________________________________________");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-male-24.png"))); // NOI18N
        jLabel3.setText("Username");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-email-24.png"))); // NOI18N
        jLabel4.setText("Email");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-address-24.png"))); // NOI18N
        jLabel5.setText("Address");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-passcodes-24.png"))); // NOI18N
        jLabel6.setText("Password");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-age-24.png"))); // NOI18N
        jLabel7.setText("Age");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-gender-24.png"))); // NOI18N
        jLabel8.setText("Gender");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-phone-24.png"))); // NOI18N
        jLabel9.setText("Phone Number");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-error-24.png"))); // NOI18N
        jLabel10.setText("PLEASE DO NOT SHARE YOUR INFO ");

        SaveButton.setBackground(new java.awt.Color(186, 7, 7));
        SaveButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        SaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-save-24.png"))); // NOI18N
        SaveButton.setText("Save");
        SaveButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SaveButton.setContentAreaFilled(false);
        SaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SaveButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SaveButtonMouseExited(evt);
            }
        });

        EditButton.setBackground(new java.awt.Color(186, 7, 7));
        EditButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        EditButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-edit-24.png"))); // NOI18N
        EditButton.setText("Edit");
        EditButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        EditButton.setContentAreaFilled(false);
        EditButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                EditButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                EditButtonMouseExited(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-blood-24.png"))); // NOI18N
        jLabel11.setText("Blood Group");

        javax.swing.GroupLayout PersonalInfoPanelLayout = new javax.swing.GroupLayout(PersonalInfoPanel);
        PersonalInfoPanel.setLayout(PersonalInfoPanelLayout);
        PersonalInfoPanelLayout.setHorizontalGroup(
            PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PersonalInfoPanelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PersonalInfoPanelLayout.createSequentialGroup()
                        .addComponent(EditButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(SaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1)
                    .addComponent(jTextField2)
                    .addComponent(jTextField4)
                    .addComponent(jPasswordField1)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PersonalInfoPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addGap(41, 41, 41))
            .addGroup(PersonalInfoPanelLayout.createSequentialGroup()
                .addGap(178, 178, 178)
                .addComponent(PersonalInfoTitleLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PersonalInfoPanelLayout.setVerticalGroup(
            PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PersonalInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PersonalInfoTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PersonalInfoPanelLayout.createSequentialGroup()
                        .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PersonalInfoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)))
                .addGap(18, 18, 18)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(PersonalInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EditButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(85, 85, 85)
                .addComponent(jLabel10)
                .addContainerGap(105, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout UserHomePanelLayout = new javax.swing.GroupLayout(UserHomePanel);
        UserHomePanel.setLayout(UserHomePanelLayout);
        UserHomePanelLayout.setHorizontalGroup(
            UserHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserHomePanelLayout.createSequentialGroup()
                .addGap(428, 428, 428)
                .addComponent(PersonalInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(429, Short.MAX_VALUE))
        );
        UserHomePanelLayout.setVerticalGroup(
            UserHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PersonalInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        UserChangingPanel.add(UserHomePanel, "card2");

        UserAppoimentsPanel.setBackground(new java.awt.Color(186, 7, 7));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel22.setText("Welcome");

        jLabel25.setText("________________________________________________________________________");

        EhjozBtn.setBackground(new java.awt.Color(186, 7, 7));
        EhjozBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        EhjozBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-pray-24.png"))); // NOI18N
        EhjozBtn.setText("Download");
        EhjozBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        EhjozBtn.setContentAreaFilled(false);
        EhjozBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                EhjozBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                EhjozBtnMouseExited(evt);
            }
        });
        EhjozBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EhjozBtnActionPerformed(evt);
            }
        });

        DateLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        DateLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-age-24.png"))); // NOI18N
        DateLabel.setText("Date");

        ReasonLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ReasonLabel6.setText("Start Time");

        HosComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tebnine", "Meis El Jabal", "Italic", "Hiram", "Bent Jbeil", "Hotel Dieu", "Al Rasoul", "Bahman", "Al Hayat" }));

        ReasonLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ReasonLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-hospital-24.png"))); // NOI18N
        ReasonLabel9.setText("Hospital");

        ReasonLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ReasonLabel10.setText("End Time");

        StartTimeBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8:00", "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "1:00", "1:30" }));

        EndTimeBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8:30", "9:00", "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "1:00", "1:30", "2:00" }));

        EhjozBtn1.setBackground(new java.awt.Color(186, 7, 7));
        EhjozBtn1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        EhjozBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-pray-24.png"))); // NOI18N
        EhjozBtn1.setText("احجز");
        EhjozBtn1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        EhjozBtn1.setContentAreaFilled(false);
        EhjozBtn1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                EhjozBtn1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                EhjozBtn1MouseExited(evt);
            }
        });
        EhjozBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EhjozBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout RequestFormPanel1Layout = new javax.swing.GroupLayout(RequestFormPanel1);
        RequestFormPanel1.setLayout(RequestFormPanel1Layout);
        RequestFormPanel1Layout.setHorizontalGroup(
            RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RequestFormPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RequestFormPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(201, 201, 201))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RequestFormPanel1Layout.createSequentialGroup()
                        .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(RequestFormPanel1Layout.createSequentialGroup()
                                .addComponent(EhjozBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(EhjozBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(RequestFormPanel1Layout.createSequentialGroup()
                                .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RequestFormPanel1Layout.createSequentialGroup()
                                        .addComponent(ReasonLabel10)
                                        .addGap(158, 158, 158))
                                    .addGroup(RequestFormPanel1Layout.createSequentialGroup()
                                        .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(DateLabel)
                                            .addComponent(ReasonLabel6)
                                            .addComponent(ReasonLabel9))
                                        .addGap(142, 142, 142)))
                                .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(EndTimeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(HosComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(StartTimeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))))
                        .addGap(40, 40, 40))))
        );
        RequestFormPanel1Layout.setVerticalGroup(
            RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RequestFormPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addGap(39, 39, 39)
                .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DateLabel))
                .addGap(41, 41, 41)
                .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ReasonLabel6)
                    .addComponent(StartTimeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ReasonLabel10)
                    .addComponent(EndTimeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ReasonLabel9)
                    .addComponent(HosComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(RequestFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EhjozBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EhjozBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(153, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout UserAppoimentsPanelLayout = new javax.swing.GroupLayout(UserAppoimentsPanel);
        UserAppoimentsPanel.setLayout(UserAppoimentsPanelLayout);
        UserAppoimentsPanelLayout.setHorizontalGroup(
            UserAppoimentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserAppoimentsPanelLayout.createSequentialGroup()
                .addGap(425, 425, 425)
                .addComponent(RequestFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(425, Short.MAX_VALUE))
        );
        UserAppoimentsPanelLayout.setVerticalGroup(
            UserAppoimentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserAppoimentsPanelLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(RequestFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        UserChangingPanel.add(UserAppoimentsPanel, "card5");

        UserDonationPanel.setBackground(new java.awt.Color(186, 7, 7));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel12.setText("You are a life saver");

        jLabel13.setText("________________________________________________________________________");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-disease-30.png"))); // NOI18N
        jLabel14.setText("Diseases that you may have");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-africa-24.png"))); // NOI18N
        jLabel15.setText("Have you been in africa in the past 2 years?");

        DonateButton.setBackground(new java.awt.Color(186, 7, 7));
        DonateButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        DonateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-save-24.png"))); // NOI18N
        DonateButton.setText("Donate");
        DonateButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        DonateButton.setContentAreaFilled(false);
        DonateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                DonateButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                DonateButtonMouseExited(evt);
            }
        });
        DonateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DonateButtonActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-rain-gauge-30.png"))); // NOI18N
        jLabel23.setText("Unit (ml)");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Yes");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("No");

        Aidsbox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Aidsbox.setText("AIDS");

        checkBoxAnemia.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        checkBoxAnemia.setText("Anemia");

        BleedingBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BleedingBox.setText("Bleeding disorders");

        checkBoxLukemia.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        checkBoxLukemia.setText("Lukemia");

        checkBoxLymphoma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        checkBoxLymphoma.setText("Lymphoma");

        checkBoxMyeloma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        checkBoxMyeloma.setText("Myeloma");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-virus-24.png"))); // NOI18N
        jLabel24.setText("Cancer's that you may have");

        jLabel16.setText("________________________________________________________________________");

        HeartBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        HeartBox.setText("Heart disease");

        jLabel17.setText("________________________________________________________________________");

        jLabel18.setText("________________________________________________________________________");

        jLabel19.setText("________________________________________________________________________");

        Allergybox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Allergybox.setText("Allergy");
        Allergybox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AllergyboxActionPerformed(evt);
            }
        });

        HepatitisBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        HepatitisBox.setText("Hepatitis");

        HighBPressureBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        HighBPressureBox.setText("High Blood Pressure");

        MalariaBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        MalariaBox.setText("Malaria");

        Pregnancybox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Pregnancybox.setText("Pregnancy");

        TransfusionsBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        TransfusionsBox.setText("Transfusions");
        TransfusionsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TransfusionsBoxActionPerformed(evt);
            }
        });

        VaccinesBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        VaccinesBox.setText("Vaccines");

        ZikaBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ZikaBox.setText("ZIKA Viruse");
        ZikaBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ZikaBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DonationFormPanelLayout = new javax.swing.GroupLayout(DonationFormPanel);
        DonationFormPanel.setLayout(DonationFormPanelLayout);
        DonationFormPanelLayout.setHorizontalGroup(
            DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(DonationFormPanelLayout.createSequentialGroup()
                .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DonationFormPanelLayout.createSequentialGroup()
                        .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(DonationFormPanelLayout.createSequentialGroup()
                                .addGap(149, 149, 149)
                                .addComponent(jLabel12))
                            .addGroup(DonationFormPanelLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(jLabel14)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DonationFormPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
            .addGroup(DonationFormPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DonationFormPanelLayout.createSequentialGroup()
                        .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Aidsbox)
                            .addComponent(Allergybox)
                            .addComponent(Pregnancybox))
                        .addGap(8, 8, 8)
                        .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BleedingBox)
                            .addComponent(HepatitisBox)
                            .addComponent(TransfusionsBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(HeartBox)
                            .addComponent(MalariaBox)
                            .addComponent(VaccinesBox)
                            .addComponent(ZikaBox))
                        .addGap(28, 28, 28))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DonationFormPanelLayout.createSequentialGroup()
                        .addComponent(checkBoxAnemia)
                        .addGap(18, 18, 18)
                        .addComponent(checkBoxLymphoma)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(checkBoxLukemia)
                        .addGap(18, 18, 18)
                        .addComponent(checkBoxMyeloma)
                        .addGap(48, 48, 48))
                    .addGroup(DonationFormPanelLayout.createSequentialGroup()
                        .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(HighBPressureBox)
                            .addComponent(jLabel24)
                            .addComponent(jLabel15))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(DonationFormPanelLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(donation_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44))))
            .addGroup(DonationFormPanelLayout.createSequentialGroup()
                .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DonationFormPanelLayout.createSequentialGroup()
                        .addGap(203, 203, 203)
                        .addComponent(DonateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DonationFormPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jRadioButton1))
                    .addGroup(DonationFormPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jRadioButton2)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        DonationFormPanelLayout.setVerticalGroup(
            DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DonationFormPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Aidsbox)
                    .addComponent(HeartBox)
                    .addComponent(BleedingBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Allergybox)
                    .addComponent(HepatitisBox)
                    .addComponent(MalariaBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Pregnancybox)
                    .addComponent(TransfusionsBox)
                    .addComponent(VaccinesBox))
                .addGap(7, 7, 7)
                .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HighBPressureBox)
                    .addComponent(ZikaBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addGap(13, 13, 13)
                .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxAnemia)
                    .addComponent(checkBoxLymphoma)
                    .addComponent(checkBoxMyeloma)
                    .addComponent(checkBoxLukemia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addGroup(DonationFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(donation_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addGap(5, 5, 5)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addGap(16, 16, 16)
                .addComponent(DonateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout UserDonationPanelLayout = new javax.swing.GroupLayout(UserDonationPanel);
        UserDonationPanel.setLayout(UserDonationPanelLayout);
        UserDonationPanelLayout.setHorizontalGroup(
            UserDonationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserDonationPanelLayout.createSequentialGroup()
                .addGap(428, 428, 428)
                .addComponent(DonationFormPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(421, Short.MAX_VALUE))
        );
        UserDonationPanelLayout.setVerticalGroup(
            UserDonationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DonationFormPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        UserChangingPanel.add(UserDonationPanel, "card3");

        UserRequestPanel.setBackground(new java.awt.Color(186, 7, 7));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel20.setText("You are not alone");

        jLabel21.setText("________________________________________________________________________");

        SaveButton2.setBackground(new java.awt.Color(186, 7, 7));
        SaveButton2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        SaveButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-pray-24.png"))); // NOI18N
        SaveButton2.setText("Request");
        SaveButton2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SaveButton2.setContentAreaFilled(false);
        SaveButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SaveButton2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SaveButton2MouseExited(evt);
            }
        });
        SaveButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButton2ActionPerformed(evt);
            }
        });

        ReasonLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ReasonLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-pencil-24.png"))); // NOI18N
        ReasonLabel.setText("Reason");

        ReasonLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ReasonLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-rain-gauge-30.png"))); // NOI18N
        ReasonLabel1.setText("Unit (ml)");

        ReasonLabel2.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        ReasonLabel2.setText("Every drop of blood is a drop of hope. Stay strong!");

        BgroupComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-" }));

        ReasonLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ReasonLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-blood-24.png"))); // NOI18N
        ReasonLabel3.setText("Blood Group");

        HosComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tebnine", "Meis El Jabal", "Italic", "Hiram", "Bent Jbeil", "Hotel Dieu", "Al Rasoul", "Bahman", "Al Hayat" }));

        ReasonLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ReasonLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pics/icons8-hospital-24.png"))); // NOI18N
        ReasonLabel4.setText("Hospital");

        javax.swing.GroupLayout RequestFormPanelLayout = new javax.swing.GroupLayout(RequestFormPanel);
        RequestFormPanel.setLayout(RequestFormPanelLayout);
        RequestFormPanelLayout.setHorizontalGroup(
            RequestFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(RequestFormPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(RequestFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RequestFormPanelLayout.createSequentialGroup()
                        .addComponent(ReasonLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(RequestFormPanelLayout.createSequentialGroup()
                        .addGroup(RequestFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ReasonLabel)
                            .addComponent(ReasonLabel1)
                            .addComponent(ReasonLabel3)
                            .addComponent(ReasonLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(RequestFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ReasonTxtField)
                            .addComponent(unittxtfield)
                            .addComponent(BgroupComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(HosComboBox, 0, 200, Short.MAX_VALUE))
                        .addGap(40, 40, 40))))
            .addGroup(RequestFormPanelLayout.createSequentialGroup()
                .addGap(163, 163, 163)
                .addComponent(jLabel20)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RequestFormPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SaveButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(197, 197, 197))
        );
        RequestFormPanelLayout.setVerticalGroup(
            RequestFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RequestFormPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addGroup(RequestFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ReasonLabel)
                    .addComponent(ReasonTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(RequestFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ReasonLabel1)
                    .addComponent(unittxtfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(RequestFormPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RequestFormPanelLayout.createSequentialGroup()
                        .addComponent(BgroupComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(HosComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(RequestFormPanelLayout.createSequentialGroup()
                        .addComponent(ReasonLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(ReasonLabel4)))
                .addGap(33, 33, 33)
                .addComponent(ReasonLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(SaveButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );

        javax.swing.GroupLayout UserRequestPanelLayout = new javax.swing.GroupLayout(UserRequestPanel);
        UserRequestPanel.setLayout(UserRequestPanelLayout);
        UserRequestPanelLayout.setHorizontalGroup(
            UserRequestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UserRequestPanelLayout.createSequentialGroup()
                .addContainerGap(425, Short.MAX_VALUE)
                .addComponent(RequestFormPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(425, 425, 425))
        );
        UserRequestPanelLayout.setVerticalGroup(
            UserRequestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserRequestPanelLayout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addComponent(RequestFormPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        UserChangingPanel.add(UserRequestPanel, "card4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(UserTitlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(UserChangingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
            .addComponent(UserButtonsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1354, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(UserTitlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(UserButtonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(UserChangingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void UserLogoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserLogoutButtonActionPerformed
        this.dispose();
        login loginForm = new login();  
        loginForm.setVisible(true);
        JOptionPane.showMessageDialog(null, "You have been logged out.");
    }//GEN-LAST:event_UserLogoutButtonActionPerformed

    private void UserDonationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserDonationButtonActionPerformed
        UserChangingPanel.removeAll();
        UserChangingPanel.add(UserDonationPanel);
        UserChangingPanel.repaint();
        UserChangingPanel.revalidate();
    }//GEN-LAST:event_UserDonationButtonActionPerformed

    private void UserHomeButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UserHomeButtonMouseEntered
        UserHomeButton.setBackground(new Color(204,204,204));
        UserHomeButton.setOpaque(true);
    }//GEN-LAST:event_UserHomeButtonMouseEntered

    private void UserHomeButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UserHomeButtonMouseExited
        UserHomeButton.setBackground(new Color(102, 102, 102));
    }//GEN-LAST:event_UserHomeButtonMouseExited

    private void UserDonationButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UserDonationButtonMouseEntered
        UserDonationButton.setBackground(new Color(204,204,204));
        UserDonationButton.setOpaque(true);
    }//GEN-LAST:event_UserDonationButtonMouseEntered

    private void UserDonationButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UserDonationButtonMouseExited
        UserDonationButton.setBackground(new Color(102, 102, 102));
    }//GEN-LAST:event_UserDonationButtonMouseExited

    private void UserRequestButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UserRequestButtonMouseEntered
        UserRequestButton.setBackground(new Color(204,204,204));
        UserRequestButton.setOpaque(true);
    }//GEN-LAST:event_UserRequestButtonMouseEntered

    private void UserRequestButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UserRequestButtonMouseExited
        UserRequestButton.setBackground(new Color(102, 102, 102));
    }//GEN-LAST:event_UserRequestButtonMouseExited

    private void UserLogoutButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UserLogoutButtonMouseEntered
        UserLogoutButton.setBackground(new Color(204,204,204));
        UserLogoutButton.setOpaque(true);
    }//GEN-LAST:event_UserLogoutButtonMouseEntered

    private void UserLogoutButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UserLogoutButtonMouseExited
        UserLogoutButton.setBackground(new Color(102, 102, 102));
    }//GEN-LAST:event_UserLogoutButtonMouseExited

    private void EditButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditButtonMouseEntered
        EditButton.setBackground(new Color(102, 102, 102));
        EditButton.setOpaque(true);
    }//GEN-LAST:event_EditButtonMouseEntered

    private void EditButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EditButtonMouseExited
        EditButton.setBackground(new Color(186,7,7));
    }//GEN-LAST:event_EditButtonMouseExited

    private void SaveButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SaveButtonMouseEntered
        SaveButton.setBackground(new Color(102, 102, 102));
        SaveButton.setOpaque(true);
    }//GEN-LAST:event_SaveButtonMouseEntered

    private void SaveButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SaveButtonMouseExited
        SaveButton.setBackground(new Color(186,7,7));
    }//GEN-LAST:event_SaveButtonMouseExited

    private void UserHomeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserHomeButtonActionPerformed
        UserChangingPanel.removeAll();
        UserChangingPanel.add(UserHomePanel);
        UserChangingPanel.repaint();
        UserChangingPanel.revalidate();
    }//GEN-LAST:event_UserHomeButtonActionPerformed

    private void UserRequestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserRequestButtonActionPerformed
        UserChangingPanel.removeAll();
        UserChangingPanel.add(UserRequestPanel);
        UserChangingPanel.repaint();
        UserChangingPanel.revalidate();
    }//GEN-LAST:event_UserRequestButtonActionPerformed

    private void DonateButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DonateButtonMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_DonateButtonMouseEntered

    private void DonateButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DonateButtonMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_DonateButtonMouseExited

    private void TransfusionsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TransfusionsBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TransfusionsBoxActionPerformed

    private void ZikaBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZikaBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ZikaBoxActionPerformed

    private void DonateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DonateButtonActionPerformed
        submitDonation();
    }//GEN-LAST:event_DonateButtonActionPerformed

    private void SaveButton2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SaveButton2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_SaveButton2MouseEntered

    private void SaveButton2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SaveButton2MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_SaveButton2MouseExited

    private void SaveButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButton2ActionPerformed
        Request();
    }//GEN-LAST:event_SaveButton2ActionPerformed

    private void AllergyboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AllergyboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AllergyboxActionPerformed

    private void AppointmentsBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AppointmentsBtnMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_AppointmentsBtnMouseEntered

    private void AppointmentsBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AppointmentsBtnMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_AppointmentsBtnMouseExited

    private void AppointmentsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AppointmentsBtnActionPerformed
        UserChangingPanel.removeAll();
        UserChangingPanel.add(UserAppoimentsPanel);
        UserChangingPanel.repaint();
        UserChangingPanel.revalidate();
    }//GEN-LAST:event_AppointmentsBtnActionPerformed

    private void EhjozBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EhjozBtnMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_EhjozBtnMouseEntered

    private void EhjozBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EhjozBtnMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_EhjozBtnMouseExited

    private void EhjozBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EhjozBtnActionPerformed
        generateUserPDF();
    }//GEN-LAST:event_EhjozBtnActionPerformed

    private void EhjozBtn1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EhjozBtn1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_EhjozBtn1MouseEntered

    private void EhjozBtn1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EhjozBtn1MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_EhjozBtn1MouseExited

    private void EhjozBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EhjozBtn1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EhjozBtn1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox Aidsbox;
    private javax.swing.JCheckBox Allergybox;
    private javax.swing.JButton AppointmentsBtn;
    private javax.swing.JComboBox<String> BgroupComboBox;
    private javax.swing.JCheckBox BleedingBox;
    private javax.swing.JLabel DateLabel;
    private javax.swing.JButton DonateButton;
    private javax.swing.JPanel DonationFormPanel;
    private javax.swing.JButton EditButton;
    private javax.swing.JButton EhjozBtn;
    private javax.swing.JButton EhjozBtn1;
    private javax.swing.JComboBox<String> EndTimeBox;
    private javax.swing.JCheckBox HeartBox;
    private javax.swing.JCheckBox HepatitisBox;
    private javax.swing.JCheckBox HighBPressureBox;
    private javax.swing.JComboBox<String> HosComboBox;
    private javax.swing.JComboBox<String> HosComboBox1;
    private javax.swing.JCheckBox MalariaBox;
    private javax.swing.JPanel PersonalInfoPanel;
    private javax.swing.JLabel PersonalInfoTitleLabel;
    private javax.swing.JCheckBox Pregnancybox;
    private javax.swing.JLabel ReasonLabel;
    private javax.swing.JLabel ReasonLabel1;
    private javax.swing.JLabel ReasonLabel10;
    private javax.swing.JLabel ReasonLabel2;
    private javax.swing.JLabel ReasonLabel3;
    private javax.swing.JLabel ReasonLabel4;
    private javax.swing.JLabel ReasonLabel6;
    private javax.swing.JLabel ReasonLabel9;
    private javax.swing.JTextField ReasonTxtField;
    private javax.swing.JPanel RequestFormPanel;
    private javax.swing.JPanel RequestFormPanel1;
    private javax.swing.JButton SaveButton;
    private javax.swing.JButton SaveButton2;
    private javax.swing.JComboBox<String> StartTimeBox;
    private javax.swing.JCheckBox TransfusionsBox;
    private javax.swing.JPanel UserAppoimentsPanel;
    private javax.swing.JPanel UserButtonsPanel;
    private javax.swing.JPanel UserChangingPanel;
    private javax.swing.JButton UserDonationButton;
    private javax.swing.JPanel UserDonationPanel;
    private javax.swing.JButton UserHomeButton;
    private javax.swing.JPanel UserHomePanel;
    private javax.swing.JButton UserLogoutButton;
    private javax.swing.JButton UserRequestButton;
    private javax.swing.JPanel UserRequestPanel;
    private javax.swing.JLabel UserTitleLabel;
    private javax.swing.JPanel UserTitlePanel;
    private javax.swing.JCheckBox VaccinesBox;
    private javax.swing.JCheckBox ZikaBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox checkBoxAnemia;
    private javax.swing.JCheckBox checkBoxLukemia;
    private javax.swing.JCheckBox checkBoxLymphoma;
    private javax.swing.JCheckBox checkBoxMyeloma;
    private javax.swing.JTextField donation_quantity;
    private com.toedter.calendar.JDateChooser jDateChooser;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField unittxtfield;
    // End of variables declaration//GEN-END:variables
}
