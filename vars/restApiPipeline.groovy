// vars/restApiFlaskMongo.groovy

def call (Map pipelineParams) {
	
	def projectName = env.JOB_NAME.split('/')[1]
	def dockerLib = new docker.DockerLib()

	pipeline {
		agent { 
			label 'rest-api'
		}
		environment {
			DOCKER_IMAGE = "${DOCKER_REGISTRY}/${projectName}:${BRANCH_NAME}-${BUILD_NUMBER}"
			BRANCH_NAME = "${BRANCH_NAME}"
			PROJECT_NAME = "${projectName}"
		}
		
		stages {
			stage('Image Build') {
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " BUILD DA IMAGEM: $DOCKER_IMAGE"
						echo " --------------------------------------------------------------------------------------- "
						
						copyFiles(ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME)
						
						sh "docker build -t $PROJECT_NAME:$BRANCH_NAME-${BUILD_NUMBER} --no-cache -f Dockerfile ."
					}
				}
			}

			stage('Teste Unitários') {
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " TESTES UNITÁRIOS"
						echo " --------------------------------------------------------------------------------------- "
						
						sh "pip install -r requirements.txt"

						sh "make test"
					}
				}
			}

			stage('Image Push') {
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " PUSH DA IMAGEM: $DOCKER_IMAGE"
						echo " --------------------------------------------------------------------------------------- "

						sh "docker tag $PROJECT_NAME:$BRANCH_NAME-${BUILD_NUMBER} $DOCKER_IMAGE"
						sh "docker push $DOCKER_IMAGE"
					}
				}
			}

			stage('Image Run') {
				//agent {label 'rest-api'}
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " RODANDO A APLICAÇÃO"
						echo " --------------------------------------------------------------------------------------- "

						copyFiles(ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME)

						sh "echo DOCKER_IMAGE=$PROJECT_NAME:$BRANCH_NAME-${BUILD_NUMBER} >> .env"
						sh "echo CONTAINER_NAME=$PROJECT_NAME-$BRANCH_NAME >> .env"

						sh "docker image pull $DOCKER_IMAGE"
						sh "docker-compose -f docker-compose-ci.yml up -d"
					}
				}
			}
		}
	}
}

def copyFiles(Map params) {
	def envjson = libraryResource 'com/json/projectsFilesList.json'
	def json = readJSON text: envjson

	def fileId = json.restapi."${params.ProjectName}".findResult { environment -> environment["${params.BranchName}"] }

	if (fileId) {
		echo "ID branch ${params.BranchName} do projeto ${params.ProjectName}: ${fileId}"
		configFileProvider([configFile(fileId: fileId, targetLocation: '.env')]) {}
	} else {
		echo "Não foi encontrando o Id da branch ${params.BranchName} no projeto ${params.ProjectName}."
	}
} 