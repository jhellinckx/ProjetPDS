# -*- coding: UTF-8 -*-
#!/usr/bin/python

import sys
import requests
import ast
import re
import codecs
import base64
import threading
from time import sleep
import traceback
from copy import deepcopy
from bs4 import BeautifulSoup
from recipe_model import *

GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

domain = "http://cuisine.journaldesfemmes.com"
all_recipes_categories = "/toutes-les-recettes/"
all_from_category = "/preferes"
all_tags = "/s/?f_libelle="

min_ratings = 5

tags_filename = "jdf_tags.txt"
sub_tags_filename = "jdf_subtags.txt"

results_recipes_filename = "results_jdf_recipes.txt"


def get_all_tags():
	structured_response = BeautifulSoup(requests.get(domain + all_tags).content, "lxml")
	tags = {}
	for tag_title in structured_response.find("div",attrs={"id":"blocOption"}).find_all("aside") :
		title = tag_title.find("header",attrs={"class":"searchTitle"}).find("span").string
		tags[title] = find_all_tags_in_title(tag_title)
	with open(tags_filename, "w+") as f:
		f.write(repr(tags))
		

def find_all_tags_in_title(tag_title):
	tags = []
	tag_list = tag_title.find("fieldset",attrs={"id":"fullList1"})
	if tag_list != None:
		for tag in tag_list.find_all("a"):
			if "id" not in tag.attrs :
				tag_name = parse_real_name(tag.text)
				tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"fullList2"})
	if tag_list != None :
		for tag in tag_list.find_all("label"):
			tag_name = parse_real_name(tag.text)
			tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"fullList3"})
	if tag_list != None : 
		for tag in tag_list.find_all("label"):
			tag_name = parse_real_name(tag.text)
			tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"fullList6"})
	if tag_list != None :
		for tag in tag_list.find_all("label"):
			tag_name = parse_real_name(tag.text)
			tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"topList4"})
	if tag_list != None : 
		for tag in tag_list.find_all("a"):
			if "id" not in tag.attrs :
				tag_name = parse_real_name(tag.text)
				tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"topList5"})
	if tag_list != None : 
		for tag in tag_list.find_all("a"):
			if "id" not in tag.attrs :
				tag_name = parse_real_name(tag.text)
				tags.append(tag_name)
		return tags

def parse_real_name(name):
	return " ".join(name.split()).split("(")[0].strip(" ")


