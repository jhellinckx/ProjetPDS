#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast
import mysql.connector
from mysql.connector import errorcode
from colruyt_models import *
from recipe_model import *
import time

YELLOW = "\033[33m"
MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_colruyt"
db_encoding = "utf8"
db_properties_filename = "../../src/main/resources/dao.properties"

sports_filename = "raw/sports_data.txt"
data_char = "@"
data_start = 2
delimiter = ";"

images_directory = "raw/db_images"
image_png_filename = "db_image_{0}"
nb_images = 6573

max_sql_single_insert = 500

# Reciepes variable
recipes = []
categories = []
ingredients = []
origins = []


articles_names_correction = \
			{
				BaseArticle.BRANCH_INDEX_KEY 						: BaseArticle.BRANCH_INDEX_KEY,\
				BaseArticle.IMAGE_KEY 								: "image_url",\
				BaseArticle.PRICE_UNIT_KEY 							: BaseArticle.PRICE_UNIT_KEY,\
				BaseArticle.PRICE_KG_KEY 							: BaseArticle.PRICE_KG_KEY,\
				BaseArticle.DETAILS_URL_KEY 						: "url",\
				BaseArticle.CATEGORIES_KEY 							: "categories",\
				BaseArticle.WEIRD_NAME_KEY 							: "brand",\
				BaseArticle.SHORT_DESCRIPTION_KEY					: "product_name",\
				BaseArticle.FULL_DESCRIPTION_KEY 					: BaseArticle.FULL_DESCRIPTION_KEY,\
				DetailedArticle.REAL_DETAILS_URL_KEY 				: DetailedArticle.REAL_DETAILS_URL_KEY,\
				DetailedArticle.PORTION_ENERGY_KJ_KEY 				: DetailedArticle.PORTION_ENERGY_KJ_KEY,\
				DetailedArticle.PORTION_ENERGY_KCAL_KEY 			: DetailedArticle.PORTION_ENERGY_KCAL_KEY,\
				DetailedArticle.PORTION_TOTAL_FAT_KEY 				: DetailedArticle.PORTION_TOTAL_FAT_KEY,\
				DetailedArticle.PORTION_SATURATED_FAT_KEY 			: DetailedArticle.PORTION_SATURATED_FAT_KEY,\
				DetailedArticle.PORTION_TOTAL_CARBOHYDRATES_KEY 	: DetailedArticle.PORTION_TOTAL_CARBOHYDRATES_KEY,\
				DetailedArticle.PORTION_SUGARS_KEY 					: DetailedArticle.PORTION_SUGARS_KEY,\
				DetailedArticle.PORTION_TOTAL_PROTEINS_KEY 			: DetailedArticle.PORTION_TOTAL_PROTEINS_KEY,\
				DetailedArticle.PORTION_FIBERS_KEY 					: DetailedArticle.PORTION_FIBERS_KEY,\
				DetailedArticle.PORTION_SALT_KEY 					: DetailedArticle.PORTION_SALT_KEY,\
				DetailedArticle.PORTION_INFO_KEY 					: DetailedArticle.PORTION_INFO_KEY,\
				DetailedArticle.PER_100G_ENERGY_KJ_KEY 				: "energy_100g",\
				DetailedArticle.PER_100G_ENERGY_KCAL_KEY 			: DetailedArticle.PER_100G_ENERGY_KCAL_KEY,\
				DetailedArticle.PER_100G_TOTAL_FAT_KEY 				: "fat_100g",\
				DetailedArticle.PER_100G_SATURATED_FAT_KEY 			: "saturated_fat_100g",\
				DetailedArticle.PER_100G_TOTAL_CARBOHYDRATES_KEY 	: "carbohydrates_100g",\
				DetailedArticle.PER_100G_SUGARS_KEY 				: "sugars_100g",\
				DetailedArticle.PER_100G_TOTAL_PROTEINS_KEY 		: "proteins_100g",\
				DetailedArticle.PER_100G_FIBERS_KEY 				: "fiber_100g",\
				DetailedArticle.PER_100G_SALT_KEY					: "salt_100g",\
				DetailedArticle.TOTAL_QUANTITY_KEY 					: "quantity",\
				DetailedArticle.INGREDIENTS_TEXT_KEY 				: "ingredients_text",\
				DetailedArticle.BAR_CODE_KEY 						: "code",\
				DetailedArticle.ALLERGENS_CONTAINS_KEY 				: "allergens",\
				DetailedArticle.ALLERGENS_TRACE_OF_KEY 				: "traces",\
			}
