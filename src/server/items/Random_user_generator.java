/*******
 * Class generating a ArrayList<User> composed by Users object with random username and gender
 * -> ArrayList<User> ulist =  getRandomUsers(QUANTITY);
*******/
package items;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import dao.DAOFactory;
import dao.FoodDAO;



public class Random_user_generator
{
	//settings for random usernames
	private static int MINCHAR = 3;
	private static int MAXCHAR = 12;
	private static int MAXVALUE = 999;
	
	//nb of users wanted
	private static int QUANTITY = 10;
	
	//check unicity of usernames
	private static boolean CHECK = true;
	
	private static ArrayList<User> random_users_list = new ArrayList<User>();
	private static FoodDAO foodDao = null;

	public Random_user_generator(FoodDAO foodDao_, Boolean quick)
	{
		foodDao = foodDao_;
		if(quick){
			random_users_list =  getRandomUsers(QUANTITY);
		}
		else{
			random_users_list =  getRandomUsersComplete(QUANTITY);
		}
	}

	public Random_user_generator(int quantity_, FoodDAO foodDao_, Boolean quick)
	{
		QUANTITY = quantity_;
		foodDao = foodDao_;
		if(quick){
			random_users_list =  getRandomUsers(QUANTITY);
		}
		else{
			random_users_list =  getRandomUsersComplete(QUANTITY);
		}
	}
	
	//input : bounds of random int
	//ouptut : random int
	public static int generateRandomNumberBetween(int min, int max)
	{
		Random r = new Random();
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		return r.nextInt((max-min)+1)+min;	
	}
	
	//input : length of character composing username, maxValue of the random number
	//output : list of usernames (AAAAX where AAAA is length random letters and X is a number)
	private static String generateRandomUsername(int length, int max)
	{
		StringBuilder tmp_username = new StringBuilder(length);
		int x;
		
		for(int i=0 ; i<length ; ++i)//add length random char
		{
			tmp_username.append((char)('a'+generateRandomNumberBetween(0,25)));
		}
		x = generateRandomNumberBetween(0,max);
		tmp_username.append(x);//add x
		String username = tmp_username.toString();
		return username;
	}
	
	
	//input : minimum and maximum characters length composing the username, max value terminating username
	//ouput : randomly created user;
	private static User generateRandomUserComplete()
	{
		//user created
		User random_user = generateRandomUser();

		//init food pref
		
		for (int j = 0 ; j<2 ; ++j){//appreciated and deppreciated
			List<Food> foodList = new ArrayList<Food>();
			List<Long> ids = new ArrayList<Long> ();
			int nb_of_pref = generateRandomNumberBetween(1,5);
			for(int i = 0 ; i<nb_of_pref ; ++i){
				Long randomId = new Long(generateRandomNumberBetween(1,63016));
				ids.add(randomId);
			}
			foodList = foodDao.findByIds(ids);
			if(j==0){
				random_user.setApprecitedFood(foodList);
			}
			else{
				random_user.setDepreciatedFood(foodList);
			}
		}
		
		return random_user;
	}

	private static User generateRandomUser()
	{
		//init username
		String username = generateRandomUsername(generateRandomNumberBetween(MINCHAR,MAXCHAR),MAXVALUE);

		//init genre
		String gender;
		int random_flag = generateRandomNumberBetween(0,2);
		if(random_flag==0){gender = "M";}
		else if(random_flag==1){gender = "F";}
		else{gender = "K";}
		
		//user created
		User random_user = new User(username,gender);
		return random_user;
	}

	public static ArrayList<Long> generateFoodIds(){
		int nb_of_pref = generateRandomNumberBetween(1,5);
		ArrayList<Long> Ids = new ArrayList<Long>();
		for (int i=0; i<nb_of_pref ; ++i){
			Ids.add(new Long(generateRandomNumberBetween(1,63016)));
		}
		return Ids;
	}
	
	
	//input : user and list of users
	//ouput : true if user's username alreadi in list of users, else false
	private static boolean alreadyExists(User u)
	{
		for( int i=0 ; i<random_users_list.size(); ++i)
		{
			if(u.getUsername() == random_users_list.get(i).getUsername())
			{
				return true;
			}
		}
		return false;
	}
	
	
	//input : number of random users desired
	//output : List of randomly generated users
	public static ArrayList<User> getRandomUsersComplete(int length)
	{
		ArrayList<User> random_users = new ArrayList<User>();
		for(int i=0 ; i<length ; ++i)
		{
			User random_user = generateRandomUserComplete();
			if(CHECK)
			{
				if(!alreadyExists(random_user))
				{
					random_users.add(random_user);
				}
			}
			else{random_users.add(random_user);}
		}
		return random_users;
	}

	public static ArrayList<User> getRandomUsers(int length)
	{
		ArrayList<User> random_users = new ArrayList<User>();
		for(int i=0 ; i<length ; ++i)
		{
			User random_user = generateRandomUser();
			if(CHECK)
			{
				if(!alreadyExists(random_user))
				{
					random_users.add(random_user);
				}
			}
			else{random_users.add(random_user);}
		}
		return random_users;
	}
	
	
	//input : list of users
	//output : print username and gender of users in user list on prompt
	public static void printUsers()
	{
		for (int i=0 ; i<random_users_list.size() ; ++i)
		{
			User u = random_users_list.get(i);
			System.out.println(u.getUsername()+" - "+ u.getGender()+"\n");
		}
	}
	
	
	public static ArrayList<User> get_random_users_list(){return random_users_list;}


	//input : list of users
	//ouput : SQL instruction tu insert users into User table in mysql
	public static String SQL_generateInsertionInstruction()
	{
		String SQL_instruction = new String();
		String values = new String();
		
		for (int i=0 ; i<random_users_list.size() ; ++i)
		{	
			values+= "('";
			values += random_users_list.get(i).getUsername();
			values += "','";
			values += random_users_list.get(i).getGender();
			values += "')";
			if (i != random_users_list.size()-1){values += ",";}
		}
		SQL_instruction = "INSERT INTO User (username,gender) VALUES"+values+";";
		return SQL_instruction;
	}
	
}
