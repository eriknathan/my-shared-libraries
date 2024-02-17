// vars/exemploPipeline.groovy

def call (Map pipelineParams) {

	def cleanLib = new functions.CleanLib()

    pipeline {
        agent { 
            label 'desktop'
        }

		stages {
			stage('Monitoramento') {
                steps {
                        
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " MONITORAMENTO EM PYTHON"
						echo " --------------------------------------------------------------------------------------- "

						def scriptpython = libraryResource 'com/scripts/monitoramento.py'
						writeFile file: './monitoramento.py', text: scriptpython
						sh 'python3 ./monitoramento.py Erik'
						
						sh cleanLib.cleanFiles(File: "monitoramento.py")
					}
				}
			}
		}
	}
}

