def dir (Map pipelineParams) {

	pipeline {
		agent { 
			label 'desktop'
		}
		
		stages {
			stage('Teste') {
				steps {
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " TESTE: HELLOWORLD "
						echo " --------------------------------------------------------------------------------------- "
						
						echo "HelloWorld Testando"
					}
				}
			}
		}
	}
}