RDAs = \
		[
			DetailedArticle.PORTION_ENERGY_KJ_KEY 			,\
			DetailedArticle.PORTION_ENERGY_KCAL_KEY 		,\
			DetailedArticle.PORTION_TOTAL_FAT_KEY 			,\
			DetailedArticle.PORTION_SATURATED_FAT_KEY 		,\
			DetailedArticle.PORTION_TOTAL_CARBOHYDRATES_KEY ,\
			DetailedArticle.PORTION_SUGARS_KEY 				,\
			DetailedArticle.PORTION_TOTAL_PROTEINS_KEY 		,\
			DetailedArticle.PORTION_FIBERS_KEY 				,\
			DetailedArticle.PORTION_SALT_KEY 				,\
			DetailedArticle.PER_100G_ENERGY_KJ_KEY 			,\
			DetailedArticle.PER_100G_ENERGY_KCAL_KEY 		,\
			DetailedArticle.PER_100G_TOTAL_FAT_KEY 			,\
			DetailedArticle.PER_100G_SATURATED_FAT_KEY 		,\
			DetailedArticle.PER_100G_TOTAL_CARBOHYDRATES_KEY ,\
			DetailedArticle.PER_100G_SUGARS_KEY 			,\
			DetailedArticle.PER_100G_TOTAL_PROTEINS_KEY 	,\
			DetailedArticle.PER_100G_FIBERS_KEY 			,\
			DetailedArticle.PER_100G_SALT_KEY				,\
		]

articles_details_sql_types = \
			{
				BaseArticle.BRANCH_INDEX_KEY 						: "INT UNSIGNED",\
				BaseArticle.IMAGE_KEY 								: "VARCHAR(255)",\
				BaseArticle.PRICE_UNIT_KEY 							: "FLOAT",\
				BaseArticle.PRICE_KG_KEY 							: "FLOAT",\
				BaseArticle.DETAILS_URL_KEY 						: "VARCHAR(255)",\
				BaseArticle.CATEGORIES_KEY 							: "TEXT",\
				BaseArticle.WEIRD_NAME_KEY 							: "VARCHAR(255)",\
				BaseArticle.SHORT_DESCRIPTION_KEY					: "VARCHAR(255)",\
				BaseArticle.FULL_DESCRIPTION_KEY 					: "TEXT",\
				DetailedArticle.REAL_DETAILS_URL_KEY 				: "VARCHAR(255)",\
				DetailedArticle.PORTION_ENERGY_KJ_KEY 				: "FLOAT",\
				DetailedArticle.PORTION_ENERGY_KCAL_KEY 			: "FLOAT",\
				DetailedArticle.PORTION_TOTAL_FAT_KEY 				: "FLOAT",\
				DetailedArticle.PORTION_SATURATED_FAT_KEY 			: "FLOAT",\
				DetailedArticle.PORTION_TOTAL_CARBOHYDRATES_KEY 	: "FLOAT",\
				DetailedArticle.PORTION_SUGARS_KEY 					: "FLOAT",\
				DetailedArticle.PORTION_TOTAL_PROTEINS_KEY 			: "FLOAT",\
				DetailedArticle.PORTION_FIBERS_KEY 					: "FLOAT",\
				DetailedArticle.PORTION_SALT_KEY 					: "FLOAT",\
				DetailedArticle.PORTION_INFO_KEY 					: "VARCHAR(255)",\
				DetailedArticle.PER_100G_ENERGY_KJ_KEY 				: "FLOAT",\
				DetailedArticle.PER_100G_ENERGY_KCAL_KEY 			: "FLOAT",\
				DetailedArticle.PER_100G_TOTAL_FAT_KEY 				: "FLOAT",\
				DetailedArticle.PER_100G_SATURATED_FAT_KEY 			: "FLOAT",\
				DetailedArticle.PER_100G_TOTAL_CARBOHYDRATES_KEY 	: "FLOAT",\
				DetailedArticle.PER_100G_SUGARS_KEY 				: "FLOAT",\
				DetailedArticle.PER_100G_TOTAL_PROTEINS_KEY 		: "FLOAT",\
				DetailedArticle.PER_100G_FIBERS_KEY 				: "FLOAT",\
				DetailedArticle.PER_100G_SALT_KEY					: "FLOAT",\
				DetailedArticle.TOTAL_QUANTITY_KEY 					: "VARCHAR(255)",\
				DetailedArticle.INGREDIENTS_TEXT_KEY 				: "TEXT",\
				DetailedArticle.BAR_CODE_KEY 						: "VARCHAR(255)",\
				DetailedArticle.ALLERGENS_CONTAINS_KEY 				: "TEXT",\
				DetailedArticle.ALLERGENS_TRACE_OF_KEY 				: "TEXT",\
			}

