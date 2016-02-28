#!/usr/bin/python
# -*- coding: UTF-8 -*-

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

domain = "https://colruyt.collectandgo.be"
cookie_setup_url = domain + "/cogo/homepage"
search_by_branch_url = domain + "/cogo/fr/branch/"

last_cookies_filename = "last_cookies.txt"
results_filename = "results_branch_articles.txt"
branch_status_filename = "branch_status.txt"
error_messages_filename = "errors.txt"

init_branch_index = 2
total_threads = 5


class BaseArticle:
	BRANCH_INDEX_KEY = "branch_index"
	IMAGE_KEY = "image_url"
	PRICE_UNIT_KEY = "price_unit"
	PRICE_KG_KEY = "price_kg"
	DETAILS_KEY = "details_url"
	CATEGORIES_KEY = "categories"
	WEIRD_NAME_KEY = "weird_name"
	SHORT_DESCRIPTION_KEY = "short_description"
	FULL_DESCRIPTION_KEY = "full_description"

	KEYS = 	[
				BRANCH_INDEX_KEY,\
				IMAGE_KEY,\
				PRICE_UNIT_KEY,\
				PRICE_KG_KEY,\
				DETAILS_KEY,\
				CATEGORIES_KEY,\
				WEIRD_NAME_KEY,\
				SHORT_DESCRIPTION_KEY,\
				FULL_DESCRIPTION_KEY
			]

	def __init__(self, article = None):
		self.infos = {}
		for key in BaseArticle.KEYS:
			self.infos[key] = None
			self.infos[BaseArticle.CATEGORIES_KEY] = []
		if isinstance(article, dict):
			self.from_dict(article)
		elif isinstance(article, BaseArticle):
			self.from_dict(article.infos)

	def __repr__(self):
		return self.infos.__repr__()

	def from_dict(self, infos_dict):
		if isinstance(infos_dict, dict):
			for key in infos_dict:
				if key in BaseArticle.KEYS :
					if key == BaseArticle.CATEGORIES_KEY :
						self.infos[key] = deepcopy(infos_dict[key])
					else : 
						self.infos[key] = infos_dict[key]


class DetailedArticle(BaseArticle):
	PORTION_ENERGY_KJ_KEY = "portion_energy_kj"
	PORTION_ENERGY_KCAL_KEY = "portion_energy_kcal"
	PORTION_TOTAL_FAT_KEY = "portion_total_fat"
	PORTION_SATURATED_FAT_KEY = "portion_saturated_fat"
	PORTION_TOTAL_CARBOHYDRATES_KEY = "portion_total_carbohydrates"
	PORTION_TOTAL_PROTEINS_KEY = "portion_proteins"
	PORTION_FIBERS_KEY = "portion_fibers"
	PORTION_SALT_KEY = "portion_salt"
	PORTIONS_NUMBER_KEY = "portions_number"

	PER_100G_ENERGY_KJ_KEY = "100g_energy_kj"
	PER_100G_ENERGY_KCAL_KEY = "100g_energy_kcal"
	PER_100G_TOTAL_FAT_KEY = "100g_total_fat"
	PER_100G_SATURATED_FAT_KEY = "100g_saturated_fat"
	PER_100G_TOTAL_CARBOHYDRATES_KEY = "100g_total_carbohydrates"
	PER_100G_TOTAL_PROTEINS_KEY = "100g_proteins"
	PER_100G_FIBERS_KEY = "100g_fibers"
	PER_100G_SALT_KEY = "100g_salt"

	TOTAL_QUANTITY_KEY = "total_quantity"
	INGREDIENTS_TEXT_KEY = "ingredients_text"
	BAR_CODE_KEY = "bar_code"
	ALLERGENS_KEY = "allergens"

	KEYS = 	[
				PORTION_ENERGY_KJ_KEY,\
				PORTION_ENERGY_KCAL_KEY,\
				PORTION_TOTAL_FAT_KEY,\
				PORTION_SATURATED_FAT_KEY,\
				PORTION_TOTAL_CARBOHYDRATES_KEY,\
				PORTION_TOTAL_PROTEINS_KEY,\
				PORTION_FIBERS_KEY,\
				PORTION_SALT_KEY,\
				PORTIONS_NUMBER_KEY,\
				PER_100G_ENERGY_KJ_KEY,\
				PER_100G_ENERGY_KCAL_KEY,\
				PER_100G_TOTAL_FAT_KEY,\
				PER_100G_SATURATED_FAT_KEY,\
				PER_100G_TOTAL_CARBOHYDRATES_KEY,\
				PER_100G_TOTAL_PROTEINS_KEY,\
				PER_100G_FIBERS_KEY,\
				PER_100G_SALT_KEY,\
				TOTAL_QUANTITY_KEY,\
				INGREDIENTS_TEXT_KEY,\
				BAR_CODE_KEY,\
				ALLERGENS_KEY
			]

	def __init__(self, base_article):
		BaseArticle.__init__(self, base_article)




