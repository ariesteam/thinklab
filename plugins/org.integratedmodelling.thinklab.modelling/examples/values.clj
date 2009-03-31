(defmodel esdval-2 'esval:value

  "Estimated value of Nutrient regulation in Open Ocean
   138.890 USD-2001 using Direct Non-Market Group Valuation "
    
	; context
	[(classification 'esval:biome)  :as biome  :state 'biomes:OpenOcean,
	 (identification 'metadata:JournalArticle) :state 'kbox://esd-bibliodata#refer-221,
	 (temporal-location) :state 1998,
	 (areal-location)    :refer 'kbox://admin-98#WholeWorld]	 
	
	; what this model is
	(measurement 
		'(eserv:GroupAssignedValue (hasBenefit 'eserv:NutrientRegulation)) 
		"USD@2002/(ha yr)") :state 138.890)
	
