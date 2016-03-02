#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast
import mysql.connector
from mysql.connector import errorcode
from colruyt_parser import *


YELLOW = "\033[33m"
MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_colruyt"
db_encoding = "utf8"
db_properties_filename = "../../src/main/resources/dao.properties"

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

def create_food_table():
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, password=password, database=db_name)
	cursor = cnx.cursor()

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
				BaseArticle.PRICE_UNIT_KEY 							: "FLOAT",\
				BaseArticle.PRICE_KG_KEY 							: "FLOAT",\
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
				DetailedArticle.PORTION_INFO_KEY 					: "VARCHAR(16)",\
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

	food_table_command = "CREATE TABLE `Food` ("
	food_table_command += "`id_food` INT UNSIGNED NOT NULL AUTO_INCREMENT, "
	for db_entry in articles_details_sql_types : 
		food_table_command +=  "`" + articles_names_correction[db_entry] + "`" + " " + articles_details_sql_types[db_entry] + ", " 
	food_table_command += "PRIMARY KEY (`id_food`)) ENGINE=InnoDB"
	sys.stdout.write("Creating " + MAGENTA + "Food" + RESET + " table... ")
	try:
		cursor.execute(food_table_command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	cnx.commit()
	cursor.close()
	cnx.close()

def add_items():
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, password=password)
	cursor = cnx.cursor()


	articles = []
	with open(details_results_filename, "r") as f:
		for line in details_results_filename:
			articles.append(ast.literal_eval(line.rstrip("\n")))
	for article in articles:
		pass

	cnx.commit()
	cursor.close()
	cnx.close()

def create_db():
	(username, password) = db_params()
	drop_command = "DROP DATABASE " + db_name + ";"
	create_command = "CREATE DATABASE " + db_name + " CHARACTER SET " + db_encoding + ";"
	cnx = mysql.connector.connect(user=username, password=password)
	cursor = cnx.cursor()
	try:
		sys.stdout.write("Dropping database " + MAGENTA + db_name + RESET + "... ")
		cursor.execute(drop_command)
		sys.stdout.write(GREEN + "OK " + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	finally:
		sys.stdout.write("Creating database " + MAGENTA + db_name + RESET + "... ")
		cursor.execute(create_command)
		sys.stdout.write(GREEN + "OK " + RESET + "\n")
	cnx.commit()
	cursor.close()
	cnx.close()

if __name__ == "__main__" :
	create_db()
	create_food_table()