class BranchParserWorker(threading.Thread):
	index_lock = threading.Lock()
	file_lock = threading.Lock()
	branch_status_lock = threading.Lock()
	error_write_lock = threading.Lock()

	branch_index = init_branch_index
	status_OK = "ok_index"
	status_FAIL = "fail_index"
	branch_status = { status_OK : [], status_FAIL : [] }

	def __init__(self):
		threading.Thread.__init__(self)
		# Need cookies else Colruyt sends a 'Service Unavailable' reponse
		self.cookies = requests.get(cookie_setup_url).cookies 

	@staticmethod
	def nextBranchIndex():
		BranchParserWorker.index_lock.acquire()
		acquired_index = BranchParserWorker.branch_index
		BranchParserWorker.branch_index += 1
		BranchParserWorker.index_lock.release()
		return acquired_index

	@staticmethod
	def branchIndex():
		BranchParserWorker.index_lock.acquire()
		index = BranchParserWorker.branch_index
		BranchParserWorker.index_lock.release()
		return index

	@staticmethod
	def setBranchIndex(new_index):
		BranchParserWorker.index_lock.acquire()
		try:
			BranchParserWorker.branch_index = new_index
		except Exception as e:
			raise e
		finally:
			BranchParserWorker.index_lock.release()

	@staticmethod
	def addBranchStatus(index, status_code):
		BranchParserWorker.branch_status_lock.acquire()
		try:
			BranchParserWorker.branch_status[status_code].append(index)
		except Exception as e:
			raise e
		finally:
			BranchParserWorker.branch_status_lock.release()

	@staticmethod
	def saveBranchBaseArticles(articles):
		BranchParserWorker.file_lock.acquire()
		try:
			with open(results_filename,"a") as f:
				for article in articles:
					f.write(repr(article))
					f.write("\n")
		except Exception as e:
			raise e
		finally:
			BranchParserWorker.file_lock.release()

	@staticmethod
	def writeError(e, branch):
		BranchParserWorker.error_write_lock.acquire()
		try :
			with open(error_messages_filename,"a") as f:
				f.write(\
					"-" * 40 \
					+ "\n" + str(type(e)) + " : " + str(e) + \
					"\nFOR ID -> " + str(branch)+"\n")
				f.write(">>> FORMAT <<<\n")
				traceback.print_exc(file=f)
				f.write(">>> END FORMAT <<<\n" + "-" *40)
		except Exception as e:
			raise e 
		finally:
			BranchParserWorker.error_write_lock.release()

	def run(self):
		self.running = True
		while(self.running):
			branch_index = BranchParserWorker.nextBranchIndex()
			try:
				self.parse_all_articles_from_branch(branch_index)
			except Exception as e:
				BranchParserWorker.writeError(e, branch_index)

	def stop(self):
		self.running = False

	def parse_all_articles_from_branch(self, branch_index):
		# HTTP GET and use BeautifulSoup to structure HTML tree
		raw_response = requests.get(search_by_branch_url + str(branch_index), cookies=self.cookies)
		structured_response = BeautifulSoup(raw_response.content, "lxml")

		# Check first if response is not empty
		if "Pas de produits" in structured_response.get_text() :
			BranchParserWorker.addBranchStatus(branch_index, BranchParserWorker.status_FAIL)
			return 
		BranchParserWorker.addBranchStatus(branch_index, BranchParserWorker.status_OK)
		articles = []

		# Init parsed product
		parsed_article = BaseArticle()
		parsed_article.infos[BaseArticle.BRANCH_INDEX_KEY] = branch_index

		# Find categories
		script_container = structured_response.body.script.string
		if "utag_data" in script_container :
			open_bracket_index = script_container.find("{")
			close_bracket_index = script_container.find("}")
			script_dict = script_container[open_bracket_index:close_bracket_index+1]
			for elem in script_dict.split(","):
				if "page_cat" in elem:
					parsed_article.infos[BaseArticle.CATEGORIES_KEY].append(elem.split(":")[1].strip(" ").strip("'"))

		# Iterate on each article/product
		for article in structured_response.find("div",attrs={"id":"articles"}).children:
			# Image url
			article_image_wrap = article.find("div",attrs={"class":"imageWrap"})
			if(article_image_wrap != None):
				parsed_article.infos[BaseArticle.IMAGE_KEY] = domain + str(article_image_wrap.find("img").attrs["src"]) 


			# Prod infos
			articles_infos_wrap = article.find("a", attrs={"class":"prodInfo"})
			if(articles_infos_wrap != None):
				if("href" in articles_infos_wrap.attrs) : 
					parsed_article.infos[BaseArticle.DETAILS_KEY] = domain + articles_infos_wrap.attrs["href"]
				# Weird name
				weird_name_tag = articles_infos_wrap.find("span", attrs={"name"})
				if weird_name_tag != None : parsed_article.infos[BaseArticle.WEIRD_NAME_KEY] = weird_name_tag.string
				# Short description
				short_description_tag = articles_infos_wrap.find("span", attrs={"description"})
				if short_description_tag != None : parsed_article.infos[BaseArticle.SHORT_DESCRIPTION_KEY] = short_description_tag.string
				# Full description
				full_description_tag = articles_infos_wrap.find("p", attrs={"fullDescription"})
				if full_description_tag != None : parsed_article.infos[BaseArticle.FULL_DESCRIPTION_KEY] = full_description_tag.string

			# Price 
			article_price_div = article.find("div", attrs={"class":"price"})
			if article_price_div != None:
				article_price_unit = article_price_div.find("p", attrs={"class":"piece"})
				if article_price_unit != None:
					script_tag = article_price_unit.find("script")
					if script_tag != None:
						try :
							parsed_article.infos[BaseArticle.PRICE_UNIT_KEY] = base64.b64decode(script_tag.string.split(",")[0].split("(")[1].strip("'"))
						except :
							sys.stdout.write("Price Unit not parsed\n")
					

				article_price_kg = article_price_div.find("p", attrs={"class":"unit"})
				if article_price_kg != None:
					script_tag = article_price_kg.find("script")
					if script_tag != None:
						try :
							parsed_article.infos[BaseArticle.PRICE_KG_KEY] = base64.b64decode(script_tag.string.split(",")[0].split("(")[1].strip("'"))
						except :
							sys.stdout.write("Price kg not parsed\n")
			articles.append(parsed_article)

		BranchParserWorker.saveBranchBaseArticles(articles)

