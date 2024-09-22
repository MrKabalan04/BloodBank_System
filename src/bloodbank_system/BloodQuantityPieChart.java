/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bloodbank_system;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BloodQuantityPieChart {

    // Method to return a ChartPanel, which will be embedded in your panel
    public ChartPanel createChartPanel() {
        // Create the dataset (data for the pie chart)
        DefaultPieDataset dataset = createDataset();
        
        // Create the pie chart using the dataset
        JFreeChart chart = ChartFactory.createPieChart(
                "Blood Quantity by Blood Group",   // Chart title
                dataset,          // Dataset
                true,             // Show legend
                true,             // Use tooltips
                false             // URLs not needed
        );

        // Customize the chart (optional)
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("A+", new java.awt.Color(255, 102, 102));  // Red for A+
        plot.setSectionPaint("B+", new java.awt.Color(102, 102, 255));  // Blue for B+
        plot.setSectionPaint("O+", new java.awt.Color(102, 102, 255));  // Blue for O+
        plot.setSectionPaint("AB+", new java.awt.Color(102, 102, 255));  // Blue for AB+
        plot.setSectionPaint("A-", new java.awt.Color(102, 102, 255));  // Blue for A-
        plot.setSectionPaint("B-", new java.awt.Color(102, 102, 255));  // Blue for B-
        plot.setSectionPaint("O-", new java.awt.Color(102, 102, 255));  // Blue for O-
        plot.setSectionPaint("AB-", new java.awt.Color(102, 102, 255));  // Blue for AB-
        
        
        // Customize other blood groups similarly...

        // Create and return a ChartPanel with the pie chart
        return new ChartPanel(chart);
    }

    // Method to create the dataset (blood quantities for each group)
    private DefaultPieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try {
            // Query to fetch blood group quantities
            String sql = "SELECT blood_group, SUM(quantity_ml) as total_quantity FROM blood GROUP BY blood_group";

            // Establishing database connection and executing the query
            Connection con = DatabaseConnection.getConnection(); // Assumes you have this method
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            // Add data to the dataset
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
