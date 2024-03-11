def call (Map pipelineParams) {
	
	def projectName = pipelineParams.projectName

	pipeline {
		agent { label 'desktop' }
		
		environment {
			DOCKER_IMAGE = "${ECR_DEFAULT_REGISTRY}/${projectName}:${BRANCH_NAME}-${BUILD_NUMBER}"
		}
	
		stages {
			stage('Image Run') {
				agent { label getAgentForDeploy(env.BRANCH_NAME) }
                
				steps {				
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " CHAMANDO PIPELINE!"
						echo " --------------------------------------------------------------------------------------- "
					}
				}
			}
		}
		post {
			success {
					script {
					echo " --------------------------------------------------------------------------------------- "
					echo " AUTOMAÇÃO DO ROBOT "
					echo " --------------------------------------------------------------------------------------- "

					def scriptpython = libraryResource 'com/scripts/call_pipeline.py'
					writeFile file: './call_pipeline.py', text: scriptpython

					sh 'python3 ./call_pipeline.py Teste/pipeline-chamada'
					echo "Limpando arquivos temporários..."
					sh "sudo rm call_pipeline.py"
				}
			}
		}
	}
}
