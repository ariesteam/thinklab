(ns model)

(tl/load-bindings 'corescience)

(defn measurement 
	"Build a model for a measurement, optionally mediating another model (which must also be
	a measurement"
	[concept-or-model unitspecs]
	(new org.integratedmodelling.modelling.corescience.MeasurementModel
			concept-or-model (str unitspecs)))
			
;(defn computed-measurement 
; 	""
;	( [concept unitspecs equation] nil)
;	( [concept unitspecs equation dependencies] nil))
; 	
;(defn dde-measurement 
; 	""
;	( [concept unitspecs equation] nil)
;	( [concept unitspecs equation dependencies] nil))
; 	
;(defn ranking 
;	"Build a model for a measurement, optionally mediating another model (which must also be
;	a measurement"
;	([concept-or-model & specs]
;	(new 
;		(org.integratedmodelling.modelling.corescience.MeasurementModel
;			((tl/conc concept) (str unitspecs) nil))))
;	([concept-or-model unitspecs]
;	(new
;		(org.integratedmodelling.modelling.corescience.MeasurementModel
;			((tl/conc concept) (str unitspecs) model)))))
;					
;(defn computed-ranking 
; 	""
;	( [concept-or-model equation] nil)
;	( [concept-or-model equation dependencies] nil))
; 	
;(defn dde-ranking 
; 	""
;	( [concept-or-model equation] nil)
;	( [concept-or-model equation dependencies] nil))
; 	
 	
			
