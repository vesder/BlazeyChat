package me.vesder.blazeyChat.database;

import lombok.Getter;
import me.vesder.blazeyChat.BlazeyChat;
import me.vesder.blazeyChat.configs.ConfigManager;
import me.vesder.blazeyChat.configs.customconfigs.SettingsConfig;
import org.h2.Driver;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class UserDatabase {

    @Getter
    private static final UserDatabase instance;
    private static final SettingsConfig settingsConfig =
        (SettingsConfig) ConfigManager.getConfigManager().getCustomConfig("settings.yml");
    private final Connection connection;

    static {
        try {
            instance = new UserDatabase();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to connect to database!", ex);
        }
    }

    private UserDatabase() throws SQLException {

        String url;
        String storageMethod = settingsConfig.getDatabaseMethod().toLowerCase();

        switch (storageMethod) {
            case "mysql", "mariadb"
                -> url = "jdbc:mysql://"
                + settingsConfig.getDatabaseAddress()
                + "/" + settingsConfig.getDatabaseName()
                + "?useSSL=" + settingsConfig.isDatabaseSSL() + "&autoReconnect=true";
            case "postgresql" ->
                url = "jdbc:postgresql://" + settingsConfig.getDatabaseAddress() + "/" + settingsConfig.getDatabaseName();
            case "h2" -> {
                new Driver();
                File h2File = new File(BlazeyChat.getPlugin().getDataFolder(), settingsConfig.getDatabaseName());
                url = "jdbc:h2:" + h2File.getAbsolutePath();
            }
            default -> {
                File sqliteFile = new File(BlazeyChat.getPlugin().getDataFolder(), settingsConfig.getDatabaseName() + ".db");
                url = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();
            }
        }

        if (storageMethod.equals("mysql") || storageMethod.equals("mariadb") || storageMethod.equals("postgresql")) {
            connection = DriverManager.getConnection(url, settingsConfig.getDatabaseUsername(), settingsConfig.getDatabasePassword());
        } else {
            connection = DriverManager.getConnection(url);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                "uuid TEXT PRIMARY KEY, " +
                "username TEXT NOT NULL, " +
                "ignoreAll INTEGER NOT NULL DEFAULT 0," +
                "ignoredPlayers TEXT)");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    protected boolean userExists(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    private void addUser(User user, UUID uuid) throws SQLException {
        //this should error if the player already exists

        String ignoredPlayersCsv = parseUUIDSetToCsv(user.getIgnoredPlayers());
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, username, ignoreAll, ignoredPlayers) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setInt(3, user.isIgnoreAll() ? 1 : 0);
            preparedStatement.setString(4, ignoredPlayersCsv);
            preparedStatement.executeUpdate();
        }
    }

    public void saveUserData(UUID uuid, User user) throws SQLException {

        //if the player doesn't exist, add them
        if (!userExists(uuid)) {
            addUser(user, uuid);
        }

        String ignoredPlayersCsv = parseUUIDSetToCsv(user.getIgnoredPlayers());
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET username = ?, ignoreAll = ?, ignoredPlayers = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setInt(2, user.isIgnoreAll() ? 1 : 0);
            preparedStatement.setString(3, ignoredPlayersCsv);
            preparedStatement.setString(4, uuid.toString());
            preparedStatement.executeUpdate();
        }

    }

    protected User getUserData(UUID uuid) throws SQLException {

        User user = new User();
        user.setIgnoreAll(isIgnoreAll(uuid));
        user.setIgnoredPlayers(getIgnoredPlayers(uuid));

        return user;
    }

    private boolean isIgnoreAll(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT ignoreAll FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() && resultSet.getInt("ignoreAll") == 1;
        }
    }

    private Set<UUID> getIgnoredPlayers(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT ignoredPlayers FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String ignoredPlayersCsv = resultSet.getString("ignoredPlayers");

                return parseCsvToUUIDSet(ignoredPlayersCsv);
            } else {
                return null;
            }
        }
    }

    private String parseUUIDSetToCsv(Set<UUID> uuidSet) {
        if (uuidSet == null || uuidSet.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (UUID uuid : uuidSet) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(uuid.toString());
        }
        return sb.toString();

    }

    private Set<UUID> parseCsvToUUIDSet(String csv) {
        if (csv == null || csv.isEmpty()) {
            return null;
        }
        Set<UUID> set = new HashSet<>();
        for (String string : csv.split(",")) {
            try {
                set.add(UUID.fromString(string.trim()));
            } catch (IllegalArgumentException ex) {
                BlazeyChat.getPlugin().getLogger().log(Level.WARNING, "Invalid UUID found in 'ignoredPlayers' column of the SQL database!", ex);
            }

        }
        return set;
    }

}
