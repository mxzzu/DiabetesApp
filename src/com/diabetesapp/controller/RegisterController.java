package com.diabetesapp.controller;

import com.diabetesapp.Main;
import com.diabetesapp.config.Validator;
import com.diabetesapp.model.Patient;
import com.diabetesapp.model.User;
import com.diabetesapp.model.UserRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;

import static io.github.palexdev.materialfx.utils.RandomUtils.random;

public class RegisterController {

    public VBox medicalInfoCard;

    @FXML
    private Label userTypeLabel, genderLabel, taxCodeLabel,nameLabel, surnameLabel, birthDateLabel, emailLabel, docUserLabel, statusLabel;

    @FXML
    private MFXComboBox<String> userTypeBox, genderBox;

    @FXML
    private MFXTextField usernameField, nameField, surnameField, emailField, riskField, prevPatField, comField, docField;

    @FXML
    private MFXDatePicker birthDateField;

    private UserRepository userRepository;

    @FXML
    public void initialize() {
        userRepository = Main.getUserRepository();

        if (ViewNavigator.isUserSaved()) {
            showSuccess();
        }

        userTypeBox.valueProperty().addListener((_, _, newValue) -> {
            if (newValue.equals("Patient")) {
                riskField.setVisible(true);
                riskField.setManaged(true);
                prevPatField.setVisible(true);
                prevPatField.setManaged(true);
                comField.setVisible(true);
                comField.setManaged(true);
                docField.setVisible(true);
                docField.setManaged(true);
            } else {
                riskField.setVisible(false);
                riskField.setManaged(false);
                prevPatField.setVisible(false);
                prevPatField.setManaged(false);
                comField.setVisible(false);
                comField.setManaged(false);
                docField.setVisible(false);
                docField.setManaged(false);
            }
        });
        birthDateField.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", birthDateField.getLocale()));
        Validator.emptyFieldConstraints(userTypeBox, userTypeLabel);
        Validator.emptyFieldConstraints(genderBox, genderLabel);
        Validator.createTaxCodeConstraints(usernameField, taxCodeLabel);
        Validator.emptyFieldConstraints(nameField, nameLabel);
        Validator.emptyFieldConstraints(surnameField, surnameLabel);
        Validator.createMailConstraints(emailField, emailLabel);
        Validator.createDateConstraints(birthDateField, birthDateLabel);
        Validator.createTaxCodeConstraints(docField, docUserLabel);

    }

    @FXML
    private void handleRegister() {
        boolean userTypeCheck = Validator.checkConstraints(userTypeBox, userTypeLabel);
        boolean genderCheck = Validator.checkConstraints(genderBox, genderLabel);
        boolean taxCodeCheck = Validator.checkConstraints(usernameField, taxCodeLabel);
        boolean nameCheck = Validator.checkConstraints(nameField, nameLabel);
        boolean surnameCheck = Validator.checkConstraints(surnameField, surnameLabel);
        boolean emailCheck = Validator.checkConstraints(emailField, emailLabel);
        boolean birthDateCheck = Validator.checkConstraints(birthDateField, birthDateLabel);
        boolean docCheck = true;

        if (userTypeCheck && userTypeBox.getValue().equals("Patient")) {
            docCheck = Validator.checkConstraints(docField, docUserLabel);
        }

        if (!userTypeCheck || !genderCheck || !taxCodeCheck || !nameCheck || !surnameCheck || !emailCheck || !birthDateCheck || !docCheck) {
            return;
        }

        String psw = createRandomPsw();

        String userType = userTypeBox.getValue().toLowerCase();
        String gender = genderBox.getValue();
        String taxCode = usernameField.getText();
        String userName = nameField.getText();
        String userSurname = surnameField.getText();
        String userEmail = emailField.getText();
        String birthDate = birthDateField.getText();

        if (userRepository.getUser(taxCode) != null) {
            showError("User already exists");
            return;
        }

        if (userType.equals("patient")) {
            String riskFactors =  riskField.getText();
            String prevPat = prevPatField.getText();
            String com = comField.getText();
            String doc = docField.getText();
            if (!checkDoc(doc)) {
                showError("Doc Tax Code not found");
                return;
            }
            Patient newPatient = new Patient(taxCode, psw, userType, userName, userSurname, birthDate, gender, userEmail, true, riskFactors, prevPat, com, doc);
            userRepository.addUser(newPatient);
        } else {
            User newDoctor = new User(taxCode, psw, userType, userName, userSurname, birthDate, gender, userEmail, true);
            userRepository.addUser(newDoctor);
        }

        ViewNavigator.setUserSaved(true);
        ViewNavigator.navigateToRegister(true);

    }

    private String createRandomPsw() {
        String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LOWER = UPPER.toLowerCase();
        String DIGITS = "0123456789";
        String ALL_CHARS = UPPER + LOWER + DIGITS;

        StringBuilder password = new StringBuilder(8);

        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));

        for (int i = 3; i < 8; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        System.out.println(password);
        return BCrypt.hashpw(password.toString(), BCrypt.gensalt());
    }

    private boolean checkDoc(String docUser) {
        return userRepository.getUser(docUser) != null;
    }

    private void showSuccess() {
        statusLabel.setText("User has been saved");
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add("alert-success");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add("alert-danger");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

}
