package com.diabetesapp.config;

import com.diabetesapp.Main;
import com.diabetesapp.model.Detection;
import com.diabetesapp.model.DetectionRepository;
import com.diabetesapp.model.Therapy;
import com.diabetesapp.model.TherapyRepository;
import com.diabetesapp.view.ViewNavigator;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static io.github.palexdev.materialfx.utils.StringUtils.containsAny;
import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;

public class Validator {
    private static final String[] upperChar = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
    private static final String[] lowerChar = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
    private static final String[] digits = "0 1 2 3 4 5 6 7 8 9".split(" ");

    public static void createPasswordConstraints(MFXPasswordField passwordField, MFXPasswordField passwordField2, Label errorLabel) {
        BooleanBinding blankBinding = passwordField.textProperty().isNotEmpty();
        Constraint blankConstraint = createConstraint("Password Field can't be blank", blankBinding);

        Constraint matchConstraint = null;
        if (passwordField2 != null) {
            BooleanBinding matchBinding = passwordField.textProperty().isEqualTo(passwordField2.textProperty());
            matchConstraint = createConstraint("Passwords must match", matchBinding);
        }

        BooleanBinding lengthBinding = passwordField.textProperty().length().greaterThanOrEqualTo(8);
        Constraint lengthConstraint =  createConstraint("Password must be at least 8 characters long", lengthBinding);

        BooleanBinding digitBinding = Bindings.createBooleanBinding(
                () -> containsAny(passwordField.getText(), "", digits), passwordField.textProperty());
        Constraint digitConstraint = createConstraint("Password must contain at least one digit", digitBinding);

        BooleanBinding charactersBinding = Bindings.createBooleanBinding(
                () -> containsAny(passwordField.getText(), "", upperChar) && containsAny(passwordField.getText(), "", lowerChar), passwordField.textProperty());
        Constraint charactersConstraint = createConstraint("Password must contain at least one lowercase and one uppercase characters", charactersBinding);

        if (matchConstraint != null) {
            passwordField.getValidator()
                    .constraint(blankConstraint)
                    .constraint(digitConstraint)
                    .constraint(charactersConstraint)
                    .constraint(lengthConstraint)
                    .constraint(matchConstraint);
        } else {
            passwordField.getValidator()
                    .constraint(blankConstraint)
                    .constraint(digitConstraint)
                    .constraint(charactersConstraint)
                    .constraint(lengthConstraint);
        }

        setupValidationListeners(
                passwordField.getValidator().validProperty(),
                passwordField.delegateFocusedProperty(),
                passwordField::validate,
                state -> passwordField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, state),
                msg -> {
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                },
                () -> {
                    errorLabel.setVisible(false);
                    errorLabel.setManaged(false);
                }
        );
    }

    public static void createMailConstraints(MFXTextField mailField, Label errorLabel) {
        BooleanBinding blankBinding = mailField.textProperty().isNotEmpty();
        Constraint blankConstraint = createConstraint("Field can't be blank", blankBinding);

        BooleanBinding regexBinding = Bindings.createBooleanBinding(() -> mailField.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"), mailField.textProperty());
        Constraint regexConstraint = createConstraint("Enter a valid email address", regexBinding);

        mailField.getValidator()
                .constraint(blankConstraint)
                .constraint(regexConstraint);

        setupValidationListeners(
                mailField.getValidator().validProperty(),
                mailField.delegateFocusedProperty(),
                mailField::validate,
                state -> mailField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, state),
                msg -> {
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                },
                () -> {
                    errorLabel.setVisible(false);
                    errorLabel.setManaged(false);
                }
        );
    }

    public static void createUsernameConstraints(MFXTextField usernameField, Label errorLabel) {
        BooleanBinding blankBinding = usernameField.textProperty().isNotEmpty();
        Constraint blankConstraint = createConstraint("Field can't be blank", blankBinding);

        BooleanBinding regexBinding = Bindings.createBooleanBinding(() -> {
            String username = usernameField.getText();

            if (username.contains("@")) {
                return username.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
            } else {
                return username.equals("admin") || username.toUpperCase().matches("^[A-Z]{6}[0-9]{2}[A-EHLMPR-T][0-9]{2}[A-Z][0-9]{3}[A-Z]$");
            }
        }, usernameField.textProperty());
        Constraint regexConstraint = createConstraint("Enter a valid email address or tax code", regexBinding);

        usernameField.getValidator()
                .constraint(blankConstraint)
                .constraint(regexConstraint);

        setupValidationListeners(
                usernameField.getValidator().validProperty(),
                usernameField.delegateFocusedProperty(),
                usernameField::validate,
                state -> usernameField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, state),
                msg -> {
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                },
                () -> {
                    errorLabel.setVisible(false);
                    errorLabel.setManaged(false);
                }
        );
    }

    public static void emptyFieldConstraints(MFXTextField field, Label errorLabel) {
        BooleanBinding blankBinding = field.textProperty().isNotEmpty();
        Constraint blankConstraint = createConstraint("Field can't be empty", blankBinding);

        field.getValidator()
                .constraint(blankConstraint);

        setupValidationListeners(
                field.getValidator().validProperty(),
                field.delegateFocusedProperty(),
                field::validate,
                state -> field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, state),
                msg -> {
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                },
                () -> {
                    errorLabel.setVisible(false);
                    errorLabel.setManaged(false);
                }
        );
    }

    public static void createTaxCodeConstraints(MFXTextField taxCodeField, Label errorLabel) {
        BooleanBinding blankBinding = taxCodeField.textProperty().isNotEmpty();
        Constraint blankConstraint = createConstraint("Field can't be empty", blankBinding);

        BooleanBinding regexBinding = Bindings.createBooleanBinding(() -> taxCodeField.getText().matches("^[A-Z]{6}[0-9]{2}[A-EHLMPR-T][0-9]{2}[A-Z][0-9]{3}[A-Z]$"), taxCodeField.textProperty());
        Constraint regexConstraint = createConstraint("Enter a valid tax code", regexBinding);

        taxCodeField.getValidator()
                .constraint(blankConstraint)
                .constraint(regexConstraint);

        setupValidationListeners(
                taxCodeField.getValidator().validProperty(),
                taxCodeField.delegateFocusedProperty(),
                taxCodeField::validate,
                state -> taxCodeField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, state),
                msg -> {
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                },
                () -> {
                    errorLabel.setVisible(false);
                    errorLabel.setManaged(false);
                }
        );
    }

    public static void createDateConstraints(MFXTextField dateField, Label errorLabel) {
        BooleanBinding blankBinding = dateField.textProperty().isNotEmpty();
        Constraint blankConstraint = createConstraint("Field can't be empty", blankBinding);

        BooleanBinding regexBinding = Bindings.createBooleanBinding(() -> dateField.getText().matches("^[0-9]{2}+/[0-9]{2}+/[0-9]{4}$"), dateField.textProperty());
        Constraint regexConstraint = createConstraint("Enter a valid date", regexBinding);

        dateField.getValidator()
                .constraint(blankConstraint)
                .constraint(regexConstraint);

        setupValidationListeners(
                dateField.getValidator().validProperty(),
                dateField.delegateFocusedProperty(),
                dateField::validate,
                state -> dateField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, state),
                msg -> {
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                },
                () -> {
                    errorLabel.setVisible(false);
                    errorLabel.setManaged(false);
                }
        );
    }

    public static void createDetectionConstraints(MFXComboBox<String> comboBox1, MFXComboBox<String> comboBox2, Label errorLabel) {
        DetectionRepository detectionRepository = Main.getDetectionRepository();
        List<Detection> todaysDetections = detectionRepository.getDailyDetections(ViewNavigator.getAuthenticatedUsername());

        BooleanBinding blankBinding = comboBox1.textProperty().isNotEmpty();
        Constraint blankConstraint = createConstraint("Field can't be empty", blankBinding);

        Constraint duplicateConstraint = null;
        if (comboBox2.getId().equals("mealBox")) {
            BooleanBinding duplicateBinding = Bindings.createBooleanBinding(() -> todaysDetections.stream()
                    .noneMatch(detection ->
                            detection.meal().equals(comboBox2.getSelectedItem()) &&
                                    detection.period().equals(comboBox1.getSelectedItem())
                    ), comboBox1.textProperty(), comboBox2.textProperty());
            duplicateConstraint = createConstraint("Detection already registered", duplicateBinding);
        }

        if (duplicateConstraint != null) {
            comboBox1.getValidator()
                    .constraint(blankConstraint)
                    .constraint(duplicateConstraint);
        } else {
            comboBox1.getValidator()
                    .constraint(blankConstraint);
        }

        setupValidationListeners(
                comboBox1.getValidator().validProperty(),
                comboBox1.delegateFocusedProperty(),
                comboBox1::validate,
                state -> comboBox1.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, state),
                msg -> {
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                },
                () -> {
                    errorLabel.setVisible(false);
                    errorLabel.setManaged(false);
                }
        );
    }

    public static void createTherapyConstraints(MFXComboBox<String> patientBox, MFXTextField drugField, Label errorLabel) {
        TherapyRepository therapyRepository = Main.getTherapyRepository();
        String patientString = patientBox.getValue();
        int start = patientString.indexOf('(') + 1;
        int end = patientString.indexOf(')');
        String patient = patientString.substring(start, end);
        List<Therapy> therapies = therapyRepository.getTherapiesByPatient(patient);

        BooleanBinding blankBinding = drugField.textProperty().isNotEmpty();
        Constraint blankConstraint = createConstraint("Field can't be empty", blankBinding);

        BooleanBinding alredyPresent = Bindings.createBooleanBinding(() -> therapies.stream().noneMatch(therapy -> therapy.patient().equals(patient) && therapy.drug().equals(drugField.getText())), patientBox.textProperty(), drugField.textProperty());
        Constraint alreadyPresentConstraint = createConstraint("Therapy already present", alredyPresent);

        drugField.getValidator()
                .constraint(alreadyPresentConstraint)
                .constraint(blankConstraint);

        setupValidationListeners(
                drugField.getValidator().validProperty(),
                drugField.delegateFocusedProperty(),
                drugField::validate,
                state -> drugField.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, state),
                msg -> {
                    errorLabel.setText(msg);
                    errorLabel.setVisible(true);
                    errorLabel.setManaged(true);
                },
                () -> {
                    errorLabel.setVisible(false);
                    errorLabel.setManaged(false);
                }
        );
    }

    public static boolean checkConstraints(MFXTextField field, Label errorLabel) {
        List<Constraint> constraints = field.validate();
        if (!constraints.isEmpty()) {
            field.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
            errorLabel.setText(constraints.getFirst().getMessage());
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return false;
        }
        return true;
    }

    private static Constraint createConstraint(String errorMessage, BooleanBinding condition) {
        return Constraint.Builder.build()
                .setSeverity(Severity.ERROR)
                .setMessage(errorMessage)
                .setCondition(condition)
                .get();
    }

    public static void removeConstraints(MFXTextField field, Label errorLabel) {
        BooleanBinding blankBinding = field.textProperty().isNotEmpty();
        Constraint blankConstraint = createConstraint("Field can't be empty", blankBinding);

        BooleanBinding regexBinding = Bindings.createBooleanBinding(() -> field.getText().matches("^[0-9]{2}+/[0-9]{2}+/[0-9]{4}$"), field.textProperty());
        Constraint regexConstraint = createConstraint("Enter a valid date", regexBinding);

        field.getValidator().removeConstraint(blankConstraint).removeConstraint(regexConstraint);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private static void setupValidationListeners(
            ObservableValue<Boolean> validProperty,
            ObservableValue<Boolean> focusedProperty,
            Supplier<List<Constraint>> validatorFunction,
            Consumer<Boolean> pseudoClassUpdater,
            Consumer<String> errorMessageSetter,
            Runnable hideError) {

        // Listener sulla validProperty (campo valido → nascondi errore)
        validProperty.addListener((_, _, newValue) -> {
            if (newValue) {
                hideError.run();
                pseudoClassUpdater.accept(false);
            }
        });

        // Listener su perdita di focus → mostra errore se necessario
        focusedProperty.addListener((_, oldValue, newValue) -> {
            if (oldValue && !newValue) { // Quando il focus si perde
                List<Constraint> constraints = validatorFunction.get();
                if (!constraints.isEmpty()) {
                    pseudoClassUpdater.accept(true);
                    errorMessageSetter.accept(constraints.getFirst().getMessage());
                }
            }
        });
    }
}
