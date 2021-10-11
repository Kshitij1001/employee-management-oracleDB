package org.app;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Logger logger = Logger.getLogger(App.class);
    //    static final String filePath = "src/main/resources/log4j.properties";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
//        PropertyConfigurator.configure(filePath);
        logger.info("main method started");
        int num;
        DB_Handler dbObj = new DB_Handler();
        try {
            dbObj.createTable();   //tries to create table "case_study_employees" if it does not exist already
        } catch (SQLException e) {
            logger.error("Database connectivity issue; " + e);
            enterToContinue();
            return;    // Cannot talk to database... app fails :(
        }

        while (true) {       //App runs continuously unless user chooses to exit
            do {          //Forces user to enter an option number
                menu();
                try {
                    num = Integer.parseInt(sc.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Just enter a number!!, nothing else");
                    enterToContinue();
                    clrscr();
                }
            } while (true);
            // after successfully entering a number...
            clrscr();
            switch (num) {
                case 1:
                    Employee newEmp = Employee.createEmployee();
                    dbObj.addEmployee(newEmp);
                    break;
                case 2: //View an employee
                    System.out.println("Enter employee's first name");
                    String fn = sc.nextLine();
                    System.out.println("Enter employee's last name");
                    try {
                        Employee fetched = dbObj.showEmployee(fn, sc.nextLine());
                        if (fetched != null)   // Null if any SQL Exception was caught (DB connection problem)
                            System.out.println(fetched);
                    } catch (NameNotFoundException e) {     // Handling a user defined exception
                        System.out.println("Sorry, no employee with the given name was found");
                    }
                    break;
                case 3://Edit employee
                    System.out.println("Enter first name of the employee");
                    fn = sc.nextLine();
                    System.out.println("Enter employee's last name");
                    try {
                        Employee fetched = dbObj.showEmployee(fn, sc.nextLine());
                        if (fetched == null)    //database connectivity issue
                            break;
                        else {
                            System.out.println("Want to update the address? (y/n)");
                            if (sc.nextLine().equalsIgnoreCase("y")) {
                                System.out.println("Enter new address");
                                fetched.setAddress(sc.nextLine());
                            }
                            System.out.println("Want to update the Email ID? (y/n)");
                            if (sc.nextLine().equalsIgnoreCase("y"))
                                fetched.setEmailID(Employee.forceGetValidEmail());
                            System.out.println("Want to update the phone number? (y/n)");
                            if (sc.nextLine().equalsIgnoreCase("y"))
                                fetched.setPhoneNum(Employee.forceGetValidPhone());
                            System.out.println("Want to update the Wedding anniversary date (y/n)");
                            if (sc.nextLine().equalsIgnoreCase("y"))
                                fetched.setAnniversary(Employee.forceGetValidAnniversary());
                            dbObj.updateEmployee(fetched);
                        }
                    } catch (NameNotFoundException e) {
                        System.out.println("No such employee exists in our database");
                    }
                    break;
                case 4://common birthday
                    String bday;
                    do { // forcefully getting birthday in desired format
                        System.out.println("enter birthday in DD/MM format");
                        bday = sc.nextLine();
                        try {
                            if (bday.length() != 5)
                                throw new ParseException("invalid date format", 0);
                            (new SimpleDateFormat("dd/MM")).parse(bday);
                            break; // if no exception is thrown
                        } catch (ParseException e) {
                            System.out.println("invalid format entered... try again");
                        }
                    } while (true);
                    List<String[]> bdayEmps = dbObj.employeesWithGivenBirthday(bday);
                    if (bdayEmps == null)   //due to some database connection issue
                        break;
                    if (bdayEmps.isEmpty())
                        System.out.println("No match found :(");
                    else {
                        Formatter fmt = new Formatter();
                        fmt.format("%22s %20s %34s\n", "First Name", "Last Name", "Email ID");
                        bdayEmps.forEach((emp) -> fmt.format("%20s %20s %40s\n", emp[0], emp[1], emp[2]));
                        System.out.print(fmt);
                    }
                    break;
                case 5://common anniversary
                    String anniv;
                    do {  //forcfully getting date in right format
                        System.out.println("enter Wedding anniversary in DD/MM format");
                        anniv = sc.nextLine();
                        try {
                            if (anniv.length() != 5)
                                throw new ParseException("invalid date format", 0);
                            (new SimpleDateFormat("dd/MM")).parse(anniv);
                            break; // if no exception is thrown
                        } catch (ParseException e) {
                            System.out.println("invalid format entered... try again");
                        }
                    } while (true);
                    List<String[]> annivEmps = dbObj.employeesWithGivenAnniversary(anniv);
                    if (annivEmps == null)   //due to some database connection issue
                        break;
                    if (annivEmps.isEmpty())
                        System.out.println("No match found :(");
                    else {
                        Formatter fmt = new Formatter();
                        fmt.format("%22s %20s %12s\n", "First Name", "Last Name", "Phone no.");
                        annivEmps.forEach((emp) -> fmt.format("%20s %20s %14s\n", emp[0], emp[1], emp[2]));
                        System.out.print(fmt);
                    }
                    break;
                case 6: // View all employee's first name and phone number
                    List<String[]> allEmployees = dbObj.getAllEmployees();
                    if (allEmployees == null)  //due to some database connection issue
                        break;
                    if (allEmployees.isEmpty())
                        System.out.println("No employee data found");
                    else {
                        Formatter fmt = new Formatter();
                        fmt.format("%22s %12s\n", "First Name", "Phone No.");
                        allEmployees.forEach((emp) -> fmt.format("%20s %14s\n", emp[0], emp[1]));
                        System.out.print(fmt);
                    }
                    break;
                case 7://View whole employees' data table
                    List<Employee> table = dbObj.getEntireTable();
                    if (table == null)  //due to some database connection issue
                        break;
                    if (table.isEmpty())
                        System.out.println("No employee data found");
                    else
                        table.stream().sorted(Comparator.comparing(Employee::getFirstName)).forEach(System.out::println);//Using java 8 streams
                    break;
                case 8:
                    System.out.println("Sa-yo-na-ra");
                    logger.info("Program closed");
                    return;
                default:
                    System.out.println("Incorrect op");
            }
            enterToContinue();
            clrscr();
        }
    }

    static private void menu() {
        System.out.println("******************Welcome to Employee details management System*****************");
        System.out.println("\n\t\tEnter an option number\n\n");
        System.out.println("1. Add new employee");
        System.out.println("2. View an employee");
        System.out.println("3. Edit employee details");
        System.out.println("4. View employees having birthday on a given date");
        System.out.println("5. View employees having wedding anniversary on a given date");
        System.out.println("6. View all employees");
        System.out.println("7. View entire table stored in the database (sorted by first name)");
        System.out.println("8. Exit program");
    }

    static public void enterToContinue() {
        System.out.println("Press Enter key to continue...");
        sc.nextLine();
    }

    static public void clrscr() {
        for (int i = 0; i < 50; ++i)
            System.out.println();
    }
}
