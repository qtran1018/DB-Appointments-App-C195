/*
package termProject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public abstract class LoginScreen {
    public static boolean loginCheck (String username, String password) throws SQLException {
        String sql = "SELECT User_Name FROM users WHERE User_Name = ? AND Password = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,username);
        ps.setString(2,password);
        ResultSet rs = ps.executeQuery();

        if(rs.isBeforeFirst()) {
            return true;
        }
        return false;
    }
}
*/