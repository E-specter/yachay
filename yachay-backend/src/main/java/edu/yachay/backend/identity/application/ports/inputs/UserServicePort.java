package edu.yachay.backend.identity.application.ports.inputs;

import edu.yachay.backend.identity.application.dtos.*;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para las operaciones relacionadas con usuarios.
 * Define el contrato de servicios que deben implementarse en la capa de aplicación.
 */
public interface UserServicePort {
    UserDTO createUser(CreateUserRequest request);

    UserDTO updateUser(String userId, UpdateUserRequest request);

    UserDTO getUserById(String userId);

    Optional<UserDTO> getUserByEmail(String email);

    List<UserDTO> getAllUsers();

    void deleteUser(String userId);

    void assignRoleToUser(String userId, String roleName);

    void removeRoleFromUser(String userId, String roleName);

    List<UserDTO> getUsersByRole(String roleName);
}
