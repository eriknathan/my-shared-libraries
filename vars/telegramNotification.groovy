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
					    discordStartNotify(Stage: "build", ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME, BuildNumber: BUILD_NUMBER, BuildUrl: BUILD_URL)
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
			    telegramStartNotify(Stage: "success", ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME, BuildNumber: BUILD_NUMBER, BuildUrl: BUILD_URL)
			    discordStartNotify(Stage: "success", ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME, BuildNumber: BUILD_NUMBER)
            }
            failure {
                telegramStartNotify(Stage: "failure", ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME, BuildNumber: BUILD_NUMBER, BuildUrl: BUILD_URL)
                discordStartNotify(Stage: "failure", ProjectName: PROJECT_NAME, BranchName: BRANCH_NAME, BuildNumber: BUILD_NUMBER, BuildUrl: BUILD_URL)
            }
        }
	}
}

def telegramStartNotify(Map params){
	gitAuthor = sh(script: 'git show -s --format="%an" | sed "s/ //g"', returnStdout: true).trim()
    commitMessage = sh(script: 'git show -s --format=%s', returnStdout: true).trim()

    def scriptbash = libraryResource 'com/scripts/telegramNotify.sh'
	writeFile file: './telegramNotify.sh', text: scriptbash

    withCredentials([
            string(credentialsId: 'tribo-rossi-telegram-chat-id', variable: 'chatId'),
            string(credentialsId: 'tribo-rossi-telegram-bot-token', variable: 'botToken'),
	]) { 
        if ("${params.Stage}" == 'build') {
            sh "bash ./telegramNotify.sh send_build_alert '${params.ProjectName}' '${params.BranchName}' '${params.BuildNumber}' '${commitMessage}' '${gitAuthor}' "
        } else if ("${params.Stage}" == 'failure') {
            sh "bash ./telegramNotify.sh send_faliure_alert '${params.ProjectName}' '${params.BranchName}' '${params.BuildNumber}' '${commitMessage}' '${gitAuthor}' "
        } else {
            sh "bash ./telegramNotify.sh send_success_alert '${params.ProjectName}' '${params.BranchName}' '${params.BuildNumber}' '${commitMessage}' '${gitAuthor}' "
        }
	}
}

def discordStartNotify(Map params){
	gitAuthor = sh(script: 'git show -s --format="%an" | sed "s/ //g"', returnStdout: true).trim()
    commitMessage = sh(script: 'git show -s --format=%s', returnStdout: true).trim()

    def scriptbash = libraryResource 'com/scripts/discordNotify.sh'
	writeFile file: './discordNotify.sh', text: scriptbash
	
    if ("${params.Stage}" == 'build') {
        sh "bash ./discordNotify.sh send_build_alert '${params.ProjectName}' '${params.BranchName}' '${params.BuildNumber}' '${commitMessage}' '${gitAuthor}' '${params.BuildUrl}'"
    } else if ("${params.Stage}" == 'failure') {
        sh "bash ./discordNotify.sh send_faliure_alert '${params.ProjectName}' '${params.BranchName}' '${params.BuildNumber}' '${commitMessage}' '${gitAuthor}' '${params.BuildUrl}'"
    } else {
        sh "bash ./discordNotify.sh send_success_alert '${params.ProjectName}' '${params.BranchName}' '${params.BuildNumber}' '${commitMessage}' '${gitAuthor}' '${params.BuildUrl}'"
    }
}