class SubCategoryParserWorker(threading.Thread):
	file_lock = threading.Lock()
	progress_lock = threading.Lock()
	progress = 0

	def __init__(self, main_category, sub_categories):
		threading.Thread.__init__(self)
		self.main_category = main_category
		self.sub_categories = sub_categories
		SubCategoryParserWorker.progress_lock.acquire()
		SubCategoryParserWorker.progress += len(sub_categories)
		SubCategoryParserWorker.progress_lock.release()
	
	def run(self):
		self.running = True
		for sub_category in self.sub_categories :
			self.parseSubCategory(sub_category)
			SubCategoryParserWorker.progress_lock.acquire()
			SubCategoryParserWorker.progress -= 1
			print "[" + self.main_category + "] Finished parsing subcategory \"" + sub_category[0] + "\". " + GREEN + str(SubCategoryParserWorker.progress) + RESET + " subcategories left."  
			SubCategoryParserWorker.progress_lock.release()
			return # TO DELETE ------------------------------------


	def stop(self):
		self.running = False

	def parseSubCategory(self, sub_category):
		min_ratings_reached = False
		page = 0
		while not min_ratings_reached :
			page += 1
			url = domain + sub_category[1] + all_from_category + "-page" + str(page)
			structured_recipes_list = BeautifulSoup(requests.get(url).content, "lxml")
			recipes_div = structured_recipes_list.find("section", attrs={"class":"bu_cuisine_best_recipes"})
			if recipes_div != None:
				for recipe_article_tag in recipes_div.find_all("article"):
					note_tag = recipe_article_tag.find("div",attrs={"class":"bu_cuisine_recette_notes"})
					if note_tag != None:
						count_tag = note_tag.find("span",attrs={"class":"count count--fr"})
						if count_tag != None:
							ratings_string = count_tag.string
							ratings = ""
							for char in ratings_string.split("(")[1]:
								if char.isdigit(): 
									ratings += char
								else : 
									break
							if int(ratings) < min_ratings :
								min_ratings_reached = True
								break
					recipe_title_tag = recipe_article_tag.find("span",attrs={"class":"bu_cuisine_title_4"})
					if recipe_title_tag != None:
						url_tag = recipe_title_tag.find("a")
						if "href" in url_tag.attrs :
							self.parseRecipe(sub_category, url_tag.attrs["href"])
							return # TO DELETE ------------------------------------

	def parseRecipe(self, sub_category, sub_url):
		url = domain + sub_url
		recipe = {}
		recipe[URL_KEY] = url
		recipe[PRIMARY_CATEGORY_KEY] = self.main_category
		recipe[SECONDARY_CATEGORY_KEY] = sub_category[0]
		structured_recipe = BeautifulSoup(requests.get(url).content, "lxml")

		# Find recipe name
		name_tag = structured_recipe.find("h1",attrs={"class":"bu_cuisine_title_1"})
		if name_tag != None:
			name_span_tag = name_tag.find("span")
			if name_span_tag != None :
				recipe[NAME_KEY] = name_span_tag.string

		recipe_main_tag = structured_recipe.find("article",attrs={"class":"grid_line gutter grid--norwd bu_cuisine_main_recipe"})
		if recipe_main_tag != None :
			# Find image url
			image_url_div = recipe_main_tag.find("div",attrs={"class":"bu_cuisine_recette_img"})
			if image_url_div != None:
				figure_tag = image_url_div.find("figure")
				if figure_tag != None :
					image_url_tag = figure_tag.find("a")
					if image_url_tag != None and "href" in image_url_tag.attrs:
						recipe[IMAGE_URL_KEY] = image_url_tag["href"]
			# Find ratings
			rating_span = recipe_main_tag.find("span",attrs={"class":"jAverage"})
			if rating_span != None :
				recipe[RATING_KEY] = " ".join(rating_span.string.split())
			n_ratings_span = recipe_main_tag.find("span",attrs={"class":"jNbNote"})
			if n_ratings_span != None :
				recipe[NBR_RATINGS_KEY] = n_ratings_span.string
		print repr(recipe)



	@staticmethod
	def saveRecipe(recipe):
		SubCategoryParserWorker.file_lock.acquire()
		try:
			with open(results_recipes_filename, "a") as f:
				f.write(repr(recipe))
				f.write("\n")
		except Exception as e : 
			raise e
		finally:
			SubCategoryParserWorker.file_lock.release()


def get_recipes_types_urls() : 
	raw_response = requests.get(domain + all_recipes_categories)
	structured_response = BeautifulSoup(raw_response.content, "lxml")
	tag_titles = {}
	for tag_title in structured_response.find_all("a",attrs={"class":"bu_cuisine_title_3"}):
		tag_title_name = tag_title.string
		tag_titles[tag_title_name] = []
		for sub_tag in tag_title.parent.find_all("a",attrs={"class":"bu_cuisine_bloc"}):
			sub_tag_tuple = (sub_tag.string, "")
			href = ""
			if "href" in sub_tag.attrs:
				href = sub_tag.attrs["href"]
			sub_tag_tuple = (sub_tag.string, href)
			tag_titles[tag_title_name].append(sub_tag_tuple)
	with open(sub_tags_filename, "w+") as f:
		f.write(repr(tag_titles))

def start_parsing_sub_categories():
	categories = {}
	with open(sub_tags_filename, "r") as f:
		categories = ast.literal_eval(f.read())
	category = (u'Petit d\xe9jeuner', [(u'Boisson brunch', '/recette-boisson-brunch'), (u'Brioche', '/recette-brioche'), (u'Brunch sal\xe9', '/recette-brunch-sale'), (u'Brunch sucr\xe9', '/recette-brunch-sucre'), (u'Confiture', '/recette-confiture'), (u'Pain boulanger', '/recette-pain-boulanger')])
	worker = SubCategoryParserWorker(category[0], category[1])
	worker.start()
	worker.join()
	# workers = [SubCategoryParserWorker(category, categories[category]) for category in categories]
	# for worker in workers :
	# 	worker.start()
	# for worker in workers:
	# 	worker.join()




if __name__ == "__main__":
	#get_all_tags()
	#get_recipes_types_urls()
	start_parsing_sub_categories()