def db_params():
	username = None
	password = None
	f = open(db_properties_filename,"r")
	for line in f:
		line = line.strip().split(" ")
		if line[0] == "username":
			username = line[-1]
		elif line[0] == "password":
			password = line[-1]
	f.close()
	return (username, password)

def drop_db():
	(username, password) = db_params()
	drop_command = "DROP DATABASE " + db_name + ";"
	cnx = mysql.connector.connect(user=username, password=password)
	cursor = cnx.cursor()
	cursor.execute(drop_command)
	cnx.commit()
	cursor.close()
	cnx.close()

def create_db():
	(username, password) = db_params()
	create_command = "CREATE DATABASE " + db_name + " CHARACTER SET " + db_encoding + ";"
	cnx = mysql.connector.connect(user=username, password=password)
	cursor = cnx.cursor()
	cursor.execute(create_command)
	cnx.commit()
	cursor.close()
	cnx.close()

def create_food_table():
	food_table_command = "CREATE TABLE `Food` ("
	food_table_command += "`id_food` INT UNSIGNED NOT NULL AUTO_INCREMENT, "
	for db_entry in articles_details_sql_types : 
		food_table_command +=  "`" + articles_names_correction[db_entry] + "`" + " " + articles_details_sql_types[db_entry] + ", " 
	food_table_command += "PRIMARY KEY (`id_food`)) ENGINE=InnoDB"

	index_commands = \
		[
			"ALTER TABLE Food "
			"ADD INDEX ind_code(code);",\
			"ALTER TABLE Food "
			"ADD INDEX ind_url(url);",\
			"ALTER TABLE Food "
			"ADD INDEX ind_product_name(product_name);"\
		]

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, password=password, database=db_name)
	cursor = cnx.cursor()
	cursor.execute(food_table_command)
	for index_command in index_commands:
		cursor.execute(index_command)
	cnx.commit()
	cursor.close()
	cnx.close()

def insert_items_in_food():
	article_insert_command = "INSERT INTO Food "
	db_names = "("
	values = "VALUES ("
	for key in articles_names_correction :
		db_names += articles_names_correction[key] + ", "
		values += "%(" + key + ")s, "
	db_names = db_names[:-2] + ") "
	values = values[:-2] +")"
	article_insert_command += db_names + values

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, password=password,database=db_name)
	cursor = cnx.cursor()
	with open(details_results_filename, "r") as f:
		for line in f:
			article = ast.literal_eval(line.rstrip("\n"))
			for key in article:
				if isinstance(article[key], list):
					if len(article[key]) == 0:
						article[key] = None
					else:
						listString = ""
						for item in article[key]:
							listString += item + ", "
						listString = listString[:-2]
						article[key] = listString
				elif article[key] != None and (key == BaseArticle.PRICE_KG_KEY or key == BaseArticle.PRICE_UNIT_KEY) :
					article[key] = float(article[key].replace(',','.'))
				elif article[key] != None and key in RDAs:
					unit = article[key].split(" ")[-1]
					maybe_float = article[key].split(unit)[0].rstrip(' ')
					try:
						article[key] = float(maybe_float.replace(',','.'))
					except ValueError:
						article[key] = None
			cursor.execute(article_insert_command, article)
	cnx.commit()
	cursor.close()
	cnx.close()

	add_image_path_column_to_food_table()
	insert_images_path_into_food()

def create_user_table():
	user_table_command = \
	"CREATE TABLE User\
	(id_user INT UNSIGNED NOT NULL AUTO_INCREMENT,\
	username VARCHAR(60) NOT NULL,\
	gender VARCHAR(1) NOT NULL,\
	weight FLOAT,\
	password VARCHAR(255) NOT NULL,\
	UNIQUE(username),\
	PRIMARY KEY(id_user)\
	) ENGINE=INNODB;"

	add_index_command = \
	"ALTER TABLE User\
	ADD INDEX ind_username (username);"

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, password=password,database=db_name)
	cursor = cnx.cursor()
	cursor.execute(user_table_command)
	cursor.execute(add_index_command)
	cnx.commit()
	cursor.close()
	cnx.close()


