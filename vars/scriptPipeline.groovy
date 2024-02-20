// vars/exemploPipeline.groovy

def call (Map pipelineParams) {

	def cleanLib = new functions.CleanLib()

    pipeline {
        agent { 
            label 'desktop'
        }

		environment {
            VAR_TESTE = "${PROJECT_NAME}"
		}


		stages {
			
			stage('Script Shell') {
                steps {
                        
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " INICIANDO O TESTE DO SCRIPT SH "
						echo " --------------------------------------------------------------------------------------- "
						
						def scriptbash = libraryResource 'com/scripts/segredos.sh'
						writeFile file: './segredos.sh', text: scriptbash
						sh 'bash ./segredos.sh "${VAR_TESTE}"'

						sh cleanLib.cleanFiles(File: "segredos.sh")
					}
				}
			}

			stage('Script Python') {
                steps {
                        
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " INICIANDO O TESTE DO SCRIPT PYTHON "
						echo " --------------------------------------------------------------------------------------- "

						def scriptpython = libraryResource 'com/scripts/teste.py'
						writeFile file: './teste.py', text: scriptpython
						sh 'python3 ./teste.py Erik'
						
						sh cleanLib.cleanFiles(File: "teste.py")
					}
				}
			}

			stage('Script Json') {
                steps {
                        
					script {
						echo " --------------------------------------------------------------------------------------- "
						echo " INICIANDO O TESTE DO SCRIPT JSON "
						echo " --------------------------------------------------------------------------------------- "

						def scriptjson = libraryResource 'com/json/request.json'

						def json = readJSON text: scriptjson
						echo "JSON: ${json}"

						json.pessoas.each { person ->
                        	echo "Nome: ${person.nome}"
                        	echo "Idade: ${person.idade}"
						}
					}
				}
			}
		}
	}
}

