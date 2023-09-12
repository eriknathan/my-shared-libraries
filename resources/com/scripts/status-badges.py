import os
import sys
import requests
from dotenv import load_dotenv

load_dotenv()


def status_lastbuild(jenkins_url, job_name, username, password):
    api_url = f'{jenkins_url}/job/{job_name}/lastBuild/api/json'
    response = requests.get(api_url, auth=(username, password))

    if response.status_code == 200:
        build_result = response.json()
        return build_result['result']
    else:
        return None


def generate_badge(label, message, color):
    badge_url = f"https://img.shields.io/badge/{label}-{message}-{color}"
    response = requests.get(badge_url)

    if response.status_code == 200:
        return response.content
    else:
        return None


def main():
    job_name = sys.argv[1]
    JENKINS_URL="http://localhost:8080/"
    JOB_NAME=f"{job_name}"
    USERNAME="admin"
    PASSWORD="admin123"

    build_status = status_lastbuild(JENKINS_URL, JOB_NAME, USERNAME, PASSWORD)

    print(f"Status do último build de {job_name}: {build_status}")
    if build_status is not None:
        label = f"Build Status: {job_name}"
        message = build_status
        if build_status == 'SUCCESS':
            color = "green"
        else:
            color = "red"

        badge_content = generate_badge(label, message, color)

        if badge_content is not None:
            with open(".jenkins/badge-status-build.svg", "wb") as badge_file:
                badge_file.write(badge_content)
            print("Badge gerado com sucesso!")
        else:
            print("Falha ao gerar o badge.")
    else:
        print("Falha ao obter o status do build.")


if __name__ == "__main__":
    main()
