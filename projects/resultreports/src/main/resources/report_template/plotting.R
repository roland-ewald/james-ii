#
# The general modelling and simulation framework JAMES II. Copyright by the University of Rostock.
#  
# LICENCE: JAMESLIC
#
# Plotting tools for result report generation 
# 
# Author: Roland Ewald, University of Rostock
#
# 

#
# Reads data with given name from sub-directory for raw data.  
#
# name - name of the file containing the raw data
#
readData <- function(name){
	read.csv(paste("./raw/",name,".dat",sep=""),head=FALSE);
}
