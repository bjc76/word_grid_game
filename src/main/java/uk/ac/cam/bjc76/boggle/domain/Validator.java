package uk.ac.cam.bjc76.boggle.domain;

import java.sql.*;

public class Validator {

    public static boolean checkWordIsValid(String word) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:data/words.db");
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT word from words WHERE word = ?");
            pstmt.setString(1, word);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error : " + e);
        }
        return false;
    }
}
