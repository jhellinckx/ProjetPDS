package manager;

import java.lang.Math;
import dao.AllCategoriesDAO;
import nioserver.Message;

import org.json.simple.JSONObject;
import java.util.List;

import static org.calorycounter.shared.Constants.network.*;

public class CategoriesRequestManager implements RequestManager{
	
	private AllCategoriesDAO _categoriesDatabase;

	public CategoriesRequestManager(AllCategoriesDAO cdb){
		_categoriesDatabase = cdb;
	}

	private void manageResponse(JSONObject response, List<String> categories, int nbPackets){
		for (int j=0 ; j < nbPackets; j++){
			for(int i = 0 ; i<Math.min(JSON_THRESHOLD, categories.size()); i++){
				response.put(CATEGORY_NAME+Integer.toString(i), categories.get(i+Math.min(JSON_THRESHOLD, categories.size())*j));
			}
			
		}
	}

	@Override
	public JSONObject manageRequest(Message msg){
		List<String> categories = null;
		int nbPackets = (int) Math.ceil((double)FOOD_CATEGORIES_SIZE/JSON_THRESHOLD);
		String request = (String) msg.toJSON().get(REQUEST_TYPE);
		if(request.equals(FOOD_CATEGORIES_REQUEST)){
			categories = _categoriesDatabase.getAllFoodCategories();
		} else {
			categories = _categoriesDatabase.getAllRecipeCategories();
		}
		for(String s : categories){
		}
		JSONObject response = new JSONObject();
		manageResponse(response, categories, nbPackets);
		return response;
	}
}