#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
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

db_name = "db_colruyt"
db_properties_filename = "../../src/main/resources/dao.properties"
filename = "raw/images_binary_file.txt"
delimiter = "€£"

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

def selectImagesFromDb():
	selectInfoFromFoodCommand = (
		"SELECT `image_blob`"
		"FROM `Food`;"
		)

	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	sys.stdout.write("Selecting info from DB...")
	images = []
	try:
		cursor.execute(selectInfoFromFoodCommand)
		for image_blob in cursor:
			if (image_blob[0] != None):
				images.append(image_blob[0])
		sys.stdout.write("All images fetched !\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	cursor.close()
	cnx.close()
	return images

def writeImagesToFile(images):
	with open(filename, 'wb') as f:
		for image in images:
			f.write(image + delimiter)


def execute():
	images = selectImagesFromDb()
	writeImagesToFile(images)

if __name__ == "__main__":
	execute()