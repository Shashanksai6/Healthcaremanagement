package com.sjsu.HealthConnect.service;

import java.util.List;

import com.sjsu.HealthConnect.dao.UserDao;
import com.sjsu.HealthConnect.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface UserService {

    public Boolean isAuthorized(String token, String authRole);

    public Boolean isAuthorized(String token, int userId);

    public User registerUser(User user) ;

    public User getUserByEmailIdAndPassword(String emailId, String password) ;

    public User getUserByEmailIdAndPasswordAndRole(String emailId, String password, String role) ;

    public User getUserByEmailIdAndRole(String emailId, String role) ;

    public User getUserByEmailId(String emailId);

    public User getUserById(int userId);

    public User updateUser(User user);

    public List<User> getAllUserByRole(String role) ;

    public void deletUser(User user);
}

