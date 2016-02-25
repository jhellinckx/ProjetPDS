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
db_properties_filename = "../dao/dao.properties"
ingredients_filename = "raw/db_ingredients.txt"
recipes_filename = "raw/db_recipes.txt"
def executePythonScripts(recipies,categories_ratings,sports,add_columns_for_quantity,first_time,addWeight):
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


def getWhichScriptsNeedToBeExecuted():
	recipies = (raw_input("Add the recipies talbe (Y/N): ").upper() == "Y")
	categories_ratings = (raw_input("Add the categories_ratings talbe (Y/N): ").upper() == "Y")
	sports = (raw_input("Add the sports talbe (Y/N)?: ").upper() == "Y")
	add_columns_for_quantity = (raw_input("Update the Food talbe for quantities? (takes over 35 min) (Y/N): ").upper() == "Y")
	first_time = False
	if add_columns_for_quantity:
		first_time = (raw_input("Is it the first time that you do this update? (Y/N): ").upper() == "Y")
	addWeight = (raw_input("Add weight column for User ? (Y/N): ").upper() == "Y")

	executePythonScripts(recipies, categories_ratings, sports, add_columns_for_quantity, first_time,addWeight)


if __name__ == "__main__":
	getWhichScriptsNeedToBeExecuted()

