package me.vesder.blazeyChat.utils;

import me.vesder.blazeyChat.BlazeyChat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.logging.Level;

public class LibraryLoader {

    private static final String MAVEN_BASE = "https://repo1.maven.org/maven2/";

    public static void load(String... libs) {

        File libsFolder = new File(BlazeyChat.getPlugin().getDataFolder(), "libs");
        if (!libsFolder.exists() && !libsFolder.mkdirs()) {
            BlazeyChat.getPlugin().getLogger().warning("Could not create libs folder!");
            return;
        }

        for (String lib : libs) {
            try {

                String[] split = lib.split(":", 3);

                String groupId = split[0];
                String artifactId = split[1];
                String version = split[2];

                String groupPath = groupId.replace('.', '/');
                String jarName = artifactId + "-" + version + ".jar";
                String url = MAVEN_BASE + groupPath + "/" + artifactId + "/" + version + "/" + jarName;

                File jarFile = new File(libsFolder, jarName);

                if (!jarFile.exists()) {
                    BlazeyChat.getPlugin().getLogger().info("Downloading " + artifactId + "...");
                    downloadFile(url, jarFile);
                    BlazeyChat.getPlugin().getLogger().info(jarName + " downloaded.");
                }

                URLClassLoader child = new URLClassLoader(
                    new URL[]{jarFile.toURI().toURL()},
                    LibraryLoader.class.getClassLoader()
                );

                String driverClassName = switch (artifactId) {
                    case "h2" -> "org.h2.Driver";
                    case "sqlite-jdbc" -> "org.sqlite.JDBC";
                    case "mysql-connector-j" -> "com.mysql.cj.jdbc.Driver";
                    case "postgresql" -> "org.postgresql.Driver";
                    default -> null;
                };

                if (driverClassName != null) {
                    Class<?> classToLoad = Class.forName(driverClassName, true, child);
                    Driver driverInstance = (Driver) classToLoad.getDeclaredConstructor().newInstance();
                    DriverManager.registerDriver(new DriverShim(driverInstance));
                }

            } catch (Exception ex) {
                BlazeyChat.getPlugin().getLogger().log(Level.SEVERE, "Failed to load library", ex);
            }

        }
        BlazeyChat.getPlugin().getLogger().info("Loaded all libs successfully!");
    }

    private static void downloadFile(String url, File destination) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, destination.toPath());
        } catch (IOException ex) {
            Files.deleteIfExists(destination.toPath());
        }
    }

    private static class DriverShim implements java.sql.Driver {
        private final Driver driver;

        DriverShim(Driver d) {
            this.driver = d;
        }

        @Override
        public boolean acceptsURL(String u) throws java.sql.SQLException {
            return driver.acceptsURL(u);
        }

        @Override
        public java.sql.Connection connect(String u, java.util.Properties p) throws java.sql.SQLException {
            return driver.connect(u, p);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public java.sql.DriverPropertyInfo[] getPropertyInfo(String u, java.util.Properties p) throws java.sql.SQLException {
            return driver.getPropertyInfo(u, p);
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public java.util.logging.Logger getParentLogger() {
            try {
                return driver.getParentLogger();
            } catch (java.sql.SQLFeatureNotSupportedException e) {
                return null;
            }
        }
    }

}
