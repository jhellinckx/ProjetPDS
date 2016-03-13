package dao;

public class CBUserPredictionsDAOImpl implements CBUserPredictionsDAO{
	private DAOFactory _daoFactory;

	CBUserPredictionsDAOImpl(DAOFactory fac){
		_daoFactory = fac;
	}
}