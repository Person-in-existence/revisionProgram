package revisionprogram;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Settings {
    public static String settingsPath = Main.saveRoot + "settings";

    public static final String darkModeKey = "dark_mode";
    public static boolean darkModeDefault = false;
    public boolean darkMode = darkModeDefault;
    public Settings(boolean darkMode) {
        this.darkMode = darkMode;
    }
    public Settings() {
        // Read from the settings file
        File settingsFile = new File(settingsPath);
        if (settingsFile.exists()) {
            try {
                FileReader settingsReader = new FileReader(settingsFile);
                StringBuilder stringBuilder = new StringBuilder();
                while (true) {
                    int character = settingsReader.read();
                    if (character != -1) {
                        stringBuilder.append((char) character);
                    }
                    else {
                        break;
                    }
                }
                String[] settingPairs = stringBuilder.toString().split("\n");

                // Parse settings

                for (String pair : settingPairs) {
                    String[] parts = pair.split("=");
                    if (parts.length != 2) {
                        continue;
                    }
                    switch (parts[0]) {
                        case darkModeKey:
                            try {
                                this.darkMode = Boolean.parseBoolean(parts[1]);
                            } catch (Exception e) {
                                continue;
                            }
                            break;
                    }
                }
                settingsReader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void save() {
        String toWrite = "";
        toWrite += darkModeKey;
        toWrite += "=";
        toWrite += Boolean.toString(this.darkMode);

        // Write to a file using FileWriter
        File settingFile = new File(settingsPath);
        for (int time = 0; time < 5; time++) {
            try {
                FileWriter settingsWriter = new FileWriter(settingFile);
                settingsWriter.write(toWrite);
                settingsWriter.close();
            } catch (Exception e) {
                System.err.println("Write failed");
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
        }

    }
}
