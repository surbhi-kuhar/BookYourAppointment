import java.sql.*;
import java.util.Scanner;

public class HomePage {
    private static final String url ="jdbc:mysql://localhost:3306/bookyourappointment";
    private static final String username = "root";

    private static final String password = "Supergirl7)";

    public static void main(String args[]){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            SignUp patient = new SignUp(connection,sc);
            Doctor doctor = new Doctor(connection);
            System.out.println("WELCOME !! BOOK YOUR APPOINTMENT NOW");
            System.out.println("Enter 1 to login if your account already exists");
            System.out.println("Enter 2 to sign up");
            int choice = sc.nextInt();
            switch (choice){
                case 1:
                    login(sc,connection);
                    break;
                case 2:
                    signup(sc,connection);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }


    }

    public static void login(Scanner sc, Connection connection){
        System.out.println("Enter your patient id");
        int pid = sc.nextInt();
        String query = "select * from logindetails where ID=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,pid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                String name = resultSet.getString("Name");
                System.out.println("Logged in successfully");
                System.out.println("Welcome " + name.toUpperCase());
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void signup(Scanner sc, Connection connection){
        System.out.println("Enter patient's name: ");
        String name = sc.next();
        System.out.println("Enter patient's age: ");
        int age = sc.nextInt();
        System.out.println("Enter patient's gender: ");
        String gender = sc.next();

        try{
            String query = "INSERT INTO logindetails(name,age,gender) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2,age);
            preparedStatement.setString(3,gender);
            int affectedRows = preparedStatement.executeUpdate();   // returns number of rows affected
            if(affectedRows>0){
                System.out.println("Signed Up Successfully!!");
            }
            else{
                System.out.println("Failed to sign up");
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Connection connection, Scanner sc, SignUp patient, Doctor doctor){
        System.out.println("Enter patient id");
        int patient_id = sc.nextInt();
        System.out.println("Enter doctor id");
        int doctor_id = sc.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD):");
        String appointment_date = sc.next();

        // check if patient and doctor exists
        if(patient.getPatientById(patient_id) && doctor.getDoctorById(doctor_id)){
            // check if doctor is available or not
            if(checkDoctorAvailability(doctor_id,appointment_date,connection)){
                String appointment_query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?,?,?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointment_query);
                    preparedStatement.setInt(1, patient_id);
                    preparedStatement.setInt(2, doctor_id);
                    preparedStatement.setString(3, appointment_date);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked");
                    }
                    else{
                        System.out.println("Failed to book appointment");
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Doctor is not available on this date");
            }
        }
        else{
            System.out.println("Either doctor or patient does not exist");
        }

    }

    public static boolean checkDoctorAvailability(int doctor_id, String appointment_date,Connection connection){
        String query = "select COUNT(*) from appointments where doctor_id=? AND appointment_date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctor_id);
            preparedStatement.setString(2, appointment_date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;

    }

}