def stopWorkers(workers):
	# Stop threads
	for worker in workers:
		worker.stop()
		worker.join()

def saveBranchStatus():
	# Save used branch index
	with open(branch_status_filename, "w+") as f:
		f.write(str(BranchParserWorker.branch_status)+"\n")

def branch_prompt():
	def print_help():
		sys.stdout.write("exit/stop : terminate all workers\n")
		sys.stdout.write("index     : get current branch index\n")
		sys.stdout.write("set       : set new branch index\n")

	last_branch_index = 1
	try:
		with open(branch_status_filename,"r") as f:
			all_branch_status = ast.literal_eval(f.read())
			BranchParserWorker.branch_status = all_branch_status
			size_OK = len(all_branch_status[BranchParserWorker.status_OK])
			size_FAIL = len(all_branch_status[BranchParserWorker.status_FAIL])
			if size_OK == 0 and size_FAIL != 0 :
				last_branch_index = all_branch_status[BranchParserWorker.status_FAIL][-1]
			elif size_OK != 0 and size_FAIL == 0 :
				last_branch_index = all_branch_status[BranchParserWorker.status_OK][-1]
			elif size_OK != 0 and size_FAIL != 0 :
				last_branch_index = max([all_branch_status[BranchParserWorker.status_OK][-1], all_branch_status[BranchParserWorker.status_FAIL][-1]])
			sys.stdout.write("Last checked branch index was "+str(last_branch_index)+".\nDo you want to continue with this index ? (y/n)")
			user_input = raw_input().lower()
			if user_input == "n" :
				while not user_input.isdigit():
					sys.stdout.write("Enter new index : ")
					user_input = raw_input()
				last_branch_index = int(user_input) - 1
				sys.stdout.write("New branch index set at " + user_input + '\n')
	except IOError:
		pass

	BranchParserWorker.branch_index = last_branch_index + 1
	workers = [BranchParserWorker() for i in range(total_threads)]
	try:
		for worker in workers :
			worker.start()

		stop = False
		print_help()
		while not stop :
			sys.stdout.write("[Branch prompt] Type help for a list of commands.\n")
			sys.stdout.write(">> ")
			user_input = raw_input().lower()
			if user_input == "stop" or user_input == "exit":
				stop = True
			elif user_input == "index" :
				sys.stdout.write("Current branch index : " + str(BranchParserWorker.branchIndex()) + "\n")
			elif user_input == "set" :
				sys.stdout.write("Set new branch index : ")
				new_branch = raw_input()
				if(new_branch.isdigit()):
					previous = BranchParserWorker.setBranchIndex(int(new_branch))
					sys.stdout.write(str(previous) + " -> " + new_branch + "\n")
				else:
					sys.stdout.write(new_branch + " is not a digit.\n")
			elif user_input == "help" :
				print_help()
			else:
				sys.stdout.write("Command unknown.\n")
	except:
		sys.stdout.write("Unexpected error : " + str(sys.exc_info()[0]))
		
	finally:
		stopWorkers(workers)
		saveBranchStatus()

def details_prompt():

	sys.stdout.write("Entering details !\n")

if __name__ == "__main__" :
	def print_help():
		sys.stdout.write("exit/stop  : leave prompt and terminate process\n")
		sys.stdout.write("branch     : start Colruyt branch parsing\n")
		sys.stdout.write("details    : use " + results_filename + " to parse Colruyt products details\n")
	print_help()
	exit = False
	while not exit:
		user_input = raw_input("[Main prompt] Type help for a list of commands.\n>> ").lower()
		if user_input == "help":
			print_help()
		elif user_input == "exit" or user_input == "stop":
			exit = True
		elif user_input == "branch":
			branch_prompt()
		elif user_input == "details":
			details_prompt()
		else :
			sys.stdout.write("Command unknown.\n")






