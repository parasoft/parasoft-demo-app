package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.RoleNotFoundException;
import com.parasoft.demoapp.messages.UserMessages;
import com.parasoft.demoapp.exception.RoleNameExistsAlreadyException;
import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.repository.global.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public RoleEntity addNewRole(String roleName) throws RoleNameExistsAlreadyException {

        if(roleExists(roleName)){
            throw new RoleNameExistsAlreadyException(
                    MessageFormat.format(UserMessages.ROLE_NAME_EXISTS_ALREADY, roleName));
        }
        RoleEntity role = new RoleEntity(roleName);

        return roleRepository.save(role);
    }

    public boolean roleExists(String roleName){
        return roleRepository.existsByName(roleName);
    }

    public RoleEntity getRoleByRoleName(String name) throws RoleNotFoundException {
        RoleEntity role = roleRepository.findByName(name);
        if(role == null){
            throw new RoleNotFoundException(MessageFormat.format(UserMessages.ROLE_NAME_NOT_FOUND, name));
        }
        return role;
    }

}
