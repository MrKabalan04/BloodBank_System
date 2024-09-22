/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bloodbank_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class admindash extends javax.swing.JFrame {
    private DefaultTableModel donationTableModel;
    private DefaultTableModel reqeustTableModel;

    public admindash() {
        initComponents();
        Image icon = new ImageIcon(this.getClass().getResource("/pics/blood-icon-11.jpg")).getImage();
        this.setIconImage(icon);

        DonorsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        DonorsTable.getTableHeader().setOpaque(false);
        DonorsTable.getTableHeader().setBackground(new Color(32, 136, 203));

        donationTableModel = new DefaultTableModel(
            new Object[]{"Username", "Email", "Age", "Phone Number", "Gender", "Address", "Blood Type", "Donation History", "Quantity", "Status"},
            0
        );
        DonorsTable.setModel(donationTableModel);
        loadDonorsData();

        reqeustTableModel = new DefaultTableModel(
            new Object[]{"Username", "Email", "Reason", "Blood Group", "Requested Quantity", "Request Date"},
            0
        );
        reqeustAdminTable.setModel(reqeustTableModel);
        loadRequestsData();

        // Add the pie chart for blood quantity
        addPieChartToAdminBloodQuantity();
    }

    // Method to load donors data
    private void loadDonorsData() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT u.username, u.email, u.age, u.phone, u.gender, u.address, u.blood_type, "
                         + "d.donation_date, d.blood_quantity, b.status "
                         + "FROM users u "
                         + "LEFT JOIN donations d ON u.user_id = d.user_id "
                         + "LEFT JOIN blood b ON d.donation_id = b.donation_id "
                         + "ORDER BY d.donation_date DESC"; // Order by donation date
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();

            // Clear existing data
            donationTableModel.setRowCount(0);

            // Add data to the table model
            while (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                int age = rs.getInt("age");
                String phoneNb = rs.getString("phone");
                String gender = rs.getString("gender");
                String address = rs.getString("address");
                String bloodType = rs.getString("blood_type");
                String donationDate = rs.getString("donation_date");
                int bloodQuantity = rs.getInt("blood_quantity");
                String status = rs.getString("status");

                donationTableModel.addRow(new Object[]{
                    username, email, age, phoneNb, gender, address, bloodType, bloodQuantity, donationDate, status
                });
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while loading data: " + e.getMessage());
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

    // Method to load requests data
    private void loadRequestsData() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT u.username, u.email, r.reason, r.blood_group, r.requested_quantity, r.date_of_request "
                         + "FROM users u "
                         + "JOIN requests r ON u.user_id = r.user_id "
                         + "ORDER BY r.date_of_request DESC";
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();

            // Clear existing data
            reqeustTableModel.setRowCount(0);

            // Add data to the table model
            while (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                String reason = rs.getString("reason");
                String bloodGroup = rs.getString("blood_group");
                int requestedQuantity = rs.getInt("requested_quantity");
                String dateOfRequest = rs.getString("date_of_request");

                reqeustTableModel.addRow(new Object[]{
                    username, email, reason, bloodGroup, requestedQuantity, dateOfRequest
                });
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while loading requests: " + e.getMessage());
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

    // Method to add the pie chart to the AdminBloodQuantity panel
    private void addPieChartToAdminBloodQuantity() {
        BloodQuantityPieChart pieChart = new BloodQuantityPieChart();
        ChartPanel chartPanel = pieChart.createChartPanel();
        
        // Add the pie chart to the AdminBloodQuantity panel
        AdminBloodQuantityPanel.removeAll();  // Clear any existing content in the panel
        AdminBloodQuantityPanel.setLayout(new BorderLayout());  // Set layout manager
        AdminBloodQuantityPanel.add(chartPanel, BorderLayout.CENTER);  // Add the chart panel
        AdminBloodQuantityPanel.revalidate();  // Refresh the panel to display the chart
        AdminBloodQuantityPanel.repaint();  // Repaint the panel
    }

    // Inner class to handle the pie chart logic
    public class BloodQuantityPieChart {

        // Method to create a ChartPanel with the pie chart
        public ChartPanel createChartPanel() {
            DefaultPieDataset dataset = createDataset();

            // Create the pie chart using the dataset
            JFreeChart chart = ChartFactory.createPieChart(
                "Blood Quantity by Blood Group",   // Chart title
                dataset,          // Data
                true,             // Include legend
                true,             // Tooltips
                false             // No URLs
            );
            
            // Customize the pie chart (optional)
            
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator("{0}: {1} ml ({2})"));
            plot.setSectionPaint("A+", new java.awt.Color(255, 255, 255));  // white for A+
            plot.setSectionPaint("B+", new java.awt.Color(255, 255, 0));  // yellow for B+
            plot.setSectionPaint("O+", new java.awt.Color(102, 102, 255));  // Blue for O+
            plot.setSectionPaint("AB+", new java.awt.Color(255, 102, 102));  // red for AB+
            plot.setSectionPaint("A-", new java.awt.Color(0,100,0));  // green for A-
            plot.setSectionPaint("B-", new java.awt.Color(0,255,255));  // aqua for B-
            plot.setSectionPaint("O-", new java.awt.Color(148,0,211));  // violette for O-
            plot.setSectionPaint("AB-", new java.awt.Color(139,69,19));  // brown for AB-
            // Add more customizations if needed...

            return new ChartPanel(chart);
        }

        // Method to create the dataset (blood group quantities)
        private DefaultPieDataset createDataset() {
            DefaultPieDataset dataset = new DefaultPieDataset();
            try {
                Connection con = DatabaseConnection.getConnection(); // Use your DB connection class
                String query = "SELECT blood_group, SUM(quantity_ml) as total_quantity FROM blood GROUP BY blood_group";
                PreparedStatement pst = con.prepareStatement(query);
                ResultSet rs = pst.executeQuery();

                // Populate dataset
                while (rs.next()) {
                    String bloodGroup = rs.getString("blood_group");
                    double quantity = rs.getDouble("total_quantity");
                    dataset.setValue(bloodGroup, quantity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dataset;
        }
    }




    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        AdminTitlePanel = new javax.swing.JPanel();
        admintitle = new javax.swing.JLabel();
        AdminMenuPanel = new javax.swing.JPanel();
        adminHomebtn = new javax.swing.JButton();
        admindonorsbtn = new javax.swing.JButton();
        adminBquantityBtn = new javax.swing.JButton();
        adminLogBtn = new javax.swing.JButton();
        adminpatientsbtn = new javax.swing.JButton();
        ChangingPanel = new javax.swing.JPanel();
        AdminHomePanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        AdminPatientsPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        reqeustAdminTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        AdminDonorsPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        DonorsTable = new javax.swing.JTable();
        AdminHospitalPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        AdminBloodQuantityPanel = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        AdminTitlePanel.setBackground(new java.awt.Color(204, 204, 204));

        admintitle.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        admintitle.setText("ADMIN DASHBOARD");

        javax.swing.GroupLayout AdminTitlePanelLayout = new javax.swing.GroupLayout(AdminTitlePanel);
        AdminTitlePanel.setLayout(AdminTitlePanelLayout);
        AdminTitlePanelLayout.setHorizontalGroup(
            AdminTitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AdminTitlePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(admintitle)
                .addGap(511, 511, 511))
        );
        AdminTitlePanelLayout.setVerticalGroup(
            AdminTitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AdminTitlePanelLayout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addComponent(admintitle)
                .addGap(42, 42, 42))
        );

        AdminMenuPanel.setBackground(new java.awt.Color(102, 102, 102));

        adminHomebtn.setBackground(new java.awt.Color(102, 102, 102));
        adminHomebtn.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        adminHomebtn.setForeground(new java.awt.Color(255, 255, 255));
        adminHomebtn.setText("Home");
        adminHomebtn.setBorderPainted(false);
        adminHomebtn.setContentAreaFilled(false);
        adminHomebtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                adminHomebtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                adminHomebtnMouseExited(evt);
            }
        });
        adminHomebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminHomebtnActionPerformed(evt);
            }
        });

        admindonorsbtn.setBackground(new java.awt.Color(102, 102, 102));
        admindonorsbtn.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        admindonorsbtn.setForeground(new java.awt.Color(255, 255, 255));
        admindonorsbtn.setText("Donors");
        admindonorsbtn.setBorderPainted(false);
        admindonorsbtn.setContentAreaFilled(false);
        admindonorsbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                admindonorsbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                admindonorsbtnMouseExited(evt);
            }
        });
        admindonorsbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                admindonorsbtnActionPerformed(evt);
            }
        });

        adminBquantityBtn.setBackground(new java.awt.Color(102, 102, 102));
        adminBquantityBtn.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        adminBquantityBtn.setForeground(new java.awt.Color(255, 255, 255));
        adminBquantityBtn.setText("Blood Quantity");
        adminBquantityBtn.setBorderPainted(false);
        adminBquantityBtn.setContentAreaFilled(false);
        adminBquantityBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                adminBquantityBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                adminBquantityBtnMouseExited(evt);
            }
        });
        adminBquantityBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminBquantityBtnActionPerformed(evt);
            }
        });

        adminLogBtn.setBackground(new java.awt.Color(102, 102, 102));
        adminLogBtn.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        adminLogBtn.setForeground(new java.awt.Color(255, 255, 255));
        adminLogBtn.setText("Logout");
        adminLogBtn.setBorderPainted(false);
        adminLogBtn.setContentAreaFilled(false);
        adminLogBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                adminLogBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                adminLogBtnMouseExited(evt);
            }
        });
        adminLogBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminLogBtnActionPerformed(evt);
            }
        });

        adminpatientsbtn.setBackground(new java.awt.Color(102, 102, 102));
        adminpatientsbtn.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        adminpatientsbtn.setForeground(new java.awt.Color(255, 255, 255));
        adminpatientsbtn.setText("Patients");
        adminpatientsbtn.setBorderPainted(false);
        adminpatientsbtn.setContentAreaFilled(false);
        adminpatientsbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                adminpatientsbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                adminpatientsbtnMouseExited(evt);
            }
        });
        adminpatientsbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminpatientsbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AdminMenuPanelLayout = new javax.swing.GroupLayout(AdminMenuPanel);
        AdminMenuPanel.setLayout(AdminMenuPanelLayout);
        AdminMenuPanelLayout.setHorizontalGroup(
            AdminMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(adminHomebtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(admindonorsbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(adminLogBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(adminpatientsbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
            .addComponent(adminBquantityBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        AdminMenuPanelLayout.setVerticalGroup(
            AdminMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminMenuPanelLayout.createSequentialGroup()
                .addComponent(adminHomebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(adminpatientsbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(admindonorsbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(adminBquantityBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(adminLogBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        ChangingPanel.setBackground(new java.awt.Color(186, 7, 7));
        ChangingPanel.setLayout(new java.awt.CardLayout());

        AdminHomePanel.setBackground(new java.awt.Color(186, 7, 7));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Home");

        javax.swing.GroupLayout AdminHomePanelLayout = new javax.swing.GroupLayout(AdminHomePanel);
        AdminHomePanel.setLayout(AdminHomePanelLayout);
        AdminHomePanelLayout.setHorizontalGroup(
            AdminHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminHomePanelLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 1103, Short.MAX_VALUE))
        );
        AdminHomePanelLayout.setVerticalGroup(
            AdminHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminHomePanelLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 654, Short.MAX_VALUE))
        );

        ChangingPanel.add(AdminHomePanel, "card2");

        AdminPatientsPanel.setBackground(new java.awt.Color(186, 7, 7));

        reqeustAdminTable.setBackground(new java.awt.Color(204, 204, 204));
        reqeustAdminTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Username", "Email", "Age", "Phone_NB", "Gender", "Address", "Blood Type", "Req-History", "Status"
            }
        ));
        reqeustAdminTable.setRowHeight(20);
        reqeustAdminTable.setSelectionBackground(new java.awt.Color(255, 204, 0));
        jScrollPane1.setViewportView(reqeustAdminTable);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Patients Info");

        javax.swing.GroupLayout AdminPatientsPanelLayout = new javax.swing.GroupLayout(AdminPatientsPanel);
        AdminPatientsPanel.setLayout(AdminPatientsPanelLayout);
        AdminPatientsPanelLayout.setHorizontalGroup(
            AdminPatientsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminPatientsPanelLayout.createSequentialGroup()
                .addGap(248, 248, 248)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(248, Short.MAX_VALUE))
            .addGroup(AdminPatientsPanelLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        AdminPatientsPanelLayout.setVerticalGroup(
            AdminPatientsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminPatientsPanelLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(154, 154, 154)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(268, Short.MAX_VALUE))
        );

        ChangingPanel.add(AdminPatientsPanel, "card4");

        AdminDonorsPanel.setBackground(new java.awt.Color(186, 7, 7));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Donors");

        DonorsTable.setBackground(new java.awt.Color(204, 204, 204));
        DonorsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Username", "Email", "Age", "Phone_NB", "Gender", "Address", "Blood Type", "D-History", "Status"
            }
        ));
        DonorsTable.setRowHeight(20);
        DonorsTable.setSelectionBackground(new java.awt.Color(255, 204, 0));
        jScrollPane2.setViewportView(DonorsTable);

        javax.swing.GroupLayout AdminDonorsPanelLayout = new javax.swing.GroupLayout(AdminDonorsPanel);
        AdminDonorsPanel.setLayout(AdminDonorsPanelLayout);
        AdminDonorsPanelLayout.setHorizontalGroup(
            AdminDonorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminDonorsPanelLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 1083, Short.MAX_VALUE))
            .addGroup(AdminDonorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(AdminDonorsPanelLayout.createSequentialGroup()
                    .addGap(248, 248, 248)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 736, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(212, Short.MAX_VALUE)))
        );
        AdminDonorsPanelLayout.setVerticalGroup(
            AdminDonorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminDonorsPanelLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 654, Short.MAX_VALUE))
            .addGroup(AdminDonorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(AdminDonorsPanelLayout.createSequentialGroup()
                    .addGap(156, 156, 156)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(233, Short.MAX_VALUE)))
        );

        ChangingPanel.add(AdminDonorsPanel, "card3");

        AdminHospitalPanel.setBackground(new java.awt.Color(186, 7, 7));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Hospitals");

        javax.swing.GroupLayout AdminHospitalPanelLayout = new javax.swing.GroupLayout(AdminHospitalPanel);
        AdminHospitalPanel.setLayout(AdminHospitalPanelLayout);
        AdminHospitalPanelLayout.setHorizontalGroup(
            AdminHospitalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminHospitalPanelLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 1053, Short.MAX_VALUE))
        );
        AdminHospitalPanelLayout.setVerticalGroup(
            AdminHospitalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminHospitalPanelLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 654, Short.MAX_VALUE))
        );

        ChangingPanel.add(AdminHospitalPanel, "card6");

        AdminBloodQuantityPanel.setBackground(new java.awt.Color(186, 7, 7));

        javax.swing.GroupLayout AdminBloodQuantityPanelLayout = new javax.swing.GroupLayout(AdminBloodQuantityPanel);
        AdminBloodQuantityPanel.setLayout(AdminBloodQuantityPanelLayout);
        AdminBloodQuantityPanelLayout.setHorizontalGroup(
            AdminBloodQuantityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1196, Short.MAX_VALUE)
        );
        AdminBloodQuantityPanelLayout.setVerticalGroup(
            AdminBloodQuantityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 698, Short.MAX_VALUE)
        );

        ChangingPanel.add(AdminBloodQuantityPanel, "card5");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(AdminTitlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(AdminMenuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(ChangingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(AdminTitlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AdminMenuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ChangingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void adminpatientsbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminpatientsbtnActionPerformed
        ChangingPanel.removeAll();
        ChangingPanel.add(AdminPatientsPanel);
        ChangingPanel.repaint();
        ChangingPanel.revalidate();
    }//GEN-LAST:event_adminpatientsbtnActionPerformed

    private void adminpatientsbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminpatientsbtnMouseExited
        adminpatientsbtn.setBackground(new Color(102, 102, 102));
    }//GEN-LAST:event_adminpatientsbtnMouseExited

    private void adminpatientsbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminpatientsbtnMouseEntered
        adminpatientsbtn.setBackground(new Color(204,204,204));
        adminpatientsbtn.setOpaque(true);
    }//GEN-LAST:event_adminpatientsbtnMouseEntered

    private void adminLogBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminLogBtnActionPerformed
        
    this.dispose();

    
    login loginForm = new login();  
    loginForm.setVisible(true);

    
    JOptionPane.showMessageDialog(null, "You have been logged out.");
    }//GEN-LAST:event_adminLogBtnActionPerformed

    private void adminLogBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminLogBtnMouseExited
        adminLogBtn.setBackground(new Color(102, 102, 102));
    }//GEN-LAST:event_adminLogBtnMouseExited

    private void adminLogBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminLogBtnMouseEntered
        adminLogBtn.setBackground(new Color(204,204,204));
        adminLogBtn.setOpaque(true);
    }//GEN-LAST:event_adminLogBtnMouseEntered

    private void adminBquantityBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminBquantityBtnActionPerformed
        ChangingPanel.removeAll();
        ChangingPanel.add(AdminBloodQuantityPanel);
        ChangingPanel.repaint();
        ChangingPanel.revalidate();
    }//GEN-LAST:event_adminBquantityBtnActionPerformed

    private void adminBquantityBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminBquantityBtnMouseExited
        adminBquantityBtn.setBackground(new Color(102, 102, 102));
    }//GEN-LAST:event_adminBquantityBtnMouseExited

    private void adminBquantityBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminBquantityBtnMouseEntered
        adminBquantityBtn.setBackground(new Color(204,204,204));
        adminBquantityBtn.setOpaque(true);
    }//GEN-LAST:event_adminBquantityBtnMouseEntered

    private void admindonorsbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_admindonorsbtnActionPerformed
        ChangingPanel.removeAll();
        ChangingPanel.add(AdminDonorsPanel);
        ChangingPanel.repaint();
        ChangingPanel.revalidate();
    }//GEN-LAST:event_admindonorsbtnActionPerformed

    private void admindonorsbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_admindonorsbtnMouseExited
        admindonorsbtn.setBackground(new Color(102, 102, 102));
    }//GEN-LAST:event_admindonorsbtnMouseExited

    private void admindonorsbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_admindonorsbtnMouseEntered
        admindonorsbtn.setBackground(new Color(204,204,204));
        admindonorsbtn.setOpaque(true);
    }//GEN-LAST:event_admindonorsbtnMouseEntered

    private void adminHomebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminHomebtnActionPerformed
        ChangingPanel.removeAll();
        ChangingPanel.add(AdminHomePanel);
        ChangingPanel.repaint();
        ChangingPanel.revalidate();
    }//GEN-LAST:event_adminHomebtnActionPerformed

    private void adminHomebtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminHomebtnMouseExited
        adminHomebtn.setBackground(new Color(102, 102, 102));
    }//GEN-LAST:event_adminHomebtnMouseExited

    private void adminHomebtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminHomebtnMouseEntered
        adminHomebtn.setBackground(new Color(204,204,204));
        adminHomebtn.setOpaque(true);
    }//GEN-LAST:event_adminHomebtnMouseEntered
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new admindash().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AdminBloodQuantityPanel;
    private javax.swing.JPanel AdminDonorsPanel;
    private javax.swing.JPanel AdminHomePanel;
    private javax.swing.JPanel AdminHospitalPanel;
    private javax.swing.JPanel AdminMenuPanel;
    private javax.swing.JPanel AdminPatientsPanel;
    private javax.swing.JPanel AdminTitlePanel;
    private javax.swing.JPanel ChangingPanel;
    private javax.swing.JTable DonorsTable;
    private javax.swing.JButton adminBquantityBtn;
    private javax.swing.JButton adminHomebtn;
    private javax.swing.JButton adminLogBtn;
    private javax.swing.JButton admindonorsbtn;
    private javax.swing.JButton adminpatientsbtn;
    private javax.swing.JLabel admintitle;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable reqeustAdminTable;
    // End of variables declaration//GEN-END:variables
}
