#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast
import mysql.connector
from mysql.connector import errorcode
from colruyt_parser import BaseArticle,DetailedArticle,details_results_filename


YELLOW = "\033[33m"
MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_colruyt"
db_encoding = "utf8"
db_properties_filename = "../../src/main/resources/dao.properties"

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

articles_details_sql_types = \
			{
				BaseArticle.BRANCH_INDEX_KEY 						: "INT UNSIGNED",\
				BaseArticle.IMAGE_KEY 								: "VARCHAR(255)",\
				BaseArticle.PRICE_UNIT_KEY 							: "VARCHAR(10)",\
				BaseArticle.PRICE_KG_KEY 							: "VARCHAR(10)",\
				BaseArticle.DETAILS_URL_KEY 						: "VARCHAR(255)",\
				BaseArticle.CATEGORIES_KEY 							: "TEXT",\
				BaseArticle.WEIRD_NAME_KEY 							: "VARCHAR(255)",\
				BaseArticle.SHORT_DESCRIPTION_KEY					: "VARCHAR(255)",\
				BaseArticle.FULL_DESCRIPTION_KEY 					: "TEXT",\
				DetailedArticle.REAL_DETAILS_URL_KEY 				: "VARCHAR(255)",\
				DetailedArticle.PORTION_ENERGY_KJ_KEY 				: "VARCHAR(16)",\
				DetailedArticle.PORTION_ENERGY_KCAL_KEY 			: "VARCHAR(16)",\
				DetailedArticle.PORTION_TOTAL_FAT_KEY 				: "VARCHAR(16)",\
				DetailedArticle.PORTION_SATURATED_FAT_KEY 			: "VARCHAR(16)",\
				DetailedArticle.PORTION_TOTAL_CARBOHYDRATES_KEY 	: "VARCHAR(16)",\
				DetailedArticle.PORTION_SUGARS_KEY 					: "VARCHAR(16)",\
				DetailedArticle.PORTION_TOTAL_PROTEINS_KEY 			: "VARCHAR(16)",\
				DetailedArticle.PORTION_FIBERS_KEY 					: "VARCHAR(16)",\
				DetailedArticle.PORTION_SALT_KEY 					: "VARCHAR(16)",\
				DetailedArticle.PORTION_INFO_KEY 					: "VARCHAR(255)",\
				DetailedArticle.PER_100G_ENERGY_KJ_KEY 				: "VARCHAR(16)",\
				DetailedArticle.PER_100G_ENERGY_KCAL_KEY 			: "VARCHAR(16)",\
				DetailedArticle.PER_100G_TOTAL_FAT_KEY 				: "VARCHAR(16)",\
				DetailedArticle.PER_100G_SATURATED_FAT_KEY 			: "VARCHAR(16)",\
				DetailedArticle.PER_100G_TOTAL_CARBOHYDRATES_KEY 	: "VARCHAR(16)",\
				DetailedArticle.PER_100G_SUGARS_KEY 				: "VARCHAR(16)",\
				DetailedArticle.PER_100G_TOTAL_PROTEINS_KEY 		: "VARCHAR(16)",\
				DetailedArticle.PER_100G_FIBERS_KEY 				: "VARCHAR(16)",\
				DetailedArticle.PER_100G_SALT_KEY					: "VARCHAR(16)",\
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

def add_items_in_food():
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
					article[key] = str(article[key])[1:-1] # Get rid of [ and ] of list string
					if len(article[key]) == 0:
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
			add_items_in_food()
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
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
		sys.stdout.flush()







