package edu.yachay.backend.identity.infrastructure.adapters.inputs;

import edu.yachay.backend.identity.application.dtos.*;
import edu.yachay.backend.identity.application.ports.inputs.RoleServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Adaptador REST para gestionar roles.
 * Expone endpoints para crear, actualizar, obtener y eliminar roles.
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleServicePort roleService;

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleRequest request) {
        RoleDTO roleDTO = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleDTO);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable Integer roleId,
            @Valid @RequestBody RoleRequest request) {
        RoleDTO roleDTO = roleService.updateRole(roleId, request);
        return ResponseEntity.ok(roleDTO);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Integer roleId) {
        RoleDTO roleDTO = roleService.getRoleById(roleId);
        return ResponseEntity.ok(roleDTO);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getRoleByName(@PathVariable String name) {
        var role = roleService.getRoleByName(name);
        if (role.isPresent()) {
            return ResponseEntity.ok(role.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}