def create_user_preferences_table():
	user_pref_table_command = (
	"CREATE TABLE User_preferences("
	"id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
	"numUser INT UNSIGNED NOT NULL,"
	"numFood INT UNSIGNED NOT NULL,"
	"rank DECIMAL(2,1) NOT NULL,"
	"PRIMARY KEY (id)"
	")ENGINE=INNODB;")
	
	user_id_foreign_key_command = (
	"ALTER TABLE User_preferences "
	"ADD CONSTRAINT fk_numUser_idUser FOREIGN KEY (numUser) REFERENCES User(id_user) ON DELETE CASCADE ON UPDATE CASCADE;")

	food_id_foreign_key_command = (
	"ALTER TABLE User_preferences "
	"ADD CONSTRAINT fk_numFood_idFood FOREIGN KEY (numFood) REFERENCES Food(id_food) ON DELETE CASCADE ON UPDATE CASCADE;")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, password=password,database=db_name)
	cursor = cnx.cursor()
	cursor.execute(user_pref_table_command)
	cursor.execute(user_id_foreign_key_command)
	cursor.execute(food_id_foreign_key_command)
	cnx.commit()
	cursor.close()
	cnx.close()

def create_categories_ratings_table():
	categories_ratings_tabe_command = (
    	"CREATE TABLE `CategoriesRatings` ("
    	"  `id` int(11) NOT NULL AUTO_INCREMENT,"
    	"  `category_name` varchar(100) NOT NULL,"
    	"  `rating` DECIMAL(2,1) NOT NULL,"
    	"  `n_ratings` int(11) NULL DEFAULT NULL,"
    	"  `user_id` INT UNSIGNED NOT NULL,"
    	"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	user_id_foreign_key_command = (
		"ALTER TABLE `CategoriesRatings` ADD FOREIGN KEY (user_id)\
		REFERENCES `User` (`id_user`)")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(categories_ratings_tabe_command)
	cursor.execute(user_id_foreign_key_command)
	cnx.commit()
	cursor.close()
	cnx.close()


def read_sports_data_from_file():
	sports_data = []
	with open(sports_filename, 'r') as f:
		for i in f:
			line = i.strip()
			if (line[0] == data_char):
				line = line[data_start:]
				sports_data.append(line.split(delimiter))
	return sports_data
	
def create_sport_table():
	sports_table_command = (
		"CREATE TABLE `Sports` ("
    	"  `name` varchar(100) NOT NULL,"
    	"  `joule_60kg` varchar(25) NOT NULL,"
    	"  `joule_70kg` varchar(25) NOT NULL,"
    	"  `joule_85kg` varchar(25) NOT NULL,"
    	"  PRIMARY KEY (`name`)"
    	") ENGINE=InnoDB")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(sports_table_command)
	cnx.commit()
	cursor.close()
	cnx.close() 

def insert_items_in_sport():
	sport_insert_command = (
		"INSERT INTO Sports "
		"(name, joule_60kg, joule_70kg, joule_85kg) "
		"VALUES (%(name)s, %(joule_60kg)s, %(joule_70kg)s, %(joule_85kg)s)")
	
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	# Insert sports from file
	sports_data = read_sports_data_from_file()
	i = 1
	for data_list in sports_data:
		#index_format = "%" + str(len(str(len(sports_data)))) + "d"
		#indicator = "["+index_format+"/"+index_format+"]"
		#sys.stdout.write(indicator%(i, len(sports_data)) + " Inserting " + YELLOW + str(data_list) + RESET + " into " + MAGENTA + "Sports" + RESET + "... ")
		sport_data = {'name': data_list[0], 'joule_60kg': data_list[1], 'joule_70kg': data_list[2],'joule_85kg': data_list[3],}
		cursor.execute(sport_insert_command, sport_data)
		i += 1
	cnx.commit()
	cursor.close()
	cnx.close() 


