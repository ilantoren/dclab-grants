findlibrary( topicmodels )
library(slam)
library( tm )

documents <- apply( source, 2, function(x) gsub(',' , ' ', x))
names.corpus <- Corpus( VectorSource(documents))
names.dtm <- DocumentTermMatrix( names.corpus, control=list( stemming=TRUE, stopwords=TRUE, removeNumbers=TRUE, minDocFreq=1, weighing=weightTfIdf))