package edu.yachay.backend.identity.application.ports.inputs;

import edu.yachay.backend.identity.application.dtos.RoleDTO;
import edu.yachay.backend.identity.application.dtos.RoleRequest;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada para las operaciones relacionadas con roles.
 */
public interface RoleServicePort {
    RoleDTO createRole(RoleRequest request);

    RoleDTO updateRole(Integer roleId, RoleRequest request);

    RoleDTO getRoleById(Integer roleId);

    Optional<RoleDTO> getRoleByName(String name);

    List<RoleDTO> getAllRoles();

    void deleteRole(Integer roleId);
}