def create_history_table():
	history_table_command = (
		"CREATE TABLE `Users_history` ("
		"id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		"idUser INT UNSIGNED NOT NULL,"
		"idFood INT UNSIGNED,"
		"date TEXT NOT NULL,"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	history_fk_user_command = (
		"ALTER TABLE `Users_history` ADD CONSTRAINT fk_idUser_id_user FOREIGN KEY (idUser) REFERENCES User(id_user) ON DELETE CASCADE ON UPDATE CASCADE"
		)

	commands = [history_table_command,history_fk_user_command]
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	for command in commands:
			cursor.execute(command)
	cnx.commit()
	cursor.close()
	cnx.close()

def addColumnsToUsers_history():
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	command = "ALTER TABLE `Users_history` ADD (checked INT(1) NOT NULL, is_food_or_sport VARCHAR(5), sport_name VARCHAR(100), duration INT(10), energy_consumed FLOAT(10))"
	
	try:
		cursor.execute(command)

	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	cnx.commit()
	cursor.close()
	cnx.close()

def create_colruyt_categories_table():
	command = (
		"CREATE TABLE `All_categories` ("
		"id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		"category_name VARCHAR(255) NOT NULL,"
		"table_name VARCHAR(16) NOT NULL,"
		"UNIQUE(category_name),"
		"PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")
	(username, password)=db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(command)
	cnx.commit()
	cursor.close()
	cnx.close()

def add_all_colruyt_categories():
	firstCategories = []
	select_command = " SELECT `categories` FROM `Food`;"
	add_category_command = (
		"INSERT INTO All_categories"
		"(category_name, table_name)"
		"VALUES (%s , %s)"
		)
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password,charset="utf8",use_unicode=True)
	cursor = cnx.cursor()

	cursor.execute(select_command)
	for categoriesInfo in cursor:
		categoriesInfo=categoriesInfo[0].split(",")
		primaryCat=categoriesInfo[0]
		secondaryCat=categoriesInfo[1]
		if primaryCat not in firstCategories:
			firstCategories.append(primaryCat)
	for category in firstCategories :	
		cursor.execute(add_category_command, (category, "Food"))

	cnx.commit()
	cursor.close()
	cnx.close()

def add_image_path_column_to_food_table():
	addColumnToTableCommand = (
		"ALTER TABLE `Food`"
		"ADD ("
		"`image_pic` VARCHAR(255));"
		)
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	cursor.execute(addColumnToTableCommand)
	cnx.commit()
	cursor.close()
	cnx.close()

def get_images_path():
	images_path = []
	for i in range(nb_images):
		images_path.append(images_directory+"/"+image_png_filename.format(i+1))
	return images_path

def insert_images_path_into_food():
	images_path = get_images_path()
	updateImageColumnCommand = (
		"UPDATE `Food`"
		"SET `image_pic` = %s"
		"WHERE `id_food` = %s")
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	for i in range(len(images_path)):
		args = (images_path[i], i+1)
		cursor.execute(updateImageColumnCommand, args) 
	cnx.commit()
	cursor.close()
	cnx.close()

