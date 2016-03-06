#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast
import mysql.connector
from mysql.connector import errorcode
from colruyt_models import *


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
		"idFood INT UNSIGNED NOT NULL,"
		"date TEXT NOT NULL,"
		"  PRIMARY KEY (`id`)"
    	") ENGINE=InnoDB")

	history_fk_food_command = (
		"ALTER TABLE `Users_history` ADD CONSTRAINT fk_idFood_id_food FOREIGN KEY (idFood) REFERENCES Food(id_food) ON DELETE CASCADE ON UPDATE CASCADE"
		)

	history_fk_user_command = (
		"ALTER TABLE `Users_history` ADD CONSTRAINT fk_idUser_id_user FOREIGN KEY (idUser) REFERENCES User(id_user) ON DELETE CASCADE ON UPDATE CASCADE"
		)

	commands = [history_table_command,history_fk_user_command,history_fk_food_command]
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	for command in commands:
			cursor.execute(command)
	cnx.commit()
	cursor.close()
	cnx.close()

if __name__ == "__main__" :
	try:
		try:
			sys.stdout.write("Dropping database " + MAGENTA + db_name + RESET + "... ")
			sys.stdout.flush()
			drop_db()
			sys.stdout.write(GREEN + "OK " + RESET + "\n")
			sys.stdout.flush()
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
			sys.stdout.flush()
		finally:
			sys.stdout.write("Creating database " + MAGENTA + db_name + RESET + "... ")
			sys.stdout.flush()
			create_db()
			sys.stdout.write(GREEN + "OK " + RESET + "\n")
			sys.stdout.flush()

			sys.stdout.write("Creating " + MAGENTA + "Food" + RESET + " table... ")
			sys.stdout.flush()
			create_food_table()
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
			sys.stdout.flush()

			sys.stdout.write("Inserting items in " + MAGENTA + "Food" + RESET + "... ")
			sys.stdout.flush()
			insert_items_in_food()
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
			sys.stdout.flush()

			sys.stdout.write("Creating " + MAGENTA + "User" + RESET + " table... ")
			sys.stdout.flush()
			create_user_table()
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
			sys.stdout.flush()

			sys.stdout.write("Creating " + MAGENTA + "UserPreferences" + RESET + " table... ")
			sys.stdout.flush()
			create_user_preferences_table()
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
			sys.stdout.flush()

			sys.stdout.write("Creating " + MAGENTA + "CategoriesRatings" + RESET + " table... ")
			sys.stdout.flush()
			create_categories_ratings_table()
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
			sys.stdout.flush()

			sys.stdout.write("Creating " + MAGENTA + "Sports" + RESET + " table... ")
			sys.stdout.flush()
			create_sport_table()
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
			sys.stdout.flush()

			sys.stdout.write("Inserting items in " + MAGENTA + "Sports" + RESET + "... ")
			sys.stdout.flush()
			insert_items_in_sport()
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
			sys.stdout.flush()

			sys.stdout.write("Creating " + MAGENTA + "History" + RESET + " table... ")
			sys.stdout.flush()
			create_history_table()
			sys.stdout.write(GREEN + "OK" + RESET + "\n")
			sys.stdout.flush()

	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
		sys.stdout.flush()







