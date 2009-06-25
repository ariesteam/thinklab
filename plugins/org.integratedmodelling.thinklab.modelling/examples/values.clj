(load-bindings 'metadata)
(load-bindings 'sql)

(modelling/with-kbox 
	
	(modelling/kbox test "postgres://postgres:rnbh304@localhost:5432" 
				:protocol "pg" 
				:schema   "postgis"
				:metadata (
					:comment          'thinklab-core:Text
					:label            'thinklab-core:Text
					:centroid         'geospace:Point
					:bbox             'geospace:Polygon
					:valueUSD2001     'thinklab-core:FloatingPoint
					:originalCurrency 'thinklab-core:Text
					:originalValue    'thinklab-core:FloatingPoint)				
				:sql.use.pooling "false" 
				:sql.log.queries "true")
				
		:storage-policy :disable-unless-empty
		:id-prefix "biblio-"
		:metadata-generator {
							:comment   (memfn getDescription) 
							:label     (memfn getLabel)
							:centroid #(geospace/get-centroid %)														
							:bbox     #(geospace/get-bounding-box %)														
														
														
														}
					
		(modelling/object 'bibtex:Article
			"Test article"
			"Description of a test article"			
			(bibtex:hasJournal "Environmental Modelling & Software")
		  (bibtex:hasAuthor "Kollat, JB" "Reed, P")
		  (bibtex:hasTitle "A framework for visually interactive decision-making and design using evolutionary multi-objective optimization (VIDEO)")
  		(bibtex:hasAffiliation "Penn State Univ, Dept Civil & Environm Engn, University Pk, PA 16802 USA")
  		(bibtex:hasYear 2007)
	    (bibtex:hasVolume 22)
  		(bibtex:hasNumber 12)
	    (bibtex:hasPages "1691-1704")
   		(bibtex:hasKeywords "visualization; decision support; monitoring design; multi-objective optimization; genetic algorithms; kriging")
  	  (metadata:hasISIClassification "groundwater monitoring design; genetic algorithms; objectives; strategies; management; solve; model")
   		(bibtex:hasAbstract "This study presents a framework for (V) under bar isually (I) under bar nteractive (D) under bar ecision-making and (D) under bar esign using (E) under bar volutionary Multi-objective Optimization (V (I) under bar DEO). The VI (D) under bar EO framework allows users to visually navigate large multi-objective solution sets while aiding decision makers in identifying one or more optimal designs. Specifically, the interactive visualization framework is intended to provide an innovative exploration tool for high-order Pareto-optimal solution sets (i.e., solution sets for three or more objectives). The framework is demonstrated for a long-term ground-water monitoring (LTM) application in which users can explore and visualize tradeoffs for up to four design objectives, simultaneously. Interactive functionality within the framework allows the user to select solutions within the objective space and visualize the corresponding monitoring plan's perfon-nance in the design space. This functionality provides the user with a holistic picture of the information provided by a particular solution, ultimately allowing them to make a more informed decision. In addition, the ease with which the framework allows users to navigate and compare solutions as well as design tradeoffs leads to a time efficient analysis, even when there are thousands of potential solutions. (c) 2007 Elsevier Ltd. All rights reserved.")
  		(metadata:hasDownloadLink "articles/08003011183020297.pdf")) 
  	:id 'KollatRed2007
  	
  	(object currency/monetary-value 
  		(modelling/hasObservable (object 'esval:CollectiveValueAssessment (hasBenefit 'eserv:NutrientRegulation)))
  		
  		
  		
  		)
  		 
 ) 

					
;	(modelling/kbox-metadata observation:Observation
;				(:latitude  '(...)
;	 			 :longitude '(...)
;	 			 :value     '(...)))
	 			 
;	(object esval:Value 
;		"this is a nice comment"
;		(esval:hasValue 120)
;		(esval:hasReference (object esval-10) :in biblio-esd)) :as esval-100  			 

;	(modelling/model '(esval:GroupAssignedValue (hasBenefit 'eserv:NutrientRegulation))

;  	"Estimated value of Nutrient regulation in Open Ocean
;  	 138.890 USD-2001 using Direct Non-Market Group Valuation"
  	 
;		(measurement 'currency:MonetaryValue "USD@2002/(ha yr)") 
;  		:state 138.890					
;	 	 	:context
;			 ((classification 'ecology:Biome)  :as biome :state 'biomes:OpenOcean,
;	 	  	(identification 'metadata:JournalArticle)  :state 'kbox://esd-bibliodata#refer-221,
;	 	  	(temporal-location) :state 1998,
;	 	 	  (areal-location)    :refer 'kbox://admin-98#WholeWorld)) :as esdval-2 

)
	 	