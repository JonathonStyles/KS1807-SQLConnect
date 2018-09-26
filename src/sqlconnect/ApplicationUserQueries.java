package sqlconnect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ApplicationUserQueries
{
    private String EncryptPassword(String Password)
    {
        //Do something to encrypt this here.
        
        return Password;
    }
    
    public boolean IsEmailAddressUnique(String EmailAddress, Statement
            SQLStatement)
    {
        //Make the Email Address check case insensitive.
        EmailAddress = EmailAddress.toLowerCase();
        
        try
        {
            String SQLQuery = "SELECT Count(EmailAddress) AS EmailCount "
                    + "FROM UserAccount WHERE EmailAddress = '" +
                    EmailAddress + "'";
            ResultSet rs = SQLStatement.executeQuery(SQLQuery);

            int EmailCount = 0;
            if (rs.next())
            {
                EmailCount = Integer.parseInt(rs.getString("EmailCount"));
            }
            if (EmailCount > 0)
            {
                return false;
            }
            else
            {
                return true;
            }            
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return false;
        }
    }
    
    private boolean InsertNewSettings (int UserID, Statement SQLStatement)
    {
        try
        {
            String SQLQuery = "INSERT INTO UserSettings (UserID,"
            + "MoodFrequency, MakeRecommendations, RememberLogin) " +
            "VALUES('" + UserID + "','Once Per Track'," +
            "'Yes'" + ",'No');";
            SQLStatement.execute(SQLQuery);
            return true;
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return false;
        }
    }
    
    public int InsertNewUser(String FirstName, String LastName,
            String EmailAddress, String DateOfBirth, String Gender,
            String UserPassword, Statement SQLStatement)
    {
        try
        {
            int UserID = -1;
            UserPassword = EncryptPassword(UserPassword);

            //Make the Email Address all lowercase to ensure case insensitive search.
            EmailAddress = EmailAddress.toLowerCase();

            String SQLQuery = "SET NOCOUNT ON; INSERT INTO UserAccount (FirstName,"
                                + "LastName, DateOfBirth, Gender, EmailAddress,"
                                + "UserPassword)\n" +
                        "VALUES('" + FirstName + "','" + LastName + "','" +
                    DateOfBirth + "','" + Gender + "','" + EmailAddress + "','"
                    + UserPassword + "'" + ");"
                    + "SELECT SCOPE_IDENTITY() AS UserID";
                ResultSet rs = SQLStatement.executeQuery(SQLQuery);

                if (rs.next())
                {
                    UserID = Integer.parseInt(rs.getString("UserID"));
                }    

                //Insert new settings record as well.
                if(!InsertNewSettings(UserID, SQLStatement))
                {
                    //Return -1 if it failed.
                    UserID = -1;
                }
                return UserID;
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return - 1;
        }
    }
    
    public void UpdateNewUser(String FirstName, String LastName, String
            EmailAddress, String DateOfBirth, String Gender, String
                    UserPassword, String UserID, Statement SQLStatement)
    {
        try
        {
            UserPassword = EncryptPassword(UserPassword);

            //Make the Email Address all lowercase to ensure case insensitive search.
            EmailAddress = EmailAddress.toLowerCase();

            String SQLQuery = "UPDATE UserAccount SET FirstName ='" + FirstName +
                    "', LastName = '" + LastName + "',DateOfBirth = '" +
                    DateOfBirth + "'," + "Gender = '" + Gender + "'," +
                    "EmailAddress = '" + EmailAddress + "'," +
                    "UserPassword = '" + UserPassword + "' "
                    + "WHERE UserID = '" + UserID + "'";
                SQLStatement.execute(SQLQuery); 
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
        }
    }
    
    public void UpdatePassword(String UserPassword, String UserID, Statement
            SQLStatement)
    {
        try
        {
            UserPassword = EncryptPassword(UserPassword);

            String SQLQuery = "UPDATE UserAccount SET UserPassword ='" +
                    UserPassword + "' WHERE UserID = '" + UserID + "'";
                SQLStatement.execute(SQLQuery); 
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
        }
    }
    
    public void UpdateUser(String FirstName, String LastName, String
            EmailAddress, String DateOfBirth, String Gender, String UserID,
            Statement SQLStatement)
    {
        try
        {
            //Make the Email Address all lowercase to ensure case insensitive search.
            EmailAddress = EmailAddress.toLowerCase();

            String SQLQuery = "UPDATE UserAccount SET FirstName ='" + FirstName +
                    "', LastName = '" + LastName + "',DateOfBirth = '" +
                    DateOfBirth + "'," + "Gender = '" + Gender + "'," +
                    "EmailAddress = '" + EmailAddress + "'" +
                    "WHERE UserID = '" + UserID + "'";
                SQLStatement.execute(SQLQuery); 
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
        }
    }
    
    public void UpdateUserSecondPage(String PreferredPlatform, String
            MusicQuestionOne, String MusicQuestionTwo, String
                    MusicQuestionThree, String UserID, Statement SQLStatement)
    {
        try
        {
            String SQLQuery = "UPDATE UserAccount SET PreferredPlatform ='"
                    + PreferredPlatform +
                    "', MusicQuestionOne = '" + MusicQuestionOne +
                    "',MusicQuestionTwo = '" + MusicQuestionTwo + "',"
                    + "MusicQuestionThree = '" + MusicQuestionThree + "' "
                    + "WHERE UserID = '" + UserID + "'";
                SQLStatement.execute(SQLQuery); 
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
        }
    }
    
    public void UpdateSettings (String MakeRecommendations, String
            MoodFrequency, String UserID, Statement SQLStatement)
    {
        try
        {
            String SQLQuery = "UPDATE UserSettings SET MakeRecommendations ='"
                    + MakeRecommendations + "', MoodFrequency = '" +
                    MoodFrequency + "' "
                    + "WHERE UserID = '" + UserID + "'";
                SQLStatement.execute(SQLQuery); 
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
        }
    }
}