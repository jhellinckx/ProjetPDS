#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import tokenize
import pint
from pint import UnitRegistry
import ast

try:
	import mysql.connector
	from mysql.connector import errorcode

except: # no mysql.connector
	print "Error : import impossible (MySQL python connector). Sur ubuntu, suivre \
	'Installing Connector/Python on Linux Using a Debian Package' sur \
	https://dev.mysql.com/doc/connector-python/en/connector-python-installation-binary.html"
	sys.exit()

MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_appli"
db_properties_filename = "../dao/dao.properties"

modified_foods_file = "tmp_db_modified_foods.txt"

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




def changeDimension(db_input):
	ureg = UnitRegistry()
	res = -1
	try:
		newQuantity = ureg(db_input).to("g")
		res = newQuantity.magnitude
	except pint.unit.DimensionalityError:
		try:
			newQuantity = ureg(db_input).to("ml")
			res = newQuantity.magnitude
		except pint.unit.DimensionalityError:
			pass
	except (pint.unit.UndefinedUnitError, AttributeError, TypeError, SyntaxError, tokenize.TokenError, pint.compat.tokenize.TokenError) as e:
		pass 
	return res


def checkIfHaveAllInfos(infoList):
	for info in infoList:
		try:
			float(info)
			res = True
		except ValueError:
			return False
	return res

	
