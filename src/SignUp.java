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

    public boolean getPatientById(int id){
        String query = "select * from logindetails where id = ?";
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
