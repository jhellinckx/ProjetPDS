#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast
import os

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

db_name = "db_colruyt"
db_properties_filename = "../../src/main/resources/dao.properties"
directory = "raw/db_images"
png_filename = "db_image_{0}"
nb_items = 6573

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

def addOrDeleteColumnToTable(delete): #if boolean delete is True, deletes otherwise add

	addColumnToTableCommand = (
		"ALTER TABLE `Food`"
		"ADD ("
		"`image_pic` VARCHAR(255));"
		)

	deleteColumnsFromTableCommand = (
		"ALTER TABLE `Food` "
		"DROP `image_pic`;"
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

def getImagesPath():
	images_path = []
	for i in range(nb_items):
		images_path.append(directory+"/"+png_filename.format(i+1))

	return images_path

def updateDb(images_path):

	updateImageColumnCommand = (
		"UPDATE `Food`"
		"SET `image_pic` = %s"
		"WHERE `id_food` = %s")
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	sys.stdout.write("Updating image_pic in DB ..." + "\n")
	sys.stdout.flush()

	for i in range(len(images_path)):
		try:
			args = (images_path[i], i+1)
			cursor.execute(updateImageColumnCommand, args) 
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	cnx.commit()
	cursor.close()
	cnx.close()

def execute():
	addOrDeleteColumnToTable(True)
	addOrDeleteColumnToTable(False)
	images_path = getImagesPath()
	updateDb(images_path)

if __name__ == "__main__":
	execute()

