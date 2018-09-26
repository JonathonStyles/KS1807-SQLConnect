package sqlconnect;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RunServerInterface
{
    private static final String JDBCDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String ServerConnectionType = "jdbc:sqlserver";
    
    //May need to pass these parameters in through a configuration file
    private static final String ServerName = "PE-KS1807\\SQLEXPRESS";
    private static final String ServerPort = "1433";
    private static final String DatabaseName = "MFMHDatabase_UAT";
    private static final String DBUser = "TestUser";
    private static final String DBUserPassword = "P@ssw0rd1";

    private static final String JDBC_URL = ServerConnectionType + "://" +
            ServerName + ":" + ServerPort + ";databasename=" + DatabaseName +
            ";user=" + DBUser + ";password=" + DBUserPassword + ";";
    
    public static void main(String[] args)
    {
        //NEED TO WORK ON THIS - WHAT DATA DO WE PASS INTO THIS PROGRAM AND HOW?
        String QueryType = "";
        int UserID = 0;
        String TrackName = "";
        String Genre = "";
        String Artist = "";
        String Length = "";
        String MoodID = "";
        
        //User Test Data
        String FirstName = "Joseph";
        String LastName= "Zhang";
        String EmailAddress = "test@test.com";
        String DateOfBirth = "09-26-2000 05:23:34";
        String Gender = "Female";
        String UserPassword = "Testing";
        
        String PreferredPlatform = "Spotify";
        String MusicQuestionOne = "Quest1";
        String MusicQuestionTwo = "Quest2";
        String MusicQuestionThree = "Quest3";
        
        String MakeRecommendations = "Yes";
        String MoodFrequency = "Once Per Hour";

         if(args.length == 8)
         {
             QueryType = args[1];
             UserID = Integer.parseInt(args[2]);
             TrackName = args[3];
             Genre = args[4];
             Artist = args[5];
             Length = args[6];
             MoodID = args[7];
         }

        /*First try loading the drivers that connect to the server*/
        try
        {
           Class.forName(JDBCDriver).getConstructor().newInstance();
           System.out.println("JDBC driver loaded");
        }
        catch (Exception err)
        {
           System.err.println("Error loading JDBC driver");
           err.printStackTrace(System.err);
           System.exit(0);
        }

        try
        {
            //Connect to the database with the connection string.
            System.out.println(JDBC_URL);
            Connection DatabaseConnection = DriverManager.getConnection(JDBC_URL);

            //Create the statement object and result set for SQL Server.
            Statement SQLStatement = DatabaseConnection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs;

            //TEST
            QueryType = "UpdateUserSecondPage";
            UserID = 1;
            TrackName = "Musical Adventures7";
            Genre = "Adventure";
            Artist = "B-Cool";
            Length = "4:45";
            MoodID = "5";
             //TEST
             
             GeneratePlayLists Playlist = new GeneratePlayLists();
             ApplicationUserQueries UserQuery = new ApplicationUserQueries();

            switch (QueryType)
            {
                case "TrackStarted":
                    Playlist.TrackStarted(UserID, TrackName, Genre, Artist,
                            Length, SQLStatement);
                    break;
                case "TrackEnded":
                    Playlist.TrackEnded(MoodID, SQLStatement);
                    break;
                case "GetMusicHistory":
                    UserQuery.GetMusicHistory(UserID, SQLStatement);
                    break; 
                case "GetUserDetailsRegistration":
                    UserQuery.GetUserDetailsRegistration(UserID, SQLStatement);
                    break;
                case "GetUserDetails":
                    UserQuery.GetUserDetails(UserID, SQLStatement);
                    break;
                case "GetUserID":
                    UserQuery.GetUserID(EmailAddress, SQLStatement);
                    break;
                case "GetUserSettings":
                    UserQuery.GetUserSettings(UserID, SQLStatement);
                    break;
                case "IsEmailAddressUnique":
                    UserQuery.IsEmailAddressUnique(EmailAddress, SQLStatement);
                    break;
                case "InsertNewUser":
                    UserQuery.InsertNewUser(FirstName, LastName, EmailAddress,
                            DateOfBirth, Gender, UserPassword, SQLStatement);
                    break;   
                case "UpdatePassword":
                        UserQuery.UpdatePassword(UserPassword, UserID,
                                SQLStatement);
                    break;
                case "UpdateNewUser":
                    UserQuery.UpdateNewUser(FirstName, LastName, EmailAddress,
                            DateOfBirth, Gender, UserPassword, UserID,
                            SQLStatement);
                    break;
                case "UpdateUserSecondPage":
                    UserQuery.UpdateUserSecondPage(PreferredPlatform,
                            MusicQuestionOne, MusicQuestionTwo,
                            MusicQuestionThree, UserID, SQLStatement);
                    break;
                case "UpdateUser":
                        UserQuery.UpdateUser(FirstName, LastName, EmailAddress,
                            DateOfBirth, Gender, UserID, SQLStatement);
                    break;
                case "UpdateSettings":
                        UserQuery.UpdateSettings(MakeRecommendations,
                                MoodFrequency, UserID, SQLStatement);
                    break;
                case "VerifyLogin":
                        UserQuery.VerifyLogin(EmailAddress, UserPassword,
                                SQLStatement);
                    break;
                case "VerifyPassword":
                        UserQuery.VerifyPassword(UserID, UserPassword,
                                SQLStatement);
                    break;
                default:
                    break;
            }
            
            //close the database connection
            DatabaseConnection.close();
        }
        catch (SQLException err)
        {
           System.err.println("Error connecting to the database");
           err.printStackTrace(System.err);
           System.exit(0);
        }
    }
    
    
}