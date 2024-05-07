// vars/exemploPipeline.groovy

def call (Map pipelineParams) {

	def cleanLib = new functions.CleanLib()

    pipeline {
        agent { 
            label 'desktop'
        }

		environment {
            NAME = "${JOB_NAME}"
			BUILD_NUMBER = "${BUILD_NUMBER}"
		}


		stages {
			
			stage('Script Shell') {
                steps {
                        
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " COPIANDO O ACTIONS PARA DIRETORIO ATUAL "
						echo " --------------------------------------------------------------------------------------- "
						
						sh "mkdir -p teste1"
						sh "mkdir -p teste2"
						sh "mkdir -p teste3"
						sh "mkdir -p teste4"
						sh "touch -p erik.txt"
						sh "touch -p erik2.txt"

						def scriptbash = libraryResource 'com/actions/projeto01/ci.yaml'
						writeFile file: './ci.yaml', text: scriptbash
						sh "mkdir -p .github/workflows/ && mv ./ci.yaml .github/workflows/"
					}
				}
            }
		}
	}
}

