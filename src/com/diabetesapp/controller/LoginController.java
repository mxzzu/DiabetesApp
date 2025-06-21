package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.PasswordUtil;
import com.diabetesapp.config.Validator;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;

public class LoginController {
    @FXML
    private MFXTextField usernameField;

    @FXML
    private MFXPasswordField passwordField;
    
    @FXML
    private Label validationLabel1, validationLabel2;
    
    private UserRepository userRepository;

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();

        Validator.createUsernameConstraints(usernameField, validationLabel1);
        //Validator.createPasswordConstraints(passwordField, null, validationLabel2);
    }
    
    @FXML
    private void handleLogin() {
        boolean check1 = Validator.checkConstraints(usernameField, validationLabel1);
        //boolean check2 = Validator.checkConstraints(passwordField, validationLabel2);

        if (!check1) {
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();

        User user;
        if (username.contains("@")) {
            user = userRepository.getUserByEmail(username);
        } else if (!username.equals("admin")) {
            user = userRepository.getUser(username.toUpperCase());
        } else {
            user = userRepository.getUser(username);
        }

        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            // Login successful
            ViewNavigator.setAuthenticatedUser(user);
            ViewNavigator.setMustChangePassword(user.isMustChangePassword());
            ViewNavigator.navigateToDashboard();
        } else {
            showError();
        }
    }

    private void showError() {
        passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
        validationLabel2.setText("Invalid username or password");
        validationLabel2.setVisible(true);
        validationLabel2.setManaged(true);
    }
}