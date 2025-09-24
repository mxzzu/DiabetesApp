import com.diabetesapp.config.PasswordUtil;
import com.diabetesapp.controller.DoctorDataController;
import com.diabetesapp.model.Detection;
import com.diabetesapp.model.Intake;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {

    @Test
    void testGetGlucoseColor() {
        DoctorDataController controller = new DoctorDataController();

        // Test per i livelli di glucosio prima di mangiare
        assertEquals("#4CAF50", controller.getGlucoseColor("Before eating", 100));
        assertEquals("#FF9800", controller.getGlucoseColor("Before eating", 150));
        assertEquals("#F44336", controller.getGlucoseColor("Before eating", 200));

        // Test per i livelli di glucosio dopo aver mangiato
        assertEquals("#4CAF50", controller.getGlucoseColor("After eating", 150));
        assertEquals("#FF9800", controller.getGlucoseColor("After eating", 200));
        assertEquals("#F44336", controller.getGlucoseColor("After eating", 300));
    }

    @Test
    void testPasswordHashing() {
        String plainPassword = "password123";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        assertTrue(PasswordUtil.checkPassword(plainPassword, hashedPassword));
        assertFalse(PasswordUtil.checkPassword("wrongpassword", hashedPassword));
    }

    @Test
    void testIntakeToString() {
        Intake intake = new Intake("testuser", LocalDate.now(), "Metformin", "08:00", "500");
        assertEquals("METFORMIN (08:00): 500 (mg)", intake.toString());
    }

    @Test
    void testDetectionToString() {
        Detection detection = new Detection("testuser", LocalDate.now(), "Breakfast", "Before eating", 120);
        assertEquals("Breakfast (Before eating): 120 (mg/dL)", detection.toString());
    }
}
