// vars/restApiFlaskMongo.groovy

def call (Map pipelineParams) {
	
	def projectBaseName = env.JOB_NAME.split('/')[1]
	def dockerLib = new docker.DockerLib()

	pipeline {
		agent { 
			label 'ubuntu'
		}
		environment {
			DOCKER_IMAGE = "${DOCKER_REGISTRY}/${projectBaseName}:${BRANCH_NAME}-${BUILD_NUMBER}"
		}
		
		stages {
			stage('Image Build') {
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " BUILD DA IMAGEM: $DOCKER_IMAGE"
						echo " --------------------------------------------------------------------------------------- "
						
						configFileProvider([configFile(fileId: "e004133d-af4f-483d-8bdd-a9707f48a24e", targetLocation: '.env')]) {}
						//copyFiles(ProjectName: pipelineParams.projectBaseName, BranchName: "${BRANCH_NAME}")

						sh "docker build -t ${projectBaseName}:${BRANCH_NAME}-${BUILD_NUMBER} --no-cache -f Dockerfile ."
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
						sh "pip install flake8 pytest mongomock python-dotenv"

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

						sh "docker tag ${projectBaseName}:${BRANCH_NAME}-${BUILD_NUMBER} $DOCKER_IMAGE"
						sh "docker push $DOCKER_IMAGE"
					}
				}
			}

			stage('Image Run') {
				agent { label 'rest-api' }
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " RODANDO A APLICAÇÃO"
						echo " --------------------------------------------------------------------------------------- "

						configFileProvider([configFile(fileId: "e004133d-af4f-483d-8bdd-a9707f48a24e", targetLocation: '.env')]) {}
						//copyFiles(ProjectName: pipelineParams.projectBaseName, BranchName: "${BRANCH_NAME}")

						sh "echo DOCKER_IMAGE=${projectBaseName}:${BRANCH_NAME}-${BUILD_NUMBER} >> .env"
						sh "echo CONTAINER_NAME=${projectBaseName}-${BRANCH_NAME} >> .env"

						sh "docker image pull ${dockerImage}"
						sh "docker-compose -f docker-compose-ci.yml -p ${projectBaseName}-${BRANCH_NAME} up -d"
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