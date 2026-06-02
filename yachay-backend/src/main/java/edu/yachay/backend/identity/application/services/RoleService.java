package edu.yachay.backend.identity.application.services;

import edu.yachay.backend.identity.application.dtos.RoleDTO;
import edu.yachay.backend.identity.application.dtos.RoleRequest;
import edu.yachay.backend.identity.application.ports.inputs.RoleServicePort;
import edu.yachay.backend.identity.domain.exceptions.ResourceConflictException;
import edu.yachay.backend.identity.domain.exceptions.ResourceNotFoundException;
import edu.yachay.backend.identity.domain.models.Role;
import edu.yachay.backend.identity.domain.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para gestionar roles.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoleService implements RoleServicePort {

    private final RoleRepository roleRepository;
    private final IdentityMapper mapper;

    @Override
    public RoleDTO createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new ResourceConflictException("El rol '" + request.getName() + "' ya existe");
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Role savedRole = roleRepository.save(role);
        return mapper.toRoleDTO(savedRole);
    }

    @Override
    public RoleDTO updateRole(Integer roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol con ID " + roleId + " no encontrado"));

        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new ResourceConflictException("El rol '" + request.getName() + "' ya existe");
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        Role updatedRole = roleRepository.save(role);
        return mapper.toRoleDTO(updatedRole);
    }

    @Override
    public RoleDTO getRoleById(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol con ID " + roleId + " no encontrado"));
        return mapper.toRoleDTO(role);
    }

    @Override
    public Optional<RoleDTO> getRoleByName(String name) {
        return roleRepository.findByName(name).map(mapper::toRoleDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(mapper::toRoleDTO)
                .toList();
    }

    @Override
    public void deleteRole(Integer roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Rol con ID " + roleId + " no encontrado");
        }
        roleRepository.deleteById(roleId);
    }
}
