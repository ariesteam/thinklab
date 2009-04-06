(ns modelling)

(defn write-netcdf
	[observation filename]
	(doto (new org.integratedmodelling.modelling.visualization.NetCDFArchive)
		(.setObservation observation)
		(.write filename)))
		