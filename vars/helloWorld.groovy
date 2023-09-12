// vars/restApiFlaskMongo.groovy

def call (Map pipelineParams) {
	
	def projectName = env.JOB_NAME.split('/')[0]
	def dockerLib = new docker.DockerLib()
	def cleanLib = new functions.CleanLib()
	def gitLib = new functions.GitLib()

	pipeline {
		agent { 
			label 'ubuntu'
		}
		environment {
			PROJECT_NAME = "${projectName}"
			BRANCH_NAME = "${BRANCH_NAME}"
			MODIFIED_JOB_NAME = JOB_NAME.replace("${projectName}/", "${projectName}/job/")
		}
		
		stages {
			stage('Teste') {
				steps {
					scrip {
						echo " --------------------------------------------------------------------------------------- "
						echo " TESTE: HELLOWORLD | $PROJECT_NAME"
						echo " --------------------------------------------------------------------------------------- "
						
						echo "HelloWorld Testando"
					}
				}
			}
		}

		post {
			always {
				script {
					def scriptpython = libraryResource 'com/scripts/status-badges.py'
					writeFile file: '.jenkins/status-badges.py', text: scriptpython
					sh "python3 .jenkins/status-badges.py $MODIFIED_JOB_NAME $JOB_NAME"

					sh cleanLib.cleanFiles(File: ".jenkins/status-badges.py")
					withCredentials([usernamePassword(credentialsId: 'github_login_erik', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
						sh gitLib.gitPush(File: ".jenkins")
						sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/eriknathan/badge-status.git HEAD:main"
					}	
				}
			}
		}
	}
}