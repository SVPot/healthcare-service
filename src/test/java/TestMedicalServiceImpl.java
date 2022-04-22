import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestMedicalServiceImpl {

    static PatientInfoRepository patientInfoRepository;
    static SendAlertService sendAlertService;


    @BeforeAll
    public static void initSuite() {

        patientInfoRepository = mock(PatientInfoFileRepository.class);
        sendAlertService = mock(SendAlertServiceImpl.class);
        PatientInfo patientInfo = new PatientInfo(
                "id1",
                "Иван",
                "Петров",
                LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)));

        when(patientInfoRepository.getById("id1")).thenReturn(patientInfo);

//        doThrow(new Exception()).when(sendAlertService).send(anyString());
//        doNothing().when(sendAlertService).send(anyString());

    }

    @Test
    public void test_should_send_alert_if_change_blood_pressure() {
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        BloodPressure bloodPressure = new BloodPressure(130, 80);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        medicalService.checkBloodPressure("id1", bloodPressure);
        verify(sendAlertService).send(argumentCaptor.capture());
        assertEquals("Warning, patient with id: id1, need help", argumentCaptor.getValue());

    }

    @Test
    public void test_should_send_alert_if_not_normal_temperature() {
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        medicalService.checkTemperature("id1", new BigDecimal("39.0"));
        verify(sendAlertService).send(argumentCaptor.capture());
        assertEquals("Warning, patient with id: id1, need help", argumentCaptor.getValue());
    }

    @Test
    public void test_not_send_alert_if_normal_temperature() {
        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        medicalService.checkTemperature("id1", new BigDecimal("36.6"));
        verify(sendAlertService, never());
    }


}
