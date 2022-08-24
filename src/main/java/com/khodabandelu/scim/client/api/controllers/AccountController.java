package com.khodabandelu.scim.client.api.controllers;

import com.khodabandelu.scim.client.api.commands.AuthenticateUserCommand;
import com.khodabandelu.scim.client.api.dto.AuthenticateUserResponse;
import com.khodabandelu.scim.client.api.dto.BaseResponse;
import com.khodabandelu.scim.client.api.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rest controller for managing authenticate user flow
 *
 * @author Mahdi Khodabandelu
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {
    Logger logger = Logger.getLogger(AccountController.class.getName());

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * {@code POST  /authenticate} : authenticate the user.
     *
     * @param command the command with authentication data.
     * @return the token if the user authenticate request completed successfully.
     * @throws AuthenticationException {@code 401 (Unauthorized)} if the username or password is not correct.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<BaseResponse> authenticate(@RequestBody AuthenticateUserCommand command) {
        try {
            var token = accountService.authenticate(command);
            return new ResponseEntity<>(new AuthenticateUserResponse("Authenticate user request completed successfully!", token), HttpStatus.OK);
        } catch (AuthenticationException e) {
            logger.log(Level.WARNING, MessageFormat.format("Authenticate user request failed - {0}.", e.toString()));
            return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            var safeErrMessage = MessageFormat.format("Error while processing request to authenticate user for username = {0}", command.getUsername());
            logger.log(Level.SEVERE, safeErrMessage);
            return new ResponseEntity<>(new BaseResponse(safeErrMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * {@code GET  /isAuthenticated} : check if the user is authenticated, and return its principal name.
     *
     * @param request the HTTP request.
     * @return the principal name if the user is authenticated.
     */
    @GetMapping("/isAuthenticated")
    public String isAuthenticated(HttpServletRequest request) {
        return request.getUserPrincipal().getName();
    }


}
