package com.sjsu.HealthConnect.service.impl;

import java.util.List;
import java.util.Optional;

import com.sjsu.HealthConnect.dao.UserDao;
import com.sjsu.HealthConnect.entity.User;
import com.sjsu.HealthConnect.service.UserService;
import com.sjsu.HealthConnect.util.Constants.UserStatus;
import com.sjsu.HealthConnect.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Boolean isAuthorized(String token, String authRole){
        String username = jwtUtil.extractUsername(token.substring(7));
        System.out.println("username:  " + username );
        User user = userDao.findByEmailId(username);
        if(user == null || !user.getRole().equalsIgnoreCase(authRole)){
            return false;
        }
        return true;
    }

    @Override
    public Boolean isAuthorized(String token, int userId){
        String username = jwtUtil.extractUsername(token.substring(7));
        System.out.println("username:  " + username );
        User user = userDao.findByEmailId(username);
        if(user == null || !(user.getId()==userId)){
            return false;
        }
        return true;
    }

    @Override
    public User registerUser(User user) {
        User registeredUser = null;
        if (user != null) {
            registeredUser = this.userDao.save(user);
        }

        return registeredUser;
    }

    @Override
    public User getUserByEmailIdAndPassword(String emailId, String password) {
        return this.userDao.findByEmailIdAndPassword(emailId, password);
    }

    @Override
    public User getUserByEmailIdAndPasswordAndRole(String emailId, String password, String role) {
        return this.userDao.findByEmailIdAndPasswordAndRole(emailId, password, role);
    }

    @Override
    public User getUserByEmailIdAndRole(String emailId, String role) {
        return this.userDao.findByEmailIdAndRole(emailId, role);
    }

    @Override
    public User getUserByEmailId(String emailId) {
        return this.userDao.findByEmailId(emailId);
    }

    @Override
    public User getUserById(int userId) {
        return this.userDao.findById(userId).get();
    }

    @Override
    public User updateUser(User user) {
        return this.userDao.save(user);
    }

    @Override
    public List<User> getAllUserByRole(String role) {
        return this.userDao.findByRoleAndStatus(role, UserStatus.ACTIVE.value());
    }

    @Override
    public void deletUser(User user) {
        this.userDao.delete(user);
    }
}
