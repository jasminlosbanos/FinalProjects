package jas;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Projects extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private EmployeeDirectory edParent;
	private JTextField TbxSearch;
	private JTable TblProjects;
	private DefaultTableModel model;
	private int projectId;

	/**
	 * Create the frame.
	 */
	public Projects(EmployeeDirectory ed) {
		this.edParent = ed;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1000, 500);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblProjects = new JLabel("PROJECTS");
		lblProjects.setFont(new Font("Century Gothic", Font.BOLD, 22));
		lblProjects.setBounds(10, 11, 437, 28);
		contentPane.add(lblProjects);
		
		JLabel lblNewLabel_1 = new JLabel("SEARCH");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_1.setBounds(684, 18, 64, 14);
		contentPane.add(lblNewLabel_1);
		
		TbxSearch = new JTextField();
		TbxSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String search = TbxSearch.getText();
				
				if (search.isBlank() || search.isEmpty()) {
					loadProjects();
				} else {
					searchProject(search);
				}
			}
		});
		TbxSearch.setFont(new Font("Tahoma", Font.PLAIN, 12));
		TbxSearch.setColumns(10);
		TbxSearch.setBounds(744, 15, 230, 20);
		contentPane.add(TbxSearch);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 50, 964, 400);
		contentPane.add(scrollPane);
		
		String[] columns = {"ID", "PROJECT NAME", "PROJECT LEADER"};
		model = new DefaultTableModel(columns, 0);
		TblProjects = new JTable(model);
		TblProjects.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selected = TblProjects.getSelectedRow();

				if (selected >= 0) {
					String idStr = TblProjects.getValueAt(selected, 0).toString();
					projectId = Integer.parseInt(idStr);
					String project = TblProjects.getValueAt(selected, 1).toString();
					String leader = TblProjects.getValueAt(selected, 2).toString();
					
					int option = JOptionPane.showOptionDialog(
							TblProjects,
							"Delete \"" + project.toUpperCase() + "\"?",
							"Options",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							new String[]{"Delete", "Cancel"},
							"Cancel"
						);
					
					if (option == 0 && projectId != 0) {
						deleteProject(projectId);
					}
				}
			}
		});
		scrollPane.setViewportView(TblProjects);
		
		loadProjects();
	}
	
	private void loadProjects() {
		model.setRowCount(0);
		boolean found = false;
		String query = "SELECT * FROM projects JOIN employees ON projects.leader=employees.id";
		
		try {
			Connection connection = DBConnection.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery(query);
			
			while (res.next()) {
				found = true;
				model.addRow(new Object[] {
						res.getInt("id"),
						res.getString("projName").toUpperCase(),
						res.getString("name").toUpperCase()
				});
			}
			
			if (!found) {
				model.addRow(new Object[] {
						"No records found...",
						"",
						""
				});
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblProjects, "Failed: " + e.getMessage());
		}
	}
	
	private void searchProject(String searchValue) {
		model.setRowCount(0);
		boolean found = false;
		String search = "%" + searchValue + "%";
		String query = "SELECT * FROM projects JOIN employees ON projects.leader=employees.id WHERE CONCAT(name, projName) LIKE ?";
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setString(1, search);
			ResultSet res = pstmt.executeQuery();
			
			while (res.next()) {
				found = true;
				model.addRow(new Object[] {
						res.getInt("id"),
						res.getString("projName").toUpperCase(),
						res.getString("name").toUpperCase()
				});
			}
			
			if (!found) {
				model.addRow(new Object[] {
						"No records found...",
						"",
						""
				});
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblProjects, "Failed: " + e.getMessage());
		}
	}
	
	private void deleteProject(int id) {
		String query = "DELETE FROM projects WHERE id=?";
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, id);
			int affected = pstmt.executeUpdate();
			
			if (affected > 0) {
				JOptionPane.showMessageDialog(TblProjects, "ALERT: Project deleted!");
				loadProjects();
				edParent.loadDirectory();
			} else {
				JOptionPane.showMessageDialog(TblProjects, "Failed to delete project...");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblProjects, "Failed: " + e.getMessage());
		}
	}

}
