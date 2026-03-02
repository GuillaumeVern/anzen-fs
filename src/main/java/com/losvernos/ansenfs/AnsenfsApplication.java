package com.losvernos.ansenfs;

import java.sql.SQLException;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.losvernos.ansenfs.database.DBAccess;
import com.losvernos.ansenfs.database.DBInitializer;

import jakarta.annotation.PostConstruct;

@ImportRuntimeHints(AnsenfsApplication.WebResourcesHints.class)
@RestController
@SpringBootApplication
public class AnsenfsApplication {

	@PostConstruct
    public void init() {
        DBInitializer.initialize();
    }

	static class WebResourcesHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Force l'inclusion de tout le dossier static dans le binaire natif
            hints.resources().registerPattern("static/browser/**");
        }
    }

	@RequestMapping("/backend")
	String home() throws SQLException {
		var conn = DBAccess.getConnection();
		var stmt = conn.createStatement();
		var rs = stmt.executeQuery("SELECT username FROM users");
		StringBuilder sb = new StringBuilder();
		while (rs.next()) {
			sb.append(rs.getString("username")).append("\n");
		}
		return sb.toString();
	}


	public static void main(String[] args) {
		SpringApplication.run(AnsenfsApplication.class, args);
	}

}
