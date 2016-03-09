# -*- coding: UTF-8 -*-
#!/usr/bin/python

import ast

recipes = []
with open("results_jdf_recipes.txt","r") as f:
	for line in f:
		recipes.append(ast.literal_eval(line.rstrip("\n")))

ingredients_tags = []
all_tags = {}
with open("jdf_tags.txt","r") as f:
	all_tags = ast.literal_eval(f.read())


for tag in all_tags:
	if tag.encode("utf-8") == "Ingrédients":
		for t in all_tags[tag]:
			ingredients_tags.append(t.encode("utf-8").lower())
			print t.encode("utf-8")
recipes_tags = []
ingredients = []
with open("ingredients_not_in_tags.txt","w+") as ingr:
	i = len(recipes)
	for recipe in recipes :
		i -= 1
		# if i % 100 == 0:
		# 	print str(i) + " LEFT"
		if "ingredients_names" in recipe :
			for ingredient in recipe["ingredients_names"]:
				ingredient = ingredient.encode("utf-8")
				if ingredient not in ingredients and ingredient not in ingredients_tags :
					ingredients.append(ingredient)
					ingr.write(ingredient)
					ingr.write("\n")
			# if "tags" in recipe:
			# 	for tag in recipe["tags"]:
			# 		tag = tag.encode("utf-8").lower()
			# 		if tag not in tags and tag not in recipes_tags:
			# 			recipes_tags.append(tag)
			# 			f.write(tag)
			# 			f.write("\n")

