args <- commandArgs(TRUE)
library(pmml)

#Splitts the given dataset in a training dataset(trainset) and test dataset(testset)
splitDataFrame <- function(dataframe, seed = NULL, n = trainSize) {
  
  if (!is.null(seed)){
    set.seed(seed)
  }
  
  index <- 1:nrow(dataframe)
  trainindex <- sample(index, n)
  trainset <- dataframe[trainindex, ]
  testset <- dataframe[-trainindex, ]
  
  list(trainset = trainset, testset = testset)
}

loadData <- function(){
  #load the iris data
  data(iris)
  
  #Split the iris dataset into test and train sets
  dataSets <- splitDataFrame(iris, seed = 1337, n= round(0.95 * nrow(iris)))
}

buildModel <- function(dataSets){
  model <- lm(Petal.Width ~ Petal.Length, data=dataSets$trainset)
}

exportPmml <- function(model, modelName=NULL, externalId=NULL){
  pmml.lm(model,model.name=paste(modelName,externalId,sep = ";"))
}

# called from outside
rebuildModel <- function(modelName=NULL, externalId=NULL){
  dataSet = loadData()
  model = buildModel(dataSet)
  pmmlSource = exportPmml(model, modelName, externalId)
  modelOutputFile <- paste(Sys.getenv("TMPDIR"),"model.pmml.xml", sep="")
  write(toString(pmmlSource),file=modelOutputFile,append=FALSE,)
}

# called from outside
benchModel <- function(id){
  
  dataSets = loadData()
  model = buildModel(dataSets)
  summary(model)
}

if(length(args) > 1){
  if(args[1] == "rebuildModel" && length(args) == 3){
    rebuildModel(args[2],args[3]);
  }else if(args[1] == "benchModel" && length(args) == 3){
    benchModel(args[2],args[3]);
  }
}
