;; -----------------------------------------------------------------------------------------
;; Clojure bindings for the Time plugin
;; @author Ferdinando Villa
;; -----------------------------------------------------------------------------------------

(ns time)

(tl/load-bindings 'corescience)

(defn time-extent
		"Return a time extent parsed from a literal. See TimeFactory.parseTimeTopology
     for the format. Use in defcontext."
		[literal]
		(org.integratedmodelling.time.TimeFactory/parseTimeTopology literal))
