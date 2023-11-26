import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SignUp {
    private Connection connection;
    private Scanner scanner;
    public SignUp(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient(){
        System.out.println("Enter patient's name: ");
        String name = scanner.next();
        System.out.println("Enter patient's age: ");
        int age = scanner.nextInt();
        System.out.println("Enter patient's gender: ");
        String gender = scanner.next();

        try{
            String query = "INSERT INTO PATIENT(name,age,gender) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2,age);
            preparedStatement.setString(3,gender);
            int affectedRows = preparedStatement.executeUpdate();   // returns number of rows affected
            if(affectedRows>0){
                System.out.println("Patient Added Successfully!!");
            }
            else{
                System.out.println("Failed to add patient");
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getPatientById(int id){
        String query = "select * from Patient where id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
            return false;
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

}
