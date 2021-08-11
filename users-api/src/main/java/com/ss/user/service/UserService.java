package com.ss.user.service;

import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.user.*;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.User;
import com.ss.user.model.UserSettings;
import com.ss.user.repo.OrderRepo;
import com.ss.user.repo.UserRepo;
import com.ss.user.repo.UserRoleRepo;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    private final OrderRepo orderRepo;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UserService(UserRepo userRepo, UserRoleRepo userRoleRepo, OrderRepo orderRepo, ModelMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
        this.orderRepo = orderRepo;
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

    public User getUser(String email) throws UserNotFoundException {
        Optional<UserEntity> entity = userRepo.findByEmail(email);
        if(entity.isPresent()){
            return convertToDTO(entity.get());
        }
        else throw new UserNotFoundException("User not found");
    }

    public void deleteUser(Long id) {
        //TODO delete orders
        userRepo.deleteById(id);
    }

/*    public void updateOrders (User user){

    }*/

    private UserEntity convertToEntity(User user) {
        UserEntity entity = mapper.map(user, UserEntity.class);

        List<OrderEntity> orderEntities = new ArrayList<>();
        orderRepo.findAllById(user.getOrders()).forEach(orderEntities :: add);
        entity.setOrders(orderEntities);
        //populate settings, modelMapper cannot get these
        UserSettings userSettings = user.getSettings();
        SettingsEntity settings = new SettingsEntity();
        settings.setNotifications(new NotificationsEntity()
                .setEmail(userSettings.getNotifications().getEmail())
                .setPhoneOption(userSettings.getNotifications().getText()));
        settings.setThemes(new ThemesEntity().setDark(userSettings.getTheme().equals(UserSettings.ThemeEnum.DARK)));

        entity.setBirthDate(LocalDate.from(formatter.parse(user.getDOB())));
        entity.setSettings(settings);
        return entity;
    }

    private User convertToDTO(UserEntity entity){
        User user = mapper.map(entity, User.class);
        user.setDOB(entity.getBirthDate().format((formatter)));
        user.getSettings().getNotifications().setEmail(entity.getSettings().getNotifications().getEmail());
        user.getSettings().getNotifications().setText(entity.getSettings().getNotifications().getPhoneOption());
        user.getSettings().setTheme(entity.getSettings().getThemes().getDark() ? UserSettings.ThemeEnum.DARK : UserSettings.ThemeEnum.LIGHT);

        List<Long> orderIDs = new ArrayList<>();
        entity.getOrders().forEach(orderEntity -> orderIDs.add(orderEntity.getId()));
        user.setOrders(orderIDs);

        //delete password
        user.setPassword(null);
        return user;
    }
}
