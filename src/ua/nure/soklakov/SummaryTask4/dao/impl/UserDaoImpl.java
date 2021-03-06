package ua.nure.soklakov.SummaryTask4.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.soklakov.SummaryTask4.LoginDuplicateException;
import ua.nure.soklakov.SummaryTask4.core.user.Role;
import ua.nure.soklakov.SummaryTask4.core.user.Specialization;
import ua.nure.soklakov.SummaryTask4.core.user.User;
import ua.nure.soklakov.SummaryTask4.core.user.UserDao;
import ua.nure.soklakov.SummaryTask4.dao.ConnectionPool;
import ua.nure.soklakov.SummaryTask4.dao.Query;

/**
 * An Implementation of UserDao interface.
 * 
 * @author Oleg Soklakov
 *
 */
public class UserDaoImpl implements UserDao {

	private final static Logger LOG = Logger.getLogger(UserDaoImpl.class);

	private Connection connection;

	@Override
	public List<User> getDoctors() {
		List<User> users = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(Query.SELECT_ALL_DOCTORS)) {
			while (resultSet.next()) {

				users.add(new User(resultSet.getInt("id"), resultSet.getString("login"),
						resultSet.getString("password"), resultSet.getString("first_name"),
						resultSet.getString("last_name"), resultSet.getInt("role_id"),
						resultSet.getInt("specialization_id"), resultSet.getInt("count_of_patients")));
			}

		} catch (SQLException ex) {
			LOG.error("Can not find a doctors", ex);
		} finally {
			closeConnection();
		}

		return users;
	}

	@Override
	public List<User> getDoctorsBySpecialization(int specializationId) {
		List<User> users = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (final PreparedStatement prStatement = this.connection.prepareStatement(Query.SELECT_DOCTORS_BY_SPEC)) {
			prStatement.setInt(1, specializationId);
			ResultSet resultSet = prStatement.executeQuery();
			while (resultSet.next()) {
				users.add(new User(resultSet.getInt("id"), resultSet.getString("login"),
						resultSet.getString("password"), resultSet.getString("first_name"),
						resultSet.getString("last_name"), resultSet.getInt("role_id"),
						resultSet.getInt("specialization_id"), resultSet.getInt("count_of_patients")));
			}
			resultSet.close();
		} catch (SQLException e) {
			LOG.error("Can not find doctors by specialization");
		} finally {
			closeConnection();
		}

		return users;
	}

	@Override
	public void addUser(User user) throws LoginDuplicateException {
		connection = ConnectionPool.getConnection();
		try (PreparedStatement pStatement = connection.prepareStatement(Query.INSERT_USER)) {
			pStatement.setString(1, user.getLogin());
			pStatement.setString(2, user.getPassword());
			pStatement.setString(3, user.getFirstName());
			pStatement.setString(4, user.getLastName());
			pStatement.setInt(5, user.getRoleId());
			if (user.getSpecializationId() == 0) {
				pStatement.setNull(6, java.sql.Types.INTEGER);
				pStatement.setNull(7, java.sql.Types.INTEGER);
			} else {
				pStatement.setInt(6, user.getSpecializationId());
				pStatement.setInt(7, user.getCountOfPatients());
			}
			pStatement.executeUpdate();

		} catch (SQLException ex) {
			LOG.error("Can not create a new user", ex);

			// check exception if true found duplicate
			if (ex instanceof SQLIntegrityConstraintViolationException) {
				LOG.error("User with such login was created");
				throw new LoginDuplicateException();
			}
		} finally {
			closeConnection();
		}
	}

	@Override
	public User getUserByLogin(String login) {
		User user = null;
		connection = ConnectionPool.getConnection();
		try (PreparedStatement ps = connection.prepareStatement(Query.SELECT_USER_BY_LOGIN)) {
			ps.setString(1, login);
			ps.execute();

			ResultSet rs = ps.getResultSet();
			if (rs.next()) {
				user = new User(rs.getInt("id"), rs.getString("login"), rs.getString("password"),
						rs.getString("first_name"), rs.getString("last_name"), rs.getInt("role_id"),
						rs.getInt("specialization_id"), rs.getInt("count_of_patients"));
			}
			rs.close();
		} catch (SQLException ex) {
			LOG.error("Can not find user by login", ex);
		} finally {
			closeConnection();
		}

		return user;
	}

	@Override
	public User getUserById(int id) {
		User user = null;
		connection = ConnectionPool.getConnection();
		try (PreparedStatement ps = connection.prepareStatement(Query.SELECT_USER_BY_ID)) {
			ps.setInt(1, id);
			ps.execute();

			ResultSet rs = ps.getResultSet();
			if (rs.next()) {
				user = new User(rs.getInt("id"), rs.getString("login"), rs.getString("password"),
						rs.getString("first_name"), rs.getString("last_name"), rs.getInt("role_id"),
						rs.getInt("specialization_id"), rs.getInt("count_of_patients"));
			}
			rs.close();
		} catch (SQLException ex) {
			LOG.error("Can not find user by id", ex);
		} finally {
			closeConnection();
		}

		return user;
	}

	@Override
	public List<Role> getRoles() {
		List<Role> roles = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(Query.SELECT_ALL_ROLES)) {
			while (resultSet.next()) {

				// create role entity set id and add to list
				for (Role role : Role.values()) {
					if (resultSet.getString("name").toUpperCase().equals(role.toString())) {
						role.setId(resultSet.getInt("id"));
						roles.add(role);
						break;
					}
				}
			}

		} catch (SQLException ex) {
			LOG.error("Can not find a roles", ex);
		} finally {
			closeConnection();
		}

		return roles;
	}

	@Override
	public List<Specialization> getSpecializations() {
		List<Specialization> specializations = new ArrayList<>();
		connection = ConnectionPool.getConnection();
		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(Query.SELECT_ALL_SPECIALIZATIONS)) {
			while (resultSet.next()) {

				// create specialization entity set id and add to list
				for (Specialization specialization : Specialization.values()) {
					if (resultSet.getString("title").toUpperCase().equals(specialization.toString())) {
						specialization.setId(resultSet.getInt("id"));
						specializations.add(specialization);
						break;
					}
				}
			}

		} catch (SQLException ex) {
			LOG.error("Can not find a specializations", ex);
		} finally {
			closeConnection();
		}

		return specializations;
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
}
