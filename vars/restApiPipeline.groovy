// vars/restApiFlaskMongo.groovy

def call (Map pipelineParams) {
	def projectName = env.JOB_NAME.split('/')[0]
	def dockerLib = new docker.DockerLib()

	pipeline {
		agent { 
			label 'ubuntu'
		}
		environment {
			DOCKER_IMAGE = "${DOCKER_REGISTRY}/${projectName}:${BRANCH_NAME}-${BUILD_NUMBER}"
			BRANCH_NAME = "${BRANCH_NAME}"
			PROJECT_NAME = "${projectName}"
		}
		
		stages {
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

			stage('Image Build') {
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " BUILD DA IMAGEM: $DOCKER_IMAGE"
						echo " --------------------------------------------------------------------------------------- "
						
						copyFiles(ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME)
						
						sh dockerLib.imgBuildPhase(DockerImage: DOCKER_IMAGE)
					}
				}
			}

			stage('Image Push') {
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " PUSH DA IMAGEM: $DOCKER_IMAGE"
						echo " --------------------------------------------------------------------------------------- "

						sh dockerLib.imgPushPhase(DockerImage: DOCKER_IMAGE)						
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

						copyFiles(ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME)

						sh dockerLib.imgRunPhase(DockerImage: DOCKER_IMAGE, 
												 ProjectName: PROJECT_NAME,
												 BranchName: BRANCH_NAME)
					}
				}
			}
		}
	}
}

def copyFiles(Map params) {
	def envjson = libraryResource 'com/json/projectsFilesList.json'
	def json = readJSON text: envjson

	def fileId = json.restapiflask."${params.ProjectName}".findResult { environment -> environment["${params.BranchName}"] }

	if (fileId) {
		echo "ID branch ${params.BranchName} do projeto ${params.ProjectName}: ${fileId}"
		configFileProvider([configFile(fileId: fileId, targetLocation: '.env')]) {}
	} else {
		echo "Não foi encontrando o Id da branch ${params.BranchName} no projeto ${params.ProjectName}."
	}
} 