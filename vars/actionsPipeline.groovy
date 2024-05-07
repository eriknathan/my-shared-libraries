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
						
						def scriptbash = libraryResource 'com/actions/projeto01/ci.yaml'
						writeFile file: './ci.yaml', text: scriptbash
						sh "cp ./ci.yaml .github/workflows/"
					}
				}
            }
		}
	}
}

