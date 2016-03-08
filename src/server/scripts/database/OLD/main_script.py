#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import db_recipes
import db_categories_ratings
import db_sports
import add_columns_to_food_table_for_quantity

import mysql.connector

YELLOW = "\033[33m"
MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_appli"
db_properties_filename = "../../src/main/resources/dao.properties"
ingredients_filename = "raw/db_ingredients.txt"
recipes_filename = "raw/db_recipes.txt"
def executePythonScripts(recipies,categories_ratings,sports,add_columns_for_quantity,first_time,addWeight,addHistory, addPassword):
	if recipies:
		#sys.stdout.write("Taking care of the " + MAGENTA + "recipies" + RESET + " table \n")
		db_recipes.execute()
		#sys.stdout.write("RECIPIES" + GREEN + "-> DONE!")
	if categories_ratings:
		#sys.stdout.write("Taking care of the " + MAGENTA + "categories ratings" + RESET + " table \n")
		db_categories_ratings.execute()
		#sys.stdout.write("CATEGORIES RATINGS " + GREEN + "-> DONE!")
	if sports:
		#sys.stdout.write("Taking care of the " + MAGENTA + "sports" + RESET + " table \n")
		db_sports.execute()
		#sys.stdout.write("SPORTS" + GREEN + "-> DONE!")
	if add_columns_for_quantity:
		#sys.stdout.write("Taking care of the Food table update for" + MAGENTA + "quantity" + RESET + " (takes overs 35 mins)\n")
		add_columns_to_food_table_for_quantity.execute(first_time)
		#sys.stdout.write("FOOD UPDATE FOR QUANTITY" + GREEN + "-> DONE!")
	if addWeight:
		addWeightColumn()

	if addHistory:
		addHistoryTable()
	if addPassword:
		addPasswordColumn()

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

def addWeightColumn():
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	command = "ALTER TABLE `User` ADD weight FLOAT"
	
	sys.stdout.write("Creating " + MAGENTA + "weight column" + RESET + " for User... ")
	try:
		cursor.execute(command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")

	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	cnx.commit()
	cursor.close()
	cnx.close()

def addPasswordColumn():
	(username, password) = db_params()
	cnx = mysql.connector.connect(user=username, database=db_name, password=password)
	cursor = cnx.cursor()

	command = "ALTER TABLE `User` ADD password VARCHAR(255) NOT NULL"
	
	sys.stdout.write("Creating " + MAGENTA + "password column" + RESET + " for User... ")
	try:
		cursor.execute(command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")

	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	cnx.commit()
	cursor.close()
	cnx.close()

def addHistoryTable():
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

	sys.stdout.write("Creating " + MAGENTA + "Users_history" + RESET + " table + setting foreing keys (needs 3 ok)... ")
	for command in commands:
		try:
			cursor.execute(command)
			sys.stdout.write(GREEN + "OK! " + RESET)

		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET)
	sys.stdout.write("\n")
	cnx.commit()
	cursor.close()
	cnx.close()

def getWhichScriptsNeedToBeExecuted():
	recipies = (raw_input("Add the recipies talbe (Y/N): ").upper() == "Y")
	categories_ratings = (raw_input("Add the categories_ratings talbe (Y/N): ").upper() == "Y")
	sports = (raw_input("Add the sports talbe (Y/N)?: ").upper() == "Y")
	add_columns_for_quantity = (raw_input("Update the Food talbe for quantities? (takes over 35 min) (Y/N): ").upper() == "Y")
	first_time = False
	if add_columns_for_quantity:
		first_time = (raw_input("Is it the first time that you do this update? (Y/N): ").upper() == "Y")
	addWeight = (raw_input("Add weight column for User ? (Y/N): ").upper() == "Y")
	addPassword = (raw_input("Add password column for User ? (Y/N): ").upper() == "Y")
	addHistory = (raw_input("Add history table to db? (Y/N): ").upper() == "Y")

	executePythonScripts(recipies, categories_ratings, sports, add_columns_for_quantity, first_time,addWeight, addHistory, addPassword)


if __name__ == "__main__":
	getWhichScriptsNeedToBeExecuted()

