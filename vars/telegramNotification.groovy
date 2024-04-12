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
					    telegramStartNotify(Stage: "build", ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME, BuildNumber: BUILD_NUMBER)
                    } 						
                }
            }

			stage('Image Run') {
				steps {				
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " ENVIANDO ALERTAS"
						echo " --------------------------------------------------------------------------------------- "  
					}
				}
			}
		}
        post {
            success {
			    telegramStartNotify(Stage: "success", ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME, BuildNumber: BUILD_NUMBER)
            }
            failure {
                telegramStartNotify(Stage: "failure", ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME, BuildNumber: BUILD_NUMBER)
            }
        }
	}
}

def telegramStartNotify(Map params){
	gitAuthor = sh(script: 'git show -s --format="%an" | sed "s/ //g"', returnStdout: true).trim()
    commitMessage = sh(script: 'git show -s --format=%s', returnStdout: true).trim()

    def scriptbash = libraryResource 'com/scripts/telegramNotify.sh'
	writeFile file: './telegramNotify.sh', text: scriptbash

    if ("${params.Stage}" == 'build') {
	    sh "bash ./telegramNotify.sh send_build_alert '${params.ProjectName}' '${params.BranchName}' '${params.BuildNumber}' '${commitMessage}' '${gitAuthor}' "
    } else {
	    sh "bash ./telegramNotify.sh send_success_alert '${params.ProjectName}' '${params.BranchName}' '${params.BuildNumber}' '${commitMessage}' '${gitAuthor}' "
    }
}