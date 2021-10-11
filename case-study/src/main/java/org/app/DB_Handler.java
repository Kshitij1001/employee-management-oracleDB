package org.app;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_Handler {
    private final String url = "jdbc:oracle:thin:@//localhost:1521/XEPDB1", user = "hr", pswd = "hr";
    private final Logger logger = Logger.getLogger(DB_Handler.class);

    public void addEmployee(Employee emp) {
        try (Connection con = DriverManager.getConnection(url, user, pswd)) {
            PreparedStatement pst = con.prepareStatement("INSERT INTO case_study_employees values(?,?,?,?,?,?,?)");
            pst.setString(1, emp.getFirstName());
            pst.setString(2, emp.getLastName());
            pst.setString(3, emp.getAddress());
            pst.setString(4, emp.getEmailID());
            pst.setString(5, emp.getPhoneNum());
            pst.setDate(6, new Date(emp.getBirthday().getTime()));  //java.sql.Date from java.util.Date
            if (emp.getAnniversary() == null)   //case for an unmarried employee
                pst.setNull(7, Types.NULL);
            else
                pst.setDate(7, new Date(emp.getAnniversary().getTime()));
            System.out.println(pst.executeUpdate() + " employee added :)");
            logger.info("one employee added to the database");
            pst.close();
        } catch (SQLException e) {
            logger.error("An employee couldn't be added to the database");
            System.out.println("Cannot add new employee... Database problem :(");
        }
    }

    public Employee showEmployee(String firstName, String lastName) throws NameNotFoundException {
        try (Connection con = DriverManager.getConnection(url, user, pswd)) {
            //getting required employee details ignoring case sensitivity
            PreparedStatement pst = con.prepareStatement("SELECT * FROM case_study_employees WHERE UPPER(fname)=? AND UPPER(lname)=?");
            pst.setString(1, firstName.toUpperCase());
            pst.setString(2, lastName.toUpperCase());
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                logger.warn("No employee fetched from the database");
                throw new NameNotFoundException();          //Throws user defined exception if no row is selected
            }
            logger.info("one employee fetched from the database");
            return new Employee(rs.getString("fname"), rs.getString("lname"), rs.getString("address"), rs.getString("email"), rs.getString("phone"), rs.getDate("birthday"), rs.getDate("anniversary"));
        } catch (SQLException e) {
            logger.error("employee couldn't be fetched from the database");
            System.out.println("Cannot search employee... Database problem :(");
            return null;
        }
    }

    public void updateEmployee(Employee updatedEmp) {
        try (Connection con = DriverManager.getConnection(url, user, pswd)) {
            PreparedStatement pst = con.prepareStatement("UPDATE case_study_employees SET address=?, email=?, phone=?, anniversary=? WHERE UPPER(fname)=? AND UPPER(lname)=?");
            pst.setString(1, updatedEmp.getAddress());
            pst.setString(2, updatedEmp.getEmailID());
            pst.setString(3, updatedEmp.getPhoneNum());
            if (updatedEmp.getAnniversary() == null)
                pst.setNull(4, Types.NULL);
            else
                pst.setDate(4, new Date(updatedEmp.getAnniversary().getTime()));
            pst.setString(5, updatedEmp.getFirstName().toUpperCase());
            pst.setString(6, updatedEmp.getLastName().toUpperCase());
            if (pst.executeUpdate() >= 1) {
                logger.info("Row(s) were affected by the update query");
                System.out.println("Employee details updated successfully :)");
            } else {
                logger.warn("No row affected by the update query");
                System.out.println("Employee details wasn't updated");
            }
        } catch (SQLException e) {
            logger.error("employee couldn't be updated in the database");
            System.out.println("Cannot update employee... database problem :(");
        }
    }

    public List<String[]> employeesWithGivenBirthday(String bday) {
        try (Connection con = DriverManager.getConnection(url, user, pswd)) {
            PreparedStatement pst = con.prepareStatement("SELECT fname, lname, email FROM case_study_employees WHERE to_char(birthday,'DD/MM')=?");
            pst.setString(1, bday);
            ResultSet rs = pst.executeQuery();
            List<String[]> empList = new ArrayList<>();
            while (rs.next()) {
                empList.add(new String[]{rs.getString("fname"), rs.getString("lname"), rs.getString("email")});
            }
            if (empList.isEmpty())
                logger.warn("No one selected with matching birthday");
            return empList;
        } catch (SQLException e) {
            logger.error("Unable to search in in the database");
            System.out.println("Cannot search employees... Database problem :(");
            return null;
        }
    }

    public List<String[]> employeesWithGivenAnniversary(String anniv) {
        try (Connection con = DriverManager.getConnection(url, user, pswd)) {
            PreparedStatement pst = con.prepareStatement("SELECT fname, lname, phone FROM case_study_employees WHERE to_char(anniversary,'DD/MM')=?");
            pst.setString(1, anniv);
            ResultSet rs = pst.executeQuery();
            List<String[]> empList = new ArrayList<>();
            while (rs.next())
                empList.add(new String[]{rs.getString("fname"), rs.getString("lname"), rs.getString("phone")});
            if (empList.isEmpty())
                logger.warn("No one selected with matching anniversary");
            return empList;
        } catch (SQLException e) {
            logger.error("Unable to search in in the database");
            System.out.println("Cannot search employees... Database problem :(");
            return null;
        }
    }

    public void createTable() throws SQLException {
        try (Connection con = DriverManager.getConnection(url, user, pswd)) {
            Statement st = con.createStatement();
            st.executeUpdate("CREATE TABLE case_study_employees" +
                    " (fname varchar(20), lname varchar(20), address varchar(60)," +
                    " email varchar(40), phone varchar(10), birthday DATE, anniversary DATE)");
            System.out.println("New table created");
            logger.info("New table created");
        } catch (SQLSyntaxErrorException e) {
            logger.warn("couldn't create table because it already exists");
        } catch (SQLException e) {
            System.out.println("Database problem :(");
            throw e;
        }
    }

    public List<String[]> getAllEmployees() {
        List<String[]> employees = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, pswd)) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT fname, phone FROM case_study_employees");
            while (rs.next())
                employees.add(new String[]{rs.getString("fname"), rs.getString("phone")});
            if (employees.isEmpty())
                logger.warn("No row was selected from the database");
            else
                logger.info("Row(s) were selected from the database");
            return employees;
        } catch (SQLException e) {
            logger.error("Unable to get data from the database");
            System.out.println("Cannot get Employee details... Database problem :(");
            return null;
        }
    }

    public List<Employee> getEntireTable() {
        List<Employee> table = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, pswd)) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM case_study_employees");
            while (rs.next())
                table.add(new Employee(rs.getString("fname"), rs.getString("lname"),
                        rs.getString("address"), rs.getString("email"),
                        rs.getString("phone"), rs.getDate("birthday"), rs.getDate("anniversary")));
            return table;
        } catch (SQLException e) {
            logger.error("Couldn't get data from the database");
            System.out.println("Cannot get Employee details... Database problem :(");
            return null;
        }
    }

}
