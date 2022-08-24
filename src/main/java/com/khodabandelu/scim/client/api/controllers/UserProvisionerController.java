package com.khodabandelu.scim.client.api.controllers;


import com.khodabandelu.scim.client.api.commands.ProvisionUserCommand;
import com.khodabandelu.scim.client.api.dto.BaseResponse;
import com.khodabandelu.scim.client.api.dto.ProvisionUserResponse;
import com.khodabandelu.scim.client.api.services.UserProvisionerService;
import com.khodabandelu.scim.client.security.token.TokenService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class use for managing provisioned users and provisioning users by external identity systems.
 *
 * @author Mahdi Khodabandelu
 */
@RestController
@RequestMapping("/api/organizations/{organizationId}/provisioner")
public class UserProvisionerController {
    private final Logger logger = Logger.getLogger(UserProvisionerController.class.getName());

    private final UserProvisionerService userProvisionerService;

    private final TokenService tokenService;

    public UserProvisionerController(UserProvisionerService userProvisionerService, TokenService tokenService) {
        this.userProvisionerService = userProvisionerService;
        this.tokenService = tokenService;
    }

    /**
     * {@code POST  /{provisionerId}/users } : provision user by external identity system
     * The first step in provisioning the user is to verify the JWT token by using the saved secret for
     * this external identity and organization
     *
     * @param command the command with provisioner data.
     * @return the id of provisioner created.
     * @throws IllegalStateException {@code 400 (Bad Request)} if the data is not correct.
     * @throws JwtException          {@code 403 (Unauthorized)} if the jwt token is not signed by saved secret.
     */
    @PostMapping("/{provisionerId}/users")
    public ResponseEntity<BaseResponse> provisionUser(HttpServletRequest request,
                                                      @PathVariable("organizationId") String organizationId,
                                                      @PathVariable("provisionerId") String provisionerId,
                                                      @RequestBody ProvisionUserCommand command) {
        try {
            String bearerToken = request.getHeader("Authorization");
            tokenService.validateProvisionerToken(bearerToken, organizationId, provisionerId);
            var applicationId = userProvisionerService.provisionUser(command, organizationId, provisionerId);
            var response = ProvisionUserResponse.builder()
                    .id(command.getId())
                    .email(command.getEmail())
                    .name(command.getName())
                    .applicationId(applicationId)
                    .build();
            response.setMessage("Provision user request completed successfully!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            logger.log(Level.WARNING, MessageFormat.format("Client made a bad request - {0}.", e.toString()));
            return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
        } catch (JwtException e) {
            logger.log(Level.WARNING, MessageFormat.format("Authentication provisioner is failed - {0}.", e.toString()));
            return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            var safeErrMessage = MessageFormat.format("Error while processing request to provision user for this id = {0}", command.getId());
            logger.log(Level.SEVERE, safeErrMessage);
            return new ResponseEntity<>(new ProvisionUserResponse(safeErrMessage, command.getId()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
