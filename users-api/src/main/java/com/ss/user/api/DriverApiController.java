package com.ss.user.api;

import com.ss.user.errors.EmailTakenException;
import com.ss.user.model.Driver;
import com.ss.user.model.User;
import com.ss.user.service.DriverService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @ApiOperation(value = "create driver", nickname = "createDriver", response = User.class, authorizations = {
            @Authorization(value = "JWT")
    }, tags = {"user",})
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody Driver driver) throws EmailTakenException {
        return ResponseEntity.ok(driverService.createDriver(driver));
    }
}
