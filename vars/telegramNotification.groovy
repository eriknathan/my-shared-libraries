// Pipeline do Projeto MouraConnect
// - URL no jenkins: https://jenkins.isitics.com/job/mouraconnect/
// - Ambientes: mouraconnect-be (develop, homolog, qa)
//              mouraconnect-fe (develop, homolog, qa)
// - Autor: Erik Nathan - erik.batista@sistemafiepe.org.br | @eriknathan
// - OBS: 

def call (Map pipelineParams) {
	
	def projectName = pipelineParams.projectName

	pipeline {
		agent { label 'desktop' }
		
		environment {
			DOCKER_IMAGE = "${projectName}:${BRANCH_NAME}-${BUILD_NUMBER}"
			BRANCH_NAME = "${BRANCH_NAME}"
			PROJECT_NAME = "${projectName}"
		}
	
		stages {
			stage('Image Build') {
				steps {
					script {
						echo " Nome do Projeto: $PROJECT_NAME "
						echo " --------------------------------------------------------------------------------------- "
						echo " BUILD DA IMAGEM: $DOCKER_IMAGE"
						echo " --------------------------------------------------------------------------------------- "
                    } 						
                }
            }

			stage('Image Push') {
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " PUSH DA IMAGEM: $DOCKER_IMAGE"
						echo " --------------------------------------------------------------------------------------- "
					}
				}
			}

			stage('Image Run') {
				steps {				
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " ENVIANDO ALERTAS"
						echo " --------------------------------------------------------------------------------------- "  
					    telegramStartNotify(ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME, BuildNumber: BUILD_NUMBER)
					}
				}
			}
		}
	}
}

def copyFiles(Map params) {
	def envjson = libraryResource 'json/projectsFilesList.json'
	def json = readJSON text: envjson

	def fileId = json.mouraconnect."${params.ProjectName}".findResult { environment -> environment["${params.BranchName}"] }

	if (fileId) {
		echo "ID branch ${params.BranchName} do projeto ${params.ProjectName}: ${fileId}"
		configFileProvider([configFile(fileId: fileId, targetLocation: '.env')]) {}
	} else {
		echo "Não foi encontrando o Id da branch ${params.BranchName} no projeto ${params.ProjectName}."
	}
} 

def telegramStartNotify(Map params){
	gitAuthor = sh(script: 'git show -s --format="%an" | sed "s/ //g"', returnStdout: true).trim()
    commitMessage = sh(script: 'git show -s --format=%s', returnStdout: true).trim()
	author = gitAuthor.replaceAll(/^([^<]+).*$/, '$1').trim()

    def scriptbash = libraryResource 'com/scripts/telegramNotify.sh'
	writeFile file: './telegramNotify.sh', text: scriptbash
	sh "bash ./telegramNotify.sh send_build_alert ${params.ProjectName} ${params.BranchName} '${params.BuildNumber}' '${commitMessage}' '${gitAuthor}' "
}