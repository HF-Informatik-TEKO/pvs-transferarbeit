package Shared;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class AppSettingsReader {
    private static final File APP_SETTINGS_PATH = new File("appsettings.yaml");
    private static final Exception FAIL = new Exception("appsettings.yaml could not be parset correctly.");
    private static final AppSettings DEFAULT_APP_SETTINGS = new AppSettings();

    public static AppSettings read(String[] args) {

        if (args.length >= 2 && args[0].equals("--appsettings")){
            return AppSettingsReader.read(new File(args[1]));
        }
        return AppSettingsReader.read(APP_SETTINGS_PATH);
    }

    public static AppSettings read(File path) {
        if (!path.exists()) {
            System.err.println("No appsettings file found. " + path.getAbsoluteFile());
            return DEFAULT_APP_SETTINGS;
        }

        try {
            var fileContent = readFile(path);
            var rpcName = getPropValueAsString(fileContent, "rpc-name");
            var ip = getPropValueAsString(fileContent, "ip-address");
            var port = getPropValueAsInt(fileContent, "port");
            var maxServerHistory = getPropValueAsInt(fileContent, "max-message-history");
            var refreshRateMs = getPropValueAsInt(fileContent, "refresh-rate-ms");
            var resetRateMs = getPropValueAsInt(fileContent, "reset-rate-ms");
            var getMessageMaxFailCount = getPropValueAsInt(fileContent, "get-message-max-fail-count");
            
            System.out.println("Successcuflly red appsettings file. " + path.getAbsolutePath());
            return new AppSettings(
                rpcName, 
                ip, 
                port, 
                maxServerHistory, 
                refreshRateMs, 
                resetRateMs, 
                getMessageMaxFailCount
            );
        } catch (Exception e) {
            System.err.println("Failed to read appsettings file. Handing default values.");
        }

        return new AppSettings();
        
    }

    private static int getPropValueAsInt(ArrayList<String> fileContent, String key) throws Exception {
        var val = getPropValueAsString(fileContent, key);

        try {
            var number = Integer.parseInt(val);
            return number;
        } catch (NumberFormatException e) {
            // Ignore Error.
        }
        throw FAIL;
    }

    private static String getPropValueAsString(ArrayList<String> content, String key) throws Exception {
        for (var line : content) {
            if (line.contains(key)){
                return line
                    .replace("\"", "")
                    .replace(",", "")
                    .replace("_", "")
                    .replace(key + ": ", "")
                    .trim();
            }
        }
        throw FAIL;
    }

    private static ArrayList<String> readFile(File path) throws Exception {
        var list = new ArrayList<String>();
        try (var reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (Exception e) {
            System.err.println("Something went wrong reading the appsettings.yaml file. " + e.getMessage());
            throw FAIL;
        }

        return list;
    }
}
