package jas;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AssignEmployee extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private EmployeeDirectory edParent;
	private JTextField TbxProj;
	private JTextField TbxName;
	private JTextField TbxRole;
	private int projId;


	/**
	 * Create the frame.
	 */
	public AssignEmployee(EmployeeDirectory ed, String type, int id, String name) {
		this.edParent = ed;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 503, 311);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblAssignEmployee = new JLabel("ASSIGN EMPLOYEE");
		lblAssignEmployee.setBounds(146, 10, 191, 28);
		lblAssignEmployee.setFont(new Font("Century Gothic", Font.BOLD, 22));
		contentPane.add(lblAssignEmployee);
		
		JLabel lblNewLabel = new JLabel("PROJECT (FOR EXISTING PROJECTS, ENTER TO SEARCH)");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(10, 51, 464, 14);
		contentPane.add(lblNewLabel);
		
		TbxProj = new JTextField();
		TbxProj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getProjName(TbxProj.getText());
			}
		});
		TbxProj.setFont(new Font("Tahoma", Font.PLAIN, 12));
		TbxProj.setBounds(10, 76, 464, 20);
		contentPane.add(TbxProj);
		TbxProj.setColumns(10);
		
		JLabel lblName = new JLabel("NAME");
		lblName.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblName.setBounds(10, 107, 130, 14);
		contentPane.add(lblName);
		
		TbxName = new JTextField();
		TbxName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		TbxName.setColumns(10);
		TbxName.setBounds(10, 132, 464, 20);
		contentPane.add(TbxName);
		
		JLabel lblRole = new JLabel("ROLE");
		lblRole.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblRole.setBounds(10, 163, 130, 14);
		contentPane.add(lblRole);
		
		TbxRole = new JTextField();
		TbxRole.setFont(new Font("Tahoma", Font.PLAIN, 12));
		TbxRole.setColumns(10);
		TbxRole.setBounds(10, 188, 464, 20);
		contentPane.add(TbxRole);
		
		JButton BtnAssign = new JButton("ASSIGN");
		BtnAssign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String role = TbxRole.getText();
				String project = TbxProj.getText();
				if (type == "new" && !role.equals("")) {
					addProj(TbxProj.getText(), name, id, TbxRole.getText());
				} else {
					JOptionPane.showMessageDialog(contentPane, "Empty fields...");
				}
				
				if (type == "existing" && !role.equals("") && !project.equals("")) {
					assignEmployee(id, projId, TbxRole.getText(), TbxName.getText(), TbxProj.getText());
				} else {
					JOptionPane.showMessageDialog(contentPane, "Empty fields...");
				}
			}
		});
		BtnAssign.setForeground(new Color(255, 255, 255));
		BtnAssign.setBackground(new Color(102, 153, 255));
		BtnAssign.setFont(new Font("Tahoma", Font.PLAIN, 12));
		BtnAssign.setBounds(10, 229, 464, 23);
		contentPane.add(BtnAssign);
		
		if (type == "new") {
			String leader = name.toUpperCase();
			String role = "project leader";
			TbxName.setText(leader);
			TbxName.setEditable(false);
			TbxRole.setText(role.toUpperCase());
			TbxRole.setEditable(false);
		}
		
		if (type == "existing") {
			String employeeName = name.toUpperCase();
			TbxName.setText(employeeName);
			TbxName.setEditable(false);
		}
	}
	
	private void addProj(String project, String leader, int emId, String role) {
		String assigned = "SELECT * FROM projects WHERE LOWER(projName)=? AND leader=?";
		String add = "INSERT INTO projects (projName, leader) VALUES (?,?)";
		int projectId = 0;
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement assignedPstmt = connection.prepareStatement(assigned);
			assignedPstmt.setString(1, project.toLowerCase());
			assignedPstmt.setInt(2, emId);
			ResultSet res = assignedPstmt.executeQuery();
			
			if (res.next()) {
				JOptionPane.showMessageDialog(contentPane, leader.toUpperCase() + " is already assigned to the same project.");
			} else {
				PreparedStatement addPstmt = connection.prepareStatement(add, Statement.RETURN_GENERATED_KEYS);
				addPstmt.setString(1, project.toLowerCase());
				addPstmt.setInt(2, emId);
				int affected = addPstmt.executeUpdate();
				
				if (affected > 0 ) {
					ResultSet keys = addPstmt.getGeneratedKeys();
				    if (keys.next()) {
				        projectId = keys.getInt(1);  
				        JOptionPane.showMessageDialog(contentPane,
				            "Assigned " + leader.toUpperCase() + " as the project leader for project " + project.toUpperCase() + ".");
				    }
				    assignEmployee(emId, projectId, role, project, leader);
				    dispose();
				} else {
					JOptionPane.showMessageDialog(contentPane, "Failed to assign " + leader.toUpperCase() + "... Try again...");
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(contentPane, "Failed: " + e.getMessage());
		}
	}
	
	private void assignEmployee(int emId, int projId, String role, String name, String project) {
		String assigned = "SELECT * FROM assignments WHERE employee=? AND project=?";
		String query = "INSERT INTO assignments (employee, project, role) VALUES (?,?,?)";
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(assigned);
			pstmt.setInt(1, emId);
			pstmt.setInt(2, projId);
			ResultSet res = pstmt.executeQuery();
			
			if (res.next()) {
				JOptionPane.showMessageDialog(contentPane, name.toUpperCase() + " is already assigned in " + project.toUpperCase() + ".");
			} else {
				PreparedStatement addPstmt = connection.prepareStatement(query);
				addPstmt.setInt(1, emId);
				addPstmt.setInt(2, projId);
				addPstmt.setString(3, role);
				int affected = addPstmt.executeUpdate();
				
				if (affected > 0) {
					JOptionPane.showMessageDialog(contentPane, "New assigned employee alert!");
					edParent.loadDirectory();
					dispose();
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(contentPane, "Failed: " + e.getMessage());
		}
	}
	
	private void getProjName(String searchValue) {
		String search = "%" + searchValue + "%";
		String query = "SELECT * FROM projects WHERE projName LIKE ?";
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setString(1, search);
			ResultSet res = pstmt.executeQuery();
			
			if (res.next()) {
				TbxProj.setText(res.getString("projName").toUpperCase());
				projId = res.getInt("id");
			} else {
				TbxProj.setText("");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(contentPane, "Failed: " + e.getMessage());
		}
 	}
}
