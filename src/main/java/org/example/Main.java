package org.example;

import org.example.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Starting Bank Digital Application...");
        System.out.println("=====================================");

        try {
            // الحصول على instance من DatabaseConnection
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();

            // الحصول على Connection
            Connection conn = dbConnection.getConnection();

            if (conn != null) {
                System.out.println("📊 Database Info:");
                System.out.println("- Database URL: " + conn.getMetaData().getURL());
                System.out.println("- Database Product: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("- Database Version: " + conn.getMetaData().getDatabaseProductVersion());

                // اختبار استعلام بسيط
                System.out.println("\n🔍 Testing simple query...");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1 as test_value, CURRENT_TIMESTAMP as current_time");

                if (rs.next()) {
                    System.out.println("✅ Query executed successfully!");
                    System.out.println("- Test Value: " + rs.getInt("test_value"));
                    System.out.println("- Current Time: " + rs.getTimestamp("current_time"));
                }

                rs.close();
                stmt.close();

                // اختبار Singleton pattern
                System.out.println("\n🔒 Testing Singleton Pattern...");
                DatabaseConnection dbConnection2 = DatabaseConnection.getInstance();

                if (dbConnection == dbConnection2) {
                    System.out.println("✅ Singleton pattern working correctly!");
                } else {
                    System.out.println("❌ Singleton pattern failed!");
                }

                // اختبار طريقة testConnection
                if (dbConnection.testConnection()) {
                    System.out.println("✅ Connection is valid and active!");
                } else {
                    System.out.println("❌ Connection is not valid!");
                }

            } else {
                System.out.println("❌ Failed to establish database connection!");
            }

        } catch (Exception e) {
            System.err.println("❌ Error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=====================================");
        System.out.println("🏁 Application finished.");
    }
}