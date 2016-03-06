#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast
import datetime
import requests
import requests.exceptions
import time
from math import floor

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
		"`image_blob` BLOB);"
		)

	deleteColumnsFromTableCommand = (
		"ALTER TABLE `Food` "
		"DROP `image_blob`;"
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

def selectInfoFromDbColumn():
	selectInfoFromFoodCommand = (
		"SELECT `image_url`"
		"FROM `Food`;"
		)

	image_urls = []

	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	sys.stdout.write("Trying to select info from DB ..")
	try:
		cursor.execute(selectInfoFromFoodCommand)
		sys.stdout.write(GREEN + " -> OK" + RESET + "\n")
		for image_url in cursor:
			image_urls.append(image_url[0])
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	return image_urls


def getAllImagesFromUrl(image_urls):
	images = []
	print("Downloading images (takes overs 25 min for db_colruyt).. ")
	string = "Images Downloaded: {0}"
	count = 0
	s = requests.Session()				# http-presistent requests.
	for image_url in image_urls:
		try:
			response = s.get(image_url)
			images.append(response.content)
			count += 1
			if ((count%200) == 0):
				print(string.format(count))
		except requests.exceptions.ConnectionError:
			time.sleep(5)
			s = requests.Session()
		
	return images

def updateDb(images, image_urls):
	updateImageColumnCommand = (
		"UPDATE `Food`"
		"SET `image_blob` = %s"
		"WHERE `image_url` = %s")
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()

	for i in range(len(images)):
		try:
			args = (images[i], image_urls[i])
			cursor.execute(updateImageColumnCommand, args) 
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	cnx.commit()
	cursor.close()
	cnx.close()



def execute():
	addOrDeleteColumnToTable(False)
	image_urls = selectInfoFromDbColumn()
	images = getAllImagesFromUrl(image_urls)
	updateDb(images, image_urls)


if __name__ == "__main__":
	execute()