package excel;

import java.sql.*;
import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
public class ReadExcel {

	public List<Map<String, Object>> readExcel(String filename) {
		List<Map<String, Object>> tbStudent = new ArrayList<Map<String, Object>>();

		try {
			FileInputStream fis = new FileInputStream(filename);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);

			// System.out.println(sheet.getFirstRowNum()); // 0, column header, id, name,
			// sheet.getLastRowNum(), 3

			for (int i = 1; i <= 3; i++) {
				Map<String, Object> student = new HashMap<String, Object>();

				XSSFRow row = sheet.getRow(i);

				for (int j = 0; j < 6; j++) {
					XSSFCell cell = row.getCell(j);
					cell.setCellType(CellType.STRING);
					String value = cell.getStringCellValue().trim();
					String name = null;
					switch (j) {
					case 0:
						name = "id";
						break;
					case 1:
						name = "name";
						break;
					case 2:
						name = "gender";
						break;
					case 3:
						name = "class";
						break;
					case 4:
						name = "mobile";
						break;
					case 5:
						name = "email";
						break;
					}
					student.put(name, value);
				}
				tbStudent.add(student);
			}

			workbook.close();
			fis.close();

			System.out.println(tbStudent);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbStudent;
	}

	public void writeToMySQL(List<Map<String, Object>> tbStudent) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/assignment", "java", "java12345");

			PreparedStatement pstmtInsert = con
					.prepareStatement("insert into tbStudent(id,name,gender,class,mobile,email) values(?,?,?,?,?,?)");

			PreparedStatement pstmtUpdate = con.prepareStatement(
					"update tbStudent set name = ?, gender = ?, class = ?, mobile = ?, email = ? where id = ?");

			PreparedStatement pstmtSelect = con.prepareStatement("select id from tbStudent where id=?");

			for (Map<String, Object> student : tbStudent) {

				pstmtSelect.setString(1, student.get("id").toString());
				ResultSet rs = pstmtSelect.executeQuery();
				boolean exist = rs.next();
				rs.close();

				if (exist) { // exist
					pstmtUpdate.setString(1, student.get("name").toString());
					pstmtUpdate.setInt(2, student.get("gender").toString().equals("男") ? 1 : 0);
					pstmtUpdate.setString(3, student.get("class").toString());
					pstmtUpdate.setString(4, student.get("mobile").toString());
					pstmtUpdate.setString(5, student.get("email").toString());
					pstmtUpdate.setString(6, student.get("id").toString());

					int rows = pstmtUpdate.executeUpdate();
					System.out.println("update " + rows + " rows, " + student);
				} else {
					pstmtInsert.setString(1, student.get("id").toString());
					pstmtInsert.setString(2, student.get("name").toString());
					pstmtInsert.setInt(3, student.get("gender").toString().equals("男") ? 1 : 0);
					pstmtInsert.setString(4, student.get("class").toString());
					pstmtInsert.setString(5, student.get("mobile").toString());
					pstmtInsert.setString(6, student.get("email").toString());

					int rows = pstmtInsert.executeUpdate();
					System.out.println("insert " + rows + " rows, " + student);
				}
			}

			pstmtInsert.close();
			pstmtUpdate.close();
			pstmtSelect.close();

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void go() {
		writeToMySQL(readExcel("list.xlsx"));
		System.out.println("done.");
	}

	public static void main(String[] args) {
		new ReadExcel().go();
	}
}
