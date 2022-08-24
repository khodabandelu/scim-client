package com.khodabandelu.scim.client.api.controllers;


import com.khodabandelu.scim.client.api.commands.CreateProvisionerCommand;
import com.khodabandelu.scim.client.api.commands.SyncProvisioningCommand;
import com.khodabandelu.scim.client.api.dto.BaseResponse;
import com.khodabandelu.scim.client.api.dto.CreateProvisionerResponse;
import com.khodabandelu.scim.client.api.dto.SyncProvisioningResponse;
import com.khodabandelu.scim.client.api.services.ProvisionerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class use for managing provisioners and sync between scim and these external identity systems.
 *
 * @author Mahdi Khodabandelu
 */
@RestController
@RequestMapping("/api/provisioner")
public class ProvisionerController {
    private final Logger logger = Logger.getLogger(ProvisionerController.class.getName());

    private final ProvisionerService provisionerService;

    public ProvisionerController(ProvisionerService provisionerService) {
        this.provisionerService = provisionerService;
    }

    /**
     * {@code POST  /} : create new provisioner by admin.
     *
     * @param command the command with provisioner data.
     * @return the id of provisioner created.
     * @throws IllegalStateException {@code 400 (Bad Request)} if the data is not correct.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<BaseResponse> createProvisioner(@RequestBody CreateProvisionerCommand command) {
        try {
            var provisioner = this.provisionerService.save(command);
            return new ResponseEntity<>(new CreateProvisionerResponse("Sync provisioning request completed successfully!", provisioner.getId()), HttpStatus.OK);
        } catch (IllegalStateException e) {
            logger.log(Level.WARNING, MessageFormat.format("Client made a request - {0}.", e.toString()));
            return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrMessage = "Error while processing request to sync provisioning ";
            return new ResponseEntity<>(new BaseResponse(safeErrMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * {@code POST  /syncProvisioning} : sync provisioning with external identity system by admin and return secret key to sign jwt by external identity system.
     * this endpoint generates secret for organization to sync with provisioner.
     *
     * @param command the command with provisioner data.
     * @return the secret to sign jwt by external identity system to user provisioning.
     * @throws IllegalStateException {@code 400 (Bad Request)} if the data is not correct.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/syncProvisioning")
    public ResponseEntity<BaseResponse> syncProvisioning(@RequestBody SyncProvisioningCommand command) {
        try {
            var secret = this.provisionerService.syncProvisioning(command);
            return new ResponseEntity<>(new SyncProvisioningResponse("Sync provisioning request completed successfully!", secret), HttpStatus.OK);
        } catch (IllegalStateException e) {
            logger.log(Level.WARNING, MessageFormat.format("Client made a request - {0}.", e.toString()));
            return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrMessage = "Error while processing request to sync provisioning ";
            return new ResponseEntity<>(new BaseResponse(safeErrMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
