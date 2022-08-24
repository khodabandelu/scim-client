package com.khodabandelu.scim.client.api.controllers;


import com.khodabandelu.scim.client.api.commands.LookupUserCommand;
import com.khodabandelu.scim.client.api.dto.BaseResponse;
import com.khodabandelu.scim.client.api.dto.LookupUserResponse;
import com.khodabandelu.scim.client.api.dto.UserDto;
import com.khodabandelu.scim.client.api.mappers.UserMapper;
import com.khodabandelu.scim.client.api.services.UserService;
import com.khodabandelu.scim.client.domains.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class use for managing users.
 *
 * @author Mahdi Khodabandelu
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final Logger logger = Logger.getLogger(UserController.class.getName());

    private final UserService userService;
    private final UserMapper userMapper;


    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * {@code GET /api/users} : get all users by parameters.
     *
     * @param pageable the pagination information.
     * @param command  query by command parameters.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    @GetMapping("")
    public ResponseEntity<BaseResponse> getAllUsers(LookupUserCommand command, Pageable pageable) {
        Page<UserDto> page = this.userService.findAll(command, pageable).map(userMapper::toDto);
        return new ResponseEntity<>(new LookupUserResponse("Lookup users request completed successfully!", page.getContent()), HttpStatus.OK);
    }

    /**
     * {@code GET /api/users} : get all users by parameters for own organization
     *
     * @param pageable the pagination information.
     * @param command  query by command parameters.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/byOrganization")
    public ResponseEntity<BaseResponse> getAllUsersByOrganization(LookupUserCommand command, Pageable pageable) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        var user = (User) securityContext.getAuthentication().getPrincipal();
        command.setOrganizationId(user.getOrganizationId());
        Page<UserDto> page = this.userService.findAll(command, pageable).map(userMapper::toDto);
        return new ResponseEntity<>(new LookupUserResponse("Lookup users request completed successfully!", page.getContent()), HttpStatus.OK);
    }


    /**
     * {@code Post /api/users/activate} : activate user.
     *
     * @param userId id of user that we want to activate it.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/activate/{userId}")
    public ResponseEntity<BaseResponse> activate(@PathVariable("userId") String userId) {
        try {
            this.userService.activate(userId);
            return new ResponseEntity<>(new BaseResponse("Activate user request completed successfully!"), HttpStatus.OK);
        } catch (IllegalStateException e) {
            logger.log(Level.WARNING, MessageFormat.format("Client made a request - {0}.", e.toString()));
            return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrMessage = "Error while processing request to activate user ";
            return new ResponseEntity<>(new BaseResponse(safeErrMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
