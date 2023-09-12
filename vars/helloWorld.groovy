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
			stage('Teste Unitários') {
				steps {
					script {
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
						echo " --------------------------------------------------------------------------------------- "
						echo " GERANDO BADGE DE STATUS "
						echo " --------------------------------------------------------------------------------------- "

						def scriptpython = libraryResource 'com/scripts/status-badges.py'
						writeFile file: '.jenkins/status-badges.py', text: scriptpython
						sh "python3 .jenkins/status-badges.py $MODIFIED_JOB_NAME"

						sh cleanLib.cleanFiles(File: ".jenkins/status-badges.py")	
						sh gitLib.gitPush(Arquivo: ".jenkins", BranchName: "main")
						//sh gitLib.gitPush(Arquivo: ".jenkins", GitUser: "${GIT_USERNAME}", GitPass: ${"GIT_PASSWORD"}, BranchName: BRANCH_NAME)
				}
			}
		}
	}
}