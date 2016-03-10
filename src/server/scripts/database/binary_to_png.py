#!/usr/bin/python
# -*- coding: UTF-8 -*-

from os import mkdir
import os
from os import rmdir


filename = "raw/images_binary_file.txt"
directory = "raw/db_images"
png_filename = "db_image_{0}"
delimiter = "€£"
no_pic_image = "raw/nopic.jpg"

from PIL import Image
from cStringIO import StringIO


def getBinaryImagesFromFile():
	images = []
	print "Getting images from file ..."
	with open(filename, 'rb') as f:
		images = f.read().split(delimiter)
		images = images[:len(images)-1]
	return images

def convertBinaryToImages(image_dict):

	for i in image_dict:
		imagefile = StringIO(image_dict[i])
		image_dict[i] = Image.open(imagefile)

def deleteFilesInDirectory():
	for f in os.listdir(directory):
		path = os.path.join(directory, f)
		try:
			if os.path.isfile(path):
				os.unlink(path)
		except Exception as e:
			print e

def createDirectory():
	try:
		deleteFilesInDirectory()
		rmdir(directory)
		mkdir(directory)
	except OSError as e:
		mkdir(directory)

def saveImagesAsPNG(image_dict):
	for i in image_dict:
		f = directory + "/" + png_filename.format(i)
		img = image_dict[i]
		try:
			img.save(f, 'jpeg')
		except IOError:
			img = Image.open(no_pic_image)
			img.save(f, 'jpeg')

def convertImages(image_dict):
	convertBinaryToImages(image_dict)
	createDirectory()
	saveImagesAsPNG(image_dict)

def execute():
	binary_images = getBinaryImagesFromFile()
	convertImages(binary_images)
	

if __name__ == "__main__":
	execute()