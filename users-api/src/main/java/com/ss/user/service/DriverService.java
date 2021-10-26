package com.ss.user.service;

import com.database.ormlibrary.driver.DriverEntity;
import com.database.ormlibrary.driver.DriverRatingEntity;
import com.database.ormlibrary.user.*;
import com.ss.user.errors.DriverNotFoundException;
import com.ss.user.errors.EmailTakenException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.Driver;
import com.ss.user.model.DriverRating;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.repo.DriverRepo;
import com.ss.user.repo.UserRepo;
import com.ss.user.repo.UserRoleRepo;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;
    private final DriverRepo driverRepo;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DriverService(UserRepo userRepo, UserRoleRepo userRoleRepo, PasswordEncoder passwordEncoder, ModelMapper mapper, DriverRepo driverRepo) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.driverRepo = driverRepo;
    }

    public Driver getDriver(String email) throws DriverNotFoundException {
        Optional<DriverEntity> entity = driverRepo.findByUserEmail(email);
        if (entity.isPresent()) {
            return convertToDTO(entity.get());
        } else throw new DriverNotFoundException("Driver not found");
    }

    public Driver getDriverById(Long id) throws UserNotFoundException {
        return convertToDTO(driverRepo.findById(id).orElseThrow(() -> new UserNotFoundException("driver not found")));
    }

    public Driver createDriver(Driver toCreate) throws EmailTakenException {
        DriverEntity driver = convertToEntity(toCreate);
        if (userRepo.existsByEmail(driver.getUser().getEmail())) throw new EmailTakenException("email taken");
        driver.getUser().setActivated(true); //admin is creating account, validation not necessary?
        driver.setUser(userRepo.save(driver.getUser()));
        driver.getUser().setPassword(passwordEncoder.encode(toCreate.getPassword()));
        return convertToDTO(driverRepo.save(driver));
    }

    public Driver updateDriver(Driver driver, boolean updatePassword) throws UserNotFoundException {
        DriverEntity driverEntity = driverRepo.findById(driver.getId())
                .orElseThrow(() -> new UserNotFoundException("driver not present"));

        DriverEntity newDriverDetails = convertToEntity(driver);
        newDriverDetails.setId(driverEntity.getId());
        newDriverDetails.getUser().setId(driverEntity.getUser().getId());
        if (updatePassword) {
            newDriverDetails.getUser().setPassword(passwordEncoder.encode(driver.getPassword()));
        } else {
            newDriverDetails.getUser().setPassword(driverEntity.getUser().getPassword());
        }

        mapper.map(newDriverDetails, driverEntity);

        return convertToDTO(driverRepo.save(driverEntity));
    }

    public void deleteDriver(Long id){
        driverRepo.deleteById(id);
    }


    private UserRoleEntity getDriverRole() {
        Optional<UserRoleEntity> role = userRoleRepo.findByRole("driver");
        if (role.isPresent()) {
            return role.get();
        } else {
            UserRoleEntity entity = new UserRoleEntity().setRole("driver");
            return userRoleRepo.save(entity);
        }
    }

    public void changeStatus(Long userId, boolean checkedIn) throws UserNotFoundException {
        DriverEntity driverEntity = driverRepo.findById(userId).orElseThrow(
                () -> new UserNotFoundException("invalid driver id"));
        driverEntity.setCheckedIn(checkedIn);
        driverRepo.save(driverEntity);
    }


    public DriverEntity convertToEntity(Driver driver) {
        UserEntity userEntity = mapper.map(driver, UserEntity.class);
        userEntity.setVeteran(false);
        userEntity.setPoints(0);
        //populate settings, modelMapper cannot get these
        convertSettingsToEntity(userEntity, driver.getSettings(), formatter, driver.getDOB());
        userEntity.setUserRole(getDriverRole());

        DriverEntity driverEntity = new DriverEntity();
        driverEntity.setUser(userEntity);
        driverEntity.setCar(driver.getCar());
        driverEntity.setRatings(null);
        driverEntity.setId(null);

        return driverEntity;
    }

    public Driver convertToDTO(DriverEntity entity) {
        Driver driver = mapper.map(entity.getUser(), Driver.class);
        driver.setDOB(entity.getUser().getBirthDate().format((formatter)));
        driver.getSettings().getNotifications().setEmail(entity.getUser().getSettings().getNotifications().getEmail());
        driver.getSettings().getNotifications().setText(entity.getUser().getSettings().getNotifications().getPhoneOption());
        driver.getSettings().setTheme(entity.getUser().getSettings().getThemes().getDark() ? UserSettings.ThemeEnum.DARK : UserSettings.ThemeEnum.LIGHT);
        driver.setPassword(null);

        driver.setCar(entity.getCar());
        if (entity.getRatings() != null)
            driver.setRatings(entity.getRatings().stream().map(this::convertRatingToDTO).collect(Collectors.toList()));
        else
            driver.setRatings(new ArrayList<>());

        driver.setPassword(null);

        driver.setId(entity.getId());

        return driver;
    }

    private DriverRating convertRatingToDTO(DriverRatingEntity entity) {
        return mapper.map(entity, DriverRating.class);
    }

    static void convertSettingsToEntity(UserEntity userEntity, UserSettings settings2, DateTimeFormatter formatter, String dob) {
        SettingsEntity settings = new SettingsEntity();
        settings.setNotifications(new NotificationsEntity()
                .setEmail(settings2.getNotifications().getEmail())
                .setPhoneOption(settings2.getNotifications().getText()));
        settings.setThemes(new ThemesEntity().setDark(settings2.getTheme().equals(UserSettings.ThemeEnum.DARK)));

        userEntity.setBirthDate(LocalDate.from(formatter.parse(dob)));
        userEntity.setSettings(settings);
    }
}
