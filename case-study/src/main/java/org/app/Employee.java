package org.app;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Employee {
    private final static Logger logger = Logger.getLogger(Employee.class);
    private String firstName, lastName, address, emailID, phoneNum;
    private Date birthday, anniversary;

    public Employee() {
        logger.info("One employee object created");
    }

    public Employee(String firstName, String lastName, String address, String emailID, String phoneNum, Date birthday, Date anniversary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.emailID = emailID;
        this.phoneNum = phoneNum;
        this.birthday = birthday;
        this.anniversary = anniversary;
        logger.info("One employee object created");
    }

    static Employee createEmployee() {
        logger.info("creating a new Employee object from user inputs...");
        Employee emp = new Employee();
        System.out.println("Enter first name of the employee");
        String tmp = App.sc.nextLine();
        emp.setFirstName(tmp.substring(0, 1).toUpperCase() + tmp.substring(1));
        System.out.println("Enter last name of the employee");
        tmp = App.sc.nextLine();
        emp.setLastName(tmp.substring(0, 1).toUpperCase() + tmp.substring(1));
        System.out.println("Enter address of the employee");
        emp.setAddress(App.sc.nextLine());

        emp.setEmailID(forceGetValidEmail());
        emp.setPhoneNum(forceGetValidPhone());
        emp.setBirthday(forceGetValidBirthday());
        emp.setAnniversary(forceGetValidAnniversary());

        return emp;
    }

    public static String forceGetValidEmail() {
        final String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"; //for checking email
        String email;
        do {
            System.out.println("Enter email ID of the employee");
            email = App.sc.nextLine();
            if (Pattern.compile(regex).matcher(email).matches())
                return email;
            else
                System.out.println("Invalid email ID...\n");
        } while (true);
    }

    public static String forceGetValidPhone() {
        String phone;
        do {
            System.out.println("Enter phone number of the employee");
            phone = App.sc.nextLine();
            if (Pattern.compile("^[0-9]{10}$").matcher(phone).matches())
                return phone;
            else
                System.out.println("Contact number should only contain numbers with a total of ten digits...\n");
        } while (true);
    }

    public static Date forceGetValidBirthday() {
        String bday;
        do {
            System.out.println("Enter birthday in DD/MM/YYYY format");
            bday = App.sc.nextLine();
            try {
                Date bDate = new SimpleDateFormat("dd/MM/yyyy").parse(bday);
                if (Integer.parseInt(bday.split("/")[0]) > 31)
                    throw new ParseException("invalid day", 0);
                if (Integer.parseInt(bday.split("/")[1]) > 12)
                    throw new ParseException("invalid month", 3);
                if (Integer.parseInt(bday.split("/")[2]) > 2022)
                    throw new ParseException("future year not allowed", 6);
                return bDate;
            } catch (ParseException e) {
                System.out.println("Invalid date entered...\n");
            }
        } while (true);
    }

    public static Date forceGetValidAnniversary() {
        String anniv;
        do {
            System.out.println("Enter wedding anniversary date in DD/MM/YYYY format or enter NA if unmarried");
            anniv = App.sc.nextLine();
            if (anniv.equalsIgnoreCase("NA"))
                return null;
            else
                try {
                    return new SimpleDateFormat("dd/MM/yyyy").parse(anniv);
                } catch (ParseException e) {
                    System.out.println("Invalid date entered...\n");
                }
        } while (true);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        logger.info("Address of an Employee object changed");
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
        logger.info("email ID of an Employee object changed");
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
        logger.info("Phone number of an Employee object changed");
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(Date anniversary) {
        this.anniversary = anniversary;
        logger.info("Anniversary date of an Employee object changed");
    }

    @Override
    public String toString() {
        SimpleDateFormat f = new SimpleDateFormat("E, dd MMMM yyyy");
        return "First Name: " + firstName +
                "; Last Name: " + lastName +
                "; Address: " + address +
                ";\nEmail ID: " + emailID +
                "; Phone No.: " + phoneNum +
                "; Birthdate: " + ((birthday == null) ? "null" : f.format(birthday)) +
                "; Wedding anniversary: " + ((anniversary == null) ? "unmarried" : f.format(anniversary)) +
                "\n---------------------------------------------------------------------------------------------------------------------";
    }
}
