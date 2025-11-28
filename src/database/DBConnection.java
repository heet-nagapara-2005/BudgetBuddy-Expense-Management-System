package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ExpenseManagement";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getDBConnection()
    {
        Connection con = null;
        try{
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        catch(SQLException e)
        {
           System.out.println("Error in connection" + e);
        }
        return con;
    }
    public static void closeDBConnection(Connection temp)
    {
         try{

            if(temp != null)
            {
                temp.close();
            }
         }
         catch(SQLException e)
         {
            System.out.println("Error in closing connection" + e);
         }
    }

}
 