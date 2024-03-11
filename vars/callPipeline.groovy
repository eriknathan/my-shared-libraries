def call (Map pipelineParams) {
	
	pipeline {
		agent { label 'desktop' }
		
		stages {
			stage('Image Run') {                
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

                    sh 'pip install jenkinsapi'
					sh 'python3 ./call_pipeline.py Teste/pipeline-chamada'
					echo "Limpando arquivos temporários..."
					sh "sudo rm call_pipeline.py"
				}
			}
		}
	}
}
