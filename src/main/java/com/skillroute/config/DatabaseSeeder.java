package com.skillroute.config;

import com.skillroute.repository.SkillDictionaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final DataSource dataSource;
    private final SkillDictionaryRepository dictionaryRepository;

    @Override
    public void run(String... args) {
        if (dictionaryRepository.count() > 0) {
            return;
        }

        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/data/skills-seed.sql"));
            populator.setSqlScriptEncoding("UTF-8");

            populator.execute(dataSource);

        } catch (Exception e) {
            log.error("Ошибка во время наполнения базы данных: ", e);
        }
    }
}
