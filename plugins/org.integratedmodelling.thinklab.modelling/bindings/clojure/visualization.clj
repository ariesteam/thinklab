(ns modelling)

(defn write-netcdf
	"Write out a netcdf file with all the states linked to the passed observation"
	[observation filename]
	(doto (new org.integratedmodelling.modelling.visualization.NetCDFArchive)
		(.setObservation observation)
		(.write filename)))
		