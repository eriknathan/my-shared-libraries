import sys

def script():
    if len(sys.argv) < 2:
        print("Uso: python3 teste.py <argumentos>")
        return

    argumento = sys.argv[1]

    print(f": Olá {argumento}! Seja bem vindo ao JenkinsLib")

script()