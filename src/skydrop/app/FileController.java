package skydrop.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileController {

    // Save delivery events and important system actions
    public void writeLog(String message) {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter("delivery_log.txt", true))) {

            writer.write(message);
            writer.newLine();

        } catch (IOException e) {
            System.out.println("Error writing log: " + e.getMessage());
        }
    }

    // Save the generated report as a TXT file
    public void saveReportToFile(String reportText) {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter("report.txt"))) {

            writer.write(reportText);

        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
}