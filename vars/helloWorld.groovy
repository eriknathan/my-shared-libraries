// vars/restApiFlaskMongo.groovy

def call (Map pipelineParams) {
	
	def projectName = env.JOB_NAME.split('/')[0]
	def dockerLib = new docker.DockerLib()

	pipeline {
		agent { 
			label 'ubuntu'
		}
		environment {
			PROJECT_NAME = "${projectName}"
			//MODIFIED_JOB_NAME = JOB_NAME.replace("badges/", "badges/job/")
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
						sh "python3 .jenkins/status-badges.py $PROJECT_NAME_BADGE"
					}
			}
		}
	}
}