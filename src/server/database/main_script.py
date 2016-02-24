#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import db_recipes
import db_categories_ratings
import db_sports
import add_columns_to_food_table_for_quantity

MAGENTA = "\033[35m"
GREEN = "\033[32m"
RESET = "\033[0m"

def executePythonScripts(recipies,categories_ratings,sports,add_columns_for_quantity,first_time):
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
		add_columns_to_food_for_quantity.execute(first_time)
		#sys.stdout.write("FOOD UPDATE FOR QUANTITY" + GREEN + "-> DONE!")

def getWhichScriptsNeedToBeExecuted():
	recipies = (raw_input("Add the recipies talbe (Y/N): ").upper() == "Y")
	categories_ratings = (raw_input("Add the categories_ratings talbe (Y/N): ").upper() == "Y")
	sports = (raw_input("Add the sports talbe (Y/N)?: ").upper() == "Y")
	add_columns_for_quantity = (raw_input("Update the Food talbe for quantities? (takes over 35 min) (Y/N): ").upper() == "Y")
	first_time = False
	if add_columns_for_quantity:
		first_time = (raw_input("Is it the first time that you do this update? (Y/N): ").upper() == "Y")

	executePythonScripts(recipies, categories_ratings, sports, add_columns_for_quantity, first_time)


if __name__ == "__main__":
	getWhichScriptsNeedToBeExecuted()

