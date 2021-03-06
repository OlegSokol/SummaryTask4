package ua.nure.soklakov.SummaryTask4.web.commands;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ua.nure.soklakov.SummaryTask4.Path;
import ua.nure.soklakov.SummaryTask4.core.patient.PatientManager;
import ua.nure.soklakov.SummaryTask4.core.patient.PatientManagerImpl;
import ua.nure.soklakov.SummaryTask4.core.patient.Treatment;
import ua.nure.soklakov.SummaryTask4.web.ActionType;
import ua.nure.soklakov.SummaryTask4.web.utils.validation.HospitalCardInputValidator;

/**
 * Add treatment command.
 * 
 * @author Oleg Soklakov
 *
 */
public class AddTreatmentCommand extends Command {

	private static final long serialVersionUID = -8451623053220784931L;

	private static final Logger LOG = Logger.getLogger(AddTreatmentCommand.class);

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response, ActionType actionType)
			throws IOException, ServletException {
		LOG.debug("Start executing Command");

		String result = null;

		if (actionType == ActionType.POST) {
			result = doPost(request, response);
		} else {
			result = null;
		}

		LOG.debug("End executing command");
		return result;
	}

	/**
	 * Redirect after submitting treatment add form.
	 * 
	 * @return path to the view of patient hospital card if fields properly
	 *         filled, otherwise redisplays hospital card with error message.
	 */
	private String doPost(HttpServletRequest request, HttpServletResponse response) {

		int typeOfTreatmentId = Integer.parseInt(request.getParameter("typeOfTreatmentId"));
		int hospitalCardId = (int) request.getSession().getAttribute("hospitalCardId");
		String nameOfaMedication = request.getParameter("nameOfMedication");
		LOG.trace("Fields: TyoeId - " + typeOfTreatmentId + ", hospital card id - " + hospitalCardId
				+ ", name of medication - " + nameOfaMedication);

		boolean valid = HospitalCardInputValidator.validateTreatmentParametrs(nameOfaMedication);
		if (!valid) {
			LOG.trace("Field not properly valid");
			return Path.REDIRECT_TO_VIEW_HOSPITAL_CARD + "&error=notValidTreatment";
		}

		Treatment treatment = new Treatment(typeOfTreatmentId, hospitalCardId, nameOfaMedication);

		PatientManager manager = new PatientManagerImpl();
		manager.addTreatment(treatment);
		LOG.trace("Treatment added to data base");

		return Path.REDIRECT_TO_VIEW_HOSPITAL_CARD;
	}

}
