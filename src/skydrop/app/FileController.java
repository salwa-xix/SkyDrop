package skydrop.app;
import java.io.*;
import java.util.ArrayList;

public class FileController {
// examples

    public void writeLog(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("delivery_log.txt", true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing log: " + e.getMessage());
        }
    }



    // read from DB + write report
    public void saveReportToFile(String reportText) {
    // Queries to calculate totals
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("report.txt"))) {
            writer.write(reportText);
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }

}