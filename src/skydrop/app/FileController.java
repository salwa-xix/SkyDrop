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

    public ArrayList<String> readLogs() {
        ArrayList<String> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("delivery_log.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading logs: " + e.getMessage());
        }
        return logs;
    }

    public void saveReportToFile(String reportText) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("report.txt"))) {
            writer.write(reportText);
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }

    public String readReportFromFile() {
        StringBuilder report = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("report.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                report.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading report: " + e.getMessage());
        }
        return report.toString();
    }

    public ArrayList<String> readPlacesFromFile(String fileName) {
        ArrayList<String> places = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                places.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading places file: " + e.getMessage());
        }
        return places;
    }
}