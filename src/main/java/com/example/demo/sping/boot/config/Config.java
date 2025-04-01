package com.example.demo.sping.boot.config;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;

@Component
public class Config {

    private Dotenv dotenv = Dotenv.load();

    private final String envProfile;

    public Config() {
        this.dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        String profile = dotenv.get("ENV_PROFILE", "UAT").toUpperCase();

        switch (profile) {
            case "PRO":
            case "PRODUCTION":
                this.envProfile = "PRODUCTION";
                break;
            case "UAT":
            default:
                this.envProfile = "UAT";
                break;
        }
    }

    public String getKeyPassword() {
        return dotenv.get("KEY_PASSWORD");
    }

    public String getAlgorithmHashPassword() {
        return dotenv.get("ALGORITHM_HASH_PASSWORD");
    }
    
    public String getBaseUrl() {
        String protocol = dotenv.get(envProfile + "_SPRING_BOOT_PROTOCOL");
        String host = dotenv.get(envProfile + "_SPRING_BOOT_HOST");
        String port = dotenv.get(envProfile + "_SPRING_BOOT_PORT");

        return protocol + "://" + host + ":" + port;
    }

    public String getEnvProfile() {
        return envProfile;
    }
}
