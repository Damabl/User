package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public String createUser(String username, String email, String firstName, String lastName, String password) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(true);

            Response response = usersResource.create(user);

            if (response.getStatus() == 201) {
                String userId = extractUserIdFromLocation(response.getLocation().getPath());
                
                setUserPassword(userId, password);
                
                assignRole(userId, "user");
                
                log.info("User created in Keycloak: {} (ID: {})", username, userId);
                return userId;
            } else {
                String error = response.readEntity(String.class);
                log.error("Failed to create user in Keycloak. Status: {}, Error: {}", response.getStatus(), error);
                throw new RuntimeException("Failed to create user in Keycloak: " + error);
            }
        } catch (Exception e) {
            log.error("Error creating user in Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating user in Keycloak", e);
        }
    }


    private void setUserPassword(String userId, String password) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        usersResource.get(userId).resetPassword(credential);
        log.info("Password set for user: {}", userId);
    }

    private void assignRole(String userId, String roleName) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            RoleRepresentation role;
            try {
                role = realmResource.roles().get(roleName).toRepresentation();
            } catch (Exception e) {
                log.info("Role '{}' not found, creating it...", roleName);
                createRole(roleName);
                role = realmResource.roles().get(roleName).toRepresentation();
            }

            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role));
            
            log.info("Role '{}' assigned to user: {}", roleName, userId);
        } catch (Exception e) {
            log.warn("Failed to assign role '{}' to user: {}. Error: {}", roleName, userId, e.getMessage());
        }
    }
    

    private void createRole(String roleName) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription("Auto-created role: " + roleName);
            
            realmResource.roles().create(role);
            log.info("Role '{}' created successfully", roleName);
        } catch (Exception e) {
            log.error("Failed to create role '{}': {}", roleName, e.getMessage());
        }
    }


    private String extractUserIdFromLocation(String locationPath) {
        String[] parts = locationPath.split("/");
        return parts[parts.length - 1];
    }


    public void deleteUser(String keycloakUserId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            usersResource.delete(keycloakUserId);
            log.info("User deleted from Keycloak: {}", keycloakUserId);
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak: {}", e.getMessage(), e);
        }
    }


    public UserRepresentation getUserByUsername(String username) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            List<UserRepresentation> users = usersResource.search(username, true);
            if (users != null && !users.isEmpty()) {
                return users.get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting user from Keycloak: {}", e.getMessage(), e);
            return null;
        }
    }
}

