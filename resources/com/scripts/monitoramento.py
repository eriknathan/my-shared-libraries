import psutil
import datetime
import platform
import socket
import requests

# Variaveis de Horas e Datas
data_hora_atual = datetime.datetime.now()
data_hora_formatada = data_hora_atual.strftime("%Y-%m-%d %H:%M:%S")
# ------------------------------------------------------------------


# INFORMAÇÃO NOME DO SO
def informacao_sistema():
    start_time = datetime.datetime.fromtimestamp(psutil.boot_time())
    uptime = data_hora_atual - start_time
    print(f" - Sistema Operacional: {platform.system()}")
    print(f" - Nome do Sistema: {socket.gethostname()}")
    print(f" - Versão do Sistema: {platform.version()}")
    print(f" - Release do Sistema: {platform.release()}")
    print(f" - Arquitetura do Sistema: {platform.architecture()[0]} {platform.architecture()[1]} bits")
    print(f" - Uptime: {uptime}")


# INFORMAÇÕES DE CPU
def informacao_cpu():
    num_cores = psutil.cpu_count(logical=False)
    num_threads = psutil.cpu_count(logical=True)
    cpu_porcentagem = psutil.cpu_percent()
    print(f"- Métricas de CPU: {cpu_porcentagem}%")
    print(f"    Número de núcleos físicos: {num_cores}")
    print(f"    Número total de threads (incluindo hyperthreading): {num_threads}")


# INFORMAÇÕES DE MEMÓRIA
def informacao_memoria():
    memoria_virutal = psutil.virtual_memory()
    memoria_swap = psutil.swap_memory().percent
    print(f"- Memória Virtual: {memoria_virutal.percent}%")
    print(f"    Total de RAM: {memoria_virutal.total / (1024 ** 3):.2f} GB")
    print(f"    RAM utilizada: {memoria_virutal.used / (1024 ** 3):.2f} GB")
    print(f"    RAM livre: {memoria_virutal.available / (1024 ** 3):.2f} GB")
    print(f"    Memória Swap: {memoria_swap}%")


# INFORMAÇÃO DO DISCO
def informacao_disco():
    disco = psutil.disk_usage('/')
    print(f"- Disco [/]: {disco.percent}%")
    print(f"    Espaço total: {disco.total / (1024 ** 3):.2f} GB")
    print(f"    Espaço usado: {disco.used / (1024 ** 3):.2f} GB")
    print(f"    Espaço livre: {disco.free / (1024 ** 3):.2f} GB")


# INFORMAÇÕES DE REDE (RECEBIDO / ENVIADO)
def informacao_network():
    interface = "enp3s0"
    status_network = psutil.net_io_counters(pernic=True)
    endereco_rede = psutil.net_if_addrs()
    if interface not in status_network:
        print(f"A interface '{interface}' não foi encontrada.")
    stats = status_network[interface]
    print(f"- Interface: {interface}")
    print(f"    Bytes enviados: {stats.bytes_sent}")
    print(f"    Bytes recebidos: {stats.bytes_recv}")
    print(f"    Pacotes enviados: {stats.packets_sent}")
    print(f"    Pacotes recebidos: {stats.packets_recv}")
    if interface in endereco_rede:
        print("    Endereços de Rede:")
        for addr in endereco_rede[interface]:
            print(f"      Tipo: {addr.family}, Endereço: {addr.address}, Máscara: {addr.netmask}")


def main():
    print(f"------------------------------------------------------------------")
    print("          I N F O R M A Ç Õ E S  D O  S I S T E M A S")
    print(f"------------------------------------------------------------------")
    informacao_sistema()
    print(f"------------------------------------------------------------------")
    informacao_cpu()
    print(f"------------------------------------------------------------------")
    informacao_memoria()
    print(f"------------------------------------------------------------------")
    informacao_disco()
    print(f"------------------------------------------------------------------")
    informacao_network()
    print(f"------------------------------------------------------------------")


if __name__ == "__main__":
    main()
