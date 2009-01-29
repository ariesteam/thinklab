;; -----------------------------------------------------------------------------------------------
;; Sample semantic model of Climate Stability
;; -----------------------------------------------------------------------------------------------
;; -----------------------------------------------------------------------------------------------

(load-bindings 'modelling)

(let [
	 ;; --------------------------------------------------------------------------------------- 
	 ;; We ask the system to create the type of rainfall that we will need later, and bind it to
	 ;; the variable "discreteRainfall" so it can be observed in the database supplied to run(). 
	 ;; --------------------------------------------------------------------------------------- 
	 discreteRainfall (model/make-type
	 					;; -------------------------------------------------------------------------
						;; In order for the discretization to make sense we must ensure the numbers
						;; represent millimeters. By using the type below instead of the raw
						;; carbonEcology:RainfallAmount observable type, we tell the system that
						;; whatever observation of RainfallAmount we decide to use, we need it 
						;; converted into the model represented by unit "mm" before it's used.
	 					;; NOTE this should be ecology:RainfallAmount in order to be transparent
	 					;; to the database query.
	 					;; -------------------------------------------------------------------------
	 					(model/make-type 'carbonEcology:RainfallAmount (corescience/make-unit "mm"))
	 					
	 					;; -------------------------------------------------------------------------
	 					;; Observations of the above type (rainfall in mm) will be discretized as 
	 					;; follows to obtain the kind of rainfall we want to use in the final model.
	 					;; -------------------------------------------------------------------------
	 					(corescience/make-discretizer 
	 						 'carbonEcology:RainfallClassA "400)"
	 						 'carbonEcology:RainfallClassB "[400,600)"
	 						 'carbonEcology:RainfallClassC "[600,800)"
	 						 'carbonEcology:RainfallClassD "[800,1000)"
	 						 'carbonEcology:RainfallClassE "[1000,1200)"
	 						 'carbonEcology:RainfallClassF "[1200,1600)"
	 						 'carbonEcology:RainfallClassG "[1600,2000)"
	 						 'carbonEcology:RainfallClassH "[2000,2600)"
	 						 'carbonEcology:RainfallClassI "[2600,3200)"
	 						 'carbonEcology:RainfallClassJ "[3200,3600)"
	 						 'carbonEcology:RainfallClassK "[3600,5000)"
	 						 'carbonEcology:RainfallClassL "[5000" )) 
	 ]

	;; ---------------------------------------------------------------------------------------
	;; The following specifies a conditional model structure for climate stability, depending on
	;; a context of land use and including variables whose nature and observation model depend 
	;; on the context.
	;; ---------------------------------------------------------------------------------------
  (model/make-model 'carbonService:ClimateStability

			;; ---------------------------------------------------------------------------------
			;; The context bindings defines the variables that influence the structure of the model.
			;; ---------------------------------------------------------------------------------
			;; the structure of this model depends on land use. In the specification, the variable
			;; :landuse will be bound to the land use class in each context of valuation.
			;; ---------------------------------------------------------------------------------
			[:landuse 'carbonEcology:VegetationType]



			;; ---------------------------------------------------------------------------------
			;; Rainfall is used everywhere just as data, discretized into 12 categories defined
			;; in the discreteRainfall type. We only need to define its default model.
			;; ---------------------------------------------------------------------------------
			('carbonEcology:Rainfall :default discreteRainfall)))

 