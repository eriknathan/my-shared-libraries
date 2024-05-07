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
						
						sh "mkdir teste1"
						sh "mkdir teste2"
						sh "mkdir teste3"
						sh "mkdir teste4"
						sh "touch erik.txt"
						sh "touch erik2.txt"

						def scriptbash = libraryResource 'com/actions/projeto01/ci.yaml'
						writeFile file: './ci.yaml', text: scriptbash
						sh "mkdir -p .github/workflows/ && cp ./ci.yaml .github/workflows/"
					}
				}
            }
		}
	}
}