def create_recipe_table():
	recipe_table_command = (
		"CREATE TABLE `Recipe` ("
		+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		+ NAME_KEY + " VARCHAR(255),"
		+ IMAGE_URL_KEY + " VARCHAR(255),"
		+ URL_KEY + " VARCHAR(255),"
		+ RATING_KEY + " FLOAT,"
		+ NBR_RATINGS_KEY + " INT UNSIGNED,"
		+ INGREDIENTS_LIST_KEY + " TEXT,"
		+ PORTIONS_KEY + " INT UNSIGNED,"
		+ DIFFICULTY_KEY + " VARCHAR(20),"
		+ PREPARATION_TIME_KEY + " VARCHAR(10),"
		+ PREPARATION_STEPS_KEY + " TEXT,"
		+ CALORIE_PER_PORTION_KEY + " FLOAT,"
		+ FAT_PER_PORTION_KEY + " FLOAT,"
		+ CARBO_PER_PORTION_KEY + " FLOAT,"
		+ PROTEIN_PER_PORTION_KEY + " FLOAT,"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(recipe_table_command)
	cnx.commit()
	cursor.close()
	cnx.close()

recipe_table_keys = \
		[
			NAME_KEY,\
			IMAGE_URL_KEY,\
			URL_KEY,\
			RATING_KEY,\
			NBR_RATINGS_KEY,\
			INGREDIENTS_LIST_KEY,\
			PORTIONS_KEY,\
			DIFFICULTY_KEY,\
			PREPARATION_TIME_KEY,\
			PREPARATION_STEPS_KEY,\
			CALORIE_PER_PORTION_KEY,\
			FAT_PER_PORTION_KEY,\
			CARBO_PER_PORTION_KEY,\
			PROTEIN_PER_PORTION_KEY,\
		]

def insert_recipes_in_table():
	insert_recipe_command = "INSERT INTO Recipe ("
	for key in recipe_table_keys : 
		insert_recipe_command += key + ", "
	insert_recipe_command = insert_recipe_command[:-2] + ") VALUES "
	insert_values_commmand = "("
	for key in recipe_table_keys :
		insert_values_commmand += "%(" + key + ")s" + ", "
	insert_values_commmand = insert_values_commmand[:-2] + ") "

	recipes = []
	with open(results_recipes_filename, "r") as f:
		for line in f:
			recipes.append(ast.literal_eval(line.rstrip("\n")))

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	
	i = 0
	insert = False
	to_insert_recipes = []
	for recipe in recipes :
		if i % max_sql_single_insert == 0 or i == len(recipes) - 1:
			insert = True
		i += 1
		try:
			found_keys = []
			for key in recipe : 
				found_keys.append(key)
				if isinstance(recipe[key], list):
					if len(recipe[key]) == 0:
						recipe[key] = None
					else:
						listString = ""
						for item in recipe[key]:
							listString += item + ", "
						listString = listString[:-2]
						recipe[key] = listString
				elif key == RATING_KEY :
					recipe[key] = float(recipe[key])
				elif key == NBR_RATINGS_KEY : 
					pos = recipe[key].find(" ") 
					if pos != -1:
						recipe[key] = recipe[key][:pos] + recipe[key][pos+1:]
					recipe[key] = int(recipe[key])
				elif key == PORTIONS_KEY :
					recipe[key] = int(recipe[key])
				elif key in [CALORIE_PER_PORTION_KEY, PROTEIN_PER_PORTION_KEY, FAT_PER_PORTION_KEY, CARBO_PER_PORTION_KEY] :
					try:
						pos = recipe[key].find(",")
						if pos != -1:
							recipe[key] = recipe[key][:pos] + recipe[key][pos+1:]
						recipe[key] = float(recipe[key].split(" ")[0])
					except UnicodeEncodeError as e:
						recipe[key] = None
					except ValueError as e:
						recipe[key] = None
				if isinstance(recipe[key],str) or isinstance(recipe[key],unicode):
					recipe[key] = recipe[key].replace("\"","'")
			for key in recipe_table_keys:
				if key not in found_keys:
					recipe[key] = None
			to_insert_recipes.append(recipe)
			if insert :
				insert_command = insert_recipe_command + insert_values_commmand 
				cursor.executemany(insert_command,to_insert_recipes)
				insert = False
				to_insert_recipes = []

		except Exception as e: 
			#print recipe
			print insert_command
			raise e
	cnx.commit()
	cursor.close()
	cnx.close()

def create_ingredients_table():
	ingredients_table_command = (
		"CREATE TABLE `Ingredient` ("
		+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		+ "ingredient_name VARCHAR(255),"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	recipesingredients_table_command = (
		"CREATE TABLE `RecipeIngredients` ("
		+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		+ "recipe_id INT UNSIGNED,"
		+ "ingredient_id INT UNSIGNED,"
		+ "FOREIGN KEY (recipe_id) REFERENCES Recipe(id),"
		+ "FOREIGN KEY (ingredient_id) REFERENCES Ingredient(id),"
		"  PRIMARY KEY (`id`)"
		" "
    	") ENGINE=InnoDB")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(ingredients_table_command)
	cnx.commit()
	cursor.execute(recipesingredients_table_command)
	cnx.commit()
	cursor.close()
	cnx.close()

def insert_ingredients_in_table():
	global recipes
	recipes = []
	with open(results_recipes_filename, "r") as f:
		for line in f:
			recipes.append(ast.literal_eval(line.rstrip("\n")))
	insert_ingredient_command = (
		"INSERT INTO Ingredient "
		"(ingredient_name) VALUES ")
	ingredient_value = "(\"%s\")"
		

	recipes_ingredients_binding_command = (
		"INSERT INTO RecipeIngredients"
		" (recipe_id, ingredient_id) VALUES ")
	recipe_ingredient_value = "((SELECT id from Recipe WHERE recipe_url=%(recipe_url)s), (SELECT id from Ingredient WHERE ingredient_name=%(ingredient_name)s))"

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	global ingredients

	ingredients = []
	for recipe in recipes :
		recipe_ingredient_insert_values = "" 
		recipe_ingredient_binding_values = ""
		for ingredient in recipe[INGREDIENTS_NAMES_KEY]:
			ingredient = ingredient.lower()
			if ingredient not in ingredients :
				ingredients.append(ingredient)
				recipe_ingredient_insert_values += ingredient_value%ingredient + ", "
			recipe_ingredient_binding_values += recipe_ingredient_value %{"recipe_url":recipe[URL_KEY], "ingredient_name":ingredient} +" ,"
		if len(recipe_ingredient_insert_values) != 0 :
			command = insert_ingredient_command + recipe_ingredient_insert_values[:-2]
			cursor.execute(command)
		if len(recipe_ingredient_binding_values) != 0 :
			command = recipes_ingredients_binding_command + recipe_ingredient_binding_values[:-2]
			cursor.execute(command ,multi=True)

	cnx.commit()
	cursor.close()
	cnx.close()

def create_categories_table():
	categories_table_command = (
	"CREATE TABLE `JDFCategory` ("
		+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		+ "category_name VARCHAR(255),"
		+ "is_main INT(1),"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")
	recipescategories_table_command = (
	"CREATE TABLE `RecipeCategories` ("
		+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		+ "recipe_id INT UNSIGNED,"
		+ "category_id INT UNSIGNED,"
		+ "FOREIGN KEY (recipe_id) REFERENCES Recipe(id),"
		+ "FOREIGN KEY (category_id) REFERENCES JDFCategory(id),"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(categories_table_command)
	cnx.commit()
	cursor.execute(recipescategories_table_command)
	cnx.commit()
	cursor.close()
	cnx.close()

def insert_categories_in_table():
	insert_category_command = (
		"INSERT INTO JDFCategory "
		"(category_name, is_main) VALUES ")
	category_value = "(\"%s\", \"%s\")"
		
	recipes_categories_binding_command = (
		"INSERT INTO RecipeCategories"
		" (recipe_id, ingredient_id) VALUES ")
	recipe_category_value = "((SELECT id from Recipe WHERE recipe_url=\"%(recipe_url)s\"), (SELECT id from JDFCategory WHERE category_name=\"%(category_name)s\"))"
	
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	global recipes

	main_categories = []
	sub_categories = []
	for recipe in recipes :
		recipe_category_insert_values = ""
		recipe_category_binding_vales = ""
		for key in recipe :
			if key == PRIMARY_CATEGORY_KEY:
				if recipe[key] not in main_categories:
					main_categories.append(recipe[key])
					recipe_category_insert_values += category_value%(recipe[key], 1) + ", "
					recipe_category_binding_vales += recipe_category_value %{"recipe_url":recipe[URL_KEY], "category_name":recipe[key]} +" ,"

			elif key == SECONDARY_CATEGORY_KEY:
				if recipe[key] not in sub_categories:
					sub_categories.append(recipe[key])
					recipe_category_insert_values += category_value%(recipe[key], 0) + ", "
					recipe_category_binding_vales += recipe_category_value %{"recipe_url":recipe[URL_KEY], "category_name":recipe[key]} +" ,"

		if len(recipe_category_insert_values) != 0 :
			command = insert_category_command + recipe_category_insert_values[:-2]
			cursor.execute(command)
		if len(recipe_category_binding_vales) != 0 :
			command = recipes_categories_binding_command + recipe_category_binding_vales[:-2]
			cursor.execute(command ,multi=True)

	cnx.commit()
	cursor.close()
	cnx.close()

	global categories
	categories = main_categories + sub_categories

def create_origin_table():
	origin_table_command = (	
	"CREATE TABLE `Origin` ("
		+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		+ "origin_name VARCHAR(255),"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	recipesorigins_table_command = (
	"CREATE TABLE `RecipeOrigins` ("
		+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		+ "recipe_id INT UNSIGNED,"
		+ "origin_id INT UNSIGNED,"
		+ "FOREIGN KEY (recipe_id) REFERENCES Recipe(id),"
		+ "FOREIGN KEY (origin_id) REFERENCES Origin(id),"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(origin_table_command)
	cnx.commit()
	cursor.execute(recipesorigins_table_command)
	cnx.commit()
	cursor.close()
	cnx.close()


def insert_origins_in_table():
	global origins
	origins = ["Cuisine italienne","Cuisine américaine","Cuisine marocaine",\
	"Cuisine belge","Cuisine chinoise","Cuisine espagnole","Cuisine indienne",\
	"Cuisine anglaise","Cuisine japonaise","Cuisine algérienne","Portugal","Canada",\
	"Cuisine thailandaise","Cuisine mexicaine"]

	for i in range(len(origins)):
		origins[i] = origins[i].decode("utf-8")
	
	insert_origin_command = (
		"INSERT INTO Origin "
		"(origin_name)"
		"VALUES (\"%s\")"
		)

	for i in range(len(origins)):
		origins[i] = origins[i].lower()

	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	for origin in origins :
		cursor.execute(insert_origin_command%origin)
	cnx.commit()
	cursor.close()
	cnx.close()


def create_tags_table():
	tags_table_command = (
		"CREATE TABLE `Tag` ("
		+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		+ "tag_name VARCHAR(255),"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()
	cursor.execute(tags_table_command)
	cnx.commit()
	cursor.close()
	cnx.close()

def insert_tags_in_table():
	global recipes
	global origins
	global ingredients
	global categories
	tags_not_in_table = []
	tags_in_table = ingredients + categories + origins
	for recipe in recipes :
		for tag in recipe[TAGS_KEY]:
			tag = tag.lower()
			if tag not in tags_not_in_table:
				if tag not in tags_in_table:
					tags_not_in_table.append(tag)
	
	insert_tag_command = (
		"INSERT INTO Tag "
		"(tag_name)"
		"VALUES (\"%s\")"
		)
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	for tag in tags_not_in_table :
		cursor.execute(insert_tag_command%tag)
	cnx.commit()
	cursor.close()
	cnx.close()

def create_recipetags_table():
	recipesingredients_table_command = (
		"CREATE TABLE `RecipeTags` ("
		+ "id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
		+ "FOREIGN KEY (recipe_id) REFERENCES Recipe(id),"
		+ "FOREIGN KEY (tag_id) REFERENCES Tag(id),"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

def create_recipeorigins_table():
	pass
	




def log_create_table(table, create_table_func):
	sys.stdout.write("Creating " + MAGENTA + table + RESET + " table... ")
	sys.stdout.flush()
	create_table_func()
	sys.stdout.write(GREEN + "OK" + RESET + "\n")
	sys.stdout.flush()

def log_insert_items(table, add_items_func):
	sys.stdout.write("Inserting items in " + MAGENTA + table + RESET + " table... ")
	sys.stdout.flush()
	add_items_func()
	sys.stdout.write(GREEN + "OK" + RESET + "\n")
	sys.stdout.flush()

def log_drop_db(db_name, drop_func):
	sys.stdout.write("Dropping database " + MAGENTA + db_name + RESET + "... ")
	sys.stdout.flush()
	drop_func()
	sys.stdout.write(GREEN + "OK " + RESET + "\n")
	sys.stdout.flush()

def log_create_db(db_name, create_func):
	sys.stdout.write("Creating database " + MAGENTA + db_name + RESET + "... ")
	sys.stdout.flush()
	create_func()
	sys.stdout.write(GREEN + "OK " + RESET + "\n")
	sys.stdout.flush()



if __name__ == "__main__" :
	try:
		try:
			log_drop_db(db_name, drop_db)
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
			sys.stdout.flush()
		finally:
			log_create_db(db_name, create_db)

			log_create_table("Food", create_food_table)
			#log_insert_items("Food", insert_items_in_food)

			log_create_table("User", create_user_table)

			log_create_table("UserPreferences", create_user_preferences_table)

			log_create_table("CategoriesRatings", create_categories_ratings_table)

			log_create_table("Sport", create_sport_table)
			log_insert_items("Sport", insert_items_in_sport)

			log_create_table("History", create_history_table)
			log_insert_items("History", addColumnsToUsers_history)

			log_create_table("All_categories", create_colruyt_categories_table)
			log_insert_items("All_categories", add_all_colruyt_categories)

			log_create_table("Recipe", create_recipe_table)
			log_insert_items("Recipe", insert_recipes_in_table)

			log_create_table("Ingredient", create_ingredients_table)
			log_insert_items("Ingredient", insert_ingredients_in_table)

			log_create_table("JDFCategory", create_categories_table)
			log_insert_items("JDFCategory", insert_categories_in_table)

			log_create_table("Origin", create_origin_table)
			log_insert_items("Origin", insert_origins_in_table)

			log_create_table("Tag", create_tags_table)
			log_insert_items("Tag", insert_tags_in_table)


	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
		sys.stdout.flush()







