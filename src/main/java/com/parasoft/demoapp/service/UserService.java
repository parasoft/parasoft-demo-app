package com.parasoft.demoapp.service;

import com.parasoft.demoapp.exception.UserNotFoundException;
import com.parasoft.demoapp.exception.UsernameExistsAlreadyException;
import com.parasoft.demoapp.messages.UserMessages;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.repository.global.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
    public UserEntity getUserByUsername(String username) throws UsernameNotFoundException {
        Objects.requireNonNull(username, UserMessages.USERNAME_CANNOT_NULL);

    	UserEntity user = userRepository.findByUsername(username);
    	if(user == null) {
    		throw new UsernameNotFoundException(MessageFormat.format(UserMessages.USERNAME_NOT_FOUND, username));
    	}
    	
        return user;
    }

    public UserEntity addNewUser(String username, String password) throws UsernameExistsAlreadyException {

        Objects.requireNonNull(password , UserMessages.PASSWORD_CANNOT_NULL);
    	
        if(userExist(username)){
            throw new UsernameExistsAlreadyException(
                    MessageFormat.format(UserMessages.USERNAME_EXISTS_ALREADY, username));
        }
        
        password = passwordEncoder.encode(password);
        UserEntity user = new UserEntity(username, password);

        return userRepository.save(user);
    }

    public UserEntity updateUser(UserEntity user) throws UserNotFoundException {
        Objects.requireNonNull(user, UserMessages.USER_CANNOT_NULL);

        Long id = user.getId();
        if(id == null || !userRepository.existsById(id)){
            throw new UserNotFoundException(MessageFormat.format(UserMessages.USER_ID_NOT_FOUND, id));
        }

        return userRepository.save(user);
    }

    public boolean userExist(String username){
        Objects.requireNonNull(username, UserMessages.USERNAME_CANNOT_NULL);
        return userRepository.existsByUsername(username);
    }

    public void removeUser(Long id) throws UserNotFoundException {
        Objects.requireNonNull(id, UserMessages.USER_ID_CANNOT_NULL);

    	if(!userRepository.existsById(id)){
            throw new UserNotFoundException(MessageFormat.format(UserMessages.USER_ID_NOT_FOUND, id));
        }
        userRepository.deleteById(id);
    }
    
}
