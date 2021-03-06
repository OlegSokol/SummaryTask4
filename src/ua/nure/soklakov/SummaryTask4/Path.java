package ua.nure.soklakov.SummaryTask4;

/**
 * Contains all paths.
 * 
 * @author Oleg Soklakov
 *
 */
public class Path {

	public static final String ERROR_PAGE = "/WEB-INF/jsp/errorPage.jsp";
	public static final String WELCOME_PAGE = "login.jsp";

	public static final String FORWARD_VIEW_ALL_DOCTORS = "/WEB-INF/jsp/listDoctors.jsp";
	public static final String FORWARD_VIEW_ALL_PATIENTS = "/WEB-INF/jsp/listPatients.jsp";
	public static final String FORWARD_VIEW_DISCHARGED_PATIENTS = "/WEB-INF/jsp/listDischargedPatients.jsp";
	public static final String FORWARD_USER_ADD = "/WEB-INF/jsp/addUserForm.jsp";
	public static final String FORWARD_PATIENT_ADD = "/WEB-INF/jsp/addPatientForm.jsp";
	public static final String FORWARD_HOSPITAL_CARD = "/WEB-INF/jsp/hospitalCard.jsp";
	
	//task
	public static final String FORWARD_GET_PATIENTS = "/WEB-INF/jsp/getPatients.jsp";

	public static final String REDIRECT_TO_VIEW_ALL_DOCTORS = "controller?command=listDoctors";
	public static final String REDIRECT_TO_VIEW_ALL_PATIENTS = "controller?command=listPatients";
	public static final String REDIRECT_TO_VIEW_DISCHARGED_PATIENTS = "controller?command=listDischargedPatients";
	public static final String REDIRECT_TO_VIEW_HOSPITAL_CARD = "controller?command=hospitalCard";
	public static final String REDIRECT_TO_VIEW_PATIENTS_BY_DOCTOR_ID = "controller?command=listPatientsByDoctorId";
	public static final String REDIRECT_TO_VIEW_ADD_USER_FORM = "controller?command=addUser";
	public static final String REDIRECT_TO_VIEW_ADD_PATIENT_FORM = "controller?command=addPatient";
	public static final String REDIRECT_TO_LOGIN_FORM = "controller?command=login";

}
