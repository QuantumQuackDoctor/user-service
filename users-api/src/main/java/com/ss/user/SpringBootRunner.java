package com.ss.user;

import com.database.ormlibrary.user.UserRoleEntity;
import com.database.security.SecurityConfig;
import com.fasterxml.jackson.databind.Module;
import com.ss.user.repo.UserRoleRepo;
import org.modelmapper.ModelMapper;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Import(SecurityConfig.class)
@EnableJpaRepositories(basePackages = {"com.database.security", "com.ss.user"})
@EntityScan("com.database.ormlibrary")
public class SpringBootRunner implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplication(SpringBootRunner.class).run(args);
    }

    @Override
    public void run(String... arg0) throws Exception {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner initialization(UserRoleRepo userRoleRepo) {
        return val -> {
            if (!userRoleRepo.findByRole("user").isPresent()) {
                userRoleRepo.save(new UserRoleEntity().setRole("user"));
            }
        };
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

    static class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }
    }

}
