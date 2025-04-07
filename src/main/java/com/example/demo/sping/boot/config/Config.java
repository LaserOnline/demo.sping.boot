package com.example.demo.sping.boot.config;

import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;

@Component
public class Config {

    private Dotenv dotenv = Dotenv.load();

    private final String envProfile;

    public Config() {
        this.dotenv = Dotenv.configure()
                .directory("/app")
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

    public String getNginxUploadPath() {
        String volumes = dotenv.get("NGINX_VOLUMES", "./nginx_data");
        String upload = dotenv.get("NGINX_VOLUMES_UPLOAD", "/upload");
        return Paths.get(volumes, upload).toString();
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
    
    public String getJwtSecret() {
        return dotenv.get("JWT_SECRET");
    }

    public long getAccessTokenExpireMs() {
        return Long.parseLong(dotenv.get("ACCESS_TOKEN_EXPIRE_MS", "60000").trim());
    }

    public long getRefreshTokenExpireMs() {
        return Long.parseLong(dotenv.get("REFRESH_TOKEN_EXPIRE_MS", "60000").trim());
    }
}
