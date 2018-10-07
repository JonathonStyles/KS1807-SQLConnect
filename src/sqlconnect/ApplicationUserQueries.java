package sqlconnect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class ApplicationUserQueries
{
    /*Code for this algorithm derived from:
    https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
    */
    private String EncryptPassword(String Password)
    {
        String EncryptedPassword = "";
        try
        {
            //Using MD5 Message-Digest Algorithm to encrypt the password.
            MessageDigest Digest = MessageDigest.getInstance("MD5");
            
            //Add password bytes to digest.
            Digest.update(Password.getBytes());
            
            //Get the hash's bytes.
            byte[] Bytes = Digest.digest();
            
            //Convert these decimal bytes to hexadecimal format.
            StringBuilder StringToBuild = new StringBuilder();
            
            for(int i=0; i< Bytes.length ;i++)
            {
                StringToBuild.append(Integer.toString((Bytes[i] & 0xff) +
                        0x100, 16).substring(1));
            }

            EncryptedPassword = StringToBuild.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }    
        return EncryptedPassword;
    }
    
    //Gets the last ten music tracks. Gets the Name, Genre, Artist and Length.
    public String[][] GetMusicHistory(int UserID, Statement SQLStatement)
    {
        try
        {
            /*Gets the last ten music tracks that the user has listened to,
            using the mood after time as the time when the user finished the
            song*/
            String SQLQuery = "SELECT DISTINCT TOP (10) TrackName, Genre, "
                    + "Artist, Length, MoodAfterTime "
                    + "FROM MusicTrack INNER JOIN UserMood ON "
                    + "MusicTrack.TrackID = UserMood.TrackID " +
                    "WHERE UserMood.UserID = '" + UserID + "' " +
                    "AND MoodAfterTime IS NOT NULL AND MoodBeforeTime IS NOT NULL" +
                    " ORDER BY UserMood.MoodAfterTime DESC";
            ResultSet rs = SQLStatement.executeQuery(SQLQuery);

            String MusicDetails[][] = new String[10][4];
            
            int i = 0;           
            while (rs.next())
            {
                MusicDetails[i][0] = rs.getString("TrackName");
                MusicDetails[i][1] = rs.getString("Genre");
                MusicDetails[i][2] = rs.getString("Artist");
                MusicDetails[i][3] = rs.getString("Length");
                i++;
            }
            return MusicDetails;           
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return new String[0][0];
        }
    }
    
    public String[] GetUserDetails(int UserID, Statement SQLStatement)
    {
        try
        {
            String SQLQuery = "SELECT FirstName, LastName, EmailAddress, "
                    + "DateOfBirth, Gender "
                    + "FROM UserAccount WHERE UserID = '" + UserID + "'";
            ResultSet rs = SQLStatement.executeQuery(SQLQuery);

            String UserDetails[] = new String[5];
            
            if (rs.next())
            {
                UserDetails[0] = rs.getString("FirstName");
                UserDetails[1] = rs.getString("LastName");
                UserDetails[2] = rs.getString("EmailAddress");
                UserDetails[3] = rs.getString("DateOfBirth");
                UserDetails[4] = rs.getString("Gender");
            }
            return UserDetails;           
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return new String[0];
        }
    }
    
    public String[] GetUserDetailsRegistration(int UserID,
                Statement SQLStatement)
    {
        try
        {
            String SQLQuery = "SELECT FirstName, LastName, EmailAddress, "
                    + "DateOfBirth, Gender, UserPassword "
                    + "FROM UserAccount WHERE UserID = '" + UserID + "'";
            ResultSet rs = SQLStatement.executeQuery(SQLQuery);

            String UserDetails[] = new String[6];
            
            if (rs.next())
            {
                UserDetails[0] = rs.getString("FirstName");
                UserDetails[1] = rs.getString("LastName");
                UserDetails[2] = rs.getString("EmailAddress");
                UserDetails[3] = rs.getString("DateOfBirth");
                UserDetails[4] = rs.getString("Gender");
                UserDetails[5] = rs.getString("UserPassword");
            }
            return UserDetails;           
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return new String[0];
        }
    }
    
    public int GetUserID(String EmailAddress, Statement SQLStatement)
    {
        try
        {
            String SQLQuery = "SELECT UserID "
                    + "FROM UserAccount WHERE EmailAddress = '" +
                    EmailAddress + "'";
            ResultSet rs = SQLStatement.executeQuery(SQLQuery);

            if (rs.next())
            {
                String UserIDString = "";
                UserIDString = rs.getString("UserID");
                if (!UserIDString.equals(""))
                {
                    return Integer.parseInt(UserIDString);
                }
                else
                {
                    return -1;
                }
            }
            return -1;
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return -1;
        }
    }
    
    public String GetUserPassword(int UserID, Statement SQLStatement)
    {
        try
        {
            String SQLQuery = "SELECT UserPassword "
                    + "FROM UserAccount WHERE UserID = '" + UserID + "'";
            ResultSet rs = SQLStatement.executeQuery(SQLQuery);

            String UserPassword = "";
            
            if (rs.next())
            {
                UserPassword = rs.getString("UserPassword");
            }
            return UserPassword;           
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return "";
        }
    }
    
    public String[] GetUserSettings(int UserID, Statement SQLStatement)
    {
        try
        {
            String SQLQuery = "SELECT MakeRecommendations, MoodFrequency "
                    + "FROM UserSettings WHERE UserID = '" + UserID + "'";
            ResultSet rs = SQLStatement.executeQuery(SQLQuery);

            String UserSettings[] = new String[2];
            
            if (rs.next())
            {
                UserSettings[0] = rs.getString("MakeRecommendations");
                UserSettings[1] = rs.getString("MoodFrequency");
            }
            return UserSettings;           
        }
        catch (SQLException err)
        {
            System.err.println("Error executing query");
            err.printStackTrace(System.err);
            System.exit(0);
            return new String[0];
        }
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
                    UserPassword, int UserID, Statement SQLStatement)
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
    
    public void UpdatePassword(String UserPassword, int UserID, Statement
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
            EmailAddress, String DateOfBirth, String Gender, int UserID,
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
                    MusicQuestionThree, int UserID, Statement SQLStatement)
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
            MoodFrequency, int UserID, Statement SQLStatement)
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
    
    public int VerifyLogin(String EmailAddress, String UserPassword,
            Statement SQLStatement)
    {
        int UserID = GetUserID(EmailAddress, SQLStatement);

        //Don't bother checking password if the ID does not match.
        if(UserID == - 1)
        {
            return -1;
        }

        UserPassword = EncryptPassword(UserPassword);
        String StoredPassword = GetUserPassword(UserID, SQLStatement);

        //If password is wrong, return -1.
        if (UserPassword.equals(StoredPassword))
        {
            return UserID;
        }
        else
        {
            return -1;
        }
    }

    public boolean VerifyPassword(int UserID, String UserPassword,
            Statement SQLStatement)
    {
        UserPassword = EncryptPassword(UserPassword);       
        String StoredPassword = GetUserPassword(UserID, SQLStatement);

        if (UserPassword.equals(StoredPassword))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}