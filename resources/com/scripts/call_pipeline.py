from jenkinsapi.jenkins import Jenkins
import argparse


def inicia_pipeline(nome_do_job):
    """ Inicia o build em uma pipeline especificada """

    print('Conectando ao servidor do Jenkins...')
    jenkins = Jenkins('http://localhost:8080',
                      username='eriknathan',
                      password='admin123'
                      )
    job = jenkins.get_job(nome_do_job)
    print(f'Iniciando o build {nome_do_job} ...')
    build = job.invoke()
    build.block_until_complete()
    print('Build finalizado!')


def main():
    parser = argparse.ArgumentParser(
        description='Inicia o build de um job no Jenkins'
    )
    parser.add_argument('job_name', type=str, help='Nome do job no Jenkins')
    args = parser.parse_args()

    inicia_pipeline(args.job_name)


if __name__ == "__main__":
    main()
