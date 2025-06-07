package jas;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.PublicKey;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Component;

public class EmployeeDirectory extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultTableModel model;
	private JPanel header;
	private JLabel lblNewLabel;
	private JPanel panel;
	private JLabel lblNewLabel_1;
	private JScrollPane scrollPane;
	private JTable TblAssignments;
	private Component horizontalStrut;
	private JButton BtnEmployee;
	private JButton BtnProj;
	private JLabel lblNewLabel_2;
	private JTextField TbxSearch;
	private Component horizontalStrut_1;
	private JButton btnProjects;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				EmployeeDirectory frame = new EmployeeDirectory();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public EmployeeDirectory() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 709, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		header = new JPanel();
		contentPane.add(header, BorderLayout.NORTH);
		
		lblNewLabel = new JLabel("EMPLOYEE DIRECTORY");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
		header.add(lblNewLabel);
		
		horizontalStrut = Box.createHorizontalStrut(50);
		header.add(horizontalStrut);
		
		BtnEmployee = new JButton("EMPLOYEES");
		BtnEmployee.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Employees(EmployeeDirectory.this).setVisible(true);
			}
		});
		header.add(BtnEmployee);
		
		btnProjects = new JButton("PROJECTS");
		btnProjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Projects(EmployeeDirectory.this).setVisible(true);
			}
		});
		header.add(btnProjects);
		
		horizontalStrut_1 = Box.createHorizontalStrut(50);
		header.add(horizontalStrut_1);
		
		lblNewLabel_2 = new JLabel("SEARCH");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		header.add(lblNewLabel_2);
		
		TbxSearch = new JTextField();
		TbxSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String search = TbxSearch.getText();
				
				if (search.isBlank() || search.isEmpty()) {
					loadDirectory();
				} else {
					searchAssignment(search);
				}
			}
		});
		TbxSearch.setFont(new Font("Tahoma", Font.PLAIN, 12));
		header.add(TbxSearch);
		TbxSearch.setColumns(50);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		
		lblNewLabel_1 = new JLabel("BY JASMIN KATE LOS BAÑOS");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.ITALIC, 12));
		panel.add(lblNewLabel_1);
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		String[] columns = {"ID", "EMPLOYEE", "ROLE" ,"PROJECT", "PROJECT LEADER"};
		model = new DefaultTableModel(columns, 0);
		TblAssignments = new JTable(model);
		scrollPane.setViewportView(TblAssignments);
		
		loadDirectory();
		
	}
	
	public void loadDirectory() {
		model.setRowCount(0);
		boolean found = false;
		String query = "SELECT a.id AS id, a.role AS role, " +
	               "em.id AS emid, CONCAT(em.name, ' - ', em.department) AS employee, " +
	               "pr.id AS prid, pr.projName AS project, " +
	               "leaderEmp.name AS leader " +  
	               "FROM assignments a " +
	               "JOIN employees em ON a.employee = em.id " +
	               "JOIN projects pr ON a.project = pr.id " +
	               "JOIN employees leaderEmp ON pr.leader = leaderEmp.id";

		
		try {
			Connection connection = DBConnection.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery(query);
			
			while (res.next()) {
				found = true;
				model.addRow(new Object[] {
						res.getInt("id"),
						res.getString("employee").toUpperCase(),
						res.getString("role").toUpperCase(),
						res.getString("project").toUpperCase(),
						res.getString("leader").toUpperCase()
				});
			}
			
			if (!found) {
				model.addRow(new Object[] { "No records found...", "", "", "", "" });
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblAssignments, "Failed: " + e.getMessage());
		}
	}
	
	private void searchAssignment(String searchValue) {
		model.setRowCount(0);
		boolean found = false;
		String search = "%" + searchValue + "%";
		String query = "SELECT a.id AS id, a.role AS role, " +
	               "em.id AS emid, CONCAT(em.name, ' - ', em.department) AS employee, " +
	               "pr.id AS prid, pr.projName AS project, " +
	               "leaderEmp.name AS leader " +  
	               "FROM assignments a " +
	               "JOIN employees em ON a.employee = em.id " +
	               "JOIN projects pr ON a.project = pr.id " +
	               "JOIN employees leaderEmp ON pr.leader = leaderEmp.id "
	               + "WHERE em.name LIKE ? OR pr.projName LIKE ?";
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setString(1, search);
			pstmt.setString(2, search);
			ResultSet res = pstmt.executeQuery();
			
			while (res.next()) {
				found = true;
				model.addRow(new Object[] {
						res.getInt("id"),
						res.getString("employee").toUpperCase(),
						res.getString("role").toUpperCase(),
						res.getString("project").toUpperCase(),
						res.getString("leader").toUpperCase()
				});
			}
			
			if (!found) {
				model.addRow(new Object[] { "No records found...", "", "", "", "" });
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblAssignments, "Failed: " + e.getMessage());
		}
	}
}
