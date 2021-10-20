package com.ss.user.api;

import com.database.security.AuthDetails;
import com.ss.user.errors.EmailTakenException;
import com.ss.user.errors.UserNotFoundException;
import com.ss.user.model.Driver;
import com.ss.user.service.DriverService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/accounts")
public class DriverApiController {

    private final DriverService driverService;

    public DriverApiController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PutMapping("/driver")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Created", response = Driver.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 409, message = "Email conflict")})
    @ApiOperation(value = "create driver", nickname = "createDriver", response = Driver.class, authorizations = {
            @Authorization(value = "JWT")
    }, tags = {"user",})
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody Driver driver) throws EmailTakenException {
        return ResponseEntity.ok(driverService.createDriver(driver));
    }

    @GetMapping("/driver/{id}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Driver.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 404, message = "Not found")})
    @ApiOperation(value = "get driver", nickname = "getDriver", response = Driver.class, authorizations = {
            @Authorization(value = "JWT")
    }, tags = {"user",})
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Driver> getDriver(@PathVariable(value = "id") Long id) throws UserNotFoundException {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @DeleteMapping("/driver/{id}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Driver.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 404, message = "Not found")})
    @ApiOperation(value = "delete driver", nickname = "deleteDriver", authorizations = {
            @Authorization(value = "JWT")
    }, tags = {"user",})
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> deleteDriver(@PathVariable(value = "id") Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok(null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleDriverNotFound() {
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/driver")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Driver> getDriver(
            @RequestBody @Valid Driver driver,
            @RequestParam(value = "update-password", defaultValue = "false") boolean updatePassword) throws UserNotFoundException {
        if (driver.getId() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(driverService.updateDriver(driver, updatePassword));
    }

    @PostMapping("/driver/checkin")
    @PreAuthorize("hasAuthority('driver')")
    public ResponseEntity<Void> checkIn(Authentication authentication) throws UserNotFoundException {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        driverService.changeStatus(authDetails.getId(), true);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/driver/checkout")
    @PreAuthorize("hasAuthority('driver')")
    public ResponseEntity<Void> checkOut(Authentication authentication) throws UserNotFoundException {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        driverService.changeStatus(authDetails.getId(), false);
        return ResponseEntity.ok(null);
    }
}
