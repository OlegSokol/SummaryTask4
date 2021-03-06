package ua.nure.soklakov.SummaryTask4.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.soklakov.SummaryTask4.core.patient.HospitalCard;
import ua.nure.soklakov.SummaryTask4.core.patient.Patient;
import ua.nure.soklakov.SummaryTask4.core.patient.PatientDao;
import ua.nure.soklakov.SummaryTask4.core.patient.Treatment;
import ua.nure.soklakov.SummaryTask4.core.patient.TypeOfTreatment;
import ua.nure.soklakov.SummaryTask4.dao.ConnectionPool;
import ua.nure.soklakov.SummaryTask4.dao.Query;

/**
 * An implementation of PatientDao interface.
 * 
 * @author Oleg Soklakov
 *
 */
public class PatientDaoImpl implements PatientDao {

	private final static Logger LOG = Logger.getLogger(PatientDaoImpl.class);

	private Connection connection;

	@Override
	public List<Patient> getPatients() {
		List<Patient> patients = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(Query.SELECT_ALL_PATIENTS)) {
			while (rs.next()) {
				patients.add(new Patient(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"),
						rs.getDate("birthday"), rs.getInt("doctor_id"), rs.getInt("card_id")));
			}

		} catch (SQLException ex) {
			LOG.error("Can not find a patients", ex);
		} finally {
			closeConnection();
		}
		return patients;
	}

	@Override
	public List<TypeOfTreatment> getTypesOfTreatment() {
		List<TypeOfTreatment> typesOfTreatment = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(Query.SELECT_ALL_TYPES_OF_TREATMENT)) {
			while (rs.next()) {
				
				// create entity set id and add to list
				for(TypeOfTreatment typeOfTreatment : TypeOfTreatment.values()) {
					if (rs.getString("title").toUpperCase().equals(typeOfTreatment.toString())) {
						typeOfTreatment.setId(rs.getInt("id"));
						typesOfTreatment.add(typeOfTreatment);
						break;
					}
				}
			}

		} catch (SQLException ex) {
			LOG.error("Can not find a types of treatment", ex);
		} finally {
			closeConnection();
		}
		return typesOfTreatment;
	}

	@Override
	public List<Patient> getPatientsByDoctorId(int doctorId) {
		List<Patient> patients = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (PreparedStatement pStatement = connection.prepareStatement(Query.SELECT_PATIENTS_BY_DOCTOR_ID)) {
			pStatement.setInt(1, doctorId);
			pStatement.execute();

			ResultSet rs = pStatement.getResultSet();
			while (rs.next()) {
				patients.add(new Patient(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"),
						rs.getDate("birthday"), rs.getInt("doctor_id"), rs.getInt("card_id")));
			}

		} catch (SQLException ex) {
			LOG.error("Can not find a patients by doctor id", ex);
		} finally {
			closeConnection();
		}
		return patients;
	}

	@Override
	public void addPatient(Patient patient) {
		connection = ConnectionPool.getConnection();
		try (PreparedStatement pStatement = connection.prepareStatement(Query.INSERT_PATIENT)) {
			pStatement.setString(1, patient.getFirstName());
			pStatement.setString(2, patient.getLastName());
			pStatement.setDate(3, patient.getBirthday());
			pStatement.setInt(4, patient.getCardId());
			pStatement.executeUpdate();

		} catch (SQLException ex) {
			LOG.error("Can not create a new patient", ex);
		} finally {
			closeConnection();
		}

	}

	@Override
	public void setDoctorToPatient(int parientId, int doctoeId) {
		connection = ConnectionPool.getConnection();
		try (PreparedStatement pStatement = connection.prepareStatement(Query.SET_DOCTOR_TO_PATIENT);
				PreparedStatement psIncrement = connection.prepareStatement(Query.INCREMENT_COUNT_OF_PATIENTS)) {
			connection.setAutoCommit(false);

			pStatement.setInt(1, doctoeId);
			pStatement.setInt(2, parientId);
			pStatement.executeUpdate();

			psIncrement.setInt(1, doctoeId);
			psIncrement.executeUpdate();

			connection.commit();
		} catch (SQLException ex) {
			LOG.error("Can not set a doctor to the patient", ex);
		} finally {
			closeConnection();
		}

	}

	@Override
	public void updateDiagnosisInHospitalCard(int cardId, String diagnosis) {
		connection = ConnectionPool.getConnection();
		try (PreparedStatement pStatement = connection.prepareStatement(Query.UPDATE_DIAGNOSIS)) {
			pStatement.setString(1, diagnosis);
			pStatement.setInt(2, cardId);
			pStatement.executeUpdate();
		} catch (SQLException ex) {
			LOG.error("Can not update diagnosis", ex);
		} finally {
			closeConnection();
		}

	}

	@Override
	public int addHospitalCard() {
		int cardId = 0;
		connection = ConnectionPool.getConnection();
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(Query.CREATE_HOSPITAL_CARD, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = statement.getGeneratedKeys();
			if (rs != null && rs.next()) {
				cardId = rs.getInt(1);
			}
		} catch (SQLException ex) {
			LOG.error("Can not create hospital card", ex);
		} finally {
			closeConnection();
		}

		return cardId;
	}

	@Override
	public List<Treatment> getTreatmentsByCardId(int cardId) {
		List<Treatment> treatments = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (final PreparedStatement prStatement = this.connection
				.prepareStatement(Query.SELECT_TREATMENTS_BY_HOSPITAL_CARD)) {
			prStatement.setInt(1, cardId);
			ResultSet rs = prStatement.executeQuery();
			while (rs.next()) {
				treatments.add(new Treatment(rs.getInt("id"), rs.getInt("type_of_treatment_id"),
						rs.getInt("hospital_card_id"), rs.getString("name_of_medication"), rs.getBoolean("done")));
			}
			rs.close();
		} catch (SQLException e) {
			LOG.error("Can not find treatments by hospitalcard id");
		} finally {
			closeConnection();
		}

		return treatments;
	}

	@Override
	public void addTreatment(Treatment treatment) {
		connection = ConnectionPool.getConnection();
		try (PreparedStatement pStatement = connection.prepareStatement(Query.INSERT_TREATMENT)) {
			pStatement.setInt(1, treatment.getHospitalCardId());
			pStatement.setInt(2, treatment.getTypeOfTreatmentId());
			pStatement.setString(3, treatment.getNameOfMedication());
			pStatement.executeUpdate();

		} catch (SQLException ex) {
			LOG.error("Can not create a new treatment", ex);
		} finally {
			closeConnection();
		}

	}

	@Override
	public void finishTreatment(int treatmentId) {
		connection = ConnectionPool.getConnection();
		try (PreparedStatement pStatement = connection.prepareStatement(Query.UPDATE_FINISH_TREATMENT)) {
			pStatement.setInt(1, treatmentId);
			pStatement.executeUpdate();

		} catch (SQLException ex) {
			LOG.error("Can not finish treatment", ex);
		} finally {
			closeConnection();
		}

	}

	@Override
	public HospitalCard getHospitalCardById(int id) {
		connection = ConnectionPool.getConnection();
		HospitalCard card = null;
		try (PreparedStatement ps = connection.prepareStatement(Query.SELECT_HOSPITAL_CARD_BY_ID)) {
			ps.setInt(1, id);
			ps.execute();

			ResultSet rs = ps.getResultSet();
			if (rs.next()) {
				card = new HospitalCard(rs.getInt("id"), rs.getString("diagnosis"));
			}
			rs.close();
		} catch (SQLException ex) {
			LOG.error("Can not find hospital card by id", ex);
		} finally {
			closeConnection();
		}

		return card;
	}

	@Override
	public Patient getPatientByHospitalCardId(int hospitalCardId) {
		connection = ConnectionPool.getConnection();
		Patient patient = null;
		try (PreparedStatement ps = connection.prepareStatement(Query.SELECT_PATIENT_BY_HOSPITAL_CARD_ID)) {
			ps.setInt(1, hospitalCardId);
			ps.execute();

			ResultSet rs = ps.getResultSet();
			if (rs.next()) {
				patient = new Patient(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"),
						rs.getDate("birthday"), rs.getInt("doctor_id"), rs.getInt("card_id"));
			}
			rs.close();
		} catch (SQLException ex) {
			LOG.error("Can not find patient by hospital card id", ex);
		} finally {
			closeConnection();
		}

		return patient;
	}

	@Override
	public List<Patient> getDischargedPatientsByDoctorId(int doctorId) {
		List<Patient> patients = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (PreparedStatement pStatement = connection
				.prepareStatement(Query.SELECT_DISCHARGED_PATIENTS_BY_DOCTOR_ID)) {
			pStatement.setInt(1, doctorId);
			pStatement.execute();

			ResultSet rs = pStatement.getResultSet();
			while (rs.next()) {
				patients.add(new Patient(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"),
						rs.getDate("birthday"), rs.getInt("doctor_id")));
			}

		} catch (SQLException ex) {
			LOG.error("Can not find a patients by doctor id", ex);
		} finally {
			closeConnection();
		}
		return patients;
	}

	@Override
	public void completeTheCourseOfTreatment(Patient patient) {
		connection = ConnectionPool.getConnection();
		try (PreparedStatement psPatient = connection.prepareStatement(Query.DELETE_PATIENT_BY_ID);
				PreparedStatement psHospitalCard = connection.prepareStatement(Query.DELETE_HOSPITAL_CARD_BY_ID);
				PreparedStatement psDoctorCount = connection.prepareStatement(Query.DECREMENT_COUNT_OF_PATIENTS);
				PreparedStatement psDischargedPatient = connection.prepareStatement(Query.INSERT_DISCHARGED_PATIENT);
				PreparedStatement psTreatments = connection.prepareStatement(Query.DELETE_TREATMENTS_BY_HOSPITAL_CARD_ID);) {
			connection.setAutoCommit(false);

			LOG.trace("Patient data: id: " + patient.getId() + ", firstName: " + patient.getFirstName() + ", lastName: "
					+ patient.getLastName() + ",date: " + patient.getBirthday() + ", cardId: " + patient.getCardId()
					+ ", doctorID: " + patient.getDoctorId());

			// set patient id for delete
			psPatient.setInt(1, patient.getId());
			psPatient.executeUpdate();

			// set hospital card id for delete treatments
			psTreatments.setInt(1, patient.getCardId());
			psTreatments.executeUpdate();
			
			// set hospital card id for delete
			psHospitalCard.setInt(1, patient.getCardId());
			psHospitalCard.executeUpdate();

			// set doctor id for decrement count of patients 
			psDoctorCount.setInt(1, patient.getDoctorId());
			psDoctorCount.executeUpdate();

			// set data for insert new discharged patient 
			psDischargedPatient.setString(1, patient.getFirstName());
			psDischargedPatient.setString(2, patient.getLastName());
			psDischargedPatient.setDate(3, patient.getBirthday());
			psDischargedPatient.setInt(4, patient.getDoctorId());
			psDischargedPatient.executeUpdate();

			connection.commit();
		} catch (SQLException ex) {
			LOG.error("Can not compleate the course of treatment", ex);
		} finally {
			closeConnection();
		}

	}

	/**
	 * The method closes connection.
	 */
	private void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				LOG.error("Can not close connection");
			}
		}
	}

	@Override
	public List<Patient> getPatientsByTreatmentOperation() {
		List<Patient> patients = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(Query.SELECT_PATIENTS_BY_DIAGNOSIS)) {
			while (rs.next()) {
				patients.add(new Patient(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"),
						rs.getDate("birthday"), rs.getInt("doctor_id"), rs.getInt("card_id")));
			}

		} catch (SQLException ex) {
			LOG.error("Can not find a patients", ex);
		} finally {
			closeConnection();
		}
		return patients;
	}
}
