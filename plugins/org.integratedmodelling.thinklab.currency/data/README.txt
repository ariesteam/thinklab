Raw data in this directory come from scrapping web sites with the minimum amount of 
effort. The util/TableGenerator.java class converts them into what CpiConversionFactory 
uses.

CPI data are the historical tables from http://inflationdata.com/inflation/. The cpiUS.txt
file contains mere cut-and-paste of the HTML tables for all years.

Currency exchange data come from http://fx.sauder.ubc.ca/data.html; the various d[n].txt
files are the CSV files returned from successive queries of the historical archive (limited
to 20 currencies at a time) for the whole 1971-current period.

To update, add the relevant files for the period still uncovered and run TableGenerator again
after editing the file names in it.