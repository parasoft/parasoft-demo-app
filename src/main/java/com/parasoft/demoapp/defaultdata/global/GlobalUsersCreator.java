package com.parasoft.demoapp.defaultdata.global;

import com.parasoft.demoapp.defaultdata.AbstractDataCreator;
import com.parasoft.demoapp.exception.RoleNameExistsAlreadyException;
import com.parasoft.demoapp.exception.RoleNotFoundException;
import com.parasoft.demoapp.exception.UserNotFoundException;
import com.parasoft.demoapp.exception.UsernameExistsAlreadyException;
import com.parasoft.demoapp.messages.DatabaseOperationMessages;
import com.parasoft.demoapp.model.global.RoleEntity;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.service.RoleService;
import com.parasoft.demoapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static com.parasoft.demoapp.model.global.RoleType.ROLE_APPROVER;
import static com.parasoft.demoapp.model.global.RoleType.ROLE_PURCHASER;

@Component
@Slf4j
@Order(1) // The order of default data creation
public class GlobalUsersCreator extends AbstractDataCreator {

	public static final String USERNAME_PURCHASER = "purchaser";
	public static final String USERNAME_APPROVER = "approver";
	public static final String PASSWORD = "password";
	
	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	@Qualifier("globalDataSource")
	protected DataSource globalDataSource;

	@Override
	public void switchIndustry() {
		// no need to do anything
	}

	@Override
	@Transactional
	public void populateData() {
		log.info(messages.getString(DatabaseOperationMessages.WRITE_DEFAULT_USERS));

		// create the roles if not exists
		RoleEntity purchaserRole = createRoleIfNotExists(ROLE_PURCHASER.toString());
		RoleEntity approverRole = createRoleIfNotExists(ROLE_APPROVER.toString());

		// create the users if not exists
		createUserIfNotExists(USERNAME_PURCHASER, PASSWORD, purchaserRole);
		createUserIfNotExists(USERNAME_APPROVER, PASSWORD, approverRole);

		for (int i = 2; i < 51; i++) {
			createUserIfNotExists(USERNAME_PURCHASER + i, PASSWORD, purchaserRole);
			createUserIfNotExists(USERNAME_APPROVER + i, PASSWORD, approverRole);
		}

		log.info(messages.getString(DatabaseOperationMessages.WRITE_DONE));
	}

	private RoleEntity createRoleIfNotExists(String roleName){

		RoleEntity role = null;

		try {
			role = roleService.addNewRole(roleName);
		} catch (RoleNameExistsAlreadyException e) {
			try {
				role = roleService.getRoleByRoleName(roleName);
			} catch (RoleNotFoundException ex) {
				// can not reach here
			}
		}

		return role;
	}

	private UserEntity createUserIfNotExists(String username, String password, RoleEntity role) {

		UserEntity user = null;
		try {
			user = userService.addNewUser(username, password);
		} catch (UsernameExistsAlreadyException e) {
			try {
				user = userService.getUserByUsername(username);
			}catch (UsernameNotFoundException ex){
				ex.printStackTrace();
				// can not reach here
			}
		}

		user.setRole(role);
		try {
			user = userService.updateUser(user);
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			// can not reach here
		}

		return user;
	}
}