def addOrDeleteColumnsToTable(delete): #if boolean delete is True, deletes otherwise add

	addColumnToTableCommand = (
		"ALTER TABLE `Food`"
		"ADD ("
		"`total_energy` FLOAT,"
		"`total_fat` FLOAT,"
		"`total_proteins` FLOAT,"
		"`total_saturated_fat` FLOAT,"
		"`total_carbohydrates` FLOAT,"
		"`total_sugars` FLOAT,"
		"`total_sodium` FLOAT);"
		)

	deleteColumnsFromTableCommand = (
		"ALTER TABLE `Food` "
		"DROP `total_energy`,"
		"DROP `total_fat`,"
		"DROP `total_proteins`,"
		"DROP `total_saturated_fat`,"
		"DROP `total_carbohydrates`,"
		"DROP `total_sugars`,"
		"DROP `total_sodium`;"
		)

	printName="Adding"
	command=addColumnToTableCommand

	if delete:
		printName="Deleting"
		command=deleteColumnsFromTableCommand

	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()

	# Add or delete columns to the Food table
	sys.stdout.write(printName + " columns to table Food.. ")
	try:
		cursor.execute(command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	cnx.commit()
	cursor.close()
	cnx.close()


def selectInfoFromDbAndAddNewInfos():

	selectInfoFromFoodCommand = (
		"SELECT `id_food`, `quantity`, `energy_100g`, `fat_100g`, `proteins_100g`,`saturated_fat_100g`, `carbohydrates_100g`, `sugars_100g`, `sodium_100g`"
		"FROM `Food`"
		"WHERE `energy_100g` > 0;"
		)

	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	sys.stdout.write("Trying to select info from DB ..")
	try:
		cursor.execute(selectInfoFromFoodCommand)
		sys.stdout.write(GREEN + " -> OK" + RESET + "\n")
		sys.stdout.write("Adding columns to Food table (takes overs 35 min).. ")
		for (id_food, quantity, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, sodium_100g) in cursor:
			correctQuantity = changeDimension(quantity)
			if correctQuantity != -1 and checkIfHaveAllInfos([energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, sodium_100g]): #only add info in columns if the quantity was correctly changed to g/ml
				correctQuantity=correctQuantity/100 #because we have energy, fat,... for 100g/ml in db
				addInfoIntoColumn(int(id_food), correctQuantity, float(energy_100g), float(fat_100g), float(proteins_100g), float(saturated_fat_100g), float(carbohydrates_100g), float(sugars_100g), float(sodium_100g))
		sys.stdout.write(GREEN + " -> DONE!" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	cursor.close()
	cnx.close()

def write_to_file():
	selectInfoFromFoodCommand = (
		"SELECT `id_food`, `quantity`, `energy_100g`, `fat_100g`, `proteins_100g`,`saturated_fat_100g`, `carbohydrates_100g`, `sugars_100g`, `sodium_100g`"
		"FROM `Food`"
		"WHERE `energy_100g` > 0;"
		)

	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	sys.stdout.write("Selecting info from DB...")
	all_foods = []
	try:
		cursor.execute(selectInfoFromFoodCommand)
		for (id_food, quantity, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, sodium_100g) in cursor:
			all_foods.append([id_food, quantity, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, sodium_100g])
		sys.stdout.write("All foods fetched !\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	cursor.close()
	cnx.close()

	# Write modified foods to file
	all_modified_foods = []
	i=0
	for (id_food, quantity, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, sodium_100g) in all_foods:
		correctQuantity = changeDimension(quantity)
		if correctQuantity != -1 and checkIfHaveAllInfos([energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, sodium_100g]): #only add info in columns if the quantity was correctly changed to g/ml
			correctQuantity=correctQuantity/100 #because we have energy, fat,... for 100g/ml in db
			all_modified_foods.append([int(id_food), float(energy_100g)*correctQuantity, float(fat_100g)*correctQuantity, float(proteins_100g)*correctQuantity, float(saturated_fat_100g)*correctQuantity, float(carbohydrates_100g)*correctQuantity, float(sugars_100g)*correctQuantity, float(sodium_100g)*correctQuantity])
		i+=1
		sys.stdout.write(str(id_food)+"\n")
	f = open(modified_foods_file,"w+");
	f.write(str(all_modified_foods));
	cursor.close()
	cnx.close()

def read_and_update_db(f):
	addInfoIntoColumnCommand = (
		"UPDATE `Food`"
		"SET `total_energy` = %s , `total_fat` = %s , `total_proteins` = %s , `total_saturated_fat` = %s, `total_carbohydrates` = %s , `total_sugars` = %s , `total_sodium` = %s"
		"WHERE `id_food` = %s;"
		)
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	
	data = f.read()
	modified_foods = ast.literal_eval(data)

	for (id_food, total_energy, total_fat, total_proteins, total_saturated_fat, total_carbohydrates, total_sugars, total_sodium) in modified_foods:
		try:
			cursor.execute(addInfoIntoColumnCommand, (float(total_energy), float(total_fat), float(total_proteins), float(total_saturated_fat), float(total_carbohydrates), float(total_sugars), float(total_sodium), id_food)) 
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	cnx.commit()
	cursor.close()
	cnx.close()


def addInfoIntoColumn(id_food, correctQuantity, energy_100g, fat_100g, proteins_100g, saturated_fat_100g, carbohydrates_100g, sugars_100g, sodium_100g):

	addInfoIntoColumnCommand = (
		"UPDATE `Food`"
		"SET `total_energy` = %s , `total_fat` = %s , `total_proteins` = %s , `total_saturated_fat` = %s, `total_carbohydrates` = %s , `total_sugars` = %s , `total_sodium` = %s"
		"WHERE `id_food` = %s;"
		)

	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	try:
		cursor.execute(addInfoIntoColumnCommand, (correctQuantity*energy_100g, correctQuantity*fat_100g, correctQuantity*proteins_100g, correctQuantity*saturated_fat_100g, correctQuantity*carbohydrates_100g, correctQuantity*sugars_100g, correctQuantity*sodium_100g, id_food))
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	cnx.commit()
	cursor.close()
	cnx.close()

def execute(first_time):
	if first_time:
		addOrDeleteColumnsToTable(True) #delete columns
	addOrDeleteColumnsToTable(False) #add columns
	selectInfoFromDbAndAddNewInfos()

if __name__ == "__main__":
	f = None
	try:
		f = open(modified_foods_file, "r")
	except Error as err:
		sys.stdout.write(err+'\n')
		f = None
		
	if f == None : 
		write_to_file()
	else :
		read_and_update_db(f)
		f.close()


