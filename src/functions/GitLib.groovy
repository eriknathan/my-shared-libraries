// src/functions/GitLib.groovy

package functions

class GitLib {
	def gitPush(Map params) {
		("git config --global user.email 'eriknathan.contato@gmail.com';" +
		"git config --global user.name 'eriknathan';" +
		"git add ${params.Arquivo};" +
		"git commit -m 'Adicionar nova linha';" +
		"git push -u origin ${params.BranchName}")
		//"git push https://${params.GitUser}:${params.GitPass}@github.com/seu-usuario/seu-repositorio.git HEAD:${params.BranchName}" )
	}
}




