package jas;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.zip.Inflater;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.spec.EdDSAParameterSpec;

public class Employees extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField TbxSearch;
	private JTable TblEmployees;
	private DefaultTableModel model;
	private JTextField TbxName;
	private JTextField TbxContact;
	private JComboBox CbxDept;
	private JPanel panel;
	private JButton BtnAdd;
	private JButton BtnUpdate;
	private JButton BtnDelete;
	private JButton BtnAssign;
	private int employeeId;
	private EmployeeDirectory edParent;

	/**
	 * Create the frame.
	 */
	public Employees(EmployeeDirectory ed) {
		this.edParent = ed;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1000, 500);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("EMPLOYEES");
		lblNewLabel.setFont(new Font("Century Gothic", Font.BOLD, 22));
		lblNewLabel.setBounds(10, 11, 437, 28);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("SEARCH");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_1.setBounds(684, 18, 64, 14);
		contentPane.add(lblNewLabel_1);
		
		TbxSearch = new JTextField();
		TbxSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String search = TbxSearch.getText();
				
				if (search.equals("")) {
					loadEmployees();
				} else {
					searchEmployee(search);
				}
			}
		});
		TbxSearch.setFont(new Font("Tahoma", Font.PLAIN, 12));
		TbxSearch.setBounds(744, 15, 230, 20);
		contentPane.add(TbxSearch);
		TbxSearch.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 50, 660, 400);
		contentPane.add(scrollPane);
		
		String[] columnNames = {"ID", "NAME", "DEPARTMENT", "CONTACT NO."};
		model = new DefaultTableModel(columnNames, 0);
		TblEmployees = new JTable(model);
		TblEmployees.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selected = TblEmployees.getSelectedRow();

				if (selected >= 0) {
					String idStr = TblEmployees.getValueAt(selected, 0).toString();
					employeeId = Integer.parseInt(idStr);
					String name = TblEmployees.getValueAt(selected, 1).toString();
					String dept = TblEmployees.getValueAt(selected, 2).toString();
					String contact = TblEmployees.getValueAt(selected, 3).toString();
					
					populate(name, dept, contact);
				}
			}
		});
		scrollPane.setViewportView(TblEmployees);
		
		panel = new JPanel();
		panel.setBorder(new LineBorder(Color.BLACK));
		panel.setBounds(684, 50, 290, 400);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("EMPLOYEE INFORMATION");
		lblNewLabel_2.setBounds(61, 11, 168, 19);
		lblNewLabel_2.setFont(new Font("Century Gothic", Font.BOLD, 14));
		panel.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("NAME");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_3.setBounds(10, 41, 46, 14);
		panel.add(lblNewLabel_3);
		
		TbxName = new JTextField();
		TbxName.setBounds(10, 66, 270, 20);
		panel.add(TbxName);
		TbxName.setColumns(10);
		
		JLabel lblNewLabel_3_1 = new JLabel("CONTACT");
		lblNewLabel_3_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_3_1.setBounds(10, 121, 82, 14);
		panel.add(lblNewLabel_3_1);
		
		TbxContact = new JTextField();
		TbxContact.setColumns(10);
		TbxContact.setBounds(10, 146, 270, 20);
		panel.add(TbxContact);
		
		JLabel lblNewLabel_3_1_1 = new JLabel("DEPARTMENT");
		lblNewLabel_3_1_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_3_1_1.setBounds(10, 205, 82, 14);
		panel.add(lblNewLabel_3_1_1);
		
		String[] departments = {"IT", "Finance", "Marketing", "Sales", "Graphics"};
		CbxDept = new JComboBox(departments);
		CbxDept.setSelectedIndex(-1);
		CbxDept.setFont(new Font("Tahoma", Font.PLAIN, 12));
		CbxDept.setBounds(10, 230, 270, 22);
		panel.add(CbxDept);
		
		BtnAdd = new JButton("ADD");
		BtnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = TbxName.getText();
				String dept = (String)CbxDept.getSelectedItem();
				String contact = TbxContact.getText();
				
				if (name.equals("") && dept == null && contact.equals("")) {
					JOptionPane.showMessageDialog(panel, "Fill up fields...");
				} else {
					Employee employee = new Employee(name, dept, contact);
					addEmployee(employee);
				}
			}
		});
		BtnAdd.setBackground(new Color(102, 153, 255)); 
		BtnAdd.setForeground(Color.WHITE);           
		BtnAdd.setFocusPainted(false);
		BtnAdd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		BtnAdd.setBounds(10, 263, 270, 23);
		panel.add(BtnAdd);
		
		BtnUpdate = new JButton("UPDATE");
		BtnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = TbxName.getText();
				String dept = (String)CbxDept.getSelectedItem();
				String contact = TbxContact.getText();
				
				if (name.equals("") && dept == null && contact.equals("") && employeeId != 0) {
					JOptionPane.showMessageDialog(panel, "Fill up fields...");
				} else {
					Employee employee = new Employee(name, dept, contact);
					updateEmployee(employee, employeeId);
				}
			}
		});
		BtnUpdate.setBackground(new Color(102, 153, 255)); 
		BtnUpdate.setForeground(Color.WHITE);           
		BtnUpdate.setFocusPainted(false);
		BtnUpdate.setEnabled(false);
		BtnUpdate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		BtnUpdate.setBounds(10, 366, 270, 23);
		panel.add(BtnUpdate);
		
		BtnDelete = new JButton("DELETE");
		BtnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (employeeId != 0) {
					deleteEmployee(employeeId);
				}
			}
		});
		BtnDelete.setEnabled(false);
		BtnDelete.setForeground(Color.WHITE);
		BtnDelete.setFont(new Font("Tahoma", Font.PLAIN, 12));
		BtnDelete.setFocusPainted(false);
		BtnDelete.setBackground(new Color(102, 153, 255));
		BtnDelete.setBounds(10, 334, 270, 23);
		panel.add(BtnDelete);
		
		BtnAssign = new JButton("ASSIGN");
		BtnAssign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String type;
				int option = JOptionPane.showOptionDialog(
						panel,
						"Where do you want to assign \"" + TbxName.getText().toUpperCase() + "\"?",
						"Options",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						new String[]{"EXISTING PROJECT", "NEW PROJECT", "Cancel"},
						"Cancel"
					);
				
				if (option == 0) {
					type = "existing";
					new AssignEmployee(ed, type, employeeId, TbxName.getText()).setVisible(true);
				} else if (option == 1) {
					type = "new";
					new AssignEmployee(ed, type, employeeId, TbxName.getText()).setVisible(true);;
				}
			}
		});
		BtnAssign.setEnabled(false);
		BtnAssign.setForeground(Color.WHITE);
		BtnAssign.setFont(new Font("Tahoma", Font.PLAIN, 12));
		BtnAssign.setFocusPainted(false);
		BtnAssign.setBackground(new Color(102, 153, 255));
		BtnAssign.setBounds(10, 297, 270, 23);
		panel.add(BtnAssign);
		
		loadEmployees();
	}
	
	private void loadEmployees() {
		model.setRowCount(0);
		boolean found = false;
		String query = "SELECT * FROM employees";
		
		try {
			Connection connection = DBConnection.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery(query);
			
			while (res.next()) {
				found = true;
				model.addRow(new Object[] {
						res.getInt("id"),
						res.getString("name").toUpperCase(),
						res.getString("department"),
						res.getString("contactNo")
				});
			}
			
			if (!found) {
				model.addRow(new Object[] {
						"No records found...",
						"",
						"",
						""
				});
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblEmployees, "Failed: " + e.getMessage());
		}
	}
	
	private void searchEmployee(String searchValue) {
		model.setRowCount(0);
		String search = "%" + searchValue + "%";
		boolean found = false;
		String query = "SELECT * FROM employees WHERE name LIKE ?";
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setString(1, search);
			ResultSet res = pstmt.executeQuery();
			
			while (res.next()) {
				found = true;
				model.addRow(new Object[] {
						res.getInt("id"),
						res.getString("name").toUpperCase(),
						res.getString("department"),
						res.getString("contactNo")
				});
			}
			
			if (!found) {
				model.addRow(new Object[] {
						"No records found...",
						"",
						"",
						""
				});
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblEmployees, "Failed: " + e.getMessage());
		}
	}
	
	private void addEmployee(Employee em) {
		String existing = "SELECT * FROM employees WHERE name LIKE ?";
		String add = "INSERT INTO employees (name, department, contactNo) VALUES (?,?,?)";
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement existingPstmt = connection.prepareStatement(existing);
			existingPstmt.setString(1, em.getName().toLowerCase());
			ResultSet res = existingPstmt.executeQuery();
			
			if (res.next()) {
				JOptionPane.showMessageDialog(TblEmployees, "Employee already added: " + em.getName().toUpperCase());
			} else {
				PreparedStatement pstmt = connection.prepareStatement(add);
				pstmt.setString(1, em.getName().toLowerCase());
				pstmt.setString(2, em.getDept());
				pstmt.setString(3, em.getContact());
				int affected = pstmt.executeUpdate();
				
				if (affected > 0) {
					JOptionPane.showMessageDialog(TblEmployees, "New employee: " + em.getName().toUpperCase());
					clearFields();
					loadEmployees();
				} else {
					JOptionPane.showMessageDialog(panel, "Failed to add: " + em.getName().toUpperCase());
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(panel, "Failed: " + e.getMessage());
		}
	}
	
	private void deleteEmployee(int emId) {
		String query = "DELETE FROM employees WHERE id = ?";
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, emId);
			int affected = pstmt.executeUpdate();
			
			if (affected > 0) {
				JOptionPane.showMessageDialog(TblEmployees, "Employee deleted!");
				loadEmployees();
				clearFields();
				edParent.loadDirectory();
			} else {
				JOptionPane.showMessageDialog(TblEmployees, "Failed to delete!");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblEmployees, "Failed: " + e.getMessage());
		}
	}
	
	private void updateEmployee(Employee em, int id) {
		String update = "UPDATE employees SET name=?, department=?, contactNo=? WHERE id=?";
		
		try {
			Connection connection = DBConnection.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(update);
			pstmt.setString(1, em.getName().toLowerCase());
			pstmt.setString(2, em.getDept());
			pstmt.setString(3, em.getContact());
			pstmt.setInt(4, id);
			int affected = pstmt.executeUpdate();
			
			if (affected > 0) {
				JOptionPane.showMessageDialog(TblEmployees, "Information updated: " + em.getName().toUpperCase());
				loadEmployees();
				clearFields();
				edParent.loadDirectory();
			} else {
				JOptionPane.showMessageDialog(TblEmployees, "Failed to update: " + em.getName().toUpperCase());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblEmployees, "Failed: " + e.getMessage());
		}
	}
	
	private void clearFields() {
		TbxName.setText("");
		TbxContact.setText("");
		CbxDept.setSelectedIndex(-1);
		BtnAdd.setEnabled(true);
		BtnUpdate.setEnabled(false);
		BtnDelete.setEnabled(false);
		BtnAssign.setEnabled(false);
	}
	
	private void populate(String name, String dept, String contact) {
		TbxName.setText(name);
		TbxContact.setText(contact);
		CbxDept.setSelectedItem(dept);
		BtnAdd.setEnabled(false);
		BtnUpdate.setEnabled(true);
		BtnDelete.setEnabled(true);
		BtnAssign.setEnabled(true);
	}
}

class Employee {
	String name;
	String dept;
	String contact;
	
	public Employee(String name, String dept, String contact) {
		this.name = name;
		this.dept = dept;
		this.contact = contact;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDept() {
		return dept;
	}
	
	public String getContact() {
		return contact;
	}
}
