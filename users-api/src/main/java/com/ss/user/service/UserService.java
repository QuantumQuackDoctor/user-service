package com.ss.user.service;

import com.database.ormlibrary.user.*;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.repo.UserRepo;
import com.ss.user.repo.UserRoleRepo;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UserService(UserRepo userRepo, UserRoleRepo userRoleRepo, ModelMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailAvailable(String email) {
        return !userRepo.existsByEmail(email);
    }

    public void insertUser(User user) {
        UserEntity toInsert = convertToEntity(user);
        //set defaults
        toInsert.setId(null);
        toInsert.setPoints(0);
        toInsert.setActivated(true); //TODO change to false when confirmation is added
        toInsert.setPassword(passwordEncoder.encode(user.getPassword()));

        //TODO move this logic to initialization
        Optional<UserRoleEntity> role;
        if (!(role = userRoleRepo.findByRole("user")).isPresent()) {
            role = Optional.of(userRoleRepo.save(new UserRoleEntity().setRole("user")));
        }
        toInsert.setUserRole(role.get());

        userRepo.save(toInsert);

        //TODO send activation email

    }


    private UserEntity convertToEntity(User user) {
        UserEntity entity = mapper.map(user, UserEntity.class);

        //populate settings, modelMapper cannot get these
        UserSettings us = user.getSettings();
        SettingsEntity settings = new SettingsEntity();
        settings.setNotifications(new NotificationsEntity()
                .setEmail(us.getNotifications().getEmail())
                .setPhoneOption(us.getNotifications().getText()));
        settings.setThemes(new ThemesEntity().setDark(us.getTheme().equals(UserSettings.ThemeEnum.DARK)));

        entity.setBirthDate(LocalDate.from(formatter.parse(user.getDOB())));
        entity.setSettings(settings);
        return entity;
    }
